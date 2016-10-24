package cn.cerc.jdb.mongo;

import com.mongodb.client.MongoDatabase;

import cn.cerc.jdb.core.ISession;

public class MongoSession implements ISession {
	// private static final Logger log = Logger.getLogger(MongoSession.class);
	public static final String sessionId = "mongoSession";
	private MongoDatabase database;

	public MongoSession() {
	}

	public MongoDatabase getDatabase() {
		return database;
	}

	public void setDatabase(MongoDatabase database) {
		this.database = database;
	}

	@Override
	public void closeSession() {
		if (database != null) {
			database = null;
		}
	}

}