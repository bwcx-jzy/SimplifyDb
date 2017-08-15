package cn.jiangzeyin.database.util;

import cn.jiangzeyin.database.base.ReadBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.system.SystemDbLog;
import cn.jiangzeyin.util.Assert;
import cn.jiangzeyin.util.ref.ReflectUtil;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具类
 *
 * @author jiangzeyin
 */
public class Util {


    /**
     * 将list map 转javabean
     *
     * @param reBase base
     * @param list   list
     * @return 结果
     * @throws Exception 异常
     * @author jiangzeyin
     */
    public static <T> List<T> convertList(ReadBase<T> reBase, List<Map<String, Object>> list) throws Exception {
        Assert.notNull(list, "list map");
        Assert.notNull(reBase, "reBase");
        List<T> list_r = new ArrayList<T>();
        for (Map<String, Object> t : list) {
            list_r.add(convertMap(reBase, t, null));
        }
        return list_r;
    }

    /**
     * @param read     对象
     * @param map      map
     * @param refClass 类
     * @param <T>      参数
     * @return 实体
     * @throws Exception 异常
     */
    public static <T> T convertMap(ReadBase<T> read, Map<String, Object> map, Class<?> refClass) throws Exception {
        if (refClass == null)
            refClass = read.getTclass();
        T obj = (T) refClass.newInstance();// 创建 JavaBean 对象
        if (obj == null)
            return null;
        HashMap<String, Class<?>> refMap = read.getRefMap();
        HashMap<String, String> refWhere = read.getRefWhere();
        List<String> remove = read.getRemove();
        // 给 JavaBean 对象的属性赋值
        List<Method> methods = ReflectUtil.getAllSetMethods(obj.getClass());// .getDeclaredMethods();
        DataSource dataSource = DatabaseContextHolder.getReadDataSource(read.getTag());
        for (Method method : methods) {
            String name = method.getName();
            if (!name.startsWith("set"))
                continue;
            name = name.substring(3).toLowerCase();
            // 移除字段比较
            if (remove != null && remove.contains(name.toLowerCase()))
                continue;
            Object value = map.get(name);
            if (value == null) {
                value = map.get(name.toUpperCase());
                if (value == null)
                    continue;
            }
            // 判断外键
            if (refMap != null && refMap.containsKey(name)) {
                String where = refWhere == null ? null : refWhere.get(name);
                Class refMapClass = refMap.get(name);
                String sql = SqlUtil.getRefSql(refMapClass, read.getRefKey(), where);
                SystemDbLog.getInstance().info(sql);
                List<Object> parameters = new ArrayList<>();
                parameters.add(value);
                List<Map<String, Object>> refList = JdbcUtils.executeQuery(dataSource, sql, parameters);
                if (refList != null && refList.size() > 0) {
                    Map<String, Object> refMap_data = refList.get(0);
                    Object refValue = convertMap(read, refMap_data, refMapClass);
                    //System.out.println(refValue);
                    try {
                        method.invoke(obj, refValue);
                        //ReflectUtil.setFieldValue(obj, name, refValue);
                    } catch (IllegalArgumentException e) {
                        SystemDbLog.getInstance().error(String.format(obj.getClass() + " map转实体%s字段错误：%s -> %s", name, value.getClass(), value), e);
                    }
                }
                continue;
            }
            // 正常的字段
            try {
                method.invoke(obj, value);
            } catch (IllegalArgumentException ie) {
                // 判断关系没有配置
                Class<?>[] classes = method.getParameterTypes();
                Class pClass = classes[0];
                if (pClass == String.class) {
                    method.invoke(obj, String.valueOf(value.toString()));
                } else if (pClass == Integer.class) {
                    method.invoke(obj, (Integer) value);
                } else {
                    SystemDbLog.getInstance().error(String.format(obj.getClass() + " map转实体%s字段类型错误：%s -> %s", name, value.getClass(), value), ie);
                }
            } catch (Exception e) {
                SystemDbLog.getInstance().error(String.format(obj.getClass() + " map转实体%s字段错误：%s -> %s", name, value.getClass(), value), e);
            }
        }
        return obj;
    }
//    public static <T> T convertMap(ReadBase<T> read, Map<String, Object> map, Class<?> refClass) throws Exception {
//        //BeanInfo beanInfo = Introspector.getBeanInfo(read.getTclass()); // 获取类属性
//        T obj = (T) (refClass == null ? read.getTclass().newInstance() : refClass.newInstance());// 创建 JavaBean 对象
//        if (obj == null)
//            return null;
//        CaseInsensitiveMap refMap = read.getRefMap();
//        CaseInsensitiveMap refWhere = read.getRefWhere();
//        List<String> remove = read.getRemove();
//        // 给 JavaBean 对象的属性赋值
//        List<Method> methods = ReflectUtil.getAllSetMethods(obj.getClass());// .getDeclaredMethods();
//        Assert.notNull(methods);
//        CaseInsensitiveMap caseInsensitiveMap = new CaseInsensitiveMap(map);
//        DataSource dataSource = DatabaseContextHolder.getReadDataSource(read.getTag());
//        for (Method method : methods) {
//            String name = method.getName();
//            if (name.length() <= 3) {
//                continue;
//            }
//            name = name.substring(3);
//            // 移除字段比较
//            if (remove != null && remove.contains(name.toLowerCase()))
//                continue;
//            Object value = caseInsensitiveMap.get(name);
//            if (value == null) {
//                continue;
//            }
//            // 判断外键
//            if (refMap != null && refMap.containsKey(name)) {
//                String where = refWhere == null ? null : (String) refWhere.get(name);
//                Class refMapClass = (Class<?>) refMap.get(name);
//                String sql = SqlUtil.getRefSql(refMapClass, read.getRefKey(), where);
//                // SystemLog.SystemLog(LogType.sql, sql);
//                SystemLog.LOG(LogType.SQL).info(sql);
//                List<Object> parameters = new ArrayList<>();
//                parameters.add(value);
//                List<Map<String, Object>> refList = JdbcUtils.executeQuery(dataSource, sql, parameters);
//                //System.out.println(refList);
//                if (refList != null && refList.size() > 0) {
//                    Map<String, Object> refMap_data = refList.get(0);
//                    Object refValue = convertMap(read, refMap_data, refMapClass);
//                    //System.out.println(refValue);
//                    try {
//                        method.invoke(obj, refValue);
//                        //ReflectUtil.setFieldValue(obj, name, refValue);
//                    } catch (IllegalArgumentException e) {
//                        SystemLog.ERROR().error(String.format(obj.getClass() + " map转实体%s字段错误：%s -> %s", name, value.getClass(), value), e);
//                    }
//                }
//                continue;
//            }
//            // 判断关系没有配置
//            Class<?>[] classes = method.getParameterTypes();
//            Class pClass = classes[0];
//            if (BaseEntity.class.isAssignableFrom(pClass)) {
//                SystemLog.LOG(LogType.SQL).info(String.format("请配置 %s 字段关系%s", name, pClass));
//                continue;
//            }
//            // 正常的字段
//            try {
//                method.invoke(obj, value);
//            } catch (IllegalArgumentException ie) {
//                Class[] types = method.getParameterTypes();
//                Class parameterType = types[0];
//                if (parameterType == String.class) {
//                    method.invoke(obj, String.valueOf(value.toString()));
//                } else if (parameterType == Integer.class) {
//                    method.invoke(obj, (Integer) value);
//                } else {
//                    SystemLog.LOG(LogType.SQL_ERROR).error(String.format(obj.getClass() + " map转实体%s字段类型错误：%s -> %s", name, value.getClass(), value), ie);
//                }
//            } catch (Exception e) {
//                SystemLog.LOG(LogType.SQL_ERROR).error(String.format(obj.getClass() + " map转实体%s字段错误：%s -> %s", name, value.getClass(), value), e);
//            }
//        }
//        return obj;
//    }
}
