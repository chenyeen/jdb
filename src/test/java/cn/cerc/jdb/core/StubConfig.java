package cn.cerc.jdb.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.cerc.jdb.mysql.IConfig;

public class StubConfig implements IConfig {
	private static final Log log = LogFactory.getLog(StubConfig.class);

	private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "mysql.properties";

	private static Properties properties = new Properties();
	static {
		try {
			File file2 = new File(SETTINGS_FILE_NAME);
			if (file2.exists()) {
				properties.load(new FileInputStream(SETTINGS_FILE_NAME));
				log.info("read properties from localhost[app.properties].");
			}
		} catch (FileNotFoundException e) {
			log.warn("The settings file '" + SETTINGS_FILE_NAME + "' does not exist.");
		} catch (IOException e) {
			log.warn("Failed to load the settings from the file: " + SETTINGS_FILE_NAME);
		}
	}

	@Override
	public String getProperty(String key, String def) {
		String result = properties.getProperty(key);
		if (result == null)
			throw new RuntimeException("请先准备好测试配置文件：" + SETTINGS_FILE_NAME);
		return result;
	}
}
