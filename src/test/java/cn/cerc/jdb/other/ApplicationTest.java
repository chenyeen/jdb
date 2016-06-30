package cn.cerc.jdb.other;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.jdb.other.Application;
import cn.cerc.jdb.other.CustomForm;

public class ApplicationTest {
	private Application target = new Application();

	@Test
	@Ignore(value = "此测试无法在linux平台进行")
	public void createForm() {
		CustomForm obj = target.createForm(CustomForm.class);
		assertNotNull(obj);
	}
}
