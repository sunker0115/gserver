package com.gserver.resource.resolver;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.gserver.resource.resolver.impl.TxtResolver;
import com.gserver.resource.resolver.impl.XlsResolver;


public class ResolverManager {

	public static ResolverManager instance = new ResolverManager();
	private static ConcurrentMap<String, ResourceResolver> map = Maps.newConcurrentMap();

	static {
		try {
			TxtResolver.class.newInstance();
			XlsResolver.class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private ResolverManager() {

	}

	public static ResolverManager getInstance() {
		return instance;
	}

	public void addResourceResolver(ResourceResolver is) {
		map.put(is.getKey(), is);
	}

	public ResourceResolver getResourceResolver(String key) {
		return map.get(key);
	}

}
