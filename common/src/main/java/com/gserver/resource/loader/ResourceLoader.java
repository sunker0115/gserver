package com.gserver.resource.loader;

/**
 * 资源加载接口。
 * 
 * @author zhangbo
 * 
 */
public interface ResourceLoader {

	/**
	 * 加载资源。
	 * 
	 * @param root
	 *            资源根目录
	 */
	public void load(String... root);

}
