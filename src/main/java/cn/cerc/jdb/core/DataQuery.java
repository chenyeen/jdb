package cn.cerc.jdb.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.cerc.jdb.field.BooleanField;
import cn.cerc.jdb.field.DoubleField;
import cn.cerc.jdb.field.FieldDefine;
import cn.cerc.jdb.field.IntegerField;
import cn.cerc.jdb.field.StringField;
import cn.cerc.jdb.field.TDateTimeField;

public class DataQuery extends DataSet {
	private static final Logger log = Logger.getLogger(DataQuery.class);

	private static final long serialVersionUID = 7316772894058168187L;
	private SqlConnection connection;
	private String commandText;
	private boolean active = false;
	// private boolean closeMax = false;
	private int offset = 0;
	private int maximum = BigdataException.MAX_RECORDS;
	// 若数据有取完，则为true，否则为false
	private boolean fetchFinish;
	// 数据库保存操作执行对象
	private Operator operator;
	// 批次保存模式，默认为post与delete立即保存
	private boolean batchSave = false;
	// 仅当batchSave为true时，delList才有记录存在
	private List<Record> delList = new ArrayList<>();

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
		String sql = getSelectCommand();
		try {
			this.fetchFinish = true;
			try (Statement st = conn.createStatement()) {
				log.debug(sql.replaceAll("\r\n", " "));
				st.execute(sql.replace("\\", "\\\\"));
				try (ResultSet rs = st.getResultSet()) {
					// 取出所有数据
					append(rs);
					this.first();
					this.active = true;
					return this;
				}
			}
		} catch (SQLException e) {
			log.error(sql);
			throw new RuntimeException(e.getMessage());
		}
	}

	// 追加相同数据表的其它记录，与已有记录合并
	public int attach(String sql) {
		if (!this.active) {
			this.clear();
			this.add(sql);
			this.open();
			return this.size();
		}
		if (connection == null)
			throw new RuntimeException("SqlConnection is null");
		Connection conn = connection.getConnection();
		if (conn == null)
			throw new RuntimeException("Connection is null");
		try {
			try (Statement st = conn.createStatement()) {
				log.debug(sql.replaceAll("\r\n", " "));
				st.execute(sql.replace("\\", "\\\\"));
				try (ResultSet rs = st.getResultSet()) {
					int oldSize = this.size();
					append(rs);
					return this.size() - oldSize;
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private void append(ResultSet rs) throws SQLException {
		DataSetEvent onAfterAppend = this.getOnAfterAppend();
		try {
			this.setOnAfterAppend(null);
			rs.last();
			if (this.maximum > -1)
				BigdataException.check(this, this.size() + rs.getRow());
			// 取得字段清单
			ResultSetMetaData meta = rs.getMetaData();
			FieldDefs defs = this.getFieldDefs();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				String field = meta.getColumnLabel(i);
				if (!defs.exists(field)) {
					if (defs.isStrict()) {
						FieldDefine define = null;
						String type = meta.getColumnTypeName(i);
						if ("VARCHAR".equals(type))
							define = new StringField(meta.getColumnDisplaySize(i));
						else if ("DECIMAL".equals(type) || "BIGINT".equals(type) || "INT UNSIGNED".equals(type))
							define = new DoubleField(meta.getPrecision(i), meta.getScale(i));
						else if ("INT".equals(type))
							define = new IntegerField();
						else if ("BIT".equals(type))
							define = new BooleanField();
						else if ("DATETIME".equals(type))
							define = new TDateTimeField();
						else
							throw new RuntimeException("not support type: " + type);
						defs.add(field, define);
					} else
						defs.add(field);
				}
			}
			// 取得所有数据
			if (rs.first()) {
				int total = this.size();
				do {
					total++;
					if (this.maximum > -1 && this.maximum < total) {
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
		} finally {
			this.setOnAfterAppend(onAfterAppend);
		}
	}

	public DataQuery setActive(boolean value) {
		if (value) {
			if (!this.active)
				this.open();
			this.active = true;
		} else {
			this.close();
		}
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

	public String getCommandText() {
		return this.commandText;
	}

	public void post() {
		if (batchSave)
			return;
		Record record = this.getCurrent();
		if (record.getState() == DataSetState.dsInsert) {
			beforePost();
			getDefaultOperator().insert(record);
			super.post();
		} else if (record.getState() == DataSetState.dsEdit) {
			beforePost();
			getDefaultOperator().update(record);
			super.post();
		}
	}

	@Override
	public void delete() {
		Record record = this.getCurrent();
		super.delete();
		if (record.getState() == DataSetState.dsInsert)
			return;
		if (batchSave)
			delList.add(record);
		else {
			getDefaultOperator().delete(record);
		}
	}

	public void save() {
		if (!batchSave)
			throw new RuntimeException("batchSave is false");
		Operator operator = getDefaultOperator();
		// 先执行删除
		for (Record record : delList)
			operator.delete(record);
		delList.clear();
		// 再执行增加、修改
		this.first();
		while (this.fetch()) {
			if (this.getState().equals(DataSetState.dsInsert)) {
				beforePost();
				operator.insert(this.getCurrent());
				super.post();
			} else if (this.getState().equals(DataSetState.dsEdit)) {
				beforePost();
				operator.update(this.getCurrent());
				super.post();
			}
		}
	}

	protected Operator getDefaultOperator() {
		if (operator == null) {
			DefaultOperator def = new DefaultOperator(connection.getConnection());
			String tableName = def.findTableName(this.commandText);
			def.setTableName(tableName);
			operator = def;
		}
		return operator;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
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

	public void clear() {
		this.commandText = null;
	}

	public boolean isBatchSave() {
		return batchSave;
	}

	public void setBatchSave(boolean batchSave) {
		this.batchSave = batchSave;
	}

	public boolean isStrict() {
		return this.getFieldDefs().isStrict();
	}

	public void setStrict(boolean strict) {
		this.getFieldDefs().setStrict(strict);
	}

}
