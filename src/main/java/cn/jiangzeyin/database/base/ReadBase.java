package cn.jiangzeyin.database.base;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.database.config.SystemColumn;
import com.alibaba.druid.util.StringUtils;

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
public abstract class ReadBase<T> extends Base<T> {

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
        /**
         * 支持取一行数据
         * <p>
         * columns 确定取值的列名
         * <p>
         * 默认第一行第一列
         */
        Integer,
        /**
         * 分页信息查询
         */
        PageResultType;
    }

    protected ReadBase() {
        setThrows(true);
    }

    /**
     * 查询哪些列
     */
    protected String columns;
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
    private int isDelete = SystemColumn.Active.NO_ACTIVE;
    /**
     * 主键值
     */
    private Object keyValue;
    /**
     * 主键列
     */
    private String keyColumn;
    /**
     * 条件
     */
    private String where;
    /**
     * 是否使用索引
     */
    private boolean useIndex;

    public boolean isUseIndex() {
        return useIndex;
    }

    public ReadBase setUseIndex(boolean useIndex) {
        this.useIndex = useIndex;
        return this;
    }

    public String getWhere() {
        return where;
    }

    public ReadBase setWhere(String where) {
        this.where = where;
        return this;
    }

    public ReadBase appendWhere(String where) {
        String temp = StringUtil.convertNULL(this.where);
        where = StringUtil.convertNULL(where);
        this.where = String.format("%s %s", temp, where);
        return this;
    }

    /**
     * 获取主键列
     *
     * @return key
     * @author jiangzeyin
     */
    public String getKeyColumn() {
        if (StringUtils.isEmpty(keyColumn)) {
            return SystemColumn.getDefaultKeyName();
        }
        return keyColumn;
    }

    /**
     * 设置主键列名
     * <p>
     * 默认为 id
     *
     * @param keyColumn 名称
     * @author jiangzeyin
     */
    public ReadBase setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
        return this;
    }

    /**
     * 获取主键值
     *
     * @return 键值
     * @author jiangzeyin
     */
    public Object getKeyValue() {
        return keyValue;
    }

    /**
     * 设置查询主键值
     *
     * @param keyValue 键值
     * @author jiangzeyin
     */
    public ReadBase setKeyValue(Object keyValue) {
        this.keyValue = keyValue;
        return this;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public ReadBase setIsDelete(int isDelete) {
        this.isDelete = isDelete;
        return this;
    }

    protected Result getResultType() {
        return resultType;
    }

    public ReadBase setResultType(Result resultType) {
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
     * @author jiangzeyin
     */
    public ReadBase setParameters(Object... parameters) {
        if (this.parameters == null) {
            this.parameters = new LinkedList<>();
        }
        if (parameters != null) {
            Collections.addAll(this.parameters, parameters);
        }
        return this;
    }

    public ReadBase setParameters(List<Object> whereParameters) {
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
        if (StringUtils.isEmpty(columns)) {
            return SystemColumn.getDefaultSelectColumns();
        }
        return columns;
    }

    /**
     * 设置查询列
     * <p>
     * 默认所有 （*）
     *
     * @param columns 查询列
     * @author jiangzeyin
     */
    public ReadBase setColumns(String columns) {
        this.columns = columns;
        return this;
    }

    public String getIndex() {
        return index;
    }

    /**
     * 查询使用索引
     *
     * @param index 索引
     * @author jiangzeyin
     */
    public ReadBase setIndex(String index) {
        this.index = index;
        return this;
    }

    @SuppressWarnings("unchecked")
    public abstract <t> t run();

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
        columns = null;
        index = null;
    }
}
