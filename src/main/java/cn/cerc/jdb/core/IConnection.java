package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.IConfig;

public interface IConnection {

	// 设置连接
	public void setConfig(IConfig config);

	// 返回会话
	public Object getSession();

	// 打开会话
	public void openSession();

	// 关闭会话
	public void closeSession();
}
