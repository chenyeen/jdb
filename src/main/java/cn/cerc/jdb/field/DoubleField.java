package cn.cerc.jdb.field;

import java.math.BigDecimal;

public class DoubleField extends CustomField {
	// 数值长度，仅适用于数值型
	private int precision = 18;
	// 小数位数，仅适用于数值型
	private int scale = 6;

	public DoubleField() {
		this.setWidth(4);
	}

	public DoubleField(int precision, int scale) {
		this.setWidth(4);
		this.precision = precision;
		this.scale = scale;
	}

	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	@Override
	public boolean validate(Object object) {
		return object == null || object.getClass().equals(int.class) || object.getClass().equals(Integer.class)
				|| object.getClass().equals(double.class) || object.getClass().equals(Double.class)
				|| object.getClass().equals(float.class) || object.getClass().equals(Float.class)
				|| object.getClass().equals(long.class) || object.getClass().equals(Long.class)
				|| object.getClass().equals(BigDecimal.class);
	}
}
