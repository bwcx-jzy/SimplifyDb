package cn.simplifydb.test;

import cn.jiangzeyin.RandomUtil;
import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Insert;
import cn.simplifydb.entity.test.IdTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试添加
 * Created by jiangzeyin on 2018/9/17.
 */
public class TestInsert {

    @Before
    public void init() throws Exception {
        Init.init();
    }

    /**
     * 同步执行
     */
    @Test
    public void insert() {
        cn.simplifydb.entity.test.Test test = new cn.simplifydb.entity.test.Test();
        test.setName("测试：" + RandomUtil.getRandomCode(2));
        // 同步执行
        Object key = new Insert<>(test).syncRun();
        System.out.println("执行结果：" + key);
        System.out.println("实体中属性新也有值：" + test.getId());
    }

    /**
     * 异步执行
     */
    @Test
    public void insert2() {
        cn.simplifydb.entity.test.Test test2 = new cn.simplifydb.entity.test.Test();
        test2.setName("测试异步：" + RandomUtil.getRandomCode(2));
        // 异步执行
        Insert insert = new Insert<>(test2);
        //
        insert.setCallback(key1 -> {
            System.out.println("异步成功回调：" + key1);
            System.out.println("实体中属性新也有值：" + test2.getId());
        });
        insert.run();
        // 等待异步执行 防止程序关闭，实际代码不需要
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 同步执行
     */
    @Test
    public void insert3() {
        IdTest test = new IdTest();
        test.setName("测试：" + RandomUtil.getRandomCode(2));
        // 同步执行
        Object key = new Insert<>(test).syncRun();
        //  此类的key 和默认的key 不一致，将导致不会直接返回key
        //  推荐系统统一使用相同主键名
        System.out.println("执行结果：" + key);
        //  但是实体属性将有执行结果
        System.out.println(test.getKeyId());
    }


    /**
     * 批量
     */
    @Test
    public void insert4() {
        IdTest test = new IdTest();
        test.setName("1测试：" + RandomUtil.getRandomCode(2));
        List<IdTest> list = new ArrayList<>();
        list.add(test);

        test = new IdTest();
        test.setName("''2测试：" + RandomUtil.getRandomCode(2));
        list.add(test);
        Insert insert = new Insert<>(list);
        insert.setBatch(true);
        insert.setCallback(key -> System.out.println("成功：" + key));
        insert.run();
        // 等待异步执行 防止程序关闭，实际代码不需要
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
