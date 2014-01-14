package com.gserver.condition;

import com.gserver.game.resource.ConditionData;

public abstract class BaseCondition implements ICondition {
	protected static short AND = 1;
	protected static short OR = 2;
	protected static short NO = 3;
	protected static short TIME = 4;
	protected static short UP_AND_DOWN = 5;

	/**
	 * 加载顺序：constructor、postConstruct、afterPropertiesSet、init-method
	 */
	public BaseCondition() {
		ConditionManager.instance().addCondition(this);
	}

	public <T> Boolean consume(T go, ConditionData con) {

		return true;
	}

	public <T> void hint(T go, ConditionData con, boolean flag) {
		// default
	}

	public Boolean returnValue(Boolean flag, ConditionData con) {
		int type = con.getEffect();
		if (type == 1 && flag == true) {
			return flag;
		} else if (type == 2 && flag == false) {
			return flag;
		} else if (type == 3) {
			return flag;
		}
		return null;
	}
}
