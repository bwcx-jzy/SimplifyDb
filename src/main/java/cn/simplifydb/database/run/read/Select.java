package cn.simplifydb.database.run.read;

import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.config.SystemColumn;
import cn.simplifydb.database.util.JdbcUtil;
import cn.simplifydb.database.util.SqlUtil;
import cn.simplifydb.util.Util;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询数据库操作
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public class Select<T> extends BaseRead<T> {

    /**
     * 事务的链接信息
     */
    private Connection transactionConnection;

    /**
     * 指定链接操作
     *
     * @param transactionConnection connection
     */
    public Select(Connection transactionConnection) {
        this.transactionConnection = transactionConnection;
        setThrows(true);
    }

    public Select() {
    }

    public Select(int isDelete) {
        sqlSelectBuilder.where(SystemColumn.Active.getColumn() + "=" + isDelete);
    }


    public <t> t run(Result resultType) {
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
                limit(1);
            }
            String tag = getTag();
            String runSql = builder();
            DbLog.getInstance().info(getTransferLog(4) + getRunSql());
            List<Map<String, Object>> result;
            if (transactionConnection == null) {
                DataSource dataSource = DatabaseContextHolder.getReadDataSource(tag);
                result = JdbcUtils.executeQuery(dataSource, runSql, getParameters());
            } else {
                result = JdbcUtils.executeQuery(transactionConnection, runSql, getParameters());
            }
            // 判断是否开启还原
            if (isUnescapeHtml()) {
                JdbcUtil.htmlUnescape(result);
            }
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
                    return (t) SqlUtil.convertList(this, result);
                case ListMap:
                    return (t) result;
                case String:
                case Integer: {
                    if (Util.checkListMapNull(result)) {
                        return null;
                    }
                    Map<String, Object> map = result.get(0);
                    String column = getColumns();
                    if (SystemColumn.getDefaultSelectColumns().equals(column)) {
                        // 默认取第一行一列数据
                        return (t) map.values().toArray()[0];
                    }
                    // 取指定列
                    if (column != null) {
                        // 只有一列自动返回对应数据类型
                        column = getRealColumnName(column);
                        return (t) map.get(column);
                    }
                    return (t) map;
                }
                case ListOneColumn:
                    String column = getColumns();
                    if (column == null) {
                        throw new IllegalArgumentException(Result.ListOneColumn + " must set one columns");
                    }
                    column = getRealColumnName(column);
                    List<t> list = new ArrayList<>(result.size());
                    for (Map<String, Object> map : result) {
                        list.add((t) map.get(column));
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
        limit(1);
        setResultType(Result.Entity);
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
