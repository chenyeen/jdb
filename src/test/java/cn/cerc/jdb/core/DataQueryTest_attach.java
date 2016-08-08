package cn.cerc.jdb.core;

import org.junit.Test;

public class DataQueryTest_attach {
	private StubConnection conn = new StubConnection();
	private DataQuery ds = new DataQuery(conn);

	@Test
	public void test() {
		String sql = "select * from ourinfo where CorpNo_='%s'";
		ds.add(String.format(sql, "000000"));
		ds.open();
		ds.attach(String.format(sql, "144001"));
		ds.attach(String.format(sql, "911001"));
		for (Record record : ds) {
			System.out.println(record.toString());
		}
	}

}
