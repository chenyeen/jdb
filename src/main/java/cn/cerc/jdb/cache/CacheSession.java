package cn.cerc.jdb.cache;

import java.util.Date;

import com.danga.MemCached.MemCachedClient;

import cn.cerc.jdb.core.ISession;

public class CacheSession implements ISession, IMemcache {
	private MemCachedClient client;
	public static final String sessionId = "cacheSession";

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
