package cn.cerc.jdb.core;

public interface IConnection {

	// 设置连接
	public void setConfig(IConfig config);

	// 返回会话
	public Object getSession();

}
