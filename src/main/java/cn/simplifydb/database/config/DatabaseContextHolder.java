package cn.simplifydb.database.config;

import cn.jiangzeyin.RandomUtil;
import cn.simplifydb.system.DbLog;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

/**
 * 链接池管理
 *
 * @author jiangzeyin
 */
public final class DatabaseContextHolder {
    private DatabaseContextHolder() {
    }

    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();
    private static Map<String, DataSource>[] MAPS;
    private static String[] tagNames;
    private static DatabaseOptType databaseOptType = DatabaseOptType.One;
    private static Map<String, DataSource> targetDataSourcesMap;

    /**
     * 数据源类型
     */
    public enum DatabaseOptType {
        /**
         * 单模式
         */
        One,
        /**
         * 倆
         */
        Two,
        /**
         * 多
         */
        More
    }

    public static String getConnectionTagName() {
        return THREAD_LOCAL.get();
    }

    public static void recycling() {
        THREAD_LOCAL.remove();
    }

    static void init(Map<String, DataSource>[] maps, String[] tagName) {
        Objects.requireNonNull(maps);
        if (maps.length == 0) {
            throw new IllegalArgumentException("数据库连接信息不能为空");
        }
        DatabaseContextHolder.MAPS = maps;
        DatabaseContextHolder.tagNames = tagName;
        if (maps.length == 1) {
            DatabaseContextHolder.databaseOptType = DatabaseOptType.One;
        } else if (maps.length == 2) {
            DatabaseContextHolder.databaseOptType = DatabaseOptType.Two;
        } else {
            DatabaseContextHolder.databaseOptType = DatabaseOptType.More;
        }
        DatabaseContextHolder.targetDataSourcesMap = MAPS[0];
        DbLog.getInstance().info(" 数据库操作：" + databaseOptType.toString());
    }

    static void init(Map<String, DataSource> map, String tagName) {
        Objects.requireNonNull(map);
        if (map.size() < 1) {
            throw new RuntimeException("数据库连接加载为空");
        }
        DatabaseContextHolder.targetDataSourcesMap = map;
        DatabaseContextHolder.tagNames = new String[]{tagName};
        DbLog.getInstance().info(" 数据库操作：" + databaseOptType.toString());
    }

    private static Map<String, DataSource> randMap() {
        int index = RandomUtil.getRandom(0, MAPS.length);
        THREAD_LOCAL.set(tagNames[index]);
        return MAPS[index];
    }

    public static DataSource getReadDataSource(String tag) {
        DataSource dataSource = null;
        if (databaseOptType == DatabaseOptType.One) {
            dataSource = targetDataSourcesMap.get(tag);
            THREAD_LOCAL.set(tagNames[0]);
        } else if (databaseOptType == DatabaseOptType.Two) {
            dataSource = MAPS[1].get(tag);
            THREAD_LOCAL.set(tagNames[1]);
        } else if (databaseOptType == DatabaseOptType.More) {
            dataSource = randMap().get(tag);
        }
        Objects.requireNonNull(dataSource, "没有找到对应数据源：" + tag);
        return dataSource;
    }

    public static DataSource getWriteDataSource(String tag) {
        DataSource dataSource = null;
        if (databaseOptType == DatabaseOptType.One) {
            dataSource = targetDataSourcesMap.get(tag);
            THREAD_LOCAL.set(tagNames[0]);
        } else if (databaseOptType == DatabaseOptType.Two) {
            dataSource = targetDataSourcesMap.get(tag);
            THREAD_LOCAL.set(tagNames[0]);
        } else if (databaseOptType == DatabaseOptType.More) {
            dataSource = randMap().get(tag);
        }
        Objects.requireNonNull(dataSource, "没有找到对应数据源：" + tag);
        return dataSource;
    }

    public static Connection getWriteConnection(String tag) throws SQLException {
        return getWriteDataSource(tag).getConnection();
    }
}
