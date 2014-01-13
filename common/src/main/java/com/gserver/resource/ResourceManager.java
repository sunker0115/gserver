package com.gserver.resource;

import java.util.Collection;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
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

	private final Multimap<Object, IResourceMark> keyDic = ArrayListMultimap.create();

	public void put(IResourceMark resource) {
		dataById.put(resource.getClass(), resource.getId(), resource);
	}

	public <T> void putKey(T key, IResourceMark resource) {
		keyDic.put(key, resource);
	}

	public <T> Collection<IResourceMark> getByKey(T key) {
		return keyDic.get(key);
	}

	public void check() {
		
	}
}