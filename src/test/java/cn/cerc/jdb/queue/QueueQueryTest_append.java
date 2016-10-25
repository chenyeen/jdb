package cn.cerc.jdb.queue;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class QueueQueryTest_append {

	private QueueQuery ds = null;
	private StubHandle handle;

	@Before
	public void setUp() {
		handle = new StubHandle();
		ds = new QueueQuery(handle);
	}

	@Test
	public void test() {
		// 增加模式
		ds.add("select * from %s", "test");
		ds.open();
		System.out.println(ds.getActive());
		// ds1.append();
		// ds1.setField("ok", "ok1");
		ds.save();
	}
}
