package cn.cerc.jdb.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

public interface IRecord {
	static final Logger log = Logger.getLogger(IRecord.class);

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
			if (method.getAnnotation(Transient.class) != null)
				continue;
			Column column = method.getAnnotation(Column.class);
			String dbField = method.getName();
			String field = method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
			if (column != null && !"".equals(column.name()))
				dbField = column.name();
			if (this.exists(dbField)) {
				try {
					if (method.getType().equals(Integer.class)) {
						Integer value = this.getInt(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					} else if (method.getType().equals(int.class)) {
						int value = this.getInt(dbField);
						Method set = clazz.getMethod("set" + field, int.class);
						set.invoke(obj, value);

					} else if ((method.getType().equals(Double.class))) {
						Double value = this.getDouble(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					} else if ((method.getType().equals(double.class))) {
						double value = this.getDouble(dbField);
						Method set = clazz.getMethod("set" + field, double.class);
						set.invoke(obj, value);

					} else if ((method.getType().equals(Long.class))) {
						Double value = this.getDouble(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					} else if ((method.getType().equals(long.class))) {
						long value = (long) this.getDouble(dbField);
						Method set = clazz.getMethod("set" + field, long.class);
						set.invoke(obj, value);

					} else if (method.getType().equals(Boolean.class)) {
						Boolean value = this.getBoolean(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					} else if (method.getType().equals(boolean.class)) {
						boolean value = this.getBoolean(dbField);
						Method set = clazz.getMethod("set" + field, boolean.class);
						set.invoke(obj, value);

					} else if (method.getType().equals(TDateTime.class)) {
						TDateTime value = this.getDateTime(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					} else if (method.getType().equals(TDate.class)) {
						TDate value = this.getDate(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					} else if (method.getType().equals(String.class)) {
						String value = this.getString(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					} else {
						log.info(String.format("field:%s, other type:%s", field, method.getType().getName()));
						String value = this.getString(dbField);
						Method set = clazz.getMethod("set" + field, value.getClass());
						set.invoke(obj, value);
					}
				} catch (NoSuchMethodException | SecurityException | IllegalArgumentException
						| InvocationTargetException | IllegalAccessException e) {
					log.info(e.getMessage());
				}
			}
		}
		return obj;
	}

	default public <T> void setObject(T object) {
		Class<?> clazz = object.getClass();
		for (Field method : clazz.getDeclaredFields()) {
			if (method.getAnnotation(Transient.class) != null)
				continue;
			GeneratedValue generatedValue = method.getAnnotation(GeneratedValue.class);
			if (generatedValue != null && generatedValue.strategy().equals(GenerationType.IDENTITY))
				continue;

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
