package cn.simplifydb.database.base;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.SystemClock;
import cn.simplifydb.database.DbWriteService;
import cn.simplifydb.database.config.DataSourceConfig;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.util.DbReflectUtil;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.builder.impl.SQLDeleteBuilderImpl;
import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import com.alibaba.druid.util.StringUtils;

import java.util.*;

/**
 * 数据库操作公共
 *
 * @author jiangzeyin
 */
public abstract class Base<T> {
    private static final Map<Class, Object> OBJECT_MAP = new HashMap<>();

    /**
     * 事件接口的单利对象
     *
     * @param tclass class
     * @return object
     */
    public static Object getObject(Class tclass) {
        Object object = OBJECT_MAP.computeIfAbsent(tclass, aClass -> {
            try {
                return aClass.newInstance();
            } catch (InstantiationException | IllegalAccessException ignored) {
            }
            return null;
        });
        Objects.requireNonNull(object, tclass + " newInstance error");
        return object;
    }

    /**
     * 参数
     */
    private List<Object> parameters = new ArrayList<>();
    /**
     * 异常是否抛出
     */
    private boolean isThrows;
    /**
     * 外键表
     */
    private HashMap<String, Class<?>> refMap;
    private HashMap<String, String> refWhere;
    /**
     * 外键列名
     */
    private String refKey;
    /**
     * 排除不操作字段
     */
    private List<String> remove;
    /**
     * 数据库标示
     */
    private String tag;
    /**
     * 数据库对应class
     */
    private Class<?> tclass;
    private long runTime;
    private String runSql;
    private String tempTransferLog;
    /**
     * 操作的对应tag
     */
    private String tagName;

    /**
     * 是否使用数据库名
     */
    private boolean useDataBaseName;
    /**
     * 主键值
     */
    Object keyValue;
    String keyColumn;

    /**
     * 创建时生成开始运行时间
     */
    Base() {
        // TODO Auto-generated constructor stub
        runTime = System.currentTimeMillis();
    }

    public Object getKeyValue() {
        return keyValue;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    /**
     * 查询的主键
     *
     * @param keyValue 主键值
     * @return this
     */
    public abstract Base<T> setKeyValue(Object keyValue);

    /**
     * 查询的主键值和列名
     *
     * @param column   列名
     * @param keyValue 值
     * @return this
     */
    public abstract Base<T> setKeyColumnAndValue(String column, Object keyValue);

    public List<Object> getParameters() throws Exception {
        return parameters;
    }

    public Base<T> addParameters(Object... object) {
        parameters.addAll(Arrays.asList(object));
        return this;
    }

    public Base<T> setParameters(List<Object> whereParameters) {
        this.parameters = whereParameters;
        return this;
    }

    public boolean isUseDataBaseName() {
        return useDataBaseName;
    }

    public void setUseDataBaseName(boolean useDataBaseName) {
        this.useDataBaseName = useDataBaseName;
    }

    private String getTagName() {
        if (tagName == null) {
            return DatabaseContextHolder.getConnectionTagName();
        }
        return tagName;
    }

    private void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public HashMap<String, String> getRefWhere() {
        return refWhere;
    }


    /**
     * 生成sql
     *
     * @return sql
     * @throws Exception 实体读取
     */
    protected abstract String builder() throws Exception;

    protected void setRunSql(String runSql) {
        if (!StringUtil.isEmpty(runSql)) {
            runSql = runSql.replaceAll("[\r\n]", " ");
        }
        this.runSql = runSql;
    }

    /**
     * 获取运行的sql
     *
     * @return sql
     */
    public String getRunSql() {
        return runSql;
    }

    protected void runEnd() {
        if (DataSourceConfig.SQL_TIMEOUT <= 0) {
            return;
        }
        long time = SystemClock.now() - runTime;
        if (time > DataSourceConfig.SQL_TIMEOUT) {
            String tagName = getTagName();
            DbLog.getInstance().warn(tagName + "执行时间过长：" + time + "  " + runSql);
        }
    }


    /**
     * 返回操作的泛型类
     *
     * @return class
     */
    public Class<?> getTclass() {
        return getTclass(true);
    }

    /**
     * 获取tclass
     *
     * @param getRef true 获取泛型
     * @return 返回当前操作的泛型
     */
    public Class<?> getTclass(boolean getRef) {
        if (tclass == null && getRef) {
            tclass = DbReflectUtil.getTClass(getClass());
        }
        return tclass;
    }

    public Base<T> setTclass(Class<?> tclass) {
        this.tclass = tclass;
        return this;
    }

    public String getTag() {
        if (StringUtil.isEmpty(tag)) {
            return getTag(getTclass());
        }
        return tag;
    }

    public String getTag(Class cls) {
        if (StringUtil.isEmpty(tag)) {
            tag = DbWriteService.getInstance().getDatabaseName(cls);
        }
        return tag;
    }

    public Base<T> setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public List<String> getRemove() {
        return remove;
    }

    /**
     * 设置排除获取字段
     *
     * @param remove 要移除的字段
     * @author jiangzeyin
     */
    public Base<T> setRemove(String... remove) {
        if (remove == null) {
            return this;
        }
        if (this.remove == null) {
            this.remove = new LinkedList<>();
        }
        for (String item : remove) {
            if (!this.remove.contains(item)) {
                this.remove.add(item.toLowerCase());
            }
        }
        return this;
    }

    public String getRefKey() {
        if (StringUtils.isEmpty(refKey)) {
            return SystemColumn.getDefaultRefKeyName();
        }
        return refKey;
    }

    /**
     * 设置外键的列
     *
     * @param refKey 外键的列
     * @author jiangzeyin
     */
    public Base<T> setRefKey(String refKey) {
        this.refKey = refKey;
        return this;
    }

    public HashMap<String, Class<?>> getRefMap() {
        return refMap;
    }


    /**
     * 添加外键 关系
     *
     * @param name     外键名称
     * @param refClass 外键类
     * @author jiangzeyin
     */
    public Base<T> putRefClass(String name, Class<?> refClass) {
        if (refMap == null) {
            refMap = new HashMap<>(5);
        }
        refMap.put(name.toLowerCase(), refClass);
        return this;
    }

    public Base<T> putRefClass(String name, Class<?> refClass, String where) {
        if (refMap == null) {
            refMap = new HashMap<>(5);
        }
        if (refWhere == null) {
            refWhere = new HashMap<>(5);
        }
        refMap.put(name, refClass);
        refWhere.put(name, where);
        return this;
    }

    public boolean isThrows() {
        return isThrows;
    }

    public Base<T> setThrows(boolean isThrows) {
        this.isThrows = isThrows;
        return this;
    }

    /**
     * 处理异常
     *
     * @param t 异常信息
     * @author jiangzeyin
     */
    protected void isThrows(Throwable t) {
        if (isThrows) {
            throw new RuntimeException(t);
        }
        DbLog.getInstance().error("执行数据库操作", t);
    }

    /**
     * keyValue 和防止整表更新操作
     *
     * @param object object
     */
    protected void securityCheck(Object object) {
        if (object instanceof SQLUpdateBuilderImpl) {
            SQLUpdateBuilderImpl sqlUpdateBuilder = (SQLUpdateBuilderImpl) object;
            // key and value
            if (keyColumn != null) {
                if (keyValue == null) {
                    sqlUpdateBuilder.whereAnd(keyColumn + " = null");
                } else {
                    sqlUpdateBuilder.whereAnd(String.format("%s='%s'", keyColumn, keyValue));
                }
            }
            SQLUpdateStatement sqlUpdateStatement = sqlUpdateBuilder.getSQLUpdateStatement();
            // 防止整表更新
            if (sqlUpdateStatement == null || sqlUpdateStatement.getWhere() == null) {
                throw new RuntimeException("没有任何条件");
            }
        } else if (object instanceof SQLDeleteBuilderImpl) {
            SQLDeleteBuilderImpl sqlDeleteBuilder = (SQLDeleteBuilderImpl) object;
            // key and value
            if (keyColumn != null) {
                if (keyValue == null) {
                    sqlDeleteBuilder.whereAnd(keyColumn + " = null");
                } else {
                    sqlDeleteBuilder.whereAnd(String.format("%s='%s'", keyColumn, keyValue));
                }
            }
            SQLDeleteStatement sqlDeleteStatement = sqlDeleteBuilder.getSQLDeleteStatement();
            // 防止整表删除
            if (sqlDeleteStatement == null || sqlDeleteStatement.getWhere() == null) {
                throw new RuntimeException("没有任何条件");
            }
        }
    }

    /**
     * 回收对象信息
     *
     * @author jiangzeyin
     */
    protected void recycling() {
        refMap = null;
        refKey = null;
        refWhere = null;
        remove = null;
        tag = null;
        tclass = null;
        runSql = null;
        runTime = 0L;
        tagName = null;
        this.parameters = null;
        keyValue = null;
        keyColumn = null;
        tempTransferLog = null;
        // tag 标记
        DatabaseContextHolder.recycling();
    }

    protected String getTransferLog() {
        if (tempTransferLog != null) {
            return tempTransferLog;
        }
        return DataSourceConfig.isActive() ? "" : getLine();
    }

    private String getLine() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        return String.format("[%s-%s-%s]", StringUtil.simplifyClassName(stackTraceElement.getClassName()), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
    }

    protected void getAsyncLog() {
        if (DataSourceConfig.isActive()) {
            tempTransferLog = "";
        } else {
            tempTransferLog = getLine();
        }
        setTagName(DatabaseContextHolder.getConnectionTagName());
    }

    @Override
    public String toString() {
        return "Base{" +
                "parameters=" + parameters +
                ", isThrows=" + isThrows +
                ", refMap=" + refMap +
                ", refWhere=" + refWhere +
                ", refKey='" + refKey + '\'' +
                ", remove=" + remove +
                ", tag='" + tag + '\'' +
                ", tclass=" + tclass +
                ", runTime=" + runTime +
                ", runSql='" + runSql + '\'' +
                ", tempTransferLog='" + tempTransferLog + '\'' +
                ", tagName='" + tagName + '\'' +
                ", useDataBaseName=" + useDataBaseName +
                ", keyValue=" + keyValue +
                ", keyColumn='" + keyColumn + '\'' +
                '}';
    }
}
