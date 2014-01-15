package com.gserver.game.bootstrap;

import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.google.common.util.concurrent.Service.State;
import com.google.common.util.concurrent.ServiceManager.Listener;
import com.gserver.condition.ConditionManager;
import com.gserver.resource.ResourceManager;
import com.gserver.resource.resolver.ResolverManager;

public class ServerBootstrap {

	public static void main(String[] args) throws Throwable {
		// ResourceLoader resourceLoader = new FileResourceLoader();
		// resourceLoader.load("./res");
		//		
		//		
		// ResourceManager manager = ResourceManager.getManager();
		// ConditionData data = manager.getPool(ConditionData.class).getById(1);
		// manager.check();
		//		
		// Collection<IResourceMark> byKey =
		// manager.getPool(CodeData.class).getByKey("0c771a45397a");
		// System.out.println(data);

		Iterable<? extends Service> services = Sets.newHashSet(ResolverManager.getInstance(), ResourceManager.getInstance(), ConditionManager.getInstance());
		final ServiceManager manager = new ServiceManager(services);
		manager.addListener(new Listener() {
			public void stopped() {
			}

			public void healthy() {
				// Services have been initialized and are healthy, start
				// accepting requests...
			}

			public void failure(Service service) {
				// Something failed, at this point we could log it, notify a
				// load balancer, or take
				// some other action. For now we will just exit.
				System.exit(1);
			}
		}, MoreExecutors.sameThreadExecutor());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				// Give the services 5 seconds to stop to ensure that we are
				// responsive to shutdown
				// requests.
				try {
					manager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
				} catch (TimeoutException timeout) {
					// stopping timed out
				}
			}
		});
		manager.startAsync();
		manager.awaitHealthy();
		ImmutableMultimap<State, Service> servicesByState = manager.servicesByState();
		ImmutableCollection<Entry<State, Service>> entries = servicesByState.entries();
		for (Entry<State, Service> entry : entries) {
			System.out.println(entry.getKey()+"   "+entry.getValue());
		}
	}
}
