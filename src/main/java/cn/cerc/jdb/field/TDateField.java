package cn.cerc.jdb.field;

import cn.cerc.jdb.core.TDate;

public class TDateField extends CustomField {
	public TDateField() {
		this.setWidth(5);
	}

	@Override
	public boolean validate(Object object) {
		return object == null || object.getClass().equals(TDate.class);
	}
}
