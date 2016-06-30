package cn.cerc.jdb.core;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.cerc.jdb.core.CustomDataSet;

public class CustomDataSetTest {
	private CustomDataSet ds = new CustomDataSet();

	@Test
	public void test_append() {
		ds.append();
		ds.setField("code", "value");
		ds.post();
		assertEquals(1, ds.size());
	}

	@Test(expected = RuntimeException.class)
	public void test_append_error() {
		ds.setSearchKey("code");
		ds.append();
	}
}
