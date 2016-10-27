package cn.cerc.jdb.nas;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.queue.QueueOperator;

public class NasQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private IHandle handle;
	// 文件目录
	private String filePath;
	// 文件名称
	private String fileName;
	private QueueOperator operator;

	public NasQuery(IHandle handle) {
		super(handle);
		this.handle = handle;
	}

	@Override
	public DataQuery open() {
		try {
			this.fileName = this.getCommandText()
					.substring(this.getCommandText().indexOf("select") + 6, this.getCommandText().indexOf("from"))
					.trim();
			this.filePath = getOperator().findTableName(this.getCommandText());
		} catch (Exception e) {
			throw new RuntimeException("语法为: select fileName from filePath");
		}
		// 校验数据
		if (StringUtils.isEmpty(this.filePath))
			throw new RuntimeException("请输入文件路径");
		return this;
	}

	// 查询文件
	public String getFileContext() {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		String fileContext = "";
		try {
			fileContext = FileUtils.readFileToString(file, CharEncoding.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContext;
	}

	// 保存或更新文件
	public void save(String fileContext) {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		try {
			FileUtils.writeStringToFile(file, fileContext, CharEncoding.UTF_8, false);// 不存在则创建,存在则不追加到文件末尾
		} catch (IOException e) {
			log.info("文件:" + file.getPath() + "保存失败");
			e.printStackTrace();
		}
		log.info("文件:" + file.getPath() + "保存成功");
	}

	// 删除文件或目录
	@Override
	public void delete() {
		File file = FileUtils.getFile(this.filePath, this.fileName);
		FileUtils.deleteQuietly(file);
		log.info("文件:" + file.getPath() + "删除成功");
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
