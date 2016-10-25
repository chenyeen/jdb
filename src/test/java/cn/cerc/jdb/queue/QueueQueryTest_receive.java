package cn.cerc.jdb.queue;

import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class QueueQueryTest_receive {

	private QueueQuery ds = null;
	private StubHandle handle;

	@Before
	public void setUp() {
		handle = new StubHandle();
		ds = new QueueQuery(handle);
	}

	@Test
	public void test() {
		ds.setQueueMode(QueueMode.recevie);
		ds.add("select * from %s", "test");
		ds.open();

		System.out.println(ds.getActive());
		System.out.println(ds.getJSON());
		// do something
		ds.remove();
	}
}
