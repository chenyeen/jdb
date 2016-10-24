package cn.cerc.jdb.cache;

import org.apache.log4j.Logger;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class CacheConnection implements IConnection {
	private IConfig config;
	private static final Logger log = Logger.getLogger(CacheConnection.class);
	private static String site;
	private static SockIOPool pool;
	// private static int count = 0;

	@Override
	public void setConfig(IConfig config) {
		this.config = config;
		if (pool == null && config != null) {
			site = config.getProperty("ocs.site", "127.0.0.1");
			String port = config.getProperty("ocs.port", "11211");
			String[] servers = { site + ":" + port };
			log.info("host: " + servers[0] + " init.");
			// 建立MemcachedClient实例
			pool = SockIOPool.getInstance();
			pool.setServers(servers);// 设置连接池可用的cache服务器列表，server的构成形式是IP:PORT（如：127.0.0.1:11211）
			pool.setFailover(true);// 设置容错开关。设置为TRUE，当前socket不可用时，程序会自动查找可用连接并返回，否则返回NULL，默认状态是true，建议保持默认。
			pool.setInitConn(10);// 设置开始时每个cache服务器的可用连接数
			pool.setMinConn(5);// 设置每个服务器最少可用连接数
			pool.setMaxConn(250);// 设置每个服务器最大可用连接数
			pool.setMaintSleep(30);// 设置连接池维护线程的睡眠时间，设置为0，维护线程不启动
			pool.setNagle(false);// 设置是否使用Nagle算法，如果通讯数据量通常都比较大（相对TCP控制数据）而且要求响应及时，因此该值需要设置为false（默认是true）
			pool.setSocketTO(1000);// 设置socket的读取等待超时值
			pool.setAliveCheck(true);// 设置连接心跳监测开关。设为true则每次通信都要进行连接是否有效的监测，造成通信次数倍增，加大网络负载，因此该参数应该在对HA要求比较高的场合设为TRUE，默认状态是false
			pool.initialize();// 设置完pool参数后最后调用该方法，启动pool。
		}
	}

	@Override
	public CacheSession getSession() {
		CacheSession sess = new CacheSession();
		sess.setClient(new MemCachedClient());
		return sess;
	}

	public IConfig getConfig() {
		return config;
	}
}
