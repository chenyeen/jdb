/**
 * 
 */
package cn.cerc.jdb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.cerc.jdb.other.DelphiException;

/**
 * @author ZhangGong
 * 
 */
public class CustomDataSet extends Component implements IRecord, Iterable<Record> {
	private int recNo = 0;
	private int fetchNo = -1;
	private FieldDefs fieldDefs = new FieldDefs();
	private List<Record> records = new ArrayList<Record>();
	public DataSetNotifyEvent OnNewRecord;
	private SearchDataSet search;

	public CustomDataSet append() {
		if (search != null)
			search.clear();
		Record ar = new Record(this.fieldDefs);
		ar.setState(DataSetState.dsInsert);
		this.records.add(ar);
		recNo = records.size();
		if (OnNewRecord != null)
			OnNewRecord.execute(this);
		return this;
	}

	public void edit() {
		if (bof() || eof())
			throw new RuntimeException("当前记录为空，无法修改");
		if (search != null)
			search.clear();
		this.getCurrent().setState(DataSetState.dsEdit);
	}

	public boolean delete() {
		if (bof() || eof())
			throw new RuntimeException("当前记录为空，无法修改");
		if (search != null)
			search.clear();
		records.remove(recNo - 1);
		if (recNo > records.size()) {
			recNo = records.size();
		}
		if (this.fetchNo > -1)
			this.fetchNo--;
		return true;
	}

	public void post() {
		if (search != null)
			search.clear();
		this.getCurrent().setState(DataSetState.dsNone);
	}

	public boolean first() {
		if (records.size() > 0) {
			this.recNo = 1;
		} else {
			this.recNo = 0;
		}
		fetchNo = -1;
		return this.recNo > 0;
	}

	public boolean last() {
		this.recNo = this.records.size();
		return this.recNo > 0;
	}

	public boolean prior() {
		if (this.recNo > 0)
			this.recNo--;
		return this.recNo > 0;
	}

	public boolean next() {
		if (this.records.size() > 0 && recNo <= this.records.size()) {
			recNo++;
			return true;
		} else {
			return false;
		}
	}

	public boolean bof() {
		return this.recNo == 0;
	}

	public boolean eof() {
		return this.records.size() == 0 || this.recNo > this.records.size();
	}

	public Record getCurrent() {
		if (this.eof()) {
			throw DelphiException.createFmt("[%s]eof == true", this.getClass().getName());
		} else if (this.bof()) {
			throw DelphiException.createFmt("[%s]bof == true", this.getClass().getName());
		} else {
			return records.get(recNo - 1);
		}
	}

	protected List<Record> getRecords() {
		return records;
	}

	public void setRecNo(int recNo) {
		if (recNo > this.records.size()) {
			throw DelphiException.createFmt("[%s]RecNo %d 大于总长度 %d", this.getClass().getName(), recNo,
					this.records.size());
		} else {
			this.recNo = recNo;
		}
	}

	public int getRecNo() {
		return recNo;
	}

	public int size() {
		return this.records.size();
	}

	public FieldDefs getFieldDefs() {
		return this.fieldDefs;
	}

	public void locate(Record record){
		this.setRecNo(this.records.indexOf(record) + 1);
	}
	
	@Deprecated
	public boolean locate(String fields, Object... values) {
		if (fields == null || "".equals(fields))
			throw new DelphiException("参数名称不能为空");
		if (values == null || values.length == 0)
			throw new DelphiException("值列表不能为空或者长度不能为0");
		String[] fieldslist = fields.split(";");
		if (fieldslist.length != values.length)
			throw new DelphiException("参数名称 与 值列表长度不匹配");
		Map<String, Object> fieldValueMap = new HashMap<String, Object>();
		for (int i = 0; i < fieldslist.length; i++) {
			fieldValueMap.put(fieldslist[i], values[i]);
		}

		// 查找指定的字段值是否存在，若存在，则令RecNo等于该记录
		this.first();
		while (this.fetch()) {
			if (equalsValues(fieldValueMap))
				return true;
		}
		return false;
	}

	// 查找指定的字段值是否存在，若存在，则令RecNo等于该记录
	public Record lookup(String fields, Object value) {
		return lookup(fields, new Object[] { value }, true);
	}

	public Record lookup(String fields, Object[] values) {
		return lookup(fields, values, true);
	}

	public Record lookup(String fields, Object[] values, boolean beforeSort) {
		if (beforeSort) {
			if (search == null)
				search = new SearchDataSet(this);
			search.setFields(fields);
			return search.get(values);
		} else {
			if (fields == null || "".equals(fields))
				throw new DelphiException("参数名称不能为空");
			if (values == null || values.length == 0)
				throw new DelphiException("值列表不能为空或者长度不能为0");
			String[] fieldslist = fields.split(";");
			if (fieldslist.length != values.length)
				throw new DelphiException("参数名称 与 值列表长度不匹配");
			Map<String, Object> fieldValueMap = new HashMap<String, Object>();
			for (int i = 0; i < fieldslist.length; i++) {
				fieldValueMap.put(fieldslist[i], values[i]);
			}

			this.first();
			while (this.fetch()) {
				if (this.getCurrent().equalsValues(fieldValueMap))
					return this.getCurrent();
			}
		}
		return null;
	}

	public DataSetState getState() {
		return this.getCurrent().getState();
	}

	public Object getField(String field) {
		return this.getCurrent().getField(field);
	}

	// 排序
	public void setSort(String... fields) {
		Collections.sort(this.getRecords(), new RecordComparator(fields));
	}

	public void setSort(Comparator<Record> func) {
		Collections.sort(this.getRecords(), func);
	}

	protected void copyDataSet(CustomDataSet source) {
		if (search != null)
			search.clear();
		// 先复制字段定义
		FieldDefs tarDefs = this.getFieldDefs();
		for (String field : source.getFieldDefs().getFields()) {
			if (!tarDefs.exists(field))
				tarDefs.add(field);
		}

		// 再复制所有数据
		for (int i = 0; i < source.records.size(); i++) {
			Record src_row = source.records.get(i);
			Record tar_row = this.append().getCurrent();
			for (String field : src_row.getFieldDefs().getFields()) {
				tar_row.setField(field, src_row.getField(field));
			}
			this.post();
		}
	}

	public void close() {
		fieldDefs.clear();
		records.clear();
		recNo = 0;
		fetchNo = -1;
	}

	@Override
	public String getString(String field) {
		return this.getCurrent().getString(field);
	}

	@Override
	public double getDouble(String field) {
		return this.getCurrent().getDouble(field);
	}

	@Override
	public boolean getBoolean(String field) {
		return this.getCurrent().getBoolean(field);
	}

	@Override
	public int getInt(String field) {
		return this.getCurrent().getInt(field);
	}

	@Override
	public TDate getDate(String field) {
		return this.getCurrent().getDate(field);
	}

	@Override
	public TDateTime getDateTime(String field) {
		return this.getCurrent().getDateTime(field);
	}

	@Override
	public Record setField(String field, Object value) {
		if (field == null)
			throw new RuntimeException("field is null!");
		if (search != null && search.existsKey(field))
			search.clear();
		return this.getCurrent().setField(field, value);
	}

	public boolean fetch() {
		boolean result = false;
		if (this.fetchNo < (this.records.size() - 1)) {
			this.fetchNo++;
			this.setRecNo(this.fetchNo + 1);
			result = true;
		}
		return result;
	}

	public void copyRecord(Record source, FieldDefs defs) {
		if (search != null)
			search.clear();
		this.getCurrent().copyValues(source, defs);
	}

	public void copyRecord(Record source, String... fields) {
		if (search != null)
			search.clear();
		this.getCurrent().copyValues(source, fields);
	}

	public void copyRecord(Record sourceRecord, String[] sourceFields, String[] targetFields) {
		if (search != null)
			search.clear();
		if (targetFields.length != sourceFields.length)
			throw new RuntimeException("前后字段数目不一样，请您确认！");
		Record targetRecord = this.getCurrent();
		for (int i = 0; i < sourceFields.length; i++)
			targetRecord.setField(targetFields[i], sourceRecord.getField(sourceFields[i]));
	}

	public CustomDataSet setField(String field, TDateTime value) {
		if (search != null && search.existsKey(field))
			search.clear();
		this.getCurrent().setField(field, value);
		return this;
	}

	public CustomDataSet setField(String field, int value) {
		if (search != null && search.existsKey(field))
			search.clear();
		this.getCurrent().setField(field, value);
		return this;
	}

	public CustomDataSet setField(String field, String value) {
		if (search != null && search.existsKey(field))
			search.clear();
		this.getCurrent().setField(field, value);
		return this;
	}

	public CustomDataSet setField(String field, Boolean value) {
		if (search != null && search.existsKey(field))
			search.clear();
		this.getCurrent().setField(field, value);
		return this;
	}

	public CustomDataSet setNull(String field) {
		if (search != null && search.existsKey(field))
			search.clear();
		this.getCurrent().setField(field, null);
		return this;
	}

	public boolean isNull(String field) {
		Object obj = getCurrent().getField(field);
		return obj == null || "".equals(obj);
	}

	@Override
	public Iterator<Record> iterator() {
		return new CustomDataSetIteratior(this);
	}

	@Override
	public boolean exists(String field) {
		return this.getFieldDefs().exists(field);
	}

	// 将内容转成List
	public <T> List<T> getList(Class<T> clazz) {
		List<T> items = new ArrayList<T>();
		for (Record rs : this) {
			items.add(rs.getObject(clazz));
		}
		return items;
	}

	// 将内容转成Map
	public <T> Map<String, T> getMap(Class<T> clazz, String... keys) {
		Map<String, T> items = new HashMap<String, T>();
		for (Record rs : this) {
			String key = "";
			for (String field : keys) {
				if ("".equals(key))
					key = rs.getString(field);
				else
					key += ";" + rs.getString(field);
			}
			items.put(key, rs.getObject(clazz));
		}
		return items;
	}
}
