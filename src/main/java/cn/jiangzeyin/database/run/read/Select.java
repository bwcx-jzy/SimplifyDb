package cn.jiangzeyin.database.run.read;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.database.DbWriteService;
import cn.jiangzeyin.database.base.ReadBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.config.SystemColumn;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.database.util.Util;
import cn.jiangzeyin.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询数据库操作
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public class Select<T> extends ReadBase<T> {

    private String orderBy;
    private String sql;
    /**
     * 查询数据重多少开始
     */
    private int limitStart;
    /**
     * 查询数据个数
     */
    private int limitCount;


    public int getLimitStart() {
        return limitStart;
    }

    /**
     * 设置查询开始位置
     *
     * @param limitStart 开始行
     * @return this
     * @author jiangzeyin
     */
    public Select setLimitStart(int limitStart) {
        this.limitStart = limitStart;
        return this;
    }

    public int getLimitCount() {
        return limitCount;
    }

    /**
     * 设置查询数量
     *
     * @param limitCount 共几行
     * @return this
     * @author jiangzeyin
     */
    public Select setLimitCount(int limitCount) {
        this.limitCount = limitCount;
        return this;
    }

    public Select() {
        // setTclass(DbReflectUtil.getTClass(this));
    }

    public Select(int isDelete) {
        setIsDelete(isDelete);
    }

    public Select(String tag) {
        super.setTag(tag);
    }


    public String getOrderBy() {
        return orderBy;
    }

    public Select setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void setSql(String sql, Result resultType) {
        this.sql = sql;
        setResultType(resultType);
    }

    public T run(Result resultType) {
        setResultType(resultType);
        return run();
    }

    /**
     * 查询
     *
     * @return 结果
     * @author jiangzeyin
     */
    @Override
    public <t> t run() {
        try {
            if (getResultType() == Result.JsonObject) {
                setLimitCount(1);
            }
            String tag = getTag() == null ? DbWriteService.getInstance().getDatabaseName(getTclass()) : getTag();
            setTag(tag);
            DataSource dataSource = DatabaseContextHolder.getReadDataSource(tag);
            String runSql = getSql();
            if (StringUtils.isEmpty(runSql)) {
                runSql = SqlUtil.getSelectSql(this);
            }
            setRunSql(runSql);
            DbLog.getInstance().info(getTransferLog() + runSql);
            List<Map<String, Object>> result = JdbcUtils.executeQuery(dataSource, runSql, getParameters());
            switch (getResultType()) {
                case JsonArray:
                    return (t) JSON.toJSON(result);
                case JsonObject: {
                    if (result.size() < 1) {
                        return null;
                    }
                    Map<String, Object> map = result.get(0);
                    return (t) new JSONObject(map);
                }
                case Entity:
                    return (t) Util.convertList(this, result);
                case ListMap:
                    return (t) result;
                case String:
                case Integer: {
                    if (result.size() < 1) {
                        return null;
                    }
                    Map<String, Object> map = result.get(0);
                    if (map == null) {
                        return null;
                    }
                    //Object object = null;
                    String column = getColumns();
                    if (SystemColumn.getDefaultSelectColumns().equals(column)) {
                        // 默认取第一行一列数据
                        return (t) map.values().toArray()[0];
                    }
                    // 取指定列
                    String[] columns = StringUtil.stringToArray(column, ",");
                    if (columns != null && columns.length == 1) {
                        // 只有一列自动返回对应数据类型
                        columns[0] = getRealColumnName(columns[0]);
                        return (t) map.get(columns[0]);
                    }
                    return (t) map;
                }
                case ListOneColumn:
                    String[] columns = StringUtil.stringToArray(getColumns(), ",");
                    if (columns == null || columns.length != 1) {
                        throw new IllegalArgumentException(Result.ListOneColumn + " must set one columns");
                    }
                    columns[0] = getRealColumnName(columns[0]);
                    List<t> list = new ArrayList<>(result.size());
                    for (Map<String, Object> map : result) {
                        list.add((t) map.get(columns[0]));
                    }
                    return (t) list;
                default:
                    return (t) result;
            }
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return null;
    }

    /**
     * 查询一条数据 返回实体 会自动追加 limit 1
     *
     * @return 结果
     * @author jiangzeyin
     */
    public T runOne() {
        setLimitCount(1);
        List<T> list = run();
        if (list == null) {
            return null;
        }
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
