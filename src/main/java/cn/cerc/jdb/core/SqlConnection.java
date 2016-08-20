package cn.cerc.jdb.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class SqlConnection extends Component implements AutoCloseable {
	private static final Logger log = Logger.getLogger(SqlConnection.class);

	private Connection connection;

	public SqlConnection() {

	}

	public SqlConnection(SqlConfig config) {
		// if (Config.getAppLevel() == Config.appTest)
		init_ace(config);
	}

	private void init_ace(SqlConfig config) {
		String host = config.get_rds_host();
		String user = config.get_rds_account();
		String pwd = config.get_rds_password();
		String db = config.get_rds_database();
		if (host == null || user == null || pwd == null || db == null)
			throw new RuntimeException("RDS配置为空，无法连接主机！");
		try {
			log.debug("create connection for mysql: " + host);
			String url = String.format("jdbc:mysql://%s/%s", host, db);
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, user, pwd);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("找不到 mysql.jdbc 驱动");
		} catch (SQLException e) {
			log.error("无法连接到主机：" + host, e);
			throw new RuntimeException(e.getMessage());
		}
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
		try {
			log.debug(sql);
			Statement st = connection.createStatement();
			st.execute(sql);
			return true;
		} catch (SQLException e) {
			log.error("error sql: " + sql);
			throw new RuntimeException(e.getMessage());
		}
	}

	public Connection getConnection() {
		return connection;
	}

	@Override
	public final void close() {
		try {
			if (connection != null) {
				log.debug("close connection.");
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
