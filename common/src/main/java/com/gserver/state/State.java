package com.gserver.state;

/**
 * 表示玩家的一种状态
 * 
 * 
 * 
 */
public interface State {

	/**
	 * 取得该状态的ID
	 * 
	 * @return
	 */
	public int getId();

	/**
	 * 取得该状态类型
	 * 
	 * @return
	 */
	public int getType();

	/**
	 * 是否可以进入目标状态
	 * 
	 * @param targetState
	 * @return
	 */
	public abstract boolean canTransition(State targetState);

	/**
	 * 进入该状态
	 * 
	 * @return
	 */
	public abstract boolean enter();

	/**
	 * 退出该状态
	 * 
	 * @return
	 */
	public abstract boolean exit();

	/**
	 * 获取当前Sate的Mask
	 * 
	 * @return
	 */
	public abstract int getStateMask();
}
