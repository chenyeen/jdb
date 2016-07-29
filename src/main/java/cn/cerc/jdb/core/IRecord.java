package cn.cerc.jdb.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Column;

public interface IRecord {
	public boolean exists(String field);

	public boolean getBoolean(String field);

	public int getInt(String field);

	public double getDouble(String field);

	public String getString(String field);

	public TDate getDate(String field);

	public TDateTime getDateTime(String field);

	public IRecord setField(String field, Object value);

	// 转成指定类型的对象
	default public <T> T getObject(Class<T> clazz) {
		T obj;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e1) {
			throw new RuntimeException(e1.getMessage());
		}
		for (Field method : clazz.getDeclaredFields()) {
			String field = method.getName();
			Column column = method.getAnnotation(Column.class);
			String dbField = field;
			if (column != null && !"".equals(column.name()))
				dbField = column.name();
			if (this.exists(dbField)) {
				Object value = null;
				if (method.getType().getName().endsWith("Integer"))
					value = this.getInt(dbField);
				else if (method.getType().getName().endsWith("TDateTime"))
					value = this.getDateTime(dbField);
				else if (method.getType().getName().endsWith("TDate"))
					value = this.getDate(dbField);
				else if (method.getType().getName().endsWith("Double"))
					value = this.getDouble(dbField);
				else if (method.getType().getName().endsWith("Boolean"))
					value = this.getBoolean(dbField);
				else
					value = this.getString(dbField);

				try {
					field = field.substring(0, 1).toUpperCase() + field.substring(1);
					Method set = clazz.getMethod("set" + field, value.getClass());
					set.invoke(obj, value);
				} catch (NoSuchMethodException | SecurityException | IllegalArgumentException
						| InvocationTargetException | IllegalAccessException e) {
					// e.printStackTrace();
				}
			}
		}
		return obj;
	}

	default public <T> void setObject(T object) {
		Class<?> clazz = object.getClass();
		for (Field method : clazz.getDeclaredFields()) {
			String field = method.getName();
			Column column = method.getAnnotation(Column.class);
			String dbField = field;
			if (column != null && !"".equals(column.name()))
				dbField = column.name();
			Method get;
			try {
				field = field.substring(0, 1).toUpperCase() + field.substring(1);
				get = clazz.getMethod("get" + field);
				Object value = get.invoke(object);
				this.setField(dbField, value);
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// e.printStackTrace();
			}
		}
	}
}
