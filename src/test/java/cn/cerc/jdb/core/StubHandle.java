package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.IConfig;
import cn.cerc.jdb.mysql.SqlSession;

public class StubHandle implements IHandle {
	private SqlSession conn;

	public StubHandle() {
		super();
		conn = new SqlSession();
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
		if (SqlSession.sessionId.equals(key))
			return conn;
		return null;
	}

	// 关闭资源
	public void closeConnections() {
		conn.closeSession();
	}
}
