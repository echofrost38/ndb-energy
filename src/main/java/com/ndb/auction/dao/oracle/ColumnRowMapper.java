package com.ndb.auction.dao.oracle;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

public class ColumnRowMapper<T> extends BeanPropertyRowMapper<T> {

	private ColumnRowMapper(final Class<T> mappedClass) {
		super(mappedClass);
	}

	@Override
	protected String underscoreName(final String name) {
		final Column annotation;
		final String columnName;
		Field declaredField = null;
		try {
			declaredField = getMappedClass().getDeclaredField(name);
		} catch (Exception e) {
		}
		if (declaredField == null || (annotation = declaredField.getAnnotation(Column.class)) == null
				|| StringUtils.isEmpty(columnName = annotation.name())) {
			return super.underscoreName(name);
		}
		return columnName;
	}

	/**
	 * New instance.
	 *
	 * @param <T>         the generic type
	 * @param mappedClass the mapped class
	 * @return the bean property row mapper
	 */
	public static <T> BeanPropertyRowMapper<T> newInstance(final Class<T> mappedClass) {
		return new ColumnRowMapper<>(mappedClass);
	}

}