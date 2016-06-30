package cn.cerc.jdb.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.cerc.jdb.core.DataSet;

public class DataSetTest {
	private DataSet ds;
	private static final int MAX = 1000;

	@Before
	public void setUp() throws Exception {
		ds = new DataSet();
	}

	@After
	public void tearDown() throws Exception {
		ds.close();
	}

	@Test(timeout = 1000)
	public void testLocate() {
		for (int i = 0; i < MAX; i++) {
			String key = "code" + i;
			ds.append();
			ds.setField("code", key);
			ds.setField("value", i);
			ds.post();
		}
		for (int i = 100; i < MAX; i++)
			ds.locate("code", String.valueOf(i));
	}

	@Test(timeout = 1000)
	public void testSearch() {
		ds.setSearchKey("code");
		for (int i = 0; i < MAX; i++) {
			String key = "code" + i;
			ds.append(key);
			ds.setField("code", key);
			ds.setField("value", i);
			ds.post();
		}
		for (int i = 100; i < MAX; i++)
			ds.locate(String.valueOf(i));
	}
}
