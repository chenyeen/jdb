package cn.cerc.jdb.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class SqlConnection implements IConnection {
	private static final Logger log = Logger.getLogger(SqlConnection.class);
	private String url;
	private String host;
	private String db;
	private String user;
	private String pwd;
	private IConfig config;

	@Override
	public void setConfig(IConfig config) {
		this.config = config;
	}

	public IConfig getConfig() {
		return config;
	}

	@Override
	public SqlSession getSession() {
		init();
		try {
			log.debug("create connection for mysql: " + host);
			Connection connection = DriverManager.getConnection(url, user, pwd);
			SqlSession sess = new SqlSession();
			sess.setConnection(connection);
			return sess;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init() {
		if (url == null) {
			try {
				host = config.getProperty(SqlSession.rds_site, "127.0.0.1:3306");
				db = config.getProperty(SqlSession.rds_database, "appdb");
				url = String.format("jdbc:mysql://%s/%s", host, db);
				user = config.getProperty(SqlSession.rds_username, "appdb_user");
				pwd = config.getProperty(SqlSession.rds_password, "appdb_password");
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("找不到 mysql.jdbc 驱动");
			}
		}
		if (host == null || user == null || pwd == null || db == null)
			throw new RuntimeException("RDS配置为空，无法连接主机！");
	}
}
