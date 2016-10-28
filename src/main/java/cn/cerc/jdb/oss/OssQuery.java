package cn.cerc.jdb.oss;

import java.io.ByteArrayInputStream;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.queue.OssOperator;

public class OssQuery extends DataQuery {
	private static final long serialVersionUID = 1L;
	private OssSession session = null;
	private OssOperator operator;
	// 文件名称
	private String fileName;
	private OssMode ossMode = OssMode.create;

	public OssQuery(IHandle handle) {
		super(handle);
		session = (OssSession) this.handle.getProperty(OssSession.sessionId);
	}

	@Override
	public DataQuery open() {
		try {
			this.fileName = getOperator().findTableName(this.getCommandText());
			if (ossMode == OssMode.readWrite) {
				String value = session.getContent(this.fileName);
				if (value != null) {
					this.setJSON(value);
					this.setActive(true);
				}
			}
			return this;
		} catch (Exception e) {
			throw new RuntimeException("语法为: select * from objectId");
		}
	}

	/**
	 * 删除文件
	 */
	public void remove() {
		session.delete(this.fileName);
	}

	@Override
	public void save() {
		String content = this.getJSON();
		session.upload(fileName, new ByteArrayInputStream(content.getBytes()));
	}

	@Override
	public OssOperator getOperator() {
		if (operator == null)
			operator = new OssOperator();
		return operator;
	}

	public void sessionClose() {
		this.session.closeSession();
	}
	public OssMode getOssMode() {
		return ossMode;
	}

	public void setOssMode(OssMode ossMode) {
		this.ossMode = ossMode;
	}

}
