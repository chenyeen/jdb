package cn.cerc.jdb.core;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.cerc.jdb.core.TDate;
import cn.cerc.jdb.core.TDateTime;

public class TDateTest {

	@Test
	public void test_Today() {
		TDate obj = TDate.Today();
		assertEquals(obj.getDate(), TDateTime.Now().getDate());
	}
}
