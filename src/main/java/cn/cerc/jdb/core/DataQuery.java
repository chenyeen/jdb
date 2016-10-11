package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.SqlQuery;

public class DataQuery extends SqlQuery {
	private static final long serialVersionUID = 7316772894058168187L;

	public DataQuery(SqlConnection conn) {
		super(conn);
	}

	public DataQuery(IConnection conn) {
		super(conn);
	}

}
