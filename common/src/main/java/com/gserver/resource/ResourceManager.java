package com.gserver.resource;

import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class ResourceManager {

	private final static ThreadLocal<Class<?>> choosePool = new ThreadLocal<Class<?>>();

	private static ResourceManager instance = new ResourceManager();

	private ResourceManager() {
		super();
	}

	public final static ResourceManager getManager() {
		return instance;
	}

	private final Table<Class<? extends IResourceMark>, Integer, IResourceMark> dataById = HashBasedTable.create();

	public ResourceManager getPool(Class<? extends IResourceMark> type) {
		choosePool.set(type);
		return this;
	}

	public <T extends IResourceMark> T getById(int id) {
		Class<?> cls = choosePool.get();
		@SuppressWarnings("unchecked")
		T iResourceMark = (T) dataById.get(cls, id);
		return iResourceMark;
	}

	public void put(IResourceMark resource) {
		dataById.put(resource.getClass(), resource.getId(), resource);
	}

	public void check() {
		Map<Class<? extends IResourceMark>, Map<Integer, IResourceMark>> rowMap = dataById.rowMap();
		for (Map.Entry<Class<? extends IResourceMark>, Map<Integer, IResourceMark>> entry : rowMap.entrySet()) {
			for (Map.Entry<Integer, IResourceMark> subEntry : entry.getValue().entrySet()) {
				subEntry.getValue().checkResource();
			}
		}
	}
}