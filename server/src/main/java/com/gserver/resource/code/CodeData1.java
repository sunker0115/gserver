package com.gserver.resource.code;

import com.gserver.resource.IResourceMark;
import com.gserver.resource.ResourceManager;

public class CodeData1 implements IResourceMark {
	private int id;
	private String mac;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		builder.append("CodeData [id=");
		builder.append(id);
		builder.append(", mac=");
		builder.append(mac);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public void checkResource() {

	}

}
