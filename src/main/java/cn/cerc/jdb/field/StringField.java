package cn.cerc.jdb.field;

public class StringField extends CustomField {
	private int length;

	public StringField() {
		this.setWidth(6);
	}

	public StringField(int length) {
		this.length = length;
	}

	@Override
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public boolean validate(Object object) {
		if (object == null)
			return true;
		else if (object.getClass().equals(String.class))
			if (this.getLength() == 0)
				return true;
			else
				return ((String) object).length() <= this.getLength();
		else
			return false;
	}
}
