package cn.cerc.jdb.core;

import static cn.cerc.jdb.other.utils.roundTo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.cerc.jdb.other.DelphiException;

public class DataQuery extends DataSet {
	private static final Logger log = Logger.getLogger(DataQuery.class);

	private static final long serialVersionUID = 7316772894058168187L;
	private final String uid = "UID_";
	private SqlConnection connection;
	private String commandText;
	private boolean active = false;
	// private boolean closeMax = false;
	private int offset = 0;
	private int maximum = BigdataException.MAX_RECORDS;

	// 若数据有取完，则为true，否则为false
	private boolean fetchFinish;

	@Override
	public void close() {
		this.active = false;
		super.close();
	}

	public DataQuery(SqlConnection conn) {
		super();
		super.init(conn);
		this.connection = conn;
	}

	public DataQuery(IConnection conn) {
		super();
		super.init(conn.getConnection());
		this.connection = conn.getConnection();
	}

	public DataQuery open() {
		if (connection == null)
			throw new RuntimeException("SqlConnection is null");
		Connection conn = connection.getConnection();
		if (conn == null)
			throw new RuntimeException("Connection is null");
		try {
			this.fetchFinish = true;
			try (Statement st = conn.createStatement()) {
				log.debug("取数据: begin");
				String sql = getSelectCommand();
				log.debug(sql.replaceAll("\r\n", " "));
				st.execute(sql);
				log.debug("取数据: end");
				try (ResultSet rs = st.getResultSet()) {
					// 取出所有字段定义
					rs.last();
					if (this.maximum > -1)
						BigdataException.check(this, rs.getRow());
					String fn;
					List<String> fs = new ArrayList<String>();
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						fn = rs.getMetaData().getColumnLabel(i);
						fs.add(fn);
					}
					this.getFieldDefs().add(fs.toArray(new String[0]));
					// 取出所有数据
					log.debug("取出所有数据: begin");
					copyResultSet(rs);
					log.debug("取出所有数据: end");
					this.first();
					this.active = true;
					return this;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void copyResultSet(ResultSet rs) throws SQLException {
		if (rs.first()) {
			int total = 0;
			do {
				total++;
				if (this.maximum > -1 && total > this.maximum) {
					this.fetchFinish = false;
					break;
				}
				Record record = append().getCurrent();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					String fn = rs.getMetaData().getColumnLabel(i);
					record.setField(fn, rs.getObject(fn));
				}
				record.setState(DataSetState.dsNone);

			} while (rs.next());
		}
	}

	public DataQuery setActive(boolean value) {
		if (value) {
			if (!this.active)
				this.open();
		} else {
			this.close();
		}
		this.active = value;
		return this;
	}

	public boolean getActive() {
		return active;
	}

	public void setConnection(SqlConnection connection) {
		this.connection = connection;
	}

	public SqlConnection getConnection() {
		return connection;
	}

	public DataQuery add(String sql) {
		if (commandText == null)
			commandText = sql;
		else
			commandText = commandText + " " + sql;
		return this;
	}

	public DataQuery add(String format, Object... args) {
		return this.add(String.format(format, args));
	}

	public DataQuery setCommandText(String sql) {
		this.commandText = sql;
		return this;
	}

	// 根据 sql 获取数据库表名
	private String getTableName() {
		String result = null;
		String[] items = commandText.split("[ \r\n]");
		for (int i = 0; i < items.length; i++) {
			if (items[i].toLowerCase().contains("from")) {
				// 如果取到form后 下一个记录为数据库表名
				while (isBlank(items[i + 1])) {
					// 防止取到空值
					i++;
				}
				result = items[++i]; // 获取数据库表名
				break;
			}
		}

		if (result == null)
			throw new RuntimeException("SQL语句异常");

		return result;
	}

	public String getCommandText() {
		return this.commandText;
	}

	public void post() {
		String tableName = getTableName();
		Record record = this.getCurrent();
		if (record.getState() == DataSetState.dsInsert) {
			insertCommand(connection.getConnection(), tableName, record);
		} else if (record.getState() == DataSetState.dsEdit) {
			updateCommand(connection.getConnection(), tableName, record);
		} else {
			throw new RuntimeException("post方法调用错误");
		}
		record.setState(DataSetState.dsNone);
	}

	@Override
	public boolean delete() {
		String tableName = getTableName();
		if (this.getCurrent().getState() != DataSetState.dsNone) {
			throw new RuntimeException("在插入和修改的时候 记录不允许删除");
		}
		deleteCommand(connection.getConnection(), tableName, this.getCurrent());
		return super.delete();
	}

	public String toString() {
		StringBuffer sl = new StringBuffer();
		sl.append(String.format("[%s]%n", this.getClass().getName()));
		sl.append(String.format("CommandText:%s%n", this.getCommandText()));
		sl.append(String.format("RecordCount:%d%n", this.size()));
		sl.append(String.format("RecNo:%d%n", this.getRecNo()));
		return sl.toString();
	}

	public int getOffset() {
		return offset;
	}

	public DataQuery setOffset(int offset) {
		this.offset = offset;
		return this;
	}

	public int getMaximum() {
		return maximum;
	}

	public DataQuery setMaximum(int maximum) {
		if (maximum > BigdataException.MAX_RECORDS) {
			String str = String.format("本次请求的记录数超出了系统最大笔数为  %d 的限制！", BigdataException.MAX_RECORDS);
			throw new RuntimeException(str);
		}
		this.maximum = maximum;
		return this;
	}

	protected String getSelectCommand() {

		String sql = this.getCommandText();
		if (sql == null || sql.equals(""))
			throw new RuntimeException("[TAppQuery]CommandText is null ！");

		if (sql.indexOf("call ") > -1)
			return sql;

		if (this.offset > 0) {
			if (this.maximum < 0)
				sql = sql + String.format(" limit %d,%d", this.offset, BigdataException.MAX_RECORDS + 1);
			else
				sql = sql + String.format(" limit %d,%d", this.offset, this.maximum + 1);
		} else if (this.maximum == BigdataException.MAX_RECORDS) {
			sql = sql + String.format(" limit %d", this.maximum + 2);
		} else if (this.maximum > -1) {
			sql = sql + String.format(" limit %d", this.maximum + 1);
		} else if (this.maximum == 0) {
			sql = sql + String.format(" limit %d", 0);
		}
		return sql;
	}

	public boolean getFetchFinish() {
		return fetchFinish;
	}

	@Deprecated
	/*
	 * 此函数不再需要，请改为直接使用调用 setMaximum(-1)
	 */
	public void setCloseMax(boolean closeMax) {
		this.maximum = -1;
	}

	public void clear() {
		this.commandText = null;
	}

	private boolean insertCommand(Connection conn, String tableName, Record tRecord) {

		StringBuffer sql = new StringBuffer();
		sql.append("insert into ").append(tableName);

		Set<String> fs = tRecord.getItems().keySet();
		List<String> fileds = new ArrayList<String>(fs);
		// Logger.debug(getClass(),"fielsd == " + fileds);
		if (fileds == null || fileds.size() == 0) {
			throw new RuntimeException("字段为空");
		}

		// 1、fileds中存在UID_字段 但赋值为空 2、fileds不存在UID_ 但表KEY中存在 UID_
		// 标识是否需要从数据库中获取 自增字段 默认为false
		boolean flag = false;
		for (int i = 1; i <= fileds.size(); i++) {
			String key = fileds.get(i - 1);
			if (uid.equalsIgnoreCase(key)) {
				if (tRecord.getField(key) != null) {
					if (isBlank(tRecord.getField(key).toString())) {
						flag = true;
						break;
					}
				}
			}
		}

		if (!flag) {
			String pkkey = getKeyByDict(conn, tableName, tRecord);
			String[] pks = pkkey.split(";");

			for (String pk : pks) {
				if (uid.equalsIgnoreCase(pk)) {
					flag = true;
					break;
				}
			}
		}

		StringBuffer filedssql = new StringBuffer();
		StringBuffer values = new StringBuffer();

		for (String filed : fileds) {
			if (flag && uid.equalsIgnoreCase(filed)) { // uid值为空 使用mysql自增 此处不处理
				continue;
			}
			filedssql.append(",").append(filed);
			values.append(",?");
		}

		if (filedssql.length() > 0) {
			filedssql.deleteCharAt(0);
			values.deleteCharAt(0);
		}

		sql.append("(").append(filedssql).append(") values(").append(values).append(")");

		PreparedStatement ps = null;

		try {
			// Logger.debug(getClass(),"待保存的记录值: " + tRecord);
			ps = conn.prepareStatement(sql.toString());
			for (int i = 1; i <= fileds.size(); i++) {
				String key = fileds.get(i - 1);
				Object value = tRecord.getField(key);
				if (flag && uid.equalsIgnoreCase(key)) { // uid值为空 使用mysql自增
															// 此处不处理
					continue;
				}
				if (value instanceof TDateTime)
					ps.setObject(i, ((TDateTime) value).getData());
				else
					ps.setObject(i, value);
			}
			log.debug("insert：" + ps.toString());
			int result = ps.executeUpdate();

			if (flag) {
				int uidvalue = findAutoUid(conn);
				log.debug("自增列uid value：" + uidvalue);
				tRecord.setField(uid, uidvalue);
			}

			return result > 0;
		} catch (Exception e) {
			DelphiException err = new DelphiException(e.getMessage());
			log.debug("insert sql error: " + sql.toString());
			err.addSuppressed(e);
			throw err;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	private int findAutoUid(Connection conn) {
		Integer result = null;
		String sql = "SELECT LAST_INSERT_ID() ";
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		if (result == null) {
			throw new RuntimeException("未获取UID");
		}
		return result.intValue();
	}

	private boolean updateCommand(Connection conn, String tableName, Record record) {

		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(tableName).append(" set ");

		// List<String> fileds = tRecord.getFieldDefs().getFields();
		List<String> fileds = new ArrayList<String>(record.getDelta().keySet());

		if (fileds == null || fileds.size() == 0) {
			return true;
		}

		StringBuffer filedssql = new StringBuffer();
		StringBuffer wheresqls = new StringBuffer();

		for (String filed : fileds) {
			filedssql.append(",").append(filed).append(" = ? ");

			// 旧指
			Object oldVlaue = record.getOldField(filed);
			// 如果旧值为空
			if (oldVlaue == null) {
				wheresqls.append(" and ").append(filed).append(" is null ");
			} else {
				wheresqls.append(" and ").append(filed).append(" = ? ");
			}
		}

		if (filedssql.length() > 0) {
			filedssql.deleteCharAt(0);
		}

		sql.append(filedssql);

		sql.append(" where 1 = 1 ");

		List<String> pklist = new ArrayList<String>();
		String pkkey = getKeyByDict(conn, tableName, record);
		String[] pks = pkkey.split(";");

		for (String pk : pks) {
			pklist.add(pk);
			if (!fileds.contains(pk)) { // 如果更新字段里面不包含主键 需自动将主键加入where条件中
				wheresqls.append(" and ").append(pk).append(" = ? ");
			}
		}

		sql.append(wheresqls);

		int whereindex = fileds.size() + 1; // where条件后index
		try (PreparedStatement ps = conn.prepareStatement(sql.toString());) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 1; i <= fileds.size(); i++) {
				String field = fileds.get(i - 1);
				Object value = record.getField(field);
				if (value instanceof TDateTime) {
					ps.setObject(i, record.getField(field).toString());
				} else if (value instanceof Double) {
					ps.setObject(i, roundTo((Double) record.getField(field), -6));
				} else {
					Object val = record.getField(field);
					if (val instanceof Date)
						ps.setObject(i, sdf.format(val));
					else
						ps.setObject(i, val); // set 新值
				}
				// 旧值
				Object oldVlaue = record.getOldField(field);

				if (pklist.contains(field)) { // 如果为主键 如果delta里值为空 ，则取datas里值
												// 此时datas里值不允许为空
					if (oldVlaue == null) {
						if (value == null) {
							log.debug("tableName: " + tableName);
							log.debug("records: " + record.toString());
							throw new RuntimeException("修改主键不允许为空");
						} else {
							oldVlaue = value;
						}
					}

				}
				if (oldVlaue != null) {
					if (oldVlaue instanceof Double) {
						ps.setObject(whereindex, roundTo((Double) oldVlaue, -6));
					} else {
						// set 值
						if (oldVlaue instanceof Date)
							ps.setObject(whereindex, sdf.format(oldVlaue));
						else
							ps.setObject(whereindex, oldVlaue); // set 值
					}
					whereindex++;
				}
				pklist.remove(field); // 为后续主键获取拼接条件
			}

			for (String pk : pklist) { // 如果这个集合中 还存在主键 则说明更新字段缺少主键 需自动设置值
										// 值从datas里面获取 不允许为空
				Object value = record.getField(pk);
				if (value == null) {
					log.debug("tableName: " + tableName);
					log.debug("records: " + record.toString());
					throw new RuntimeException("修改主键不允许为空");
				}
				if (value instanceof TDateTime) {
					ps.setObject(whereindex, value.toString());
				} else if (value instanceof Double) {
					double tmp = roundTo((Double) value, -6);
					ps.setObject(whereindex, tmp);
				} else {
					ps.setObject(whereindex, value);
				}
				whereindex++;
			}
			log.debug("update：" + ps.toString());
			int result = ps.executeUpdate();
			if (result != 1) {
				throw new RuntimeException("当前记录已被其它用户修改或不存在，更新失败");
			} else {
				record.getDelta().clear();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private String getKeyByDict(Connection conn, String tableName, Record tRecord) {
		// 默认情况
		if (tRecord.getFieldDefs().getFields().contains(uid)) {
			return uid;
		}

		// 配置文件
		String key = DatabaseDict.get(tableName);
		if (!isBlank(key)) {
			return key;
		}

		// 数据库表主键KEY
		key = getKeyByDB(conn, tableName);
		if (!isBlank(key)) {
			return key;
		}

		throw new RuntimeException("获取不到主键PK");

	}

	private String getKeyByDB(Connection conn, String tableName) {
		String sql = "select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS where table_name= ? AND COLUMN_KEY= 'PRI' ";

		StringBuffer result = new StringBuffer();

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			ps.setObject(1, tableName);

			rs = ps.executeQuery();
			while (rs.next()) {
				result.append(";").append(rs.getString("COLUMN_NAME"));
			}
			if (result.length() > 0) {
				result.deleteCharAt(0);
			}
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean deleteCommand(Connection conn, String tableName, Record tRecord) {

		StringBuffer sql = new StringBuffer();

		sql.append("delete from ").append(tableName).append(" where 1 = 1 ");

		String key = getKeyByDict(conn, tableName, tRecord);
		String[] pks = key.split(";");

		for (String pk : pks) {
			sql.append(" and ").append(pk).append(" = ? ");
		}

		try (PreparedStatement ps = conn.prepareStatement(sql.toString());) {
			Object value = null;
			int index = 1;
			for (String pk : pks) {
				value = tRecord.getField(pk);
				if (value == null) {
					throw new RuntimeException("主键值为空");
				}
				ps.setObject(index, value);
				index++;
			}
			log.debug("delete：" + ps.toString());
			return ps.execute();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().equals("");
	}
}
