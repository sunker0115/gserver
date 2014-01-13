package com.gserver.file.monitor;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;

public abstract class ChangeListener {

	Kind<Path>[] eventTypes = null;

	@SafeVarargs
	public ChangeListener(Kind<Path>... eventTypes) {
		this.eventTypes = eventTypes;
	}

	public Kind<Path>[] getEventTypes() {
		return eventTypes;
	}

	public abstract void onEvent(WatchEvent<Path> anEvent, Path path);

}
