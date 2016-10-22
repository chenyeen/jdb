package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.IConfig;

public interface IConnection extends AutoCloseable {

	//设置连接
	public void setConfig(IConfig config);

	// 返回会话环境
	public Object getSession();
}
