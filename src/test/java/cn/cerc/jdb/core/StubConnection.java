package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.IConfig;
import cn.cerc.jdb.mysql.SqlConnection;

public class StubConnection extends SqlConnection {

	public StubConnection() {
		super();
		IConfig config = new StubConfig();
		this.setConfig(config);
	}
}
