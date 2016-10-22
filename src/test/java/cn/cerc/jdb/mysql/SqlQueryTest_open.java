package cn.cerc.jdb.mysql;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.StubConnection;
import cn.cerc.jdb.field.StringField;

public class SqlQueryTest_open {
	private SqlQuery ds;
	private SqlConnection conn;

	@Before
	public void setUp() {
		conn = new StubConnection();
		ds = new SqlQuery(conn);
		ds.setMaximum(1);
		ds.add("select CorpNo_,CWCode_,PartCode_ from TranB1B where CorpNo_='%s'", "911001");
	}

	@Test(expected = RuntimeException.class)
	public void test_locked() {
		ds.getFieldDefs().add("CorpNo_", new StringField(10));
		ds.getFieldDefs().add("CWCode_", new StringField(10));
		// 仅定义了2个字段即锁定
		ds.getFieldDefs().setLocked(true);
		ds.open();
	}

	@Test(expected = RuntimeException.class)
	public void test_strict_1() {
		ds.setStrict(true);
		ds.open();
		// 不存在字段名
		ds.setField("CWCode", "1234567890");
	}

	@Test(expected = RuntimeException.class)
	public void test_strict_2() {
		ds.setStrict(true);
		ds.open();
		// Value 长度大于 10
		ds.setField("CWCode_", "12345678901");
	}

}
