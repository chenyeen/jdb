package cn.cerc.jdb.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.ISession;

public class SqlSession implements ISession {
	// Propertys中识别码
	public static final String rds_site = "rds.site";
	public static final String rds_database = "rds.database";
	public static final String rds_username = "rds.username";
	public static final String rds_password = "rds.password";
	// IHandle中识别码
	public static String sessionId = "sqlSession";

	private static final Logger log = Logger.getLogger(SqlSession.class);
	private Connection connection;
	private int tag;

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

	@Override
	public void closeSession() {
		try {
			if (connection != null) {
				log.debug("close connection.");
				connection.close();
				connection = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
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
