package cn.cerc.jdb.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.cerc.jdb.other.DelphiException;

public class SearchDataSet {
	private CustomDataSet dataSet;
	private Map<String, Record> items;
	private List<String> fields = new ArrayList<>();
	private String keyFields;

	public SearchDataSet() {

	}

	public SearchDataSet(CustomDataSet dataSet) {
		this.dataSet = dataSet;
	}

	public SearchDataSet add(CustomDataSet dataSet) {
		System.out.println("add DataSet");
		dataSet.first();
		while (dataSet.fetch()) {
			add(dataSet.getCurrent());
		}
		return this;
	}

	private void add(Record record) {
		String key = null;
		for (String field : fields) {
			Object val = record.getField(field);
			if (val == null)
				val = "null";
			key = key == null ? val.toString() : key + ";" + val.toString();
		}
		items.put(key, record);
	}

	public Record get(Object key) {
		if (items == null) {
			items = new HashMap<>();
			add(dataSet);
		}
		return items.get(key.toString());
	}

	public Record get(Object[] keys) {
		if (keys == null || keys.length == 0)
			throw new DelphiException("值列表不能为空或者长度不能为0");
		if (fields.size() != keys.length)
			throw new DelphiException("参数名称 与 值列表长度不匹配");

		String key = null;
		for (Object obj : keys)
			key = key == null ? obj.toString() : key + obj.toString();

		return get(key);
	}

	public void clear() {
		items = null;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public void setFields(String keyFields) {
		if (keyFields == null || "".equals(keyFields))
			throw new DelphiException("参数名称不能为空");
		if (!keyFields.equals(this.keyFields)) {
			fields.clear();
			for (String key : keyFields.split(";"))
				fields.add(key);
			this.keyFields = keyFields;
			clear();
		}
	}

	public boolean existsKey(String field) {
		return fields.contains(field);
	}
}
