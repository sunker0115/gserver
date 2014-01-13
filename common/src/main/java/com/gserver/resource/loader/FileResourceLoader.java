package com.gserver.resource.loader;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.gserver.file.monitor.FileChangeListener;
import com.gserver.file.monitor.FolderWatchers;
import com.gserver.resource.IResourceMark;
import com.gserver.resource.ResourceManager;
import com.gserver.resource.resolver.ResolverManager;
import com.gserver.resource.resolver.ResourceResolver;

public class FileResourceLoader implements ResourceLoader {

	private static final Logger logger = LoggerFactory.getLogger(FileResourceLoader.class);

	private static ResolverManager globalInstance = ResolverManager.getInstance();
	/**
	 * f2c.properties配置文件的缓存版本，用于找到文件名和类的对应关系，保存3分钟。
	 */
	static Cache<String, String> f2cCache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).maximumSize(1000).build();
	/**
	 * f2c.properties的文件名。
	 */
	protected String F2C_PROPERTIES = FileResourceLoader.class.getResource("").getPath().substring(1) + "f2c.properties";

	/**
	 * 重新加载配置。
	 * <p>
	 * 3分钟更新一次f2c.properties。
	 */
	protected void configF2C() {
		final Properties properties = new Properties();
		try {
            Reader r = new FileReader(new File(F2C_PROPERTIES));
            try {
                properties.load(r);
            } finally {
                r.close();
            }
        } catch (IOException e) {
			logger.error("The f2c properties file <{}> is missing!", F2C_PROPERTIES);
		}
		Enumeration<?> propertyNames = properties.propertyNames();
		while (propertyNames.hasMoreElements()) {
			String f = (String) propertyNames.nextElement();
			String c = properties.getProperty(f);
			f2cCache.put(f, c);
		}
	}

	/**
	 * 资源类的包名。
	 */
	protected static final String RESOURCE_PACKAGE = "com.gserver.game.resource";

	public FileResourceLoader() {
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(String... root) {
		try {
			if (root == null || root.length == 0) {
				return;
			}
			for (String otherRoot : root) {
				Path path = Paths.get(otherRoot);
				configAndLoad(path);
				FolderWatchers.getInstance().addFolderListener(path, new FileChangeListener(ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY));
			}
		} catch (IOException e) {
			logger.error("Resource load error.", e);
			e.printStackTrace();
		}
	}

	private void configAndLoad(Path p) {
		configF2C();
		try {
			java.nio.file.Files.walkFileTree(p, visitor);
		} catch (IOException e) {
			logger.error("Loading resource file error!", e);
			e.printStackTrace();
		}
	}

	/**
	 * 每个文件的访问方法。
	 */
	final private FileVisitor<? super Path> visitor = new SimpleFileVisitor<Path>() {
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			boolean directory = Files.isDirectory(file);
			if (!directory) {				
				loadFile(file);
			}
			return FileVisitResult.CONTINUE;
		}
	};

	public static void loadFile(Path file) {
		String ext = com.google.common.io.Files.getFileExtension(file.getName(file.getNameCount() - 1).toString());

		ResourceResolver resolver = globalInstance.getResourceResolver(ext);
		String name = f2cCache.getIfPresent(file.getName(file.getNameCount() - 1).toString());
		if (name == null) {
			name = RESOURCE_PACKAGE + '.' + StringUtils.capitalize(com.google.common.io.Files.getNameWithoutExtension(file.getName(file.getNameCount() - 1).toString()));
		}
		if (logger.isInfoEnabled()) {
			logger.info("Resource {} is visited.", name);
		}
		try {
			@SuppressWarnings("unchecked")
			Class<? extends IResourceMark> clazz = (Class<? extends IResourceMark>) Class.forName(name, false, FileResourceLoader.class.getClassLoader());
			List<? extends IResourceMark> resolve = resolver.resolve(clazz, file.toFile());
			for (IResourceMark data : resolve) {
				ResourceManager.getManager().put(data);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
