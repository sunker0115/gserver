package com.gserver.resource;

import java.util.Arrays;

import com.gserver.resource.IResourceMark;

/**
 * 条件
 * 
 * @author
 * 
 */
public class ConditionData implements IResourceMark {

	/** code */
	private int code;
	/** 说明 */
	private String desc;
	/** 类型 */
	private short type;
	/** 提示ID */
	private int tip;
	/** 影响 */
	private int effect;
	/** 参数 */
	private int[] params;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public int getTip() {
		return tip;
	}

	public void setTip(int tip) {
		this.tip = tip;
	}

	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	public int[] getParams() {
		return params;
	}

	public void setParams(int[] params) {
		this.params = params;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public void handle() {
		System.out.println("ConditionData: " + this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConditionData [code=");
		builder.append(code);
		builder.append(", desc=");
		builder.append(desc);
		builder.append(", type=");
		builder.append(type);
		builder.append(", tip=");
		builder.append(tip);
		builder.append(", effect=");
		builder.append(effect);
		builder.append(", params=");
		builder.append(Arrays.toString(params));
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void checkResource() {
		
	}

}
