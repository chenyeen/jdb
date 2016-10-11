package cn.cerc.jdb.mysql;

import cn.cerc.jdb.core.IConnection;

@Deprecated
public class DataQuery extends SqlQuery {
	private static final long serialVersionUID = 7316772894058168187L;

	public DataQuery(SqlConnection conn) {
		super(conn);
	}

	public DataQuery(IConnection conn) {
		super(conn);
	}

}
