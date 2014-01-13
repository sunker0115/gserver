package com.gserver.game.bootstrap;

import com.gserver.game.resource.ConditionData;
import com.gserver.resource.ResourceManager;
import com.gserver.resource.loader.FileResourceLoader;
import com.gserver.resource.loader.ResourceLoader;

public class ServerBootstrap {

	public static void main(String[] args) throws Throwable {
		ResourceLoader resourceLoader = new FileResourceLoader();
		resourceLoader.load("./res");
		
		
		ConditionData data = ResourceManager.getManager().getPool(ConditionData.class).getById(1);
		ResourceManager.getManager().getPool(ConditionData.class).getByKey("0c771a45397a");
		System.out.println(data);

	}
}
