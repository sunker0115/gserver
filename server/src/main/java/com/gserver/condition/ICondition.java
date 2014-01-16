package com.gserver.condition;

import com.gserver.resource.ConditionData;

public interface ICondition {
	public short getType();

	public <T> Boolean check(T go, ConditionData data);

	public abstract <T> Boolean consume(T go, ConditionData data);

	public abstract <T> void hint(T go, ConditionData data, boolean flag);

}
