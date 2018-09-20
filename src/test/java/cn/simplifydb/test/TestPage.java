package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.run.read.SelectPage;
import com.alibaba.fastjson.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by jiangzeyin on 2018/9/18.
 */
public class TestPage {
    @Before
    public void init() throws Exception {
        Init.init();
    }


    /**
     * 手动调用实体方法
     */
    @Test
    public void page() {

        SelectPage<cn.simplifydb.entity.test.Test> testSelect = new SelectPage<cn.simplifydb.entity.test.Test>(1, 1) {
        };
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        List<cn.simplifydb.entity.test.Test> testList = testSelect.run();
        System.out.println(testList);
    }

    /**
     * 手动调用实体方法
     */
    @Test
    public void page2() {

        SelectPage<cn.simplifydb.entity.test.Test> testSelect = new SelectPage<cn.simplifydb.entity.test.Test>(1, 2) {
        };
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        testSelect.setResultType(BaseRead.Result.JsonArray);
        JSONArray jsonArray = testSelect.run();
        System.out.println(jsonArray);
    }


    @Test
    public void page3() {

        SelectPage<cn.simplifydb.entity.test.Test> testSelect = new SelectPage<cn.simplifydb.entity.test.Test>(3, 2) {
        };
        testSelect.orderBy("id desc");
        testSelect.where("id>2");
//        Page<cn.simplifydb.entity.test.Test> page = new Page<>();
//        page.setPageNoAndSize(3, 2);
//        page.setWhereWord("id>2");
//        page.setOrderBy("id desc");
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        testSelect.setResultType(BaseRead.Result.JsonArray);
        JSONArray jsonArray = testSelect.run();
        System.out.println(jsonArray);
//        System.out.println(page);
    }


    @Test
    public void page4() {

        SelectPage<cn.simplifydb.entity.test.Test> testSelect = new SelectPage<cn.simplifydb.entity.test.Test>(3, 2) {
        };

        testSelect.setSql("select * from test where id>2");
        testSelect.orderBy("id desc");
        testSelect.where("id>1");

        testSelect.whereAnd("id>1");
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        testSelect.setResultType(BaseRead.Result.JsonArray);
        JSONArray jsonArray = testSelect.run();
        System.out.println(jsonArray);
    }
}
