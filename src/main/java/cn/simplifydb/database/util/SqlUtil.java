package cn.simplifydb.database.util;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.DbWriteService;
import cn.simplifydb.database.annotation.EntityConfig;
import cn.simplifydb.database.annotation.FieldConfig;
import cn.simplifydb.database.base.Base;
import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.base.BaseWrite;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.database.run.write.Insert;
import cn.simplifydb.sequence.ICallbackSequence;
import cn.simplifydb.sequence.IQuietSequence;
import cn.simplifydb.sequence.ISequence;
import cn.simplifydb.sequence.SequenceConfig;
import cn.simplifydb.util.DbReflectUtil;
import com.alibaba.druid.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * sql 工具
 *
 * @author jiangzeyin
 */
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
        HashMap<String, String> systemMap = new HashMap<>();

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
                        // field.set(data, val);
//                        DbReflectUtil.setFieldValue(data, name, val);
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
                // DbReflectUtil.getFieldValue(data, name);
                Object va = field.get(data);
                // 密码字段
                if (SystemColumn.getPwdColumn().equalsIgnoreCase(name)) {
                    systemMap.put(name, "PASSWORD(?)");
                    values.add(va);
                } else {
                    // 读取外键
                    if (refMap != null && refMap.containsKey(name.toLowerCase())) {
                        //Object refData = DbReflectUtil.getFieldValue(data, field.getName());
                        //if (refData == null)
                        //  throw new RuntimeException(name + " 为null");
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
    static String getRefSql(Class<?> ref, String keyColumn, String where) {
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
}
