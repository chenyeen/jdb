package cn.cerc.jdb.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.cerc.jdb.core.BatchScript;

public class BatchScriptTest {
	private BatchScript bs;

	@Test
	public void test_getItems() {
		bs = new BatchScript(null);
		bs.add("select * from a");
		bs.addSemicolon();
		bs.add("select * from b");
		String s1 = "select * from a ;" + cn.cerc.jdb.other.utils.vbCrLf + "select * from b ";
		assertEquals(s1, bs.toString());
	}

	@Test
	public void test_exists() {
		StubConnection conn = new StubConnection();
		bs = new BatchScript(conn);
		bs.add("select * from Account where Code_='%s';", "admin");
		bs.add("select * from Account where Code_='%s';", "99900101");
		bs.exec();
		assertTrue(bs.exists());
	}

	@Test
	public void test_getItem() {
		bs = new BatchScript(null);
		bs.add("select * from a");
		bs.addSemicolon();
		bs.add("select * from b");
		assertEquals(bs.size(), 2);
		assertEquals(bs.getItem(0), "select * from a");
		assertEquals(bs.getItem(1), "select * from b");
	}

	@Test(expected = RuntimeException.class)
	public void test_getItem_err() {
		bs = new BatchScript(null);
		bs.add("select * from a");
		bs.addSemicolon();
		bs.add("select * from b");
		assertEquals(bs.size(), 2);
		assertEquals(bs.getItem(2), "select * from a");
	}
}
