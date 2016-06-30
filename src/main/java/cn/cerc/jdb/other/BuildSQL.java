package cn.cerc.jdb.other;

import cn.cerc.jdb.core.BuildQuery;
import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IConnection;

@Deprecated
public class BuildSQL extends BuildQuery implements AutoCloseable {

	public BuildSQL(DataQuery owner) {
		super(owner);
	}

	public BuildSQL(IConnection conn) {
		super(conn);
	}

	@Override
	public void close() {
		super.close();
	}
}
