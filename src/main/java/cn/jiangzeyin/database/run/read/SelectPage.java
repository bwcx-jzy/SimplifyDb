package cn.jiangzeyin.database.run.read;

import cn.jiangzeyin.database.DbWriteService;
import cn.jiangzeyin.database.Page;
import cn.jiangzeyin.database.base.ReadBase;
import cn.jiangzeyin.database.config.DatabaseContextHolder;
import cn.jiangzeyin.database.util.SqlUtil;
import cn.jiangzeyin.database.util.Util;
import cn.jiangzeyin.system.DbLog;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.sql.DataSource;
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

    /**
     * @return 结果
     * @author jiangzeyin
     */
    @SuppressWarnings({"hiding", "unchecked"})
    @Override
    public <T> T run() {
        // TODO Auto-generated method stub
        Objects.requireNonNull(page, "page");
        try {
            String tag = getTag();
            if (StringUtils.isEmpty(tag)) {
                tag = DbWriteService.getInstance().getDatabaseName(getTclass());
                setTag(tag);
            }
            String[] pageSql;
            if (StringUtils.isEmpty(page.getSql()))
                pageSql = SqlUtil.getSelectPageSql(this);
            else
                pageSql = SqlUtil.getSelectPageSql(page);
            DataSource dataSource = DatabaseContextHolder.getReadDataSource(tag);
            List<Map<String, Object>> list;
            { // 查询数据总数
                list = JdbcUtils.executeQuery(dataSource, pageSql[0], getParameters());
                if (list == null || list.size() < 1)
                    return null;
                Map<String, Object> count_map = list.get(0);
                if (count_map == null)
                    return null;
                long count = (Long) count_map.values().toArray()[0];
                page.setTotalRecord(count);
            }
            { // 查询数据
                setRunSql(pageSql[1]);
                DbLog.getInstance().info(getTransferLog() + pageSql[1]);
                list = JdbcUtils.executeQuery(dataSource, pageSql[1], getParameters());
                page.setMapList(list);
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
                List<?> result_list = Util.convertList(this, list);
                page.setResultsT(result_list);
                return (T) result_list;
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
}
