package cn.cerc.jdb.field;

public interface FieldDefine {
	// 设置字段代码
	public CustomField setCode(String code);

	// 返回字段代码
	public String getCode();

	// 设置字段名称
	public CustomField setName(String name);

	// 返回字段名称
	public String getName();

	// 判断传入值是否为该字段许可的类型
	public boolean validate(Object object);

	// 默认为0，表示长度不管控
	default public int getLength() {
		return 0;
	}

	// 数值长度，仅适用于数值型
	default public int getPrecision() {
		return 0;
	}

	// 小数位数，仅适用于数值型
	default public int getScale() {
		return 0;
	}

	// 显示宽度6;
	default public int getWidth() {
		return 6;
	}
}
