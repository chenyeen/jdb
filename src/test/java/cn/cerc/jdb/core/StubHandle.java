package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.SqlConnection;
import cn.cerc.jdb.mysql.SqlSession;

public class StubHandle implements IHandle {
	private SqlSession sess;

	public StubHandle() {
		super();
		IConfig config = new StubConfig();
		SqlConnection conn = new SqlConnection();
		conn.setConfig(config);
		sess = conn.getSession();
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
			return sess;
		return null;
	}

	// 关闭资源
	public void closeConnections() {
		sess.closeSession();
	}
}
