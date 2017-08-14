package cn.jiangzeyin.database.base;

import com.alibaba.druid.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 读取数据
 *
 * @author jiangzeyin
 * @date 2016-10-12
 */
public abstract class ReadBase<T> extends Base<T> {

    /**
     * 返回值类型
     *
     * @author jiangzeyin
     * @date 2016-10-12
     */
    public static class Result {
        public static final int JsonArray = 1;
        /**
         * 返回单个实体 json 一行
         */
        public static final int JsonObject = 6;
        public static final int Entity = 0;
        public static final int ListMap = 2;
        /**
         * 支持取一行数据
         * <p>
         * columns 确定取值的列名
         * <p>
         * 默认第一行第一列
         */
        public static final int String = 3;
        /**
         * 支持取一行数据
         * <p>
         * columns 确定取值的列名
         * <p>
         * 默认第一行第一列
         */
        public static final int Integer = 5;
        //public static final int Array = 4;
    }

    private String columns; // 查询哪些列
    private String index; // 查询索引
    private List<Object> parameters; // 参数
    private int ResultType; // 返回值类型
    private int isDelete = -1;

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    protected int getResultType() {
        return ResultType;
    }

    public void setResultType(int resultType) {
        ResultType = resultType;
    }


    public List<Object> getParameters() {
        if (parameters == null)
            return new ArrayList<>();
        return parameters;
    }

    /**
     * 设置查询参数
     *
     * @param parameters
     * @author jiangzeyin
     * @date 2016-10-13
     */
    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * @param parameters_
     * @author jiangzeyin
     * @date 2016-10-20
     */
    public void setParameters(Object... parameters_) {
        if (parameters == null)
            parameters = new LinkedList<>();
        Collections.addAll(parameters, parameters_);
    }

    /**
     * 查询列 默认*
     *
     * @return
     * @author jiangzeyin
     * @date 2016-10-20
     */
    public String getColumns() {
        if (StringUtils.isEmpty(columns))
            return "*";
        return columns;
    }

    /**
     * @return
     * @author jiangzeyin
     * @date 2016-10-13
     */
    @Override
    public List<String> getRemove() {
        // TODO Auto-generated method stub
        setRemove("pwd");
        return super.getRemove();
    }

    /**
     * 设置查询列
     * <p>
     * 默认所有 （*）
     *
     * @return
     * @author jiangzeyin
     * @date 2016-10-13
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
     * @param index
     * @author jiangzeyin
     * @date 2016-10-13
     */
    public void setIndex(String index) {
        this.index = index;
    }

    @SuppressWarnings("hiding")
    public abstract <T> T run();

    /**
     * @author jiangzeyin
     * @date 2016-11-21
     */
    @Override
    protected void recycling() {
        // TODO Auto-generated method stub
        super.recycling();
        //connection = null;
        parameters = null;
        ResultType = -1;
        columns = null;
        index = null;
    }
}
