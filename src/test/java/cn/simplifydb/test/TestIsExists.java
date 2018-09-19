package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.run.read.IsExists;
import cn.simplifydb.entity.test1.Db2Test;
import org.junit.Before;
import org.junit.Test;

/**
 * 判断是否存在
 * Created by jiangzeyin on 2018/9/19.
 */
public class TestIsExists {
    @Before
    public void init() throws Exception {
        Init.init();
    }

    @Test
    public void exists() {
        IsExists<Db2Test> isExists = new IsExists<Db2Test>() {
        };
        isExists.setKeyColumn("id");
        isExists.setKeyValue(1);
        System.out.println(isExists.runBoolean());
    }

    @Test
    public void exists2() {
        IsExists<Db2Test> isExists = new IsExists<Db2Test>() {
        };
        isExists.setKeyColumn("id");
        isExists.setKeyValue(1);
        isExists.setColumns("name");
        System.out.println((String) isExists.run());
    }
}
