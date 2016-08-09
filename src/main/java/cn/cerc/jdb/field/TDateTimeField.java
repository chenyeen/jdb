package cn.cerc.jdb.field;

import cn.cerc.jdb.core.TDateTime;

public class TDateTimeField extends CustomField {
	public TDateTimeField() {
		this.setWidth(9);
	}

	@Override
	public boolean validate(Object object) {
		return object == null || object.getClass().equals(TDateTime.class);
	}

}
