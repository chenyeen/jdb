package cn.cerc.jdb.mysql;

import static cn.cerc.jdb.other.utils.vbCrLf;

import org.apache.log4j.Logger;

import cn.cerc.jdb.core.IHandle;

public class BatchScript {
	private static final Logger log = Logger.getLogger(BatchScript.class);
	private StringBuffer items = new StringBuffer();
	private IHandle handle;
	private MysqlSession conn;
	private boolean newLine = false;

	public BatchScript(IHandle handle) {
		this.handle = handle;
		this.conn = (MysqlSession) handle.getProperty(MysqlSession.sessionId);
	}

	public BatchScript addSemicolon() {
		items.append(";" + vbCrLf);
		return this;
	}

	public BatchScript add(String sql) {
		items.append(sql.trim() + " ");
		if (newLine)
			items.append(vbCrLf);
		return this;
	}

	public BatchScript add(String format, Object... args) {
		items.append(String.format(format.trim(), args) + " ");
		if (newLine)
			items.append(vbCrLf);
		return this;
	}

	public StringBuffer getItems() {
		return items;
	}

	@Override
	public String toString() {
		return items.toString();
	}

	public void print() {
		String tmp[] = items.toString().split(";");
		for (String item : tmp) {
			if (!item.trim().equals("")) {
				log.info(item.trim() + ";");
			}
		}
	}

	public BatchScript exec() {
		String tmp[] = items.toString().split(";");
		for (String item : tmp) {
			if (!item.trim().equals("")) {
				log.debug(item.trim() + ";");
				conn.execute(item.trim());
			}
		}
		return this;
	}

	public boolean exists() {
		String tmp[] = items.toString().split(";");
		for (String item : tmp) {
			if (!item.trim().equals("")) {
				log.debug(item.trim() + ";");
				SqlQuery ds = new SqlQuery(handle);
				ds.add(item.trim());
				ds.open();
				if (ds.eof())
					return false;
			}
		}
		return tmp.length > 0;
	}

	public boolean isNewLine() {
		return newLine;
	}

	public void setNewLine(boolean newLine) {
		this.newLine = newLine;
	}

	public int size() {
		String tmp[] = items.toString().split(";");
		int len = 0;
		for (String item : tmp) {
			if (!item.trim().equals(""))
				len++;
		}
		return len;
	}

	public String getItem(int i) {
		String tmp[] = items.toString().split(";");
		if (i < 0 && i > (tmp.length - 1))
			throw new RuntimeException("命令索引超出范围！");
		return tmp[i].trim();
	}

	public void clean() {
		items = new StringBuffer();
	}
}
