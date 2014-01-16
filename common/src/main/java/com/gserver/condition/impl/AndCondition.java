package com.gserver.condition.impl;

import java.util.ArrayList;
import java.util.List;

import com.gserver.condition.BaseCondition;
import com.gserver.condition.ConditionManager;
import com.gserver.condition.ICondition;
import com.gserver.game.resource.ConditionData;
import com.gserver.resource.ResourceManager;

public class AndCondition extends BaseCondition {

	public <T> Boolean check(T go, ConditionData data) {
		String[] str = data.getParams();
		List<ConditionData> list = init(str);
		int size = list.size();
		boolean flag = true;
		for (int i = 0; i < size; i++) {
			ConditionData con1 = list.get(i);
			Boolean check = ConditionManager.instance().getCondition(data.getType()).check(go, con1);
			if (check == null) {
				return check;
			}
			if (!check) {
				flag = false;
			}
		}
		return returnValue(flag, data);
	}

	private List<ConditionData> init(String... str) {
		List<ConditionData> list = new ArrayList<ConditionData>();
		int[] codes = stringToIntArray(str);
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

	public static int[] stringToIntArray(String[] str) {
		int[] array = new int[str.length];
		for (int i = 0; i < str.length; i++) {
			array[i] = Integer.parseInt(str[i]);
		}
		return array;
	}

	public short getType() {
		return AND;
	}

}
