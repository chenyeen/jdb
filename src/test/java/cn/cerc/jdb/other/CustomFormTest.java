package cn.cerc.jdb.other;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.other.CustomForm;

public class CustomFormTest {
	@Test()
	@Ignore(value = "此测试无法在linux平台进行")
	public void create() {
		CustomForm obj = new CustomForm();
		assertNotNull(obj);
	}
}
