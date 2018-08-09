package cn.jiangzeyin.database.util;

import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * jdbc 操作util
 *
 * @author jiangzeyin
 */
public class JdbcUtil {

    /**
     * 添加操作 返回主键
     *
     * @param dataSource 数据源
     * @param sql        sql
     * @param parameters 参数
     * @return 主键
     * @throws SQLException 异常
     * @author jiangzeyin
     */
    public static Object executeInsert(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return executeInsert(conn, sql, parameters);
        } finally {
            JdbcUtils.close(conn);
        }
    }

    /**
     * 添加操作 返回主键
     *
     * @param conn       链接
     * @param sql        sql
     * @param parameters 参数
     * @return 主键
     * @throws SQLException 异常
     * @author jiangzeyin
     */
    public static Object executeInsert(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            setParameters(stmt, parameters);
            int updateCount = stmt.executeUpdate();
            if (updateCount > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getObject(1);
                }
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
        return null;
    }

    /**
     * 设置 jdbc PreparedStatement 参数
     *
     * @param stmt       p
     * @param parameters 参数
     * @throws SQLException 异常
     * @author jiangzeyin
     */
    private static void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        for (int i = 0, size = parameters.size(); i < size; ++i) {
            Object param = parameters.get(i);
            stmt.setObject(i + 1, param);
        }
    }
}
