package cn.simplifydb.util;

import cn.jiangzeyin.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;

/**
 * key-value
 *
 * @author jiangzeyin
 */
public class PropertiesParser {
    private Properties props;

    public PropertiesParser(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream, "inputStream is null");
        Properties properties = new Properties();
        properties.load(inputStream);
        inputStream.close();
        this.props = properties;
    }

    public PropertiesParser(Properties props) {
        this.props = props;
    }

    public Properties getUnderlyingProperties() {
        return this.props;
    }

    public String getStringProperty(String name) {
        return this.getStringProperty(name, null);
    }

    public String getStringProperty(String name, String def) {
        String val = this.props.getProperty(name, def);
        if (val == null) {
            return def;
        } else {
            val = val.trim();
            return val.length() == 0 ? def : val;
        }
    }

    public String[] getStringArrayProperty(String name) {
        return this.getStringArrayProperty(name, null);
    }

    public String[] getStringArrayProperty(String name, String[] def) {
        String vals = this.getStringProperty(name);
        if (vals == null) {
            return def;
        }
        return StringUtil.stringToArray(vals, ",");
    }

    public Properties getPropertyGroup(String prefix) {
        return this.getPropertyGroup(prefix, false, (String[]) null);
    }

    public Properties getPropertyGroup(String prefix, boolean stripPrefix) {
        return this.getPropertyGroup(prefix, stripPrefix, null);
    }

    public Properties getPropertyGroup(String prefix, boolean stripPrefix, String[] excludedPrefixes) {
        Enumeration keys = this.props.propertyNames();
        Properties group = new Properties();
        if (!prefix.endsWith(".")) {
            prefix = prefix + ".";
        }

        while (true) {
            String key;
            do {
                if (!keys.hasMoreElements()) {
                    return group;
                }

                key = (String) keys.nextElement();
            } while (!key.startsWith(prefix));

            boolean exclude = false;
            if (excludedPrefixes != null) {
                for (int value = 0; value < excludedPrefixes.length && !exclude; ++value) {
                    exclude = key.startsWith(excludedPrefixes[value]);
                }
            }

            if (!exclude) {
                String var9 = this.getStringProperty(key, "");
                if (stripPrefix) {
                    group.put(key.substring(prefix.length()), var9);
                } else {
                    group.put(key, var9);
                }
            }
        }
    }
}
