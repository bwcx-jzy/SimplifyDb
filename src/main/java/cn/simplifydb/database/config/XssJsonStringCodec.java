package cn.simplifydb.database.config;

import cn.simplifydb.util.Util;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.StringCodec;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * xss json 输入还原
 *
 * @author jiangzeyin
 * @date 2018/12/5
 */
public class XssJsonStringCodec extends StringCodec {
    public static XssJsonStringCodec instance = new XssJsonStringCodec();

    private XssJsonStringCodec() {
    }

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
            throws IOException {
        if (object == null) {
            super.write(serializer, object, fieldName, fieldType, features);
        } else {
            super.write(serializer, Util.unescape((String) object), fieldName, fieldType, features);
        }
    }
}
