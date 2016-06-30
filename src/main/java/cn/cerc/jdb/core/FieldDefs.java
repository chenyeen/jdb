package cn.cerc.jdb.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FieldDefs implements Serializable {
	private static final long serialVersionUID = 7478897050846245325L;
	private List<String> fields = new ArrayList<String>();

	public int indexOf(String field) {
		return fields.indexOf(field);
	}

	public boolean exists(String field) {
		return fields.contains(field);
	}

	public int count() {
		return fields.size();
	}

	@Override
	public String toString() {
		return "TFieldDefs [fields=" + fields + "]";
	}

	public List<String> getFields() {
		return fields;
	}

	public FieldDefs add(String field) {
		if (!fields.contains(field)) {
			fields.add(field);
		}
		return this;
	}

	public void add(String... strs) {
		for (String field : strs) {
			this.add(field);
		}
	}

	public void add(DataQuery query) {
		FieldDefs fds = query.getFieldDefs();
		List<String> fields = fds.getFields();
		if (fds != null) {
			for (int i = 0; i < fds.count(); i++) {
				this.add(fields.get(i));
			}
		}
	}

	public void clear() {
		fields.clear();
	}

	public int size() {
		return fields.size();
	}

}
