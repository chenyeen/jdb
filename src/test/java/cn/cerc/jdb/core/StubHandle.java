package cn.cerc.jdb.core;

import cn.cerc.jdb.mysql.SqlConnection;
import cn.cerc.jdb.mysql.SqlSession;
import cn.cerc.jdb.queue.QueueConnection;
import cn.cerc.jdb.queue.QueueSession;

public class StubHandle implements IHandle, AutoCloseable {
	private SqlSession sqlSession;
	private QueueSession queueSession;

	public StubHandle() {
		super();
		IConfig config = new StubConfig();

		SqlConnection conn1 = new SqlConnection();
		conn1.setConfig(config);
		sqlSession = conn1.getSession();

		QueueConnection conn2 = new QueueConnection();
		conn2.setConfig(config);
		queueSession = conn2.getSession();
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
			return sqlSession;
		if (QueueSession.sessionId.equals(key))
			return queueSession;
		return null;
	}

	// 关闭资源
	public void closeConnections() {
		sqlSession.closeSession();
		queueSession.closeSession();
	}

	@Override
	public void close() {
		closeConnections();
	}
}
