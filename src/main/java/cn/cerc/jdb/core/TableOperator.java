package cn.cerc.jdb.core;

import java.sql.Connection;

import cn.cerc.jdb.mysql.SqlOperator;

@Deprecated
public class TableOperator extends SqlOperator {

	public TableOperator(Connection connection) {
		super(connection);
	}
}
