package com.gserver.file.monitor;

import static java.nio.file.StandardWatchEventKinds.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gserver.resource.loader.FileResourceLoader;

public class FileChangeListener extends ChangeListener {
	private static Logger logger = LoggerFactory.getLogger(FileChangeListener.class);

	@SuppressWarnings("unchecked")
	public FileChangeListener(final Kind<Path>... eventTypes) {
		this.eventTypes = eventTypes;
	}

	@Override
	public void onEvent(WatchEvent<Path> event, Path path) {
		Kind<Path> kind = event.kind();
		Path context = event.context();
		Path resolve = path.resolve(context);

		boolean directory = Files.isDirectory(resolve);

		if (kind == ENTRY_MODIFY) {
			logger.debug("{} is modified!", path);
			if (!directory) {
				// RELOAD
				FileResourceLoader.loadFile(resolve);
			}
		}
		if (kind == ENTRY_DELETE) {
			System.out.println("delete:" + event.context());
		}
		if (kind == ENTRY_CREATE) {
			System.out.println("create:" + event.context());
		}

	}

}
