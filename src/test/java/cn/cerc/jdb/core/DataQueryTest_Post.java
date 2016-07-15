package cn.cerc.jdb.core;

import org.junit.Ignore;
import org.junit.Test;

public class DataQueryTest_Post {
	private StubConnection conn = new StubConnection();

	private DataQuery ds = new DataQuery(conn);

	@Test
	@Ignore(value = "仅允许在测试数据库运行")
	public void post() {
		// ds.add("select * from TranB1h where uid_=0");
		ds.open();
		ds.edit();
		ds.setField("UpdateDate_", TDateTime.Now().incDay(-1));
		ds.post();
		ds.edit();
		ds.setField("UpdateDate_", TDateTime.Now());
		ds.post();
	}
}
