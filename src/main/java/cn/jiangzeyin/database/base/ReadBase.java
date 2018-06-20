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
public abstract class ReadBase<T> extends Base<T> {

    /**
     * 返回值类型
     *
     * @author jiangzeyin
     */
    public enum Result {
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

    protected String columns; // 查询哪些列
    private String index; // 查询索引
    private List<Object> parameters; // 参数
    /**
     * 返回值类型
     */
    private Result resultType = Result.Entity;
    private int isDelete = SystemColumn.Active.NO_ACTIVE;
    // 主键值
    private Object keyValue;
    // 主键列
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

    public void setUseIndex(boolean useIndex) {
        this.useIndex = useIndex;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void appendWhere(String where) {
        String temp = StringUtil.convertNULL(this.where);
        where = StringUtil.convertNULL(where);
        this.where = String.format("%s %s", temp, where);
    }

    /**
     * 获取主键列
     *
     * @return key
     * @author jiangzeyin
     */
    public String getKeyColumn() {
        if (StringUtils.isEmpty(keyColumn))
            return SystemColumn.getDefaultKeyName();
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
    public void setKeyColumn(String keyColumn) {
        this.keyColumn = keyColumn;
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
    public void setKeyValue(Object keyValue) {
        this.keyValue = keyValue;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    protected Result getResultType() {
        return resultType;
    }

    public void setResultType(Result resultType) {
        this.resultType = resultType;
    }

    public List<Object> getParameters() {
        if (parameters == null)
            return new ArrayList<>();
        return parameters;
    }

    /**
     * @param parameters 参数
     * @author jiangzeyin
     */
    public void setParameters(Object... parameters) {
        if (this.parameters == null)
            this.parameters = new LinkedList<>();
        if (parameters != null)
            Collections.addAll(this.parameters, parameters);
    }

    public void setParameters(List<Object> whereParameters) {
        this.parameters = whereParameters;
    }

    /**
     * 查询列 默认*
     *
     * @return 返回对应列，名
     * @author jiangzeyin
     */
    public String getColumns() {
        if (StringUtils.isEmpty(columns))
            return SystemColumn.getDefaultSelectColumns();
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
    public void setColumns(String columns) {
        this.columns = columns;
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
    public void setIndex(String index) {
        this.index = index;
    }

    @SuppressWarnings("hiding")
    public abstract <T> T run();

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
