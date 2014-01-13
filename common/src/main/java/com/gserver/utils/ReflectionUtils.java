package com.gserver.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 反射的Utils函数集合.
 * 
 * 提供访问私有变量,获取泛型类型Class,提取集合中元素的属性等Utils函数.
 * 
 * @author
 */
public class ReflectionUtils {

	private static Logger logger = LoggerFactory
			.getLogger(ReflectionUtils.class);

	/**
	 * 清除对像中的所有Collection对像
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object clearCollection(Object object) {
		Class c = object.getClass();
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			Class[] interfaces = method.getReturnType().getInterfaces();
			for (Class tempInterface : interfaces) {
				if (tempInterface.equals(Collection.class)) {
					String fieldName = StringUtils.replaceOnce(
							method.getName(), "get", "");
					fieldName = StringUtils.uncapitalize(fieldName);
					setFieldValue(object, fieldName, null);
				}
			}
		}
		return object;
	}

	/**
	 * 请除对像中指定的Collection属性
	 * 
	 * @param object
	 * @param fieldNames
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Object clearCollection(Object object, String... fieldNames) {
		Class c = object.getClass();
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			Class[] interfaces = method.getReturnType().getInterfaces();
			for (Class tempInterface : interfaces) {
				if (tempInterface.equals(Collection.class)) {
					for (String targetField : fieldNames) {
						String fieldName = StringUtils.replaceOnce(
								method.getName(), "get", "");
						fieldName = StringUtils.uncapitalize(fieldName);
						if (fieldName.equals(targetField)) {
							setFieldValue(object, fieldName, null);
						}
					}
				}
			}
		}
		return object;
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception. 341
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(
			Exception e) {
		return convertReflectionExceptionToUnchecked(null, e);
	}

	public static RuntimeException convertReflectionExceptionToUnchecked(
			String desc, Exception e) {
		desc = (desc == null) ? "Unexpected Checked Exception." : desc;
		if (e instanceof IllegalAccessException
				|| e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(desc, e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException(desc,
					((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException(desc, e);
	}

	/**
	 * 转换字符串到相应类型.
	 * 
	 * @param value
	 *            待转换的字符串
	 * @param toType
	 *            转换目标类型
	 */
	@SuppressWarnings("unchecked")
	public static <T> T convertStringToObject(String value, Class<T> toType) {
		try {
			return (T) ConvertUtils.convert(value, toType);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 两者属性名一致时，拷贝source里的属性到dest里
	 * 
	 * @return void
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public static void copyPorperties(Object dest, Object source)
			throws IllegalAccessException, InvocationTargetException {
		Class srcCla = source.getClass();
		Field[] fsF = srcCla.getDeclaredFields();

		for (Field s : fsF) {
			String name = s.getName();
			Object srcObj = invokeGetterMethod(source, name);
			try {
				BeanUtils.setProperty(dest, name, srcObj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				throw e;
			} catch (InvocationTargetException e) {
				throw e;
			}
		}
	}

	/**
	 * 拷贝 source 指定的porperties 属性 到 dest中
	 * 
	 * @return void
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void copyPorperties(Object dest, Object source,
			String[] porperties) throws InvocationTargetException,
			IllegalAccessException {
		for (String por : porperties) {
			Object srcObj = invokeGetterMethod(source, por);
			logger.debug("属性名：" + por + "------------- 属性值：" + srcObj);
			if (srcObj != null) {
				try {
					BeanUtils.setProperty(dest, por, srcObj);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					throw e;
				} catch (InvocationTargetException e) {
					throw e;
				}
			}
		}
	}

	/**
	 * 提取集合中的对象的属性,组合成List.
	 * 
	 * @param collection
	 *            来源集合.
	 * @param propertityName
	 *            要提取的属性名.
	 */
	@SuppressWarnings("unchecked")
	public static List fetchElementPropertyToList(final Collection collection,
			final String propertyName) throws Exception {

		List list = new ArrayList();

		for (Object obj : collection) {
			list.add(PropertyUtils.getProperty(obj, propertyName));
		}

		return list;
	}

	/**
	 * 提取集合中的对象的属性,组合成由分割符分隔的字符串.
	 * 
	 * @param collection
	 *            来源集合.
	 * @param propertityName
	 *            要提取的属性名.
	 * @param separator
	 *            分隔符.
	 */
	@SuppressWarnings("unchecked")
	public static String fetchElementPropertyToString(
			final Collection collection, final String propertyName,
			final String separator) throws Exception {
		List list = fetchElementPropertyToList(collection, propertyName);
		return StringUtils.join(list, separator);
	}

	/**
	 * 循环向上转型,获取类的DeclaredField.
	 */
	@SuppressWarnings("unchecked")
	protected static Field getDeclaredField(final Class clazz,
			final String fieldName) {
		Assert.notNull(clazz, "clazz不能为空");
		Assert.hasText(fieldName, "fieldName");
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 */
	public static Field getDeclaredField(final Object object,
			final String fieldName) {
		Assert.notNull(object, "object不能为空");
		return getDeclaredField(object.getClass(), fieldName);
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod.
	 * 
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	protected static Method getDeclaredMethod(Object object, String methodName,
			Class<?>[] parameterTypes) {
		Assert.notNull(object, "object不能为空");

		for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredMethod(methodName, parameterTypes);
			} catch (NoSuchMethodException e) {// NOSONAR
												// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 直接读取对象属性值,无视private/protected修饰符,不经过getter函数.
	 */
	public static Object getFieldValue(final Object object,
			final String fieldName) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null)
			throw new IllegalArgumentException("Could not find field ["
					+ fieldName + "] on target [" + object + "]");

		makeAccessible(field);

		Object result = null;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}

	public static final <T> T getNewInstance(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的泛型参数的类型. 如public UserDao extends HibernateDao<User>
	 * 
	 * @param clazz
	 *            The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be
	 *         determined
	 */
	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(final Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的泛型参数的类型. 如public UserDao extends
	 * HibernateDao<User,Long>
	 * 
	 * @param clazz
	 *            clazz The class to introspect
	 * @param index
	 *            the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be
	 *         determined
	 */

	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(final Class clazz,
			final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warn(clazz.getSimpleName()
					+ "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.warn("Index: " + index + ", Size of "
					+ clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.warn(clazz.getSimpleName()
					+ " not set the actual class on superclass generic parameter");
			return Object.class;
		}
		return (Class) params[index];
	}

	/**
	 * 
	 * 调用Getter方法.
	 */

	public static Object invokeGetterMethod(Object target, String propertyName) {
		String getterMethodName = "get" + StringUtils.capitalize(propertyName);
		return invokeMethod(target, getterMethodName, new Class[] {},
				new Object[] {});
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 */
	public static Object invokeMethod(final Object object,
			final String methodName, final Class<?>[] parameterTypes,
			final Object[] parameters) {
		Method method = getDeclaredMethod(object, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method ["
					+ methodName + "] parameterType " + parameterTypes
					+ " on target [" + object + "]");
		}

		method.setAccessible(true);

		try {
			return method.invoke(object, parameters);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 
	 * 调用Setter方法.使用value的Class来查找Setter方法.
	 */

	public static void invokeSetterMethod(Object target, String propertyName,
			Object value) {
		invokeSetterMethod(target, propertyName, value, null);
	}

	/**
	 * 调用Setter方法.
	 * 
	 * @param propertyType
	 *            用于查找Setter方法,为空时使用value的Class替代.
	 */
	public static void invokeSetterMethod(Object target, String propertyName,
			Object value, Class<?> propertyType) {
		Class<?> type = propertyType != null ? propertyType : value.getClass();
		String setterMethodName = "set" + StringUtils.capitalize(propertyName);
		invokeMethod(target, setterMethodName, new Class[] { type },

		new Object[] { value });

	}

	/**
	 * 强制转换fileld可访问.
	 */
	protected static void makeAccessible(final Field field) {
		if (!Modifier.isPublic(field.getModifiers())
				|| !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
	 */
	public static void setFieldValue(final Object object,
			final String fieldName, final Object value) {
		Field field = getDeclaredField(object, fieldName);

		if (field == null)
			throw new IllegalArgumentException("Could not find field ["
					+ fieldName + "] on target [" + object + "]");

		makeAccessible(field);

		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}
	}

	private ReflectionUtils() {
	}
}
