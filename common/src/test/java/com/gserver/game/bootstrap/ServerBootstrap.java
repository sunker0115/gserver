package com.gserver.game.bootstrap;

import java.util.Collection;

import com.gserver.game.resource.CodeData;
import com.gserver.game.resource.ConditionData;
import com.gserver.resource.IResourceMark;
import com.gserver.resource.ResourceManager;
import com.gserver.resource.loader.FileResourceLoader;
import com.gserver.resource.loader.ResourceLoader;

public class ServerBootstrap {

	public static void main(String[] args) throws Throwable {
		ResourceLoader resourceLoader = new FileResourceLoader();
		resourceLoader.load("./res");
		
		
		ResourceManager manager = ResourceManager.getManager();
		ConditionData data = manager.getPool(ConditionData.class).getById(1);
		manager.check();
		
		Collection<IResourceMark> byKey = manager.getPool(CodeData.class).getByKey("0c771a45397a");
		System.out.println(data);

	}
}
