package com.gserver.file.monitor;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class FolderWatchers {

	private static FolderWatchers watchers = null;
	private static WatchService watchService = null;
	private static final ExecutorService executor = Executors.newCachedThreadPool();

	private FolderWatchers() throws IOException {
		watchService = FileSystems.getDefault().newWatchService();
	}

	public static FolderWatchers getInstance() throws IOException {
		if (null == watchers) {
			watchers = new FolderWatchers();
		}
		return watchers;
	}

	public FolderWatcherFuture addFolderListener(Path path, ChangeListener changeListener) throws IOException {
		FolderWatcher aWatcher = new FolderWatcher(path, changeListener, watchService);
		Future<?> f = executor.submit(aWatcher);
		FolderWatcherFuture future = new FolderWatcherFuture(aWatcher, f);
		return future;
	}

	public void cancelFolderWatching(FolderWatcherFuture folderWatcherFuture) {

	}
}

class FolderWatcher implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(FolderWatcher.class);

	private WatchKey watchKey = null;
	private ChangeListener listener = null;
	private Path path;

	protected FolderWatcher(Path path, ChangeListener changeListener, WatchService watchService) throws IOException {
		watchKey = path.register(watchService, changeListener.getEventTypes());
		listener = changeListener;
		this.path = path;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		while (!cancel) {
			try {
				Thread.sleep(1000);
				for (WatchEvent<?> anEvent : watchKey.pollEvents()) {
					listener.onEvent((WatchEvent<Path>) anEvent, path);
				}
			} catch (Throwable e) {
				logger.error("Error happend in resource-reloading thread.", e);
			}
		}
	}

	private boolean cancel = false;

	protected void cancel() {
		this.cancel = true;
	}
}

@SuppressWarnings("all")
class FolderWatcherFuture implements Future {
	private FolderWatcher watcher = null;
	private Future<?> future = null;

	public FolderWatcherFuture(FolderWatcher watcher, Future<?> future) {
		this.watcher = watcher;
		this.future = future;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		watcher.cancel();
		return future.cancel(mayInterruptIfRunning);
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}
}
