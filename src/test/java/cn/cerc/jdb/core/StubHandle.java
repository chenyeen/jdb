package cn.cerc.jdb.core;

import cn.cerc.jdb.mongo.MongoConnection;
import cn.cerc.jdb.mongo.MongoSession;
import cn.cerc.jdb.mysql.SqlConnection;
import cn.cerc.jdb.mysql.SqlSession;
import cn.cerc.jdb.queue.QueueConnection;
import cn.cerc.jdb.queue.QueueSession;

public class StubHandle implements IHandle {
	private SqlSession mysqlSession;
	private MongoSession mgSession;
	private QueueSession queueSession;

	public StubHandle() {
		super();
		IConfig config = new StubConfig();
		// mysql
		SqlConnection conn = new SqlConnection();
		conn.setConfig(config);
		mysqlSession = conn.getSession();
		// mongodb
		MongoConnection mgconn = new MongoConnection();
		mgconn.setConfig(config);
		mgSession = mgconn.getSession();
		// aliyun mq
		QueueConnection queconn = new QueueConnection();
		queconn.setConfig(config);
		queueSession = queconn.getSession();

	}

	@Override
	public String getCorpNo() {
		throw new RuntimeException("corpNo is null");
	}

	@Override
	public String getUserCode() {
		throw new RuntimeException("userCode is null");
	}

	@Override
	public Object getProperty(String key) {
		if (SqlSession.sessionId.equals(key))
			return mysqlSession;
		if (MongoSession.sessionId.equals(key))
			return mgSession;
		if (QueueSession.sessionId.equals(key))
			return queueSession;
		return null;
	}

	// 关闭资源
	public void closeConnections() {
		mysqlSession.closeSession();
		queueSession.closeSession();
		mgSession.closeSession();
	}

	public void close() {
		closeConnections();
	}
}
