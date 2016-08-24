package cn.cerc.jdb.core;

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
