package cn.cerc.jdb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.cerc.jdb.field.FieldDefine;

public class FieldDefs implements Serializable, Iterable<String> {
	private static final long serialVersionUID = 7478897050846245325L;
	private Map<String, FieldDefine> fields = new TreeMap<>();
	// 设置字段为强类型，必须预先定义，默认为弱类型
	private boolean strict = false;
	// 设置是否不再允许添加字段，默认为可随时添加
	private boolean locked = false;

	public boolean exists(String field) {
		return fields.containsKey(field);
	}

	@Override
	public String toString() {
		return "TFieldDefs [fields=" + fields + "]";
	}

	public List<String> getFields() {
		List<String> result = new ArrayList<>();
		for (String field : fields.keySet())
			result.add(field);
		return result;
	}

	public FieldDefs add(String field) {
		if (this.locked)
			throw new RuntimeException("locked is true");
		if (this.strict)
			throw new RuntimeException("strict is true");
		if (field == null || "".equals(field))
			throw new RuntimeException("field is null!");
		if (!fields.containsKey(field))
			fields.put(field, null);
		return this;
	}

	public FieldDefs add(String field, FieldDefine fieldDefine) {
		if (this.locked)
			throw new RuntimeException("locked is true");
		if (this.strict && fieldDefine == null)
			throw new RuntimeException("fieldDefine is null");
		if (field == null || "".equals(field))
			throw new RuntimeException("field is null!");
		if (!fields.containsKey(field)) {
			if (fieldDefine != null)
				fieldDefine.setCode(field);
			fields.put(field, fieldDefine);
		}
		return this;
	}

	public FieldDefine getDefine(String field) {
		return fields.get(field);
	}

	public void add(String... strs) {
		for (String field : strs) {
			this.add(field);
		}
	}

	public void add(DataQuery query) {
		FieldDefs fds = query.getFieldDefs();
		for (String field : query.getFieldDefs())
			this.add(field, fds.getDefine(field));
	}

	public void clear() {
		fields.clear();
	}

	public int size() {
		return fields.size();
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		if (strict && fields.size() > 0)
			throw new RuntimeException("fields not is null");
		this.strict = strict;
	}

	@Override
	public Iterator<String> iterator() {
		return this.getFields().iterator();
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
}
