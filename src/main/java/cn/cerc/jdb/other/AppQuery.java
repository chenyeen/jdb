package cn.cerc.jdb.other;

import cn.cerc.jdb.core.DataQuery;
import cn.cerc.jdb.core.IConnection;
import cn.cerc.jdb.core.SqlConnection;

@Deprecated
public class AppQuery extends DataQuery implements AutoCloseable {
	private static final long serialVersionUID = -2129105863703473311L;

	public AppQuery(SqlConnection conn) {
		super(conn);
	}

	public AppQuery(IConnection conn) {
		super(conn.getConnection());
	}

	@Override
	public void close() {
		super.close();
	}
}
