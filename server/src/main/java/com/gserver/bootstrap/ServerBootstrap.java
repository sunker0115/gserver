package com.gserver.bootstrap;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.common.util.concurrent.ServiceManager.Listener;
import com.gserver.condition.ConditionManager;
import com.gserver.resource.CodeData;
import com.gserver.resource.ConditionData;
import com.gserver.resource.IResourceMark;
import com.gserver.resource.ResourceManager;
import com.gserver.resource.loader.FileResourceLoader;
import com.gserver.resource.loader.ResourceLoader;
import com.gserver.resource.resolver.ResolverManager;

public class ServerBootstrap {

	public static void main(String[] args) throws Throwable {

		Iterable<? extends Service> services = Sets.newHashSet(ResolverManager.getInstance(), ResourceManager.getInstance(), ConditionManager.getInstance());
		final ServiceManager manager = new ServiceManager(services);

		// manager.addListener(new Listener() {
		// public void stopped() {
		// }
		//
		// public void healthy() {
		// // Services have been initialized and are healthy, start
		// // accepting requests...
		// }
		//
		// public void failure(Service service) {
		// // Something failed, at this point we could log it, notify a
		// // load balancer, or take
		// // some other action. For now we will just exit.
		// System.exit(1);
		// }
		// }, MoreExecutors.sameThreadExecutor());
		//
		// Runtime.getRuntime().addShutdownHook(new Thread() {
		// public void run() {
		// // Give the services 5 seconds to stop to ensure that we are
		// // responsive to shutdown
		// // requests.
		// try {
		// manager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
		// } catch (TimeoutException timeout) {
		// // stopping timed out
		// }
		// }
		// });

		manager.startAsync().awaitHealthy();
		ResourceLoader resourceLoader = new FileResourceLoader();
		resourceLoader.load(".\\res");

		ResourceManager resourceManager = ResourceManager.getInstance();
		ConditionData data = resourceManager.getById(ConditionData.class, 1);
		resourceManager.check();

		Collection<IResourceMark> byKey = resourceManager.getByKey(CodeData.class, "0c771a45397a");
		System.out.println(data);
	}
}
