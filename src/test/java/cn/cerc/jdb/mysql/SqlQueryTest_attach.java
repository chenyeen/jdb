package cn.cerc.jdb.mysql;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.core.StubConfig;

public class SqlQueryTest_attach {
	private SqlQuery ds;
	private SqlConnection conn;

	@Before
	public void setUp() {
		conn = new SqlConnection(new StubConfig());
		ds = new SqlQuery(conn);
	}

	@Test
	public void test() {
		String sql = "select * from ourinfo where CorpNo_='%s'";
		ds.attach(String.format(sql, "000000"));
		ds.attach(String.format(sql, "144001"));
		ds.attach(String.format(sql, "911001"));
		for (Record record : ds) {
			System.out.println(record.toString());
		}
	}

}
