package cn.jiangzeyin.util.ref;


import cn.jiangzeyin.util.Assert;
import com.alibaba.druid.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * 利用反射进行操作的一个工具类
 *
 * @author jiangzeyin
 * @date 2016-8-31
 */
public class ReflectUtil {
    /**
     * 利用反射获取指定对象的指定属性
     *
     * @param obj       目标对象
     * @param fieldName 目标属性
     * @return 目标属性的值
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object getFieldValue(Object obj, String fieldName) throws IllegalArgumentException, IllegalAccessException {
        Assert.notNull(obj);
        Object result = null;
        Field field = ReflectUtil.getField(obj.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            result = field.get(obj);
        }
        return result;
    }

    /**
     * @param class1
     * @param class2
     * @return
     */
    public static boolean isSuperclass(Class class1, Class class2) {
        for (Class<?> calzz = class1; calzz != Object.class; calzz = calzz.getSuperclass()) {
            if (calzz == class2)
                return true;
        }
        return false;
    }

    public static Annotation getFieldAnnotation(Class<?> cls, String name, Class<? extends Annotation> annotationClass) throws IllegalArgumentException, IllegalAccessException {
        Field field = getField(cls, name);
        if (field == null)
            return null;
        return field.getAnnotation(annotationClass);
    }

    public static Annotation getFieldAnnotation(Class<?> cls, Class<? extends Annotation> annotationClass) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = ReflectCache.getDeclaredFields(cls);
        if (fields == null)
            return null;
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation == null)
                continue;
            return annotation;
        }
        return null;
    }

    public static List<String> getAnnotationFieldNames(Class<?> cls, Class<? extends Annotation> annotationClass) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = ReflectCache.getDeclaredFields(cls);
        if (fields == null)
            return null;
        List<String> fields2 = new ArrayList<>();
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClass);
            if (annotation == null)
                continue;
            fields2.add(field.getName());
        }
        return fields2;
    }

    /**
     * 调用 get方法
     *
     * @param obj
     * @param fieldName
     * @return
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @author jiangzeyin
     * @date 2016-8-9
     */
    public static Object getMethodValue(Object obj, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // 自动 驼峰命名
        fieldName = captureName(fieldName);
        Method method = getMethod(obj.getClass(), "get" + fieldName);// obj.getClass().getMethod();
        if (method == null)
            throw new IllegalArgumentException(String.format("没有找到%s 对应get 方法", fieldName));
        return method.invoke(obj);
    }

    public static String captureName(String inString) {
        if (StringUtils.isEmpty(inString))
            return "";
        if (inString.length() > 1)
            return inString.substring(0, 1).toUpperCase() + inString.substring(1);
        return inString.toUpperCase();

    }

    /**
     * 利用反射获取指定对象里面的指定属性
     *
     * @param cls       目标对象
     * @param fieldName 目标属性
     * @return 目标字段
     */
    public static Field getField(Class<?> cls, String fieldName) {
        Field field = null;
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = ReflectCache.getDeclaredField(clazz, fieldName);// clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
                continue;
            }
        }
        return field;
    }

    /**
     * 获取方法 包含 父类
     *
     * @param cls
     * @param methodName
     * @param parameterTypes
     * @return
     * @author jiangzeyin
     * @date 2016-9-19
     */
    public static Method getMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = ReflectCache.getDeclaredMethod(clazz, methodName, parameterTypes);
                break;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                continue;
            }
        }
        return method;
    }

    /**
     * 利用反射设置指定对象的指定属性为指定的值
     *
     * @param obj        目标对象
     * @param fieldName  目标属性
     * @param fieldValue 目标值
     */
    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) throws IllegalAccessException {
        Field field = ReflectUtil.getField(obj.getClass(), fieldName);
        if (field != null) {
            field.setAccessible(true);
            Class type = field.getType();
            String typeName = type.getSimpleName();
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
     * 自定义排序
     *
     * @param accountList
     *            要排序的集合
     * @param orderField
     *            更具什么属性排序
     * @param orderDirection
     *            排序的方式
     */
    // public static void sortDate(List<?> list, String orderField,
    // String orderDirection) {
    // if (!"desc".equals(orderDirection) && !"asc".equals(orderDirection))
    // return;
    // final String methodName = "get" + orderField;
    // final String type = orderDirection;
    // Collections.sort(list, new Comparator<Object>() {
    // public int compare(Object a, Object b) {
    // int ret = 0;
    // try {
    // Method m = a.getClass().getDeclaredMethod(methodName, null);
    // try {
    // String aStr = (String) m.invoke(a, null);
    // String bStr = (String) m.invoke(b, null);
    // ret = aStr.compareTo(bStr);
    // ret = "asc".equals(type) ? ret : -ret;
    // } catch (IllegalArgumentException e) {
    // e.printStackTrace();
    // } catch (IllegalAccessException e) {
    // e.printStackTrace();
    // } catch (InvocationTargetException e) {
    // e.printStackTrace();
    // }
    //
    // } catch (SecurityException e) {
    // e.printStackTrace();
    // } catch (NoSuchMethodException e) {
    // e.printStackTrace();
    // }
    // return ret;
    // }
    // });
    // }

    /**
     * 自定义排序
     *
     * @param list
     * @param orderField
     * @param orderDirection
     */
    public static void sortString(List<?> list, String orderField, String orderDirection) {
        if (!"desc".equals(orderDirection) && !"asc".equals(orderDirection))
            return;
        final String methodName = "get" + orderField;
        final String type = orderDirection;
        Collections.sort(list, new Comparator<Object>() {
            public int compare(Object a, Object b) {
                int ret = 0;
                Method m = null;
                Class<?> a1 = a.getClass();
                for (; ; ) {
                    try {
                        m = a1.getDeclaredMethod(methodName);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        a1 = a1.getSuperclass();
                        if (a1 == null)
                            return ret;
                        continue;
                    }
                    break;
                }
                try {
                    Integer aStr = (Integer) m.invoke(a);
                    Integer bStr = (Integer) m.invoke(b);
                    ret = aStr.compareTo(bStr);
                    ret = "asc".equals(type) ? ret : -ret;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ret;
            }
        });
    }

    /**
     * 获取对象的泛型
     *
     * @param obj
     * @return
     * @author jiangzeyin
     * @date 2016-8-15
     */
    public static Class<?> getTClass(Object obj) {
        Assert.notNull(obj, "obj 不能为空");
        return getTClass(obj.getClass());
    }

    public static Class<?> getTClass(Class<?> cls) {
        Type type = cls.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getActualTypeArguments()[0];
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * list 转map
     *
     * @param key_
     * @param list
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @author jiangzeyin
     * @date 2016-8-29
     */
    @SuppressWarnings("unchecked")
    public static <V, T> HashMap<T, V> mapToList(List<V> list, Class<T> key_, String key) throws IllegalArgumentException, IllegalAccessException {
        if (list == null)
            return null;
        HashMap<T, V> map = new HashMap<T, V>();
        if (list.size() < 1)
            return map;
        for (V v : list) {
            map.put((T) getFieldValue(v, key), v);
        }
        return map;
    }

    public static List<Method> getAllGetMethods(Class cls) {
        return getAllMethods(cls, "get");
    }

    public static List<Method> getAllSetMethods(Class cls) {
        return getAllMethods(cls, "set");
    }

    private static List<Method> getAllMethods(Class cls, String prefix) {
        Assert.notNull(cls);
        List<Method> list = new ArrayList<>();
        for (Class<?> clazz = cls; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith(prefix)) {
                    list.add(method);
                }
            }
        }
        return list;
    }
}