package cn.cerc.jdb.core;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.field.StringField;

public class DataQueryTest_post1 {
	private StubConnection conn = new StubConnection();
	private DataQuery ds = new DataQuery(conn);

	@Test(expected = PostFieldException.class)
	@Ignore(value = "仅允许在测试数据库运行")
	public void post_error() {
		ds.getFieldDefs().add("Test");
		ds.add("select * from Dept where CorpNo_='%s'", "144001");
		ds.open();
		ds.edit();
		ds.setField("updateDate_", TDateTime.Now().incDay(-1));
		ds.post();
	}

	@Test()
	@Ignore(value = "仅允许在测试数据库运行")
	public void post() {
		ds.add("select * from Dept where CorpNo_='%s'", "144001");
		ds.open();
		ds.setOnBeforePost(ds -> {
			System.out.println("before post");
		});
		ds.getFieldDefs().add("Test", new StringField(0).setCalculated(true));
		ds.edit();
		ds.setField("Test", "aOK");
		ds.setField("UpdateDate_", TDateTime.Now().incDay(-1));
		ds.post();
	}
}
