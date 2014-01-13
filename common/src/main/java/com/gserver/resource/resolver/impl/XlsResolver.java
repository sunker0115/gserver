package com.gserver.resource.resolver.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.gserver.resource.IResourceMark;
import com.gserver.resource.resolver.RowBasedFileResolver;
import com.gserver.utils.ReflectionUtils;

public class XlsResolver extends RowBasedFileResolver<HSSFCell> {

	private static final Logger logger = LoggerFactory.getLogger(XlsResolver.class);

	@Override
	protected <CLASS_T extends IResourceMark> List<List<HSSFCell>> readRows(Class<CLASS_T> clazz, File input) {
		List<List<HSSFCell>> returnList = Lists.newArrayList();
		logger.info((new StringBuilder("\u5F00\u59CB\u52A0\u8F7D\u8D44\u6E90\u6587\u4EF6 [")).append(input).append("] .").toString());
		try {
			InputStream inp = new FileInputStream(input);
			HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
			HSSFSheet sheet = wb.getSheetAt(0); // !
			// 第0个，没有则IndexOutOfBoundException
			String sheetName = sheet.getSheetName();
			if (logger.isInfoEnabled())
				logger.info((new StringBuilder("\u6B63\u5728\u52A0\u8F7D\u9875\u540D\u4E3A :")).append(sheetName).toString());
			for (int i = 0; i <= sheet.getLastRowNum(); ++i) {// 行
				HSSFRow row = sheet.getRow(i);
				// List<CLASS_T> row = Lists.newArrayList();
				returnList.add(Lists.<HSSFCell> newArrayList());
				for (int j = 0; j <= row.getLastCellNum(); ++j) {
					HSSFCell cell = row.getCell(j);
					if (cell == null) {
						continue;
					}
					returnList.get(i).add(cell);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnList;
	}

	@Override
	protected List<String> getAttributeNames(List<HSSFCell> row) {
		Assert.notNull(row);
		List<String> ts = Lists.newArrayList();
		for (HSSFCell cell : row) {
			ts.add(cell.getStringCellValue().trim());
		}
		return ts;
	}

	@Override
	protected <CLASS_T extends IResourceMark> boolean fillFields(CLASS_T obj, List<String> attributeNames, List<HSSFCell> row) {
		Assert.notNull(attributeNames, "attributeNames is null.");
		Assert.notNull(row, "row is null.");
		Assert.isTrue(row.size() == attributeNames.size());
		for (int i = 0; i < row.size(); i++) {
			String fieldName = attributeNames.get(i);
			Field field = ReflectionUtils.getDeclaredField(obj, fieldName);
			if (field != null) {
				setPropertyValue(obj, row.get(i), field);
			}
		}
		return true;
	}

	private void setPropertyValue(Object obj, HSSFCell cell, Field field) {
		Class<?> type = field.getType();
		String fieldName = field.getName();
		if (cell == null || cell.toString().length() == 0) {
			return;
		}
		if (type == Integer.class || type == int.class) {
			double value = cell.getNumericCellValue();
			ReflectionUtils.setFieldValue(obj, fieldName, (int) value);
		} else if (type == Float.class || type == float.class) {
			double value = cell.getNumericCellValue();
			ReflectionUtils.setFieldValue(obj, fieldName, (float) value);
		} else if (type == Short.class || type == short.class) {
			double value = cell.getNumericCellValue();
			ReflectionUtils.setFieldValue(obj, fieldName, (short) value);
		} else if (type == Double.class || type == double.class) {
			double value = cell.getNumericCellValue();
			ReflectionUtils.setFieldValue(obj, fieldName, value);
		} else if (type == Long.class || type == long.class) {
			double value = cell.getNumericCellValue();
			ReflectionUtils.setFieldValue(obj, fieldName, (long) value);
		} else if (type == String.class) {
			String value = checkString(cell.toString());
			ReflectionUtils.setFieldValue(obj, fieldName, value);
		} else if (type == String[].class) {
			String value = checkString(cell.toString());
			ReflectionUtils.setFieldValue(obj, fieldName, value.split(","));
		} else if (type == int[].class || type == Integer[].class) {
			String[] value = cell.toString().split(",");
			int[] arr = strArrToIntArr(value);
			ReflectionUtils.setFieldValue(obj, fieldName, arr);
		}
	}

	private int[] strArrToIntArr(String[] value) {
		int arr[] = new int[value.length];
		for (int i = 0; i < value.length; i++) {
			arr[i] = Integer.parseInt(value[i]);
		}
		return arr;
	}

	private String checkString(String value) {
		if (value.endsWith(".0")) {
			value = value.substring(0, value.length() - 2);
		}
		return value;
	}

	@Override
	public String getKey() {
		return "xls";
	}
}
