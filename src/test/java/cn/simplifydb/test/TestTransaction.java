package cn.simplifydb.test;

import cn.jiangzeyin.RandomUtil;
import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Insert;
import cn.simplifydb.database.run.write.Transaction;
import cn.simplifydb.entity.test.IdTest;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangzeyin on 2018/9/28.
 */
public class TestTransaction {
    @Before
    public void init() throws Exception {
        Init.init();
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
        new Transaction(IdTest.class, new Transaction.Callback() {
            @Override
            public void start(Transaction.Operate operate) {
                Insert<IdTest> insert = operate.getInsert(IdTest.class);
                insert.setList(list);
//                insert.setBatch(true);
                insert.setCallback((key, count) -> System.out.println("成功：" + key));
                insert.syncRun();
                operate.commit();
            }

            @Override
            public void error(Exception e) {

            }
        });

    }
}
