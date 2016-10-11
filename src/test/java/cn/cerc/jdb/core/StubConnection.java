package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.SqlConnection;
import cn.cerc.jdb.mysql.SqlConnectionException;

public class StubConnection extends SqlConnection {
	public StubConnection() {
		super();
		try {
			init(new StubConfig());
		} catch (SqlConnectionException e) {
			// log.error("无法连接到主机：" + e.getHost(), e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}
}
