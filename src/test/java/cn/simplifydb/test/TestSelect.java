package cn.simplifydb.test;

import cn.simplifydb.Init;
import cn.simplifydb.database.base.BaseRead;
import cn.simplifydb.database.run.read.Select;
import com.alibaba.fastjson.JSONArray;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author jiangzeyin
 * @date 2018/9/17
 */
public class TestSelect {

    @Before
    public void init() throws Exception {
        Init.init();
    }

    /**
     * 手动调用实体方法
     */
    @Test
    public void selectEntity() {
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<>();
        testSelect.setTclass(cn.simplifydb.entity.test.Test.class);
        List<cn.simplifydb.entity.test.Test> testList = testSelect.run();
        System.out.println(testList);
    }

    /**
     * 自动解析泛型
     */
    @Test
    public void selectEntity2() {
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<cn.simplifydb.entity.test.Test>() {
        };
        List<cn.simplifydb.entity.test.Test> testList = testSelect.run();
        System.out.println("selectEntity2");
        System.out.println(testList);
    }

    /**
     * 返回json
     */
    @Test
    public void selectEntity3() {
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<cn.simplifydb.entity.test.Test>() {
        };
        testSelect.setResultType(BaseRead.Result.JsonArray);
        JSONArray jsonArray = testSelect.run();
        System.out.println("selectEntity3");
        System.out.println(jsonArray);
    }

    /**
     * 只查指定列
     */
    @Test
    public void selectEntity4() {
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<cn.simplifydb.entity.test.Test>() {
        };
        testSelect.setResultType(BaseRead.Result.JsonArray);
        testSelect.setColumns("name");
        JSONArray jsonArray = testSelect.run();
        System.out.println("selectEntity4");
        System.out.println(jsonArray);
    }

    @Test
    public void selectEntity5() {
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<cn.simplifydb.entity.test.Test>() {
        };
        testSelect.setResultType(BaseRead.Result.JsonArray);
        testSelect.setColumns("name");
        testSelect.where("id>2");
        JSONArray jsonArray = testSelect.run();
        System.out.println(jsonArray);
    }


    @Test
    public void selectEntity6() {
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<cn.simplifydb.entity.test.Test>() {
        };
        testSelect.setResultType(BaseRead.Result.JsonArray);
        testSelect.setColumns("name");
        testSelect.where("id>2");
        testSelect.orderBy("id desc");
        cn.simplifydb.entity.test.Test test = testSelect.runOne();
        System.out.println(test);
    }

    @Test
    public void selectEntity7() {
        Select<cn.simplifydb.entity.test.Test> testSelect = new Select<cn.simplifydb.entity.test.Test>() {
        };
        testSelect.setResultType(BaseRead.Result.JsonArray);
        testSelect.setColumns("name");
        testSelect.where("id>2");
        testSelect.setRemove("test");
        testSelect.orderBy("id desc");
        cn.simplifydb.entity.test.Test test = testSelect.runOne();
        System.out.println(test);
    }
}
