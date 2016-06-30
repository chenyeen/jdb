package cn.cerc.jdb.other;

import static org.junit.Assert.*;

import org.junit.Test;

import cn.cerc.jdb.other.TStringList;

public class TStringListTest {
	private TStringList sl = new TStringList();

	@Test
	public void test_add() {
		assertEquals("", sl.text());
		sl.add("a");
		assertEquals("a" + TStringList.vbCrLf, sl.text());
	}

	@Test
	public void test_getDelimitedText() {
		sl.setDelimiter(".");
		sl.add("a").add("b");
		sl.add("c");
		assertEquals("a.b.c", sl.getDelimitedText());
		assertSame(sl.count(), 3);
	}
}
