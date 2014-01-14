package com.gserver.condition.impl;

import java.util.ArrayList;
import java.util.List;

import com.gserver.condition.BaseCondition;
import com.gserver.condition.ConditionManager;
import com.gserver.condition.ICondition;
import com.gserver.game.resource.ConditionData;
import com.gserver.resource.ResourceManager;

public class NoCondition extends BaseCondition {

	public <T> Boolean check(T go, ConditionData data) {
		List<ConditionData> list = init(data.getParams());
		for (ConditionData condition : list) {
			Boolean check = ConditionManager.instance().getCondition(data.getType()).check(go, condition);
			if (check == null) {
				return check;
			}
			if (check) {
				return returnValue(false, data);
			}
		}
		return returnValue(true, data);
	}

	public short getType() {
		return NO;
	}

	public List<ConditionData> init(String... str) {
		List<ConditionData> list = new ArrayList<ConditionData>();
		ConditionData data = ResourceManager.getManager().getPool(ConditionData.class).getById(Integer.parseInt(str[0]));
		ICondition condition = ConditionManager.instance().getCondition(data.getType());
		if (condition == null) {
			System.out.println("condition(" + data.getType() + ")不存在!");
		}
		list.add(data);
		return list;
	}
}
