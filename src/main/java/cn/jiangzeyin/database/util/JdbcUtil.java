package cn.jiangzeyin.database.util;

import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * jdbc 操作util
 * 
 * @author jiangzeyin
 * @date 2016-10-10
 * 
 */
public class JdbcUtil {

	/**
	 * 添加操作 返回主键
	 * 
	 * @author jiangzeyin
	 * @date 2016-10-12
	 * 
	 * @param dataSource
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	public static long executeInsert(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
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
	 * @author jiangzeyin
	 * @date 2016-10-12
	 * 
	 * @param conn
	 * @param sql
	 * @param parameters
	 * @return
	 * @throws SQLException
	 */
	public static long executeInsert(Connection conn, String sql, List<Object> parameters) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		int updateCount;
		try {
			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			setParameters(stmt, parameters);
			
			updateCount = stmt.executeUpdate();

			if (updateCount > 0) {
				rs = stmt.getGeneratedKeys();
				if (rs.next()) {
					return rs.getLong(1);
				}
			}
		} finally {
			JdbcUtils.close(rs);
			JdbcUtils.close(stmt);
		}

		return updateCount;
	}

	/**
	 * 设置 jdbc PreparedStatement 参数
	 * 
	 * @author jiangzeyin
	 * @date 2016-10-12
	 * 
	 * @param stmt
	 * @param parameters
	 * @throws SQLException
	 */
	private static void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
		for (int i = 0, size = parameters.size(); i < size; ++i) {
			Object param = parameters.get(i);
			stmt.setObject(i + 1, param);
		}
	}
}
