package cn.cerc.jdb.core;

import static cn.cerc.jdb.other.utils.roundTo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuildStatement implements AutoCloseable {
	private Connection conn;
	private StringBuffer sb = new StringBuffer();
	private PreparedStatement ps = null;
	private List<Object> items = new ArrayList<>();
	private SimpleDateFormat sdf;

	BuildStatement(Connection conn) {
		this.conn = conn;
	}

	public BuildStatement append(String sql) {
		sb.append(sql);
		return this;
	}

	public void append(String sql, Object data) {
		sb.append(sql);
		Object result = data;
		// 转换
		if (data instanceof TDateTime) {
			result = data.toString();
		} else if (data instanceof Double) {
			result = roundTo((Double) data, -6);
		} else {
			if (data instanceof Date) {
				if (sdf == null)
					sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				result = sdf.format(data);
			}
		}
		items.add(result);
	}

	public PreparedStatement build() throws SQLException {
		if (ps != null)
			throw new RuntimeException("ps not is null");
		ps = conn.prepareStatement(sb.toString());
		int i = 0;
		for (Object value : items) {
			i++;
			ps.setObject(i, value);
		}
		return ps;
	}

	@Override
	public void close() {
		try {
			if (ps != null) {
				ps.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
