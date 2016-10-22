package cn.cerc.jdb.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.IHandle;

public class Transaction implements AutoCloseable {
	private static final Logger log = Logger.getLogger(Transaction.class);
	private Connection conn;
	private boolean active = false;
	private boolean locked = false;

	public Transaction(Connection conn) {
		this.conn = conn;
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
				this.active = true;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public Transaction(IHandle handle) {
		SqlSession cn = (SqlSession) handle.getProperty(SqlSession.sessionId);
		this.conn = cn.getConnection();
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
				this.active = true;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public boolean commit() {
		if (!active)
			return false;
		if (locked)
			throw new RuntimeException("Transaction locked is true");
		try {
			conn.commit();
			locked = true;
			return true;
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() {
		if (!active)
			return;
		try {
			try {
				conn.rollback();
			} finally {
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public boolean isActive() {
		return active;
	}
}
