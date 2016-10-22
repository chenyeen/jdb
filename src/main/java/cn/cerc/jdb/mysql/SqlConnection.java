package cn.cerc.jdb.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.IConfig;
import cn.cerc.jdb.core.IConnection;

public class SqlConnection implements IConnection {
	private static final Logger log = Logger.getLogger(SqlConnection.class);
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
		String host = config.getProperty(SqlSession.rds_site, "127.0.0.1:3306");
		String db = config.getProperty(SqlSession.rds_database, "appdb");
		String user = config.getProperty(SqlSession.rds_username, "appdb_user");
		String pwd = config.getProperty(SqlSession.rds_password, "appdb_password");
		if (host == null || user == null || pwd == null || db == null)
			throw new RuntimeException("RDS配置为空，无法连接主机！");
		try {
			log.debug("create connection for mysql: " + host);
			String url = String.format("jdbc:mysql://%s/%s", host, db);
			Class.forName("com.mysql.jdbc.Driver");
			SqlSession sess = new SqlSession();
			Connection connection = DriverManager.getConnection(url, user, pwd);
			sess.setConnection(connection);
			return sess;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("找不到 mysql.jdbc 驱动");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
