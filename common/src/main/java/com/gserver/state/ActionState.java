package com.gserver.state;

/**
 * 行动状态,行动状态是对玩家在游戏进行中的行动的表示. 
 * 行动定义为玩家为了达到某个目的而进行的一系列活动,一个行动一般会持续较长一段时间.
 * 在游戏中,玩家的行动状态同一时刻只能处于某一种状态,也就是说行动状态是排他类型的状态 例如将战斗归为行动状态,交易也认为是行动状态等
 * 
 */
public abstract class ActionState implements State {

	protected final int id;
	protected final int[] targetActionStateIds;

	/**
	 * 构建一个行动状态
	 * 
	 * @param id
	 *            状态的ID
	 * @param baseCharacter
	 *            该状态所属的角色对象
	 * @param targetActionStateIds
	 *            可以从当行动状态转换的目标状态ID数组
	 */
	public ActionState(int id, int[] targetActionStateIds) {
		this.id = id;
		this.targetActionStateIds = targetActionStateIds;
	}

	public int getId() {
		return this.id;
	}

	/**
	 * 只允许向非空的并且类型为ActionState的状态转移
	 */
	@Override
	public boolean canTransition(final State targetState) {
		if (!(targetState instanceof ActionState)) {
			return false;
		}
		final int _targetId = targetState.getId();

		for (int i = 0; i < targetActionStateIds.length; i++) {
			if (_targetId == targetActionStateIds[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean enter() {
		return true;
	}

	@Override
	public boolean exit() {
		return true;
	}

}
