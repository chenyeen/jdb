package cn.cerc.jdb.queue;

import org.junit.Test;

import cn.cerc.jdb.core.StubHandle;

public class QueueQueryTest_receive {

	@Test
	public void test() {
		try (StubHandle handle = new StubHandle();) {
			// 读取模式
			QueueQuery ds = new QueueQuery(handle);
			ds.setQueueMode(QueueMode.recevie);
			ds.add("select * from %s", "test");
			ds.open();

			System.out.println(ds.getActive());
			System.out.println(ds.getJSON());
			// do something
			ds.remove();
		}
	}
}
