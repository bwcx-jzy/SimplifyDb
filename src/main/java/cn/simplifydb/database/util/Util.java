package cn.simplifydb.database.util;

import cn.simplifydb.database.base.ReadBase;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.util.DbReflectUtil;
import cn.simplifydb.util.KeyMap;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.*;

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
     * @param <T>    泛型
     * @return 结果
     * @throws Exception 异常
     * @author jiangzeyin
     */
    public static <T> List<T> convertList(ReadBase<T> reBase, List<Map<String, Object>> list) throws Exception {
        Objects.requireNonNull(list, "list map");
        Objects.requireNonNull(reBase, "reBase");
        List<T> listR = new ArrayList<>();
        for (Map<String, Object> t : list) {
            listR.add(convertMap(reBase, t, null));
        }
        return listR;
    }

    /**
     * @param read     对象
     * @param map      map
     * @param refClass 类
     * @param <T>      参数
     * @return 实体
     * @throws Exception 异常
     */
    private static <T> T convertMap(ReadBase<T> read, Map<String, Object> map, Class<?> refClass) throws Exception {
        if (refClass == null) {
            refClass = read.getTclass();
        }
        // 创建 JavaBean 对象
        T obj = (T) refClass.newInstance();
        KeyMap<String, Object> keyMap = new KeyMap<>(map);
        HashMap<String, Class<?>> refMap = read.getRefMap();
        HashMap<String, String> refWhere = read.getRefWhere();
        List<String> remove = read.getRemove();
        // 给 JavaBean 对象的属性赋值
        List<Method> methods = DbReflectUtil.getAllSetMethods(obj.getClass());
        DataSource dataSource = DatabaseContextHolder.getReadDataSource(read.getTag());
        for (Method method : methods) {
            String name = method.getName();
            if (!name.startsWith("set")) {
                continue;
            }
            name = name.substring(3).toLowerCase();
            // 移除字段比较
            if (remove != null && remove.contains(name)) {
                continue;
            }
            if (SystemColumn.isReadRemove(name)) {
                continue;
            }
            Object value = keyMap.get(name);
            if (value == null) {
                continue;
            }
            // 判断外键
            if (refMap != null && refMap.containsKey(name)) {
                String where = refWhere == null ? null : refWhere.get(name);
                Class refMapClass = refMap.get(name);
                String sql = SqlUtil.getRefSql(refMapClass, read.getRefKey(), where);
                DbLog.getInstance().info(sql);
                List<Object> parameters = new ArrayList<>();
                parameters.add(value);
                List<Map<String, Object>> refList = JdbcUtils.executeQuery(dataSource, sql, parameters);
                if (refList != null && refList.size() > 0) {
                    Map<String, Object> refMap_data = refList.get(0);
                    Object refValue = convertMap(read, refMap_data, refMapClass);
                    try {
                        method.invoke(obj, refValue);
                    } catch (IllegalArgumentException e) {
                        DbLog.getInstance().error(String.format(obj.getClass() + " map转实体%s字段错误：%s -> %s", name, value.getClass(), value), e);
                    }
                }
                continue;
            }
            // 正常的字段
            Class<?>[] classes = method.getParameterTypes();
            if (classes == null || classes.length != 1) {
                throw new IllegalArgumentException(method + " 不符合规范");
            }
            Class pClass = classes[0];
            value = DbReflectUtil.convertType(value, pClass);
            try {
                method.invoke(obj, value);
            } catch (Exception e) {
                DbLog.getInstance().error(String.format(obj.getClass() + " map转实体%s字段错误：%s -> %s  %s", name, value.getClass(), value, pClass), e);
            }
        }
        return obj;
    }
}
