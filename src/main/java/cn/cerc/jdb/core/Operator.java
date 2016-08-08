package cn.cerc.jdb.core;

public interface Operator {

	public void setTableName(String tableName);

	public boolean insert(Record record);

	public boolean update(Record record);

	public boolean delete(Record record);

	// 根据 sql 获取数据库表名
	default public String findTableName(String sql) {
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
