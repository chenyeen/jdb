package cn.cerc.jdb.field;

public abstract class CustomField implements FieldDefine {
	private String code;
	private String name;
	private int width = 0;
	// 是否为计算字段，若为true则不予保存到数据库中
	private boolean calculated;

	@Override
	public CustomField setCode(String code) {
		this.code = code;
		return this;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getName() {
		return name != null ? name : code;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public CustomField setName(String name) {
		this.name = name;
		return this;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public String toString() {
		return String.format("code:%s, name:%s, length:%s, precision:%d, scale:%d", getCode(), getName(), getLength(),
				getPrecision(), getScale());
	}

	@Override
	public boolean isCalculated() {
		return calculated;
	}

	public CustomField setCalculated(boolean calculated) {
		this.calculated = calculated;
		return this;
	}
}
