package cn.cerc.jdb.core;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.mysql.MysqlSession;
import cn.cerc.jdb.mysql.Transaction;

public class TransactionTest {
	private StubHandle handle;
	private MysqlSession conn;

	@Before
	public void setUp() {
		handle = new StubHandle();
		conn = (MysqlSession) handle.getProperty(MysqlSession.sessionId);
	}

	@Test
	@Ignore
	public void test_0() throws SQLException {
		// value + 0
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_err+1 where uid_=1");
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.closeConnections();
	}

	@Test
	@Ignore
	public void test_1() throws SQLException {
		// value + 0
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_err+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.closeConnections();
	}

	@Test
	@Ignore
	public void test_2() throws SQLException {
		// value + 1
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.closeConnections();
	}

	@Test
	@Ignore
	public void test_3() throws SQLException {
		// value + 1
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
			conn.execute("update Dept set amount_=amount_err+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.closeConnections();
	}

	@Test
	@Ignore
	public void test_4() throws SQLException {
		// value + 3
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			child_ok();
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.closeConnections();
	}

	@Test
	@Ignore
	public void test_5() throws SQLException {
		// value + 0
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			child_error();
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.closeConnections();
	}

	private void child_ok() {
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("child commit: " + tx.commit());
		}
	}

	private void child_error() {
		try (Transaction tx = new Transaction(handle)) {
			conn.execute("update Dept set amount_=amount_error+1 where uid_=1");
			System.out.println("child commit: " + tx.commit());
		}
	}
}
