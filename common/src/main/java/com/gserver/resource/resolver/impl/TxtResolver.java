package com.gserver.resource.resolver.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.gserver.resource.IResourceMark;
import com.gserver.resource.resolver.RowBasedFileResolver;
import com.gserver.utils.ReflectionUtils;

public class TxtResolver extends RowBasedFileResolver<String> {

	private static final Logger logger = LoggerFactory.getLogger(TxtResolver.class);

	@Override
	protected <CLASS_T extends IResourceMark> List<List<String>> readRows(Class<CLASS_T> clazz, File input) {
		List<List<String>> returnList = Lists.newArrayList();
		try {
			BufferedReader d = new BufferedReader(new FileReader(input));
			String lineStr = null;
			while ((lineStr = d.readLine()) != null) {
				String[] values = lineStr.split("\t"); // 注意，split会忽略末尾的\t
				List<String> row = Lists.newArrayList(values);
				returnList.add(row);
			}
		} catch (IOException e) {
			logger.error("TxtResourceResolver error", e);
			e.printStackTrace();
		}
		return returnList;
	}

	@Override
	protected List<String> getAttributeNames(List<String> row) {
		Assert.notNull(row);
		List<String> ts = Lists.newArrayList();
		ts.add(row.get(0).substring(1)); // 去掉title行的起始标记
		for (int i = 1; i < row.size(); ++i) {
			ts.add(row.get(i).trim());
		}
		return ts;
	}

	@Override
	protected <CLASS_T extends IResourceMark> boolean fillFields(CLASS_T obj, List<String> attributeNames, List<String> row) {
		Assert.notNull(attributeNames, "attributeNames is null.");
		Assert.notNull(row, "row is null.");
		String fieldName = null;
		for (int iterator = 0, range_begin = -1, range_end = -1; iterator < row.size(); ++iterator) {
			String attrName = null;
			if (iterator < attributeNames.size()) {
				attrName = attributeNames.get(iterator);
			}
			if (!Strings.isNullOrEmpty(attrName)) {
				fieldName = attrName;
				range_begin = iterator;
				range_end = iterator + 1;
			} else {// 数组后续空格
				range_end = iterator + 1;
			}
			if (iterator < attributeNames.size() - 1 && !Strings.isNullOrEmpty(attributeNames.get(iterator + 1)) || iterator == row.size() - 1) {
				fillOneField(obj, fieldName, row, range_begin, range_end);
			}
		}
		return true;
	}

	/**
	 * 设置一个字段的值，不成功则忽略。
	 * 
	 * @param obj
	 *            对象
	 * @param attrName
	 *            字段名
	 * @param row
	 *            数据来源行
	 * @param range_begin
	 *            起始位置（包含）
	 * @param range_end
	 *            结束位置（不包含）
	 */
	private <CLASS_T extends IResourceMark> void fillOneField(CLASS_T obj, String attrName, List<String> row, int range_begin, int range_end) {
		try {
			Field field = ReflectionUtils.getDeclaredField(obj, attrName);
			if (field != null) {
				Class<?> type = field.getType();
				field.setAccessible(true);
				if (type.equals(int.class) || type.equals(Integer.class)) {
					field.set(obj, decodeInt(row.get(range_begin).trim()));
				} else if (type.equals(double.class)) {
					field.set(obj, Double.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(boolean.class)) {
					field.set(obj, Boolean.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(short.class)) {
					field.set(obj, Short.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(byte.class)) {
					field.set(obj, Byte.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(float.class)) {
					field.set(obj, Float.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(long.class)) {
					field.set(obj, Long.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(float.class)) {
					field.set(obj, Float.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(double.class)) {
					field.set(obj, Double.valueOf(row.get(range_begin).trim()));
				} else if (type.equals(int[].class)) {
					field.set(obj, parseIntArray(row.subList(range_begin, range_end)));
				} else if (type.equals(Integer[].class)) {
					field.set(obj, parseIntegerArray(row.subList(range_begin, range_end)));
				} else if (type.equals(String[].class)) {
					field.set(obj, parseStringArray(row.subList(range_begin, range_end)));
				} else {
					field.set(obj, row.get(range_begin).trim().replace('^', '\n').replace('~', ' '));
				}
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e2) {
			// TODO: handle exception
		}
	}

	private int[] parseIntArray(List<String> values) {
		int[] r = new int[values.size()];
		int length = 0;
		for (int j = 0; j < r.length; ++j) {
			if (!"".equals(values.get(j))) {
				r[j] = decodeInt(values.get(j));
				length += 1;
			}
		}
		if (length == r.length) {
			return r;
		} else {
			// 去0操作
			return Arrays.copyOf(r, length);
		}
	}

	private Integer[] parseIntegerArray(List<String> values) {
		Integer[] r = new Integer[values.size()];
		int length = 0;
		for (int j = 0; j < r.length; ++j) {
			if (!"".equals(values.get(j))) {
				r[j] = decodeInt(values.get(j));
				length += 1;
			}
		}
		if (length == r.length) {
			return r;
		} else {
			// 去0操作
			return Arrays.copyOf(r, length);
		}
	}

	private String[] parseStringArray(List<String> values) {
		String[] r = new String[values.size()];
		int length = 0;
		for (int j = 0; j < r.length; ++j) {
			if (!"".equals(values.get(j))) {
				r[j] = values.get(j);
				length += 1;
			}
		}
		if (length == r.length) {
			return r;
		} else {
			// 去null操作
			return Arrays.copyOf(r, length);
		}
	}

	private int decodeInt(String str) {
		try {
			return Integer.parseInt(str.trim());
		} catch (Exception e) {
			return Integer.decode(str);
		}
	}

	@Override
	public String getKey() {
		return "txt";
	}
}
