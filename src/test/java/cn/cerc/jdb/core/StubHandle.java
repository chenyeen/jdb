package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.IConfig;
import cn.cerc.jdb.mysql.SqlConnection;
import cn.cerc.jdb.mysql.SqlQuery;

public class StubHandle implements IHandle {
	private SqlConnection conn;

	public StubHandle() {
		super();
		conn = new SqlConnection();
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
		if (SqlQuery.sessionId.equals(key))
			return conn;
		return null;
	}

	// 关闭资源
	public void closeConnections() {
		conn.closeSession();
	}
}
