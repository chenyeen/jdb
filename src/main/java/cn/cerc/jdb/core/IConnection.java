package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.SqlConnection;

public interface IConnection {
	public SqlConnection getConnection();
}
