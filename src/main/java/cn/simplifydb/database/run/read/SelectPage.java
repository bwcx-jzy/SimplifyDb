package cn.simplifydb.database.run.read;

import cn.simplifydb.database.DbWriteService;
import cn.simplifydb.database.Page;
import cn.simplifydb.database.base.ReadBase;
import cn.simplifydb.database.config.DatabaseContextHolder;
import cn.simplifydb.database.util.SqlUtil;
import cn.simplifydb.database.util.Util;
import cn.simplifydb.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 分页查询
 *
 * @author jiangzeyin
 */
public class SelectPage<T> extends ReadBase<T> {

    private Page<T> page;


    /**
     * 分页基本使用
     *
     * @param page 分页对象
     */
    public SelectPage(Page<T> page) {
        // TODO Auto-generated constructor stub
        this.page = page;
    }

    /**
     * @param page       page
     * @param resultType 返回类型
     */
    public SelectPage(Page<T> page, Result resultType) {
        this.page = page;
        setResultType(resultType);
    }

    public Page<T> getPage() {
        return page;
    }

    public void setPage(Page<T> page) {
        this.page = page;
    }

    @Override
    public ReadBase setWhere(String where) {
        appendWhere(where);
        return this;
    }

    @Override
    public ReadBase appendWhere(String where) {
        throw new IllegalArgumentException("where please use page");
    }

    /**
     * @return 结果
     * @author jiangzeyin
     */
    @SuppressWarnings({"hiding", "unchecked"})
    @Override
    public <T> T run() {
        // TODO Auto-generated method stub
        Objects.requireNonNull(page, "page");
        String errorSql = null;
        try {
            String tag = getTag();
            if (StringUtils.isEmpty(tag)) {
                tag = DbWriteService.getInstance().getDatabaseName(getTclass());
                setTag(tag);
            }
            String[] pageSql;
            if (StringUtils.isEmpty(page.getSql())) {
                pageSql = SqlUtil.getSelectPageSql(this);
            } else {
                pageSql = SqlUtil.getSelectPageSql(page);
            }
            DataSource dataSource = DatabaseContextHolder.getReadDataSource(tag);
            List<Map<String, Object>> list;
            long count = 0;
            { // 查询数据总数
                errorSql = pageSql[0];
                list = JdbcUtils.executeQuery(dataSource, pageSql[0], getParameters());
                if (list.size() > 0) {
                    Map<String, Object> countMap = list.get(0);
                    if (countMap != null) {
                        count = (Long) countMap.values().toArray()[0];
                    }
                }
                page.setTotalRecord(count);
            }
            if (count > 0) {
                // 查询数据
                setRunSql(pageSql[1]);
                DbLog.getInstance().info(getTransferLog() + pageSql[1]);
                errorSql = null;
                list = JdbcUtils.executeQuery(dataSource, pageSql[1], getParameters());
            } else {
                list = new ArrayList<>();
            }
            page.setMapList(list);
            {
                if (getResultType() == Result.JsonArray) {
                    return (T) JSON.toJSON(list);
                }
                // 结果是分页数据
                if (getResultType() == Result.PageResultType) {
                    JSONObject data = new JSONObject();
                    data.put("results", list);
                    data.put("pageNo", page.getPageNo());
                    data.put("pageSize", page.getPageSize());
                    data.put("totalPage", page.getTotalPage());
                    data.put("totalRecord", page.getTotalRecord());
                    return (T) data;
                }
                List<?> resultList = Util.convertList(this, list);
                page.setResultsT(resultList);
                return (T) resultList;
            }
        } catch (Exception e) {
            // TODO: handle exception
            if (errorSql != null) {
                DbLog.getInstance().info(getTransferLog() + errorSql);
            }
            isThrows(e);
        } finally {
            runEnd();
            recycling();
        }
        return null;
    }
}
