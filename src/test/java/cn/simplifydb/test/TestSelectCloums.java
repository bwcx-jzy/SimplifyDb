package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.run.read.Select;
import com.alibaba.druid.sql.SQLUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jiangzeyin on 2018/12/11.
 */
public class TestSelectCloums {
    @Before
    public void init() throws Exception {
        Init.init();
    }

    @Test
    public void selectEntity() {
        Select testSelect = new Select<>();
        testSelect.setClass(cn.simplifydb.entity.test.Test.class);
//        testSelect.setSql("select name,id from  test");
        testSelect.setSql("select name from  test");
        testSelect.setResultType(BaseRead.Result.ListOneColumn);
        Object object = testSelect.run();
        System.out.println(object);
//        System.out.println(testList);
    }


    @Test
    public void selectEntity2() {
        Select testSelect = new Select<>();
        testSelect.setClass(cn.simplifydb.entity.test.Test.class);
        testSelect.setSql("select id from  test");
//        testSelect.setColumns("ss");
        testSelect.setResultType(BaseRead.Result.ListOneColumn);
        Object object = testSelect.run();
        System.out.println(object);
    }


    @Test
    public void selectEntity3() {
        Select testSelect = new Select<>();
        testSelect.setClass(cn.simplifydb.entity.test.Test.class);
        testSelect.setColumns("DISTINCT name");
        testSelect.setResultType(BaseRead.Result.ListOneColumn);
        Object object = testSelect.run();
        System.out.println(object);
    }

    @Test
    public void test_0() throws Exception {
        String sql = "select id, distinct name from a";
        SQLUtils.parseSingleMysqlStatement(sql);

    }
}
