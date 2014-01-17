package com.gserver.resource.resolver.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.base.Strings;
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
			int lastRowNum = sheet.getLastRowNum();
			for (int i = 0; i <= lastRowNum; ++i) {// 行
				HSSFRow row = sheet.getRow(i);
				// List<CLASS_T> row = Lists.newArrayList();
				returnList.add(Lists.<HSSFCell> newArrayList());
				short lastCellNum = row.getLastCellNum();
				for (int j = 0; j < lastCellNum; ++j) {
					HSSFCell cell = row.getCell(j);
					// if (cell == null) {
					// continue;
					// }
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
			if (cell == null) {
				ts.add(null);
			} else {
				ts.add(cell.getStringCellValue().trim());
			}
		}
		return ts;
	}

	@Override
	protected <CLASS_T extends IResourceMark> boolean fillFields(CLASS_T obj, List<String> attributeNames, List<HSSFCell> row) {
		Assert.notNull(attributeNames, "attributeNames is null.");
		Assert.notNull(row, "row is null.");
		Assert.isTrue(row.size() == attributeNames.size());

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
	private <CLASS_T extends IResourceMark> void fillOneField(CLASS_T obj, String attrName, List<HSSFCell> row, int range_begin, int range_end) {
		try {
			Field field = ReflectionUtils.getDeclaredField(obj, attrName);
			if (field != null) {
				Class<?> type = field.getType();
				field.setAccessible(true);
				if (type.equals(int.class) || type.equals(Integer.class)) {
					field.set(obj, (int) row.get(range_begin).getNumericCellValue());
				} else if (type.equals(double.class)) {
					field.set(obj, Double.valueOf(row.get(range_begin).getNumericCellValue()));
				} else if (type.equals(short.class)) {
					field.set(obj, (short) row.get(range_begin).getNumericCellValue());
				} else if (type.equals(byte.class)) {
					field.set(obj, (byte) row.get(range_begin).getNumericCellValue());
				} else if (type.equals(float.class)) {
					field.set(obj, (float) row.get(range_begin).getNumericCellValue());
				} else if (type.equals(long.class)) {
					field.set(obj, (long) row.get(range_begin).getNumericCellValue());
				} else if (type.equals(int[].class)) {
					field.set(obj, parseIntArray(row.subList(range_begin, range_end)));
				} else if (type.equals(Integer[].class)) {
					field.set(obj, parseIntegerArray(row.subList(range_begin, range_end)));
				} else if (type.equals(String[].class)) {
					field.set(obj, parseStringArray(row.subList(range_begin, range_end)));
				} else {
					field.set(obj, row.get(range_begin).getStringCellValue().replace('^', '\n').replace('~', ' '));
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e2) {
		}
	}

	private int[] parseIntArray(List<HSSFCell> values) {
		int[] r = new int[values.size()];
		int length = 0;
		for (int j = 0; j < r.length; ++j) {
			HSSFCell hssfCell = values.get(j);
			hssfCell.setCellType(Cell.CELL_TYPE_STRING);
			String stringCellValue = hssfCell.getStringCellValue();
			if (StringUtils.isNotBlank(stringCellValue)) {
				r[j] = Integer.valueOf(stringCellValue);
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

	private Integer[] parseIntegerArray(List<HSSFCell> values) {
		Integer[] r = new Integer[values.size()];
		int length = 0;
		for (int j = 0; j < r.length; ++j) {
			HSSFCell hssfCell = values.get(j);
			hssfCell.setCellType(Cell.CELL_TYPE_STRING);
			String stringCellValue = hssfCell.getStringCellValue();
			if (StringUtils.isNotBlank(stringCellValue)) {
				r[j] = Integer.valueOf(stringCellValue);
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

	private String[] parseStringArray(List<HSSFCell> values) {
		String[] r = new String[values.size()];
		int length = 0;
		for (int j = 0; j < r.length; ++j) {
			HSSFCell hssfCell = values.get(j);
			hssfCell.setCellType(Cell.CELL_TYPE_STRING);
			String stringCellValue = hssfCell.getStringCellValue();
			if (StringUtils.isNotBlank(stringCellValue)) {
				r[j] = stringCellValue;
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
