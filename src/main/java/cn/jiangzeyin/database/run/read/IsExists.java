package cn.jiangzeyin.database.run.read;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.database.DbWriteService;
import cn.jiangzeyin.database.base.ReadBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 判断是否存在
 *
 * @author jiangzeyin
 */
@SuppressWarnings("unchecked")
public class IsExists<T> extends ReadBase<T> {
    private int limit;

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public List<Object> getParameters() {
        List<Object> paList = super.getParameters();
        paList.add(0, getKeyValue());
        //paList.add(getKeyValue());
        //if (parameters != null)
        //  paList.addAll(parameters);
        return paList;
    }

    public IsExists(String keyColumn, String keyValue) {
        this(true);
        setKeyValue(keyValue);
        setKeyColumn(keyColumn);
    }

    public IsExists() {
        setThrows(true);
    }

    /**
     * 异常是否抛出
     *
     * @param isThrows isThrows
     */
    public IsExists(boolean isThrows) {
        setThrows(isThrows);
    }

    public IsExists(String tag) {
        setTag(tag);
    }

    /**
     * 判断是否存在
     *
     * @return 是否存在
     * @author jiangzeyin
     */
    public boolean runBoolean() {
        try {
            List<Map<String, Object>> list = doData();
            if (list == null || list.size() < 1) {
                return false;
            }
            Map<String, Object> map = list.get(0);
            Object object = map.get("countSum");
            if (object == null) {
                throw new RuntimeException("查询结果没有countSum");
            }
            if (object instanceof Long) {
                Long count = (Long) object;
                return count > 0L;
            }
            if (object instanceof Integer) {
                Integer count = (Integer) object;
                return count > 0;
            }
            throw new RuntimeException("查询结果类型异常" + object);
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return true;

    }

    private List<Map<String, Object>> doData() throws SQLException {
        Class runClass = getTclass();
        synchronized (runClass.getName()) {
            if (StringUtils.isEmpty(getKeyColumn())) {
                throw new IllegalArgumentException(" keyColumn 不能为null");
            }
            String tag = getTag();
            if (StringUtils.isEmpty(tag)) {
                tag = DbWriteService.getInstance().getDatabaseName(runClass);
            }
            String sql = SqlUtil.getIsExistsSql(this, runClass, getKeyColumn(), getWhere());
            setRunSql(sql);
            DbLog.getInstance().info(getTransferLog() + sql);
            DataSource dataSource = DatabaseContextHolder.getReadDataSource(tag);
            return JdbcUtils.executeQuery(dataSource, sql, getParameters());
        }
    }

    @Override
    public String getColumns() {
        return columns;
    }

    /**
     * 查询是否存在
     *
     * @return 结果
     * @author jiangzeyin
     */
    @Override
    public <T> T run() {
        try {
            List<Map<String, Object>> list = doData();
            if (list == null || list.size() < 1) {
                return null;
            }
            Map<String, Object> map = list.get(0);
            String[] keys = StringUtil.stringToArray(getColumns(), ",");
            if (keys == null || keys.length <= 0) {
                return (T) map.get("countSum");
            }
            if (keys.length == 1) {
                String[] array = StringUtil.stringToArray(keys[0]);
                if (array != null && array.length >= 3 && "as".equalsIgnoreCase(array[array.length - 2])) {
                    return (T) map.get(array[array.length - 1]);
                }
                return (T) map.get(keys[0]);
            }
            return (T) map;
        } catch (Exception e) {
            // TODO: handle exception
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return null;
    }

}
