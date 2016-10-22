package cn.cerc.jdb.core;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.mysql.SqlConnection;
import cn.cerc.jdb.mysql.Transaction;

public class TransactionTest {
	private SqlConnection handle;

	@Before
	public void setUp() {
		handle = new SqlConnection(new StubConfig());
	}

	@Test
	@Ignore
	public void test_0() throws SQLException {
		// value + 0
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_err+1 where uid_=1");
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.close();
	}

	@Test
	@Ignore
	public void test_1() throws SQLException {
		// value + 0
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_err+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.close();
	}

	@Test
	@Ignore
	public void test_2() throws SQLException {
		// value + 1
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.close();
	}

	@Test
	@Ignore
	public void test_3() throws SQLException {
		// value + 1
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
			handle.execute("update Dept set amount_=amount_err+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.close();
	}

	@Test
	@Ignore
	public void test_4() throws SQLException {
		// value + 3
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			child_ok();
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.close();
	}

	@Test
	@Ignore
	public void test_5() throws SQLException {
		// value + 0
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			child_error();
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("main commit: " + tx.commit());
		}
		handle.close();
	}

	private void child_ok() {
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_+1 where uid_=1");
			System.out.println("child commit: " + tx.commit());
		}
	}

	private void child_error() {
		try (Transaction tx = new Transaction(handle)) {
			handle.execute("update Dept set amount_=amount_error+1 where uid_=1");
			System.out.println("child commit: " + tx.commit());
		}
	}
}
