package cn.cerc.jdb.core;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.cerc.jdb.core.DatabaseDict;

public class DatabaseDictTest {
	@Test
	public void get() {
		String key = DatabaseDict.get("CurrentUser");
		assertEquals(key, "UID_");
	}
}
