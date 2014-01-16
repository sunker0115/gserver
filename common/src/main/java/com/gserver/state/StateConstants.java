package com.gserver.state;

public class StateConstants {

	/* Action 状态定义 ：唯一的状态 101 ~ 199 */
	/** 1 死亡状态 */
	public static final int ID_ACTION_DEAD = 101;
	/** 2 空闲状态 */
	public static final int ID_ACTION_IDLE = 102;
	/** 3 战斗状态 */
	public static final int ID_ACTION_BATTLE = 103;
	/** 4 复活状态 */
	public static final int ID_ACTION_REVIVE = 104;
	/** 5 交易状态 */
	public static final int ID_ACTION_TRADE = 105;

	/* Activity 状态定义 ：可并存的活动状态 201 ~ 299 */
	/** 201 组队状态,队员 */
	public static final int ID_ACTIVITY_GROUP_MEMBER = 201;
	/** 202 组队状态,队长 */
	public static final int ID_ACTIVITY_GROUP_LEADER = 202;
	/** 203 加点状态 */
	public static final int ID_ACTIVITY_CHANGE_POINT = 203;
	/** 204 自动寻路状态 */
	public static final int ID_ACTIVITY_AUTO_RUN = 204;

}
