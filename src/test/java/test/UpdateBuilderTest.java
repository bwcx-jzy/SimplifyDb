package test;

import com.alibaba.druid.sql.builder.impl.SQLUpdateBuilderImpl;
import com.alibaba.druid.util.JdbcConstants;

import java.util.HashMap;

/**
 * Created by jiangzeyin on 2018/9/19.
 */
public class UpdateBuilderTest {
    public static void main(String[] args) {
        SQLUpdateBuilderImpl sqlUpdateBuilder = new SQLUpdateBuilderImpl(JdbcConstants.MYSQL);
        sqlUpdateBuilder.from("sss");
        sqlUpdateBuilder.setValue("sss", 11);
        sqlUpdateBuilder.setValue("sssa", "sss");
        sqlUpdateBuilder.set("ssss=?");

        HashMap<String, Object> map = new HashMap<>();
        map.put("aa", "123");
        sqlUpdateBuilder.setValue(map);
        System.out.println(sqlUpdateBuilder);
    }
}
