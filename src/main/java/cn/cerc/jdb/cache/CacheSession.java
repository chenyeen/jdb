package cn.cerc.jdb.cache;

import java.util.Date;

import com.danga.MemCached.MemCachedClient;

import cn.cerc.jdb.core.ISession;

public class CacheSession implements ISession, IMemcache {
	public static final String ocs_site = "ocs.site";
	public static final String ocs_port = "ocs.port";
	public static final String sessionId = "cacheSession";
	private MemCachedClient client;

	@Override
	public void closeSession() {
		if (client != null)
			client = null;
	}

	public MemCachedClient getClient() {
		return client;
	}

	public void setClient(MemCachedClient client) {
		this.client = client;
	}

	@Override
	public Object get(String key) {
		return client.get(key);
	}

	@Override
	public void set(String key, Object value, int expires) {
		client.set(key, value, new Date(expires * 1000));
	}

	@Override
	public void delete(String key) {
		client.delete(key);
	}
}
