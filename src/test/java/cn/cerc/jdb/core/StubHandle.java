package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.IConfig;
import cn.cerc.jdb.mysql.MysqlSession;

public class StubHandle implements IHandle {
	private MysqlSession conn;

	public StubHandle() {
		super();
		conn = new MysqlSession();
		IConfig config = new StubConfig();
		conn.setConfig(config);
	}

	@Override
	public String getCorpNo() {
		throw new RuntimeException("corpNo is null");
	}

	@Override
	public String getUserCode() {
		throw new RuntimeException("userCode is null");
	}

	@Override
	public Object getProperty(String key) {
		if (MysqlSession.sessionId.equals(key))
			return conn;
		return null;
	}

	// 关闭资源
	public void closeConnections() {
		conn.closeSession();
	}
}
