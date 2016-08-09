package cn.cerc.jdb.field;

public class BooleanField extends CustomField {
	public BooleanField() {
		this.setWidth(2);
	}

	@Override
	public boolean validate(Object object) {
		return object == null || object.getClass().equals(boolean.class) || object.getClass().equals(Boolean.class);
	}
}
