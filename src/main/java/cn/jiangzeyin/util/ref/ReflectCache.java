package cn.jiangzeyin.util.ref;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射缓存工具类
 *
 * @author jiangzeyin
 */
public final class ReflectCache {
    private final static ConcurrentHashMap<String, Field> fieldMap = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, Field[]> fieldsMap = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, Method> methodMap = new ConcurrentHashMap<>();

    /**
     * 获取class 所有字段
     *
     * @param cls 类
     * @return 结果
     * @author jiangzeyin
     */
    public static Field[] getDeclaredFields(Class<?> cls) {
        Field[] fields = fieldsMap.get(cls.getName());
        if (fields == null) {
            fields = cls.getDeclaredFields();
            fieldsMap.put(cls.getName(), fields);
        }
        return fields;
    }

    /**
     * 获取class 字段
     *
     * @param cls  cls
     * @param name name
     * @return 字段
     * @throws NoSuchFieldException 异常
     * @throws SecurityException    异常
     * @author jiangzeyin
     */
    public static Field getDeclaredField(Class<?> cls, String name) throws NoSuchFieldException, SecurityException {
        Field field = fieldMap.get(cls.getName() + "." + name);
        if (field == null) {
            field = cls.getDeclaredField(name);
            field.setAccessible(true);
            fieldMap.put(cls.getName() + "." + name, field);
        }
        return field;
    }

    /**
     * 获取class 方法
     *
     * @param cls            cls
     * @param name           name
     * @param parameterTypes type
     * @return jieg
     * @throws NoSuchMethodException 异常
     * @throws SecurityException     异常
     * @author jiangzeyin
     */
    public static Method getDeclaredMethod(Class<?> cls, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        String mapname = cls.getName() + "." + name + "." + ParameterTypestoString(parameterTypes);
        Method method = methodMap.get(name);
        if (method == null) {
            method = cls.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            methodMap.put(mapname, method);
        }
        return method;
    }

    /**
     * 将参数转字符串名称
     *
     * @param parameterTypes 类型
     * @return 结果
     * @author jiangzeyin
     */
    private static String ParameterTypestoString(Class<?>... parameterTypes) {
        if (parameterTypes == null)
            return "";
        String name = "";
        for (Class<?> class1 : parameterTypes) {
            name = class1.getName() + ",";
        }
        return name;
    }
}
