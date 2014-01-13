package com.gserver.resource.resolver;

import java.io.File;
import java.util.List;

import com.gserver.resource.IResourceMark;

/**
 * 资源处理器。
 * 
 * @author zhangbo
 * 
 */
public interface ResourceResolver{

	/**
	 * 处理输入文件，得到解析后的数据。
	 * 
	 * @param <CLASS_T>
	 *            资源对应的类型
	 * @param clazz
	 *            资源类
	 * @param input
	 *            输入文件
	 * @return 得到的数据。
	 */
	public <CLASS_T extends IResourceMark> List<CLASS_T> resolve(Class<CLASS_T> clazz, File input);


	public String getKey();
}
