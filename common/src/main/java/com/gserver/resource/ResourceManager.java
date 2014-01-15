package com.gserver.resource;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.AbstractIdleService;

public class ResourceManager extends AbstractIdleService {

	private final static ThreadLocal<Class<?>> choosePool = new ThreadLocal<Class<?>>();

	private static ResourceManager instance = new ResourceManager();

	private ResourceManager() {
		super();
	}

	public final static ResourceManager getInstance() {
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

	private final Table<Class<? extends IResourceMark>, Object, Multiset<IResourceMark>> keyDic = HashBasedTable.create();

	public <T> void putKey(T key, IResourceMark resource) {
		Map<Object, Multiset<IResourceMark>> row = keyDic.row(resource.getClass());
		if (row == null) {
			row = Maps.newHashMap();
		}
		Multiset<IResourceMark> multiset = row.get(key);
		if (multiset == null) {
			multiset = HashMultiset.create();
		}
		multiset.add(resource);
		keyDic.put(resource.getClass(), key, multiset);
	}

	@SuppressWarnings("unchecked")
	public <T> Collection<IResourceMark> getByKey(T key) {
		Class<?> cls = choosePool.get();
		return keyDic.row((Class<? extends IResourceMark>) cls).get(key);
	}

	public void check() {
		Map<Class<? extends IResourceMark>, Map<Integer, IResourceMark>> rowMap = dataById.rowMap();
		for (Map.Entry<Class<? extends IResourceMark>, Map<Integer, IResourceMark>> entry : rowMap.entrySet()) {
			for (Map.Entry<Integer, IResourceMark> subEntry : entry.getValue().entrySet()) {
				subEntry.getValue().checkResource();
			}
		}
	}

	@Override
	protected void shutDown() throws Exception {
	}

	@Override
	protected void startUp() throws Exception {
	}
}