package cn.cerc.jdb.oss;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang.StringUtils;

import com.aliyun.oss.model.OSSObject;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.queue.QueueOperator;

public class OssQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	private OssSession session = null;
	@SuppressWarnings("unused")
	private IHandle handle;
	// 文件目录
	private String bucketName;
	// 文件名称
	private String objectId;

	private QueueOperator operator;

	public OssQuery(IHandle handle) {
		super(handle);
		this.handle = handle; // 暂时无用
		session = (OssSession) this.handle.getProperty(OssSession.sessionId);
	}

	@Override
	public DataQuery open() {
		try {
			this.objectId = this.getCommandText()
					.substring(this.getCommandText().indexOf("select") + 6, this.getCommandText().indexOf("from"))
					.trim();
			this.bucketName = getOperator().findTableName(this.getCommandText());
		} catch (Exception e) {
			throw new RuntimeException("语法为: select objectId from bucketName");
		}
		// 校验数据
		if (StringUtils.isEmpty(this.bucketName))
			throw new RuntimeException("请输入bucketName");
		return this;
	}

	// 查询文件
	public String getFileContext() {
		StringBuffer sb = new StringBuffer();
		try {
			OSSObject ossObject = session.getOssClient().getObject(this.bucketName, this.objectId);
			BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));
			while (true) {
				String line;
				line = reader.readLine();
				if (line == null)
					break;
				sb.append(line);
			}
		} catch (Exception e) {
			log.info("文件读取出错");
			e.printStackTrace();
			return null;// 没有找到则返回null
		}
		return sb.toString();
	}

	// 保存或更新文件
	public void save(String fileContext) {
		session.getOssClient().putObject(bucketName, objectId, new ByteArrayInputStream(fileContext.getBytes()));
		log.info("对象:" + this.bucketName + "." + objectId + "保存成功");
	}

	/**
	 * 删除文件或目录 Description
	 */
	@Override
	public void delete() {
		session.getOssClient().deleteObject(this.bucketName, objectId);
		log.info("对象:" + this.bucketName + "." + objectId + "删除成功");
	}

	@Deprecated
	@Override
	public void save() {
		throw new RuntimeException("本方法不提供服务,请使用save(String fileContext)");
	}

	@Override
	public QueueOperator getOperator() {
		if (operator == null)
			operator = new QueueOperator();
		return operator;
	}

}
