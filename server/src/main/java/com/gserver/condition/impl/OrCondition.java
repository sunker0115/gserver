package com.gserver.condition.impl;

import java.util.ArrayList;
import java.util.List;

import com.gserver.condition.BaseCondition;
import com.gserver.condition.ConditionManager;
import com.gserver.condition.ICondition;
import com.gserver.resource.ConditionData;
import com.gserver.resource.ResourceManager;

public class OrCondition extends BaseCondition {

	public <T> Boolean check(T go, ConditionData data) {
		int[] codes = data.getParams();
		List<ConditionData> list = init(codes);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			Boolean check = ConditionManager.instance().getCondition(data.getType()).check(go, data);
			if (check == null) {
				continue;
			}
			if (check) {
				return true;
			}
		}
		return false;
	}

	public List<ConditionData> init(int... codes) {
		List<ConditionData> list = new ArrayList<ConditionData>();
		int length = codes.length;
		for (int i = 0; i < length; i++) {
			ConditionData data = ResourceManager.getInstance().getById(ConditionData.class, codes[i]);
			ICondition condition = ConditionManager.instance().getCondition(data.getType());
			if (condition == null) {
				System.out.println("condition(" + data.getType() + ")不存在!");
				continue;
			}
			list.add(data);
		}
		return list;
	}

	public short getType() {
		return OR;
	}

}
