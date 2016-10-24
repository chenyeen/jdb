package cn.cerc.jdb.cache;

public interface IMemcache {
	void set(String key, Object value, int expires);

	Object get(String key);

	void delete(String key);
	
	default public void set(String key, Object value) {
		this.set(key, value, 3600);
	}
}
