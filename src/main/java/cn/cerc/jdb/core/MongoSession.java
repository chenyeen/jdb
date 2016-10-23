package cn.cerc.jdb.core;

import com.mongodb.client.MongoDatabase;

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