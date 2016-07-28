package cn.cerc.jdb.core;

import org.junit.Ignore;
import org.junit.Test;

public class DataQueryTest_Post {
	private StubConnection conn = new StubConnection();
	private DataQuery ds = new DataQuery(conn);

	@Test(expected = PostFieldException.class)
	@Ignore(value = "仅允许在测试数据库运行")
	public void post() {
		ds.add("select * from Dept where CorpNo_='%s'", "144001");
		ds.open();
		ds.edit();
		ds.setField("updateDate_", TDateTime.Now().incDay(-1));
		ds.post();
	}
}
