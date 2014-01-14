package com.gserver.condition;

import java.util.Map;

import com.google.common.collect.Maps;
import com.gserver.condition.impl.AndCondition;
import com.gserver.condition.impl.NoCondition;
import com.gserver.condition.impl.OrCondition;

public class ConditionManager {

	private static ConditionManager instance = null;
	private Map<Short, ICondition> map = Maps.newConcurrentMap();

	public ConditionManager() {
		if (instance != null) {
			throw new RuntimeException("DummyServer has already been instantiated.");
		}
		instance = this;
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

	static {
		try {
			AndCondition.class.newInstance();
			NoCondition.class.newInstance();
			OrCondition.class.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
