package com.gserver.resource.resolver;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.gserver.resource.IResourceMark;

public abstract class RowBasedFileResolver<GRID_T> implements ResourceResolver {

	private static Logger logger = LoggerFactory.getLogger(RowBasedFileResolver.class);

	public RowBasedFileResolver() {
		ResolverManager.getInstance().addResourceResolver(this);
	}

	@Override
	public <CLASS_T extends IResourceMark> List<CLASS_T> resolve(Class<CLASS_T> clazz, File input) {
		List<CLASS_T> returnList = Lists.newArrayList();
		try {
			// 第0行是注释，第1行是属性名，剩下行都是值
			List<List<GRID_T>> rows = readRows(clazz, input);
			List<GRID_T> titleRow = rows.get(1);
			List<String> attributeNames = getAttributeNames(titleRow);
			for (int i = 2; i < rows.size(); ++i) {
				List<GRID_T> valueRow = rows.get(i);
				CLASS_T obj;
				obj = clazz.newInstance();
				if (fillFields(obj, attributeNames, valueRow)) {
					obj.handle();
					returnList.add(obj);
				}
			}
		} catch (InstantiationException e) {
			logger.error("Resolving file={} clazz={} error!\n{}", new Object[] { input, clazz, e });
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			logger.error("Resolving file={} clazz={} error!\n{}", new Object[] { input, clazz, e });
			e.printStackTrace();
		}
		return returnList;
	}

	abstract public String getKey();

	abstract protected <CLASS_T extends IResourceMark> List<List<GRID_T>> readRows(Class<CLASS_T> clazz, File input);

	abstract protected List<String> getAttributeNames(List<GRID_T> row);

	abstract protected <CLASS_T extends IResourceMark> boolean fillFields(CLASS_T obj, List<String> attributeNames, List<GRID_T> row);

}
