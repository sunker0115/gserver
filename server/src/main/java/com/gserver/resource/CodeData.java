package com.gserver.resource;

import com.gserver.resource.IResourceMark;
import com.gserver.resource.ResourceManager;

public class CodeData implements IResourceMark {
	private int code;
	private String mac;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public void handle() {
		System.out.println("CodeData: " + this);
		ResourceManager.getInstance().putKey(getMac(), this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CodeData [code=");
		builder.append(code);
		builder.append(", mac=");
		builder.append(mac);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void checkResource() {

	}

}
