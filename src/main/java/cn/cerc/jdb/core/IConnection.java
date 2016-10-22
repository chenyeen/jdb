package cn.cerc.jdb.core;

public interface IConnection extends AutoCloseable {

	// 返回连接
	public Object getConnection();
}
