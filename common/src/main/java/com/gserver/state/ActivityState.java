package com.gserver.state;

/**
 * 活动状态,活动状态是对玩家在游戏进行中的活动的表示. 
 * 活动定义为玩家在游戏过程中所持续的某种行为状态,在游戏中,玩家的行动状态同一时刻可能处于多种状态.
 * 例如将组队归为活动状态.
 */
public abstract class ActivityState implements State {

	protected final int id;

	private final int hashCode;

	/**
	 * 
	 * @param id
	 */
	public ActivityState(int id) {
		this.id = id;
		this.hashCode = initHashCode();
	}

	@Override
	public boolean canTransition(State targetState) {
		if (!(targetState instanceof ActivityState)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean enter() {
		return true;
	}

	@Override
	public boolean exit() {
		return true;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!getClass().isAssignableFrom(obj.getClass())) {
			return false;
		}
		final ActivityState other = (ActivityState) obj;
		return id == other.id;
	}

	private int initHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
}
