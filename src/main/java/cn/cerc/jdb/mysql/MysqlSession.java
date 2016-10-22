package cn.cerc.jdb.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.IConnection;

public class MysqlSession implements IConnection {
	// Propertys中识别码
	public static final String rds_site = "rds.site";
	public static final String rds_database = "rds.database";
	public static final String rds_username = "rds.username";
	public static final String rds_password = "rds.password";
	// IHandle中识别码
	public static String sessionId = "mysqlSession";

	private static final Logger log = Logger.getLogger(MysqlSession.class);
	private IConfig config;
	private boolean active = false;
	private Connection conn;
	private int tag;

	@Override
	public void setConfig(IConfig config) {
		this.config = config;
	}

	public IConfig getConfig() {
		return config;
	}

	// private void init_tomcat() {
	// Context initContext, envContext;
	// try {
	// initContext = new InitialContext();
	// envContext = (Context) initContext.lookup("java:/comp/env");
	// DataSource ds = (DataSource) envContext.lookup("jdbc/vinedb");
	// connection = ds.getConnection();
	// } catch (NamingException | SQLException e) {
	// e.printStackTrace();
	// throw new RuntimeException(e.getMessage());
	// }
	// }

	public boolean execute(String sql) {
		openSession();
		try {
			log.debug(sql);
			Statement st = conn.createStatement();
			st.execute(sql);
			return true;
		} catch (SQLException e) {
			log.error("error sql: " + sql);
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Object getSession() {
		openSession();
		return this;
	}

	@Override
	public void openSession() {
		if (!this.active) {
			String host = config.getProperty(rds_site, "127.0.0.1:3306");
			String db = config.getProperty(rds_database, "appdb");
			String user = config.getProperty(rds_username, "appdb_user");
			String pwd = config.getProperty(rds_password, "appdb_password");
			if (host == null || user == null || pwd == null || db == null)
				throw new RuntimeException("RDS配置为空，无法连接主机！");
			try {
				log.debug("create connection for mysql: " + host);
				String url = String.format("jdbc:mysql://%s/%s", host, db);
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection(url, user, pwd);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("找不到 mysql.jdbc 驱动");
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
			this.active = true;
		}
	}

	@Override
	public void closeSession() {
		try {
			if (conn != null) {
				log.debug("close connection.");
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		openSession();
		return conn;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	//
	// public String getDB()
	// {
	// Connection conn = getConnection();
	// try (PreparedStatement ps = conn.prepareStatement("select database()");
	// ResultSet rs = ps.executeQuery();)
	// {
	// if (rs.next())
	// return rs.getString(1);
	// } catch (SQLException e)
	// {
	// throw new DelphiException(e.getMessage());
	// }
	// return null;
	// }

	// 第一条记录第一个字段，有且只有一个字段
	// private String readString(String sql)
	// {
	// String Result = "";
	// Connection conn = getConnection();
	//
	// try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs =
	// ps.executeQuery())
	// {
	// ResultSetMetaData md = rs.getMetaData();
	// int cols = md.getColumnCount();
	// String str = "";
	// if (cols == 1 && rs.next())
	// str = rs.getString(1);
	// rs.last();
	// if (rs.getRow() == 1)
	// Result = str;
	// } catch (Exception e)
	// {
	// throw new DelphiException(e.getMessage());
	// }
	// return Result;
	// }
}
