package cn.cerc.jdb.core;

import org.apache.log4j.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class MongoConnection implements IConnection {
	private static final Logger log = Logger.getLogger(MongoConnection.class);
	private static MongoClient pool;
	private static String dbname;

	@Override
	public void setConfig(IConfig config) {
		dbname = config.getProperty("mgdb.dbname", null);
		if (MongoConnection.pool == null) {
			MongoClientURI connectionString = null;
			StringBuffer sb = new StringBuffer();
			sb.append("mongodb://");
			// userName
			sb.append(config.getProperty("mgdb.username", null));
			// password
			sb.append(":").append(config.getProperty("mgdb.password", null));
			// ip
			sb.append("@").append(config.getProperty("mgdb.ipandport", null));
			// database
			sb.append("/").append(config.getProperty("mgdb.dbname", null));

			if ("true".equals(config.getProperty("mgdb.enablerep", null))) {
				// replacaset
				sb.append("?").append("replicaSet=").append(config.getProperty("mgdb.replicaset", null));
				// poolsize
				sb.append("&").append("maxPoolSize=").append(config.getProperty("mgdb.maxpoolsize", null));

				// MongoClientURI connectionString = new MongoClientURI(
				// "mongodb://ehealth:123456@115.28.67.211:3717,115.28.67.211:13717/ehealth?replicaSet=mgset-2004675");
				log.info("连接到MongoDB分片集群:" + sb.toString());
				pool = new MongoClient(new MongoClientURI(sb.toString()));
			} else {
				log.info("连接到MongoDB单节点:" + sb.toString());
				// ECS外测试环境单节点连接
				// MongoClientURI connectionString = new
				// MongoClientURI("mongodb://ehealth:123456@115.28.67.211:3717/ehealth");
				connectionString = new MongoClientURI(sb.toString());
				pool = new MongoClient(connectionString);
			}
		}
	}

	@Override
	public MongoSession getSession() {
		MongoDatabase database = pool.getDatabase(dbname);
		MongoSession sess = new MongoSession();
		sess.setDatabase(database);
		return sess;
	}
}
