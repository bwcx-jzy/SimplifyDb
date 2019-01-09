package cn.simplifydb.database.util;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.DbWriteService;
import cn.simplifydb.database.annotation.EntityConfig;
import cn.simplifydb.database.annotation.FieldConfig;
import cn.simplifydb.database.base.Base;
import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.database.run.write.Insert;
import cn.simplifydb.sequence.ICallbackSequence;
import cn.simplifydb.sequence.IQuietSequence;
import cn.simplifydb.sequence.ISequence;
import cn.simplifydb.sequence.SequenceConfig;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.util.DbReflectUtil;
import cn.simplifydb.util.KeyMap;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * sql 工具
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public final class SqlUtil {

    /**
     * 判断是否写
     *
     * @param field 字段
     * @return boolean
     * @author jiangzeyin
     */
    private static boolean isWrite(Field field) {
        return field.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL) && field.getModifiers() != (Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL) && !field.getName().startsWith("_");
    }

    /**
     * @param write write
     * @param data  数据
     * @return 结果对象
     * @throws IllegalArgumentException yi
     * @throws IllegalAccessException   yic
     * @author jiangzeyin
     */
    public static SqlAndParameters getWriteSql(BaseWrite<?> write, Object data) throws Exception {
        if (data == null) {
            data = write.getData();
        }
        Objects.requireNonNull(data, String.format("%s", write.getTclass(false)));

        List<String> columns = new ArrayList<>(20);
        List<Object> values = new ArrayList<>(20);
        HashMap<String, String> systemMap = new HashMap<>(20);

        List<String> remove = write.getRemove();
        HashMap<String, Class<?>> refMap = write.getRefMap();
        boolean isInsert = write instanceof Insert;

        Class classT = data.getClass();
        EntityConfig entityConfig = null;
        List<String> insertColumns = null;
        if (isInsert) {
            entityConfig = (EntityConfig) classT.getAnnotation(EntityConfig.class);
            if (entityConfig != null) {
                String insertColumns2 = entityConfig.insertColumns();
                String[] columns2 = StringUtil.stringToArray(insertColumns2, ",");
                if (columns2 != null) {
                    insertColumns = new ArrayList<>();
                    for (String columnItem : columns2) {
                        insertColumns.add(columnItem.toLowerCase());
                    }
                }
            }
        }
        List<?> fieldList = DbReflectUtil.getDeclaredFields(classT);
        for (Object object : fieldList) {
            Field field = (Field) object;
            if (!isWrite(field)) {
                continue;
            }
            String name = field.getName();
            // 判断排除字段
            if (remove != null && remove.contains(name.toLowerCase())) {
                continue;
            }
            FieldConfig fieldConfig = null;
            // 系统默认不可以操作
            if (SystemColumn.isWriteRemove(name)) {
                if (insertColumns == null || !insertColumns.contains(name.toLowerCase())) {
                    fieldConfig = field.getAnnotation(FieldConfig.class);
                    if (fieldConfig == null) {
                        continue;
                    }
                    Class<? extends ISequence> sequenceCls = fieldConfig.sequence();
                    ISequence sequence = SequenceConfig.parseSequence(sequenceCls);
                    if (sequence == null) {
                        continue;
                    }
                }
            }
            // 去掉mark 字段
            if (entityConfig != null) {
                if (!entityConfig.baseMark()) {
                    if ("mark".equals(name)) {
                        continue;
                    }
                }
                if (!entityConfig.update() && name.equalsIgnoreCase(SystemColumn.Modify.getColumn())) {
                    continue;
                }
            }
            // 判断insert 注解
            if (isInsert) {
                if (fieldConfig == null) {
                    fieldConfig = field.getAnnotation(FieldConfig.class);
                }
                // 获取字段属性
                if (fieldConfig != null) {
                    String insertDelValue = fieldConfig.insertDefValue();
                    if (!StringUtil.isEmpty(insertDelValue)) {
                        columns.add(name);
                        systemMap.put(name, insertDelValue);
                        continue;
                    }
                    Class<? extends ISequence> sequenceCls = fieldConfig.sequence();
                    ISequence sequence = SequenceConfig.parseSequence(sequenceCls);
                    if (sequence != null) {
                        columns.add(name);
                        String val;
                        if (IQuietSequence.class.isAssignableFrom(sequenceCls)) {
                            IQuietSequence iQuietSequence = (IQuietSequence) sequence;
                            val = iQuietSequence.nextId();
                        } else if (ICallbackSequence.class.isAssignableFrom(sequenceCls)) {
                            ICallbackSequence iCallbackSequence = (ICallbackSequence) sequence;
                            val = iCallbackSequence.nextId(classT, name);
                        } else {
                            throw new IllegalArgumentException("not find AssignableFrom");
                        }
                        values.add(val);
                        DbReflectUtil.setFieldValue(data, name, val);
                        continue;
                    }
                }
            } else {
                // 修改
                if (SystemColumn.notCanUpdate(name)) {
                    continue;
                }
                if (fieldConfig == null) {
                    fieldConfig = field.getAnnotation(FieldConfig.class);
                }
                if (fieldConfig != null) {
                    Class<? extends ISequence> sequenceCls = fieldConfig.sequence();
                    ISequence sequence = SequenceConfig.parseSequence(sequenceCls);
                    if (sequence != null) {
                        continue;
                    }
                }
            }
            columns.add(name);
            // 判断是否为系统字段
            String value1 = SystemColumn.getDefaultValue(name);
            if (value1 == null) {
                Object va = field.get(data);
                // 密码字段
                if (SystemColumn.getPwdColumn().equalsIgnoreCase(name)) {
                    systemMap.put(name, "PASSWORD(?)");
                    values.add(va);
                } else {
                    // 读取外键
                    if (refMap != null && refMap.containsKey(name.toLowerCase())) {
                        va = DbReflectUtil.getFieldValue(va, write.getRefKey());
                    }
                    values.add(va);
                }
            } else {
                systemMap.put(name, value1);
            }
        }
        SqlAndParameters sqlAndParameters = new SqlAndParameters();
        sqlAndParameters.setParameters(values);
        sqlAndParameters.setColumns(columns);
        sqlAndParameters.setSystemMap(systemMap);
        return sqlAndParameters;
    }

    /**
     * @param insert 对象
     * @return 结果数组
     * @throws IllegalArgumentException y
     * @throws IllegalAccessException   y
     * @author jiangzeyin
     */
    public static SqlAndParameters[] getInsertSqls(Insert<?> insert) throws Exception {
        List<?> list = insert.getList();
        SqlAndParameters[] andParameters = new SqlAndParameters[list.size()];
        for (int i = 0; i < andParameters.length; i++) {
            Object object = list.get(i);
            if (object == null) {
                continue;
            }
            SqlAndParameters sqlAndParameters = getWriteSql(insert, object);
            int isDelete = SystemColumn.Active.NO_ACTIVE;
            if (!StringUtils.isEmpty(SystemColumn.Active.getColumn())) {
                EntityConfig entityConfig = object.getClass().getAnnotation(EntityConfig.class);
                if (entityConfig == null || entityConfig.active()) {
                    Object isDeleteF = DbReflectUtil.getFieldValue(object, SystemColumn.Active.getColumn());
                    if (isDeleteF != null) {
                        isDelete = Integer.parseInt(isDeleteF.toString());
                    }
                }
            }
            sqlAndParameters.setIsDelete(isDelete);
            andParameters[i] = sqlAndParameters;
        }
        return andParameters;
    }

    /**
     * 获取读取外键的sql 语句
     *
     * @param ref       类
     * @param keyColumn 列
     * @return sql
     * @author jiangzeyin
     */
    private static String getRefSql(Class<?> ref, String keyColumn, String where) {
        StringBuilder sql = new StringBuilder("select ")
                .append(" * from ")
                .append(getTableName(null, ref))
                .append(" where ")
                .append(keyColumn)
                .append("=?");
        if (!StringUtils.isEmpty(where)) {
            sql.append(" and ").append(where);
        }
        return sql.toString();
    }

    /**
     * 获取表明 和 自动加主键索引
     *
     * @param base 类
     * @return 表名
     * @author jiangzeyin
     */
    public static String getTableName(Base base, Class cls) {
        if (base != null) {
            // 读取索引信息
            boolean isIndex = false;
            String index = null;
            if (base instanceof BaseRead) {
                BaseRead baseRead = (BaseRead) base;
                index = baseRead.getIndex();
                isIndex = baseRead.isUseIndex();
            }
            return DbWriteService.getInstance().getTableName(base.getTclass(), isIndex, index, base.isUseDataBaseName());
        }
        return DbWriteService.getInstance().getTableName(cls, false, null, false);
    }

    /**
     * 获取运行sql function
     *
     * @param functionName 名称
     * @param parameters   参数
     * @return 结果
     * @author jiangzeyin
     */
    public static String function(String functionName, List<Object> parameters) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ").append(functionName).append("(");
        if (parameters != null && parameters.size() > 0) {
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("?");
            }
        }
        sb.append(")");
        return sb.toString();
    }

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
    public static <T> List<T> convertList(BaseRead<T> reBase, List<Map<String, Object>> list) throws Exception {
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
    private static <T> T convertMap(BaseRead<T> read, Map<String, Object> map, Class<?> refClass) throws Exception {
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
                if (refList.size() > 0) {
                    Map<String, Object> refMapData = refList.get(0);
                    Object refValue = convertMap(read, refMapData, refMapClass);
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
            if (classes.length != 1) {
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
