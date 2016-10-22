package cn.cerc.jdb.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.FieldDefs;
import cn.cerc.jdb.core.IDataOperator;
import cn.cerc.jdb.core.IHandle;
import cn.cerc.jdb.core.Record;
import cn.cerc.jdb.field.IField;

public class SqlOperator implements IDataOperator {
	private static final Logger log = Logger.getLogger(SqlOperator.class);
	private final String CONST_UID = "UID_";
	private Connection conn;
	private String tableName;
	private String lastCommand;
	private boolean preview = false;
	private List<String> primaryKeys = new ArrayList<>();

	public SqlOperator(IHandle handle) {
		SqlConnection cn = (SqlConnection) handle.getProperty(SqlQuery.sessionId);
		this.conn = cn.getConnection();
	}

	@Override
	public boolean insert(Record record) {
		if (record.getFieldDefs().size() == 0)
			throw new RuntimeException("字段为空");

		try (BuildStatement bs = new BuildStatement(conn);) {
			if (primaryKeys.size() == 0)
				initPrimaryKeys(record);

			FieldDefs defs = record.getFieldDefs();
			bs.append("insert into ").append(tableName).append(" (");
			int i = 0;
			for (String field : record.getItems().keySet()) {
				if (!CONST_UID.equals(field)) {
					IField define = defs.getDefine(field);
					if (define == null || !define.isCalculated()) {
						i++;
						if (i > 1)
							bs.append(",");
						bs.append(field);
					}
				}
			}
			bs.append(") values (");
			i = 0;
			for (String field : record.getItems().keySet()) {
				if (!CONST_UID.equals(field)) {
					IField define = defs.getDefine(field);
					if (define == null || !define.isCalculated()) {
						i++;
						if (i == 1)
							bs.append("?", record.getField(field));
						else
							bs.append(",?", record.getField(field));
					}
				}
			}
			bs.append(")");

			PreparedStatement ps = bs.build();
			lastCommand = bs.getCommand();
			if (preview) {
				log.info(lastCommand);
				return false;
			} else
				log.debug(lastCommand);

			int result = ps.executeUpdate();

			if (primaryKeys.contains(CONST_UID)) {
				int uidvalue = findAutoUid(conn);
				log.debug("自增列uid value：" + uidvalue);
				record.setField(CONST_UID, uidvalue);
			}

			return result > 0;
		} catch (SQLException e) {
			log.error(lastCommand);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public boolean update(Record record) {
		if (!record.isModify())
			return false;
		Map<String, Object> delta = record.getDelta();
		if (delta.size() == 0)
			return false;

		try (BuildStatement bs = new BuildStatement(this.conn);) {
			if (this.primaryKeys.size() == 0)
				initPrimaryKeys(record);
			if (primaryKeys.size() == 0)
				throw new RuntimeException("primary keys not exists");
			if (!primaryKeys.contains(CONST_UID))
				log.warn(String.format("not find primary key %s in %s", CONST_UID, this.tableName));
			bs.append("update ").append(tableName);
			FieldDefs defs = record.getFieldDefs();
			// 加入set条件
			int i = 0;
			for (String field : delta.keySet()) {
				IField define = defs.getDefine(field);
				if (define == null || !define.isCalculated()) {
					if (!CONST_UID.equals(field)) {
						i++;
						bs.append(i == 1 ? " set " : ",");
						bs.append(field);
						bs.append("=?", record.getField(field));
					}
				}
			}
			if (i == 0)
				return false;
			// 加入where条件
			i = 0;
			int pkCount = 0;
			for (String field : primaryKeys) {
				i++;
				bs.append(i == 1 ? " where " : " and ").append(field);
				Object value = delta.containsKey(field) ? delta.get(field) : record.getField(field);
				if (value != null) {
					bs.append("=?", value);
					pkCount++;
				} else
					throw new RuntimeException("primaryKey not is null: " + field);
			}
			if (pkCount == 0)
				throw new RuntimeException("primary keys value not exists");
			for (String field : delta.keySet()) {
				if (!primaryKeys.contains(field)) {
					IField define = defs.getDefine(field);
					if (define == null || !define.isCalculated()) {
						i++;
						bs.append(i == 1 ? " where " : " and ").append(field);
						Object value = delta.get(field);
						if (value != null) {
							bs.append("=?", value);
						} else
							bs.append(" is null ");
					}
				}
			}

			PreparedStatement ps = bs.build();
			lastCommand = bs.getCommand();
			if (preview) {
				log.info(lastCommand);
				return false;
			}

			if (ps.executeUpdate() != 1) {
				log.error(lastCommand);
				throw new RuntimeException("当前记录已被其它用户修改或不存在，更新失败");
			} else {
				log.debug(lastCommand);
				return true;
			}
		} catch (SQLException e) {
			log.error(lastCommand);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public boolean delete(Record record) {
		try (BuildStatement bs = new BuildStatement(conn);) {
			if (this.primaryKeys.size() == 0)
				initPrimaryKeys(record);
			if (primaryKeys.size() == 0)
				throw new RuntimeException("primary keys  not exists");
			if (!primaryKeys.contains(CONST_UID))
				log.warn(String.format("not find primary key %s in %s", CONST_UID, this.tableName));

			bs.append("delete from ").append(tableName);
			int i = 0;
			Map<String, Object> delta = record.getDelta();
			for (String pk : primaryKeys) {
				Object value = delta.containsKey(pk) ? delta.get(pk) : record.getField(pk);
				if (value == null)
					throw new RuntimeException("主键值为空");
				i++;
				bs.append(i == 1 ? " where " : " and ");
				bs.append(pk).append("=? ", value);
			}
			PreparedStatement ps = bs.build();
			lastCommand = bs.getCommand();
			if (preview) {
				log.info(lastCommand);
				return false;
			} else
				log.debug(lastCommand);

			return ps.execute();
		} catch (SQLException e) {
			log.error(lastCommand);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	private void initPrimaryKeys(Record record) throws SQLException {
		for (String key : record.getFieldDefs().getFields()) {
			if (CONST_UID.equalsIgnoreCase(key)) {
				if (!CONST_UID.equals(key))
					throw new RuntimeException(String.format("%s <> %s", CONST_UID, key));
				primaryKeys.add(CONST_UID);
				break;
			}
		}
		if (primaryKeys.size() == 0) {
			String[] pks = getKeyByDB(conn, tableName).split(";");
			if (pks.length == 0)
				throw new RuntimeException("获取不到主键PK");
			for (String pk : pks) {
				if (CONST_UID.equalsIgnoreCase(pk)) {
					if (!CONST_UID.equals(pk))
						throw new RuntimeException(String.format("%s <> %s", CONST_UID, pk));
					break;
				}
				primaryKeys.add(pk);
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

	private String getKeyByDB(Connection conn, String tableName) throws SQLException {
		StringBuffer result = new StringBuffer();
		try (BuildStatement bs = new BuildStatement(conn);) {
			bs.append("select COLUMN_NAME from INFORMATION_SCHEMA.COLUMNS ");
			bs.append("where table_name= ? AND COLUMN_KEY= 'PRI' ", tableName);
			PreparedStatement ps = bs.build();
			log.debug(ps.toString().split(":")[1].trim());
			ResultSet rs = ps.executeQuery();
			int i = 0;
			while (rs.next()) {
				i++;
				if (i > 1)
					result.append(";");
				result.append(rs.getString("COLUMN_NAME"));
			}
			return result.toString();
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getLastCommand() {
		return lastCommand;
	}

	public boolean isPreview() {
		return preview;
	}

	public void setPreview(boolean preview) {
		this.preview = preview;
	}

	public List<String> getPrimaryKeys() {
		return primaryKeys;
	}

	// 根据 sql 获取数据库表名
	public String findTableName(String sql) {
		String result = null;
		String[] items = sql.split("[ \r\n]");
		for (int i = 0; i < items.length; i++) {
			if (items[i].toLowerCase().contains("from")) {
				// 如果取到form后 下一个记录为数据库表名
				while (items[i + 1] == null || "".equals(items[i + 1].trim())) {
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
}
