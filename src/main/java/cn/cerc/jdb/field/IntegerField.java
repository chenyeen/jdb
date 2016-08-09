package cn.cerc.jdb.field;

public class IntegerField extends CustomField {

	public IntegerField() {
		this.setWidth(4);
	}

	@Override
	public boolean validate(Object object) {
		return object == null 
				|| object.getClass().equals(int.class)
				|| object.getClass().equals(Integer.class);
	}

}
