package cn.cerc.jdb.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.cerc.jdb.core.IRDSConfig;

public class StubConfig implements IRDSConfig {
	private static final Log log = LogFactory.getLog(StubConfig.class);

	private static final String SETTINGS_FILE_NAME = System.getProperty("user.home")
			+ System.getProperty("file.separator") + "delphi-vcl.properties";

	private static Properties config = new Properties();
	static {
		load();
	}

	public static void load() {
		try {
			File file2 = new File(SETTINGS_FILE_NAME);
			if (file2.exists()) {
				config.load(new FileInputStream(SETTINGS_FILE_NAME));
				log.info("read properties from localhost[app.properties].");
			}
		} catch (FileNotFoundException e) {
			log.warn("The settings file '" + SETTINGS_FILE_NAME + "' does not exist.");
		} catch (IOException e) {
			log.warn("Failed to load the settings from the file: " + SETTINGS_FILE_NAME);
		}
	}

	@Override
	public String get_rds_host() {
		return getProperty("rds.site", "vine2008.mysql.rds.aliyuncs.com:3306");
	}

	@Override
	public String get_rds_database() {
		return getProperty("rds.database", "vinedb");
	}

	@Override
	public String get_rds_account() {
		return getProperty("rds.username", "vine_user");
	}

	@Override
	public String get_rds_password() {
		return getProperty("rds.password", "Vine2013user");
	}

	private static String getProperty(String key, String def1) {
		String result = config.getProperty(key);
		if (result == null)
			throw new RuntimeException("请先准备好测试配置文件：" + SETTINGS_FILE_NAME);
		return result;
	}
}
