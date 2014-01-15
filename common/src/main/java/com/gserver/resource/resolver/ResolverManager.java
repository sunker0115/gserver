package com.gserver.resource.resolver;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractIdleService;
import com.gserver.resource.resolver.impl.TxtResolver;
import com.gserver.resource.resolver.impl.XlsResolver;

public class ResolverManager extends AbstractIdleService {

	public static volatile boolean running = false;

	public static ResolverManager instance = new ResolverManager();
	private Map<String, ResourceResolver> map = Maps.newConcurrentMap();

	private ResolverManager() {
		super();
	}

	public final static ResolverManager getInstance() {
		return instance;
	}

	public void addResourceResolver(ResourceResolver is) {
		map.put(is.getKey(), is);
	}

	public ResourceResolver getResourceResolver(String key) {
		return map.get(key);
	}

	@Override
	protected void shutDown() throws Exception {
		map.clear();
		running = false;
	}

	@Override
	protected void startUp() throws Exception {
		TxtResolver.class.newInstance();
		XlsResolver.class.newInstance();
		running = true;
	}

}
