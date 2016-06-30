package cn.cerc.jdb.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cn.cerc.jdb.core.BuildQuery;
import cn.cerc.jdb.core.IConnection;
import cn.cerc.jdb.core.SqlConnection;

public class BuildQueryTest implements IConnection {
	// private static final Logger log = Logger.getLogger(BuildSQLTest.class);

	private BuildQuery bs = new BuildQuery(this);

	@Override
	public SqlConnection getConnection() {
		return null;
	}

	@Test
	public void test_close() {
		bs.add("x");
		bs.byParam("x");
		bs.byField("x", "y");
		bs.setOrderText("ok");
		bs.close();
		assertEquals("", bs.getCommandText());
	}

	@Test
	public void test_add() {
		bs.setMaximum(-1);
		bs.add("select * from %s", "TABLE");
		assertEquals("select * from TABLE", bs.getCommandText());
		bs.setMaximum(-1);
		bs.add("where code='%s'", "X");
		assertEquals("select * from TABLE" + BuildQuery.vbCrLf + "where code='X'", bs.getCommandText());
		bs.close();
		assertEquals("", bs.getCommandText());
	}

	@Test
	public void test_byField() {
		String obj = null;
		bs.byField("code", obj);
		assertEquals("", bs.getSelectCommand());
		bs.close();

		bs.byField("code", "x");
		assertEquals("where code='x'", bs.getSelectCommand());
		bs.close();

		bs.byField("code", "x*");
		assertEquals("where code like 'x%'", bs.getSelectCommand());
		bs.byField("name", "y");
		assertEquals("where code like 'x%' and name='y'", bs.getSelectCommand());
		bs.close();

		bs.byField("code", "``");
		assertEquals("where code='`'", bs.getSelectCommand());
		bs.close();

		bs.byField("code", "`is null");
		assertEquals("where (code is null or code='')", bs.getSelectCommand());
		bs.close();

		bs.byField("code", "`=100");
		assertEquals("where code=100", bs.getSelectCommand());
		bs.close();

		bs.byField("code", "`!=100");
		assertEquals("where code<>100", bs.getSelectCommand());
		bs.close();

		bs.byField("code", "`<>100");
		assertEquals("where code<>100", bs.getSelectCommand());
		bs.close();
	}
}
