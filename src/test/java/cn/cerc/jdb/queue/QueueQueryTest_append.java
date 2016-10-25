package cn.cerc.jdb.queue;

import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class QueueQueryTest_append {

	@Test
	public void test() {
		try (StubHandle handle = new StubHandle();) {
			// 增加模式
			QueueQuery ds = new QueueQuery(handle);
			ds.add("select * from %s", "test");
			ds.open();
			System.out.println(ds.getActive());
			// ds1.append();
			// ds1.setField("ok", "ok1");
			ds.save();
		}
	}
}
