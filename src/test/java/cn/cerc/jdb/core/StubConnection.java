package cn.cerc.jdb.core;

import cn.cerc.jdb.core.SqlConnection;

public class StubConnection extends SqlConnection {
	public StubConnection() {
		super(new StubConfig());
	}
}
