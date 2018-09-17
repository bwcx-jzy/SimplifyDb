package cn.simplifydb.database.config;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.des.SystemKey;
import cn.simplifydb.system.DbLog;
import cn.simplifydb.util.PropertiesParser;
import cn.simplifydb.util.ResourceUtil;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * 数据源配置信息
 *
 * @author jiangzeyin
 */
public final class DataSourceConfig {
    private static boolean active;
    private static PropertiesParser systemPropertiesParser;

    public static long SQL_TIMEOUT = 2000L;

    public static boolean isActive() {
        return active;
    }

    private DataSourceConfig() {

    }

    /**
     * 配置对象初始化
     *
     * @param props props
     * @throws Exception e
     */
    public static void init(Properties props) throws Exception {
        Objects.requireNonNull(props);
        systemPropertiesParser = new PropertiesParser(props);
        String active = systemPropertiesParser.getStringProperty(ConfigProperties.ACTIVE, "dev");
        DataSourceConfig.active = "prod".equals(active);
        String[] sourceTags = systemPropertiesParser.getStringArrayProperty(ConfigProperties.PROP_SOURCE_TAG);
        Objects.requireNonNull(sourceTags, "sourceTag is blank");
        if (sourceTags.length < 1) {
            throw new IllegalArgumentException("sourceTag is blank");
        }
        String[] configPaths = systemPropertiesParser.getStringArrayProperty(ConfigProperties.PROP_CONFIG_PATH);
        Objects.requireNonNull(configPaths, "configPath is blank");
        if (configPaths.length < 1) {
            throw new IllegalArgumentException("configPath is blank");
        }
        dataSource(sourceTags, configPaths);
        //
        ModifyUser.initModify(systemPropertiesParser.getPropertyGroup(ConfigProperties.PROP_LAST_MODIFY));
        //
        ModifyUser.initCreate(systemPropertiesParser.getPropertyGroup(ConfigProperties.PROP_CREATE));
        //  系统字段
        SystemColumn.init(systemPropertiesParser.getPropertyGroup(ConfigProperties.PROP_SYSTEM_COLUMN));
        SQL_TIMEOUT = StringUtil.parseLong(systemPropertiesParser.getStringProperty(ConfigProperties.LOG_RUN_TIMEOUT_SQL_TIME, "2000"));

    }

    /**
     * 指定文件初始化
     *
     * @param propertyPath 路径
     * @throws Exception e
     */
    public static void init(String propertyPath) throws Exception {
        if (StringUtil.isEmpty(propertyPath)) {
            throw new IllegalArgumentException("propertyPath is null ");
        }
        InputStream inputStream = ResourceUtil.getResource(propertyPath);
        Properties props = new Properties();
        props.load(inputStream);
        init(props);
    }

    private static void dataSource(String[] sourceTags, String[] configPaths) throws Exception {
        DbLog.getInstance().info("初始化连接数据库");
        if (configPaths.length == 1) {
            Map<String, DataSource> concurrentHashMap = initConfigPath(sourceTags, configPaths[0]);
            DatabaseContextHolder.init(concurrentHashMap, configPaths[0]);
        } else {
            List<Map<String, DataSource>> mapList = new ArrayList<>();
            List<String> configList = new ArrayList<>();
            for (String configPath : configPaths) {
                Map<String, DataSource> map = initConfigPath(sourceTags, configPath);
                if (map == null || map.size() < 1) {
                    continue;
                }
                mapList.add(map);
                configList.add(configPath);
            }
            DatabaseContextHolder.init(mapList.toArray(new Map[mapList.size()]), configList.toArray(new String[configList.size()]));
        }
    }

    private static Map<String, DataSource> initConfigPath(String[] sourceTags, String configPath) throws Exception {
        DbLog.getInstance().info("load " + configPath);
        PropertiesParser propertiesParser = new PropertiesParser(ResourceUtil.getResource(configPath));
        Map<String, DataSource> hashMap = new HashMap<>();
        String systemKey = systemPropertiesParser.getStringProperty(ConfigProperties.PROP_SYSTEM_KEY);
        SystemKey systemKey1 = null;
        if (systemKey != null) {
            systemKey1 = new SystemKey(systemKey);
        }
        String[] systemKeyColumn = systemPropertiesParser.getStringArrayProperty(ConfigProperties.PROP_SYSTEM_KEY_COLUMN, null);
        if (systemKeyColumn != null && systemKey1 == null) {
            DbLog.getInstance().warn(" use systemKeyColumn moust systemKey");
        }
        for (String tag : sourceTags) {
            Properties propertiesTag = propertiesParser.getPropertyGroup(tag, true);
            if (propertiesTag.isEmpty()) {
                DbLog.getInstance().warn(tag + "is blank");
                continue;
            }
            String url = propertiesTag.getProperty(DruidDataSourceFactory.PROP_URL);
            if (systemKey1 != null && arrayContainValue(systemKeyColumn, DruidDataSourceFactory.PROP_URL)) {
                url = systemKey1.decrypt(url);
            }
            String ip = url.substring(url.indexOf("://") + 3, url.lastIndexOf("/"));
            String[] ipInfo = ip.split(":");
            int port = Integer.parseInt(ipInfo[1]);
            boolean flag = isConnect(ipInfo[0], port);
            if (!flag) {
                System.err.println(ip + "not Connect continue   " + tag);
                DbLog.getInstance().warn(ip + "not Connect continue   " + tag);
                continue;
            }
            propertiesTag.setProperty(DruidDataSourceFactory.PROP_URL, url);
            String userName = propertiesTag.getProperty(DruidDataSourceFactory.PROP_USERNAME);
            if (systemKey1 != null && arrayContainValue(systemKeyColumn, DruidDataSourceFactory.PROP_USERNAME)) {
                userName = systemKey1.decrypt(userName);
                propertiesTag.setProperty(DruidDataSourceFactory.PROP_USERNAME, userName);
            }
            String pwd = propertiesTag.getProperty(DruidDataSourceFactory.PROP_PASSWORD);
            if (systemKey1 != null && arrayContainValue(systemKeyColumn, DruidDataSourceFactory.PROP_PASSWORD)) {
                pwd = systemKey1.decrypt(pwd);
                propertiesTag.setProperty(DruidDataSourceFactory.PROP_PASSWORD, pwd);
            }
            DataSource dataSource = DruidDataSourceFactory.createDataSource(propertiesTag);
            hashMap.put(tag, dataSource);
        }
        return hashMap;
    }

    private static boolean arrayContainValue(String[] array, String value) {
        if (array == null) {
            return false;
        }
        for (String item : array) {
            if (value.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isConnect(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
