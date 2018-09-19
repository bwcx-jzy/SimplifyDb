package cn.simplifydb.database.base;

import cn.jiangzeyin.StringUtil;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.database.util.SqlUtil;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.builder.SQLSelectBuilder;
import com.alibaba.druid.sql.builder.impl.SQLSelectBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 读取数据
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public abstract class BaseRead<T> extends Base<T> implements SQLSelectBuilder {

    protected SQLSelectBuilderImpl sqlSelectBuilder = new SQLSelectBuilderImpl(JdbcConstants.MYSQL);

    /**
     * 返回值类型
     *
     * @author jiangzeyin
     */
    public enum Result {
        /**
         * 数组
         */
        JsonArray,
        /**
         * 返回单个实体 json 一行
         */
        JsonObject,
        /**
         * 返回实体新
         */
        Entity,
        /**
         * 原数据格式
         */
        ListMap,
        /**
         * 支持取一行数据
         * <p>
         * columns 确定取值的列名
         * <p>
         * 默认第一行第一列
         */
        String,
        Integer,
        /**
         * 分页信息查询
         */
        PageResultType,
        /**
         * 一列多行
         */
        ListOneColumn
    }

    protected BaseRead() {
        setThrows(true);
    }

    /**
     * 查询索引
     */
    private String index;
    /**
     * 参数
     */
    private List<Object> parameters;
    /**
     * 返回值类型
     */
    private Result resultType = Result.Entity;
    /**
     * 主键值
     */
    private Object keyValue;
    /**
     * 是否使用索引
     */
    private boolean useIndex;

    public boolean isUseIndex() {
        return useIndex;
    }

    public BaseRead setUseIndex(boolean useIndex) {
        this.useIndex = useIndex;
        return this;
    }

    /**
     * 自定义sql
     *
     * @param sql sql
     * @return this
     */
    public BaseRead setSql(String sql) {
        sqlSelectBuilder = new SQLSelectBuilderImpl(sql, JdbcConstants.MYSQL);
        return this;
    }

    /**
     * 设置主键列名
     * <p>
     * 默认为 id
     *
     * @param keyColumn 名称
     * @return this
     * @author jiangzeyin
     */
    public BaseRead setKeyColumn(String keyColumn) {
        sqlSelectBuilder.where(keyColumn + "=!keyValue");
        return this;
    }

    public Result getResultType() {
        return resultType;
    }


    /**
     * 设置查询主键值
     *
     * @param keyValue 键值
     * @return this
     * @author jiangzeyin
     */
    public BaseRead setKeyValue(Object keyValue) {
        this.keyValue = keyValue;
        return this;
    }


    public BaseRead setIsDelete(int isDelete) {
        sqlSelectBuilder.where(SystemColumn.Active.getColumn() + "=" + isDelete);
        return this;
    }

    public BaseRead setResultType(Result resultType) {
        this.resultType = resultType;
        return this;
    }

    public List<Object> getParameters() {
        if (parameters == null) {
            return new ArrayList<>();
        }
        return parameters;
    }

    /**
     * @param parameters 参数
     * @return this
     * @author jiangzeyin
     */
    public BaseRead setParameters(Object... parameters) {
        if (this.parameters == null) {
            this.parameters = new LinkedList<>();
        }
        if (parameters != null) {
            Collections.addAll(this.parameters, parameters);
        }
        return this;
    }

    public BaseRead setParameters(List<Object> whereParameters) {
        this.parameters = whereParameters;
        return this;
    }

    /**
     * 查询列 默认*
     *
     * @return 返回对应列，名
     * @author jiangzeyin
     */
    public String getColumns() {
        SQLSelectQueryBlock sqlSelectQueryBlock = sqlSelectBuilder.getSQLSelect().getFirstQueryBlock();
        if (sqlSelectQueryBlock == null) {
            return null;
        }
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        if (sqlSelectItems.size() == 1) {
            return sqlSelectItems.get(0).toString();
        }
        return null;
    }

    /**
     * 设置查询列
     * <p>
     * 默认所有 （*）
     *
     * @param columns 查询列
     * @return this
     * @author jiangzeyin
     */
    public BaseRead setColumns(String columns) {
        String[] array = StringUtil.stringToArray(columns, ",");
        sqlSelectBuilder.select(array);
        return this;
    }

    public String getIndex() {
        return index;
    }

    /**
     * 查询使用索引
     *
     * @param index 索引
     * @return this
     * @author jiangzeyin
     */
    public BaseRead setIndex(String index) {
        this.index = index;
        return this;
    }

    /**
     * 执行
     *
     * @param <t> 需要的类型
     * @return object
     */
    @SuppressWarnings("unchecked")
    public abstract <t> t run();

    /**
     * 获取as 后的列名
     *
     * @param name name
     * @return as 后
     */
    protected String getRealColumnName(String name) {
        if (StringUtil.isEmpty(name)) {
            return name;
        }
        String[] names = StringUtil.stringToArray(name, " ");
        if (names.length > 1) {
            return names[names.length - 1];
        }
        return name.trim();
    }

    /**
     * @author jiangzeyin
     */
    @Override
    protected void recycling() {
        // TODO Auto-generated method stub
        super.recycling();
        //connection = null;
        parameters = null;
        resultType = null;
        index = null;
        sqlSelectBuilder = null;
    }

    @Override
    public void setTableName(String tableName) {
        super.setTableName(tableName);
        sqlSelectBuilder.from(tableName);
    }

    protected String builder() {
        SQLSelectQueryBlock sqlSelectQueryBlock = sqlSelectBuilder.getSQLSelect().getFirstQueryBlock();

        if (sqlSelectQueryBlock == null || sqlSelectQueryBlock.getFrom() == null) {
            String tableName = SqlUtil.getTableName(this, getTclass());
            sqlSelectBuilder.from(tableName);
        }
        if (sqlSelectQueryBlock == null || sqlSelectQueryBlock.getSelectList().size() <= 0) {
            sqlSelectBuilder.select(SystemColumn.getDefaultSelectColumns());
        }
        String sql = sqlSelectBuilder.toString();
        if (keyValue != null) {
            sql = sql.replaceAll("!keyValue", keyValue.toString());
        }
        setRunSql(sql);
        return sql;
    }

    @Override
    public SQLSelectStatement getSQLSelectStatement() {
        return sqlSelectBuilder.getSQLSelectStatement();
    }

    @Override
    public SQLSelectBuilder select(String... column) {
        return sqlSelectBuilder.select(column);
    }

    @Override
    public SQLSelectBuilder selectWithAlias(String column, String alias) {
        return sqlSelectBuilder.selectWithAlias(column, alias);
    }

    @Override
    public SQLSelectBuilder from(String table) {
        return sqlSelectBuilder.from(table);
    }

    @Override
    public SQLSelectBuilder from(String table, String alias) {
        return sqlSelectBuilder.from(table, alias);
    }

    @Override
    public SQLSelectBuilder orderBy(String... columns) {
        return sqlSelectBuilder.orderBy(columns);
    }

    @Override
    public SQLSelectBuilder groupBy(String expr) {
        return sqlSelectBuilder.groupBy(expr);
    }

    @Override
    public SQLSelectBuilder having(String expr) {
        return sqlSelectBuilder.having(expr);
    }

    @Override
    public SQLSelectBuilder into(String expr) {
        return sqlSelectBuilder.into(expr);
    }

    @Override
    public SQLSelectBuilder limit(int rowCount) {
        return sqlSelectBuilder.limit(rowCount);
    }

    @Override
    public SQLSelectBuilder limit(int rowCount, int offset) {
        return sqlSelectBuilder.limit(rowCount, offset);
    }

    @Override
    public SQLSelectBuilder where(String sql) {
        return sqlSelectBuilder.where(sql);
    }

    @Override
    public SQLSelectBuilder whereAnd(String sql) {
        return sqlSelectBuilder.whereAnd(sql);
    }

    @Override
    public SQLSelectBuilder whereOr(String sql) {
        return sqlSelectBuilder.whereOr(sql);
    }
}
