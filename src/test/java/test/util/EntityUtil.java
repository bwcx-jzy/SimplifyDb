package test.util;

import cn.jiangzeyin.des.SystemKey;
import test.OrderedProperties;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by jiangzeyin on 2018/12/5.
 */
public class EntityUtil {

    public static void write(String keY, String name, Entity... entitys) throws Exception {
        OrderedProperties default_proper = new OrderedProperties();
        URL url = EntityUtil.class.getResource("/");
        InputStream inputStream = EntityUtil.class.getResourceAsStream("/default.properties");
        OrderedProperties all_proper = new OrderedProperties();
        default_proper.load(inputStream);
        SystemKey systemKey = new SystemKey(keY);
        for (Entity entity : entitys) {
            System.out.println(entity);
            for (Object key : default_proper.keySet()) {
                String val = default_proper.getProperty(key.toString());
                if ("url".equals(key)) {
                    val = String.format("jdbc:mysql://%s:%s/%s%s?allowMultiQueries=true&autoReconnect=true&useSSL=true&verifyServerCertificate=false",
                            entity.getUrl(), entity.getPort(), entity.getDbPrefix(), entity.getTag());
                    System.out.println(val);
                    val = systemKey.encrypt(val);
                } else if ("username".equals(key)) {
                    val = systemKey.encrypt(entity.getName());
                } else if ("password".equals(key)) {
                    val = systemKey.encrypt(entity.getPwd());
                }
//                System.out.println(key + ":" + val);
                all_proper.put(entity.getTag() + "." + key, val);

            }
        }
        File file = new File(url.getFile(), name + ".properties");
        FileOutputStream outputStream = new FileOutputStream(file);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        all_proper.store(bufferedWriter, "Auto Comment");
        bufferedWriter.close();
        outputStream.close();
        System.out.println(file.getPath());
    }

}
