package com.gserver.condition;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractIdleService;
import com.gserver.condition.impl.AndCondition;
import com.gserver.condition.impl.NoCondition;
import com.gserver.condition.impl.OrCondition;

public class ConditionManager extends AbstractIdleService {

	private Map<Short, ICondition> map = Maps.newConcurrentMap();

	private static ConditionManager instance = new ConditionManager();

	private ConditionManager() {
		super();
	}

	public final static ConditionManager getInstance() {
		return instance;
	}

	public static ConditionManager instance() {
		return instance;
	}

	public void addCondition(ICondition is) {
		map.put(is.getType(), is);
	}

	public ICondition getCondition(int type) {
		return map.get((short) type);
	}

	@Override
	protected void shutDown() throws Exception {
		map.clear();
	}

	@Override
	protected void startUp() throws Exception {
		AndCondition.class.newInstance();
		NoCondition.class.newInstance();
		OrCondition.class.newInstance();
	}
}
