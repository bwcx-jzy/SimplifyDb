package cn.jiangzeyin.util;

import cn.jiangzeyin.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 利用反射进行操作的一个工具类
 *
 * @author jiangzeyin
 */
public class DbReflectUtil {
    /**
     * 利用反射获取指定对象的指定属性
     *
     * @param obj       目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     * @throws IllegalAccessException   一些
     * @throws IllegalArgumentException 异常
     */
    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Assert.notNull(obj);
        Field field = null;
        NoSuchFieldException noSuchFieldException = null;
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
                noSuchFieldException = ignored;
            }
        }
        if (field == null) {
            assert noSuchFieldException != null;
            throw noSuchFieldException;
        }
        return field.get(obj);
    }

    /**
     * 获取class 所有字段
     *
     * @param cls 类
     * @return 结果
     * @author jiangzeyin
     */
    public static Field[] getDeclaredFields(Class<?> cls) {
        String key = cls.getName() + "_DeclaredFields";
        Object object = ReflectCache.get(key);
        if (object instanceof Field[])
            return (Field[]) object;
        Field[] fields = cls.getDeclaredFields();
        ReflectCache.put(key, fields);
        return fields;
    }

    /**
     * 利用反射获取指定对象里面的指定属性
     *
     * @param cls       目标对象
     * @param fieldName 目标属性
     * @return 目标字段
     */
    private static Field getField(Class<?> cls, String fieldName) throws NoSuchFieldException {
        String key = cls.getName() + "_" + fieldName;
        Object object = ReflectCache.get(key);
        if (object instanceof Field)
            return (Field) object;
        Field field = null;
        NoSuchFieldException noSuchFieldException = null;
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                break;
            } catch (NoSuchFieldException ignored) {
                noSuchFieldException = ignored;
            }
        }
        if (field == null) {
            assert noSuchFieldException != null;
            throw noSuchFieldException;
        }
        ReflectCache.put(key, field);
        return field;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     *
     * @param obj        目标对象
     * @param fieldName  目标属性
     * @param fieldValue 目标值
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = getField(obj.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            Class type = field.getType();
            if (type == int.class) {
                field.set(obj, Integer.parseInt(fieldValue.toString()));
            } else if (type == double.class) {
                field.set(obj, Double.parseDouble(fieldValue.toString()));
            } else if (type == String.class) {
                if (fieldValue == null)
                    field.set(obj, "");
                else
                    field.set(obj, fieldValue.toString());
            } else if (type == long.class) {
                field.set(obj, Long.parseLong(fieldValue.toString()));
            } else if (type == Integer.class) {
                field.set(obj, Integer.valueOf(fieldValue.toString()));
            } else {
                field.set(obj, fieldValue);
            }
        }

    }

    /**
     * 获取对象的泛型
     *
     * @return 结果
     * @author jiangzeyin
     */
    public static Class<?> getTClass(Class<?> cls) {
        Type type = cls.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            return (Class<?>) type;
        }
        return null;
    }

    public static List<Method> getAllSetMethods(Class cls) {
        return getAllMethods(cls, "set");
    }

    private static List<Method> getAllMethods(Class cls, String prefix) {
        Assert.notNull(cls);
        String key = cls.getName() + "_" + prefix;
        Object object = ReflectCache.get(key);
        if (object instanceof List)
            return (List<Method>) object;
        List<Method> list = new ArrayList<>();
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith(prefix)) {
                    list.add(method);
                }
            }
        }
        ReflectCache.put(key, list);
        return list;
    }

    private static final class ReflectCache {
        private final static ConcurrentHashMap<String, Field> fieldMap = new ConcurrentHashMap<>();
        private final static ConcurrentHashMap<String, Field[]> fieldsMap = new ConcurrentHashMap<>();
        private final static ConcurrentHashMap<String, Object> CACHE = new ConcurrentHashMap<>();

        static void put(String key, Object object) {
            CACHE.put(key, object);
        }

        static Object get(String key) {
            return CACHE.get(key);
        }
    }
}