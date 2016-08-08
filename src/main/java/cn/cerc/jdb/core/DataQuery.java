package cn.cerc.jdb.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DataQuery extends DataSet {
	private static final Logger log = Logger.getLogger(DataQuery.class);

	private static final long serialVersionUID = 7316772894058168187L;
	private SqlConnection connection;
	private String commandText;
	private boolean active = false;
	// private boolean closeMax = false;
	private int offset = 0;
	private int maximum = BigdataException.MAX_RECORDS;
	private List<String> fields;
	private Operator operator;

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
					fields = new ArrayList<String>();
					ResultSetMetaData meta = rs.getMetaData();
					for (int i = 1; i <= meta.getColumnCount(); i++) {
						String field = meta.getColumnLabel(i);
						this.getFieldDefs().add(field);
						fields.add(field);
					}
					// 取出所有数据
					log.debug("load ResultSet: begin");
					copyResultSet(rs);
					log.debug("load ResultSet: end");
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

	public String getCommandText() {
		return this.commandText;
	}

	public void post() {
		Record record = this.getCurrent();
		if (fields != null && this.getFieldDefs().size() != fields.size()) {
			throw new PostFieldException(this, this.fields);
		}
		if (record.getState() == DataSetState.dsInsert) {
			getDefaultOperator().insert(record);
			record.setState(DataSetState.dsNone);
		} else if (record.getState() == DataSetState.dsEdit) {
			getDefaultOperator().update(record);
			record.setState(DataSetState.dsNone);
		} else {
			throw new RuntimeException("post方法调用错误");
		}
	}

	@Override
	public boolean delete() {
		if (this.getCurrent().getState() != DataSetState.dsNone) {
			throw new RuntimeException("在插入和修改的时候 记录不允许删除");
		}
		getDefaultOperator().delete(this.getCurrent());
		return super.delete();
	}

	private Operator getDefaultOperator() {
		if (operator == null) {
			operator = new DefaultOperator(connection.getConnection());
			String tableName = operator.findTableName(this.commandText);
			operator.setTableName(tableName);
		}
		return operator;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(DefaultOperator operator) {
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


}
