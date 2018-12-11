package cn.simplifydb.test;

import cn.jiangzeyin.RandomUtil;
import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Update;
import cn.simplifydb.entity.test1.Db2Test;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试修改
 *
 * @author jiangzeyin
 * @date 2018/9/18
 */
public class TestUpdate {

    @Before
    public void init() throws Exception {
        Init.init();
    }

    @Test
    public void update() {
        Db2Test db2Test = new Db2Test();
        db2Test.setId(1);
        db2Test.setName("修改后：" + RandomUtil.getRandomCode(2));
        db2Test.setSex(2);
        Update<Db2Test> update = new Update<>(db2Test);
        int count = update.syncRun();
        System.out.println("更新行数：" + count);
        System.out.println(update);
    }

    @Test
    public void updateRemove() {
        Db2Test db2Test = new Db2Test();
        db2Test.setId(1);
        db2Test.setName("修改后：" + RandomUtil.getRandomCode(2));
        db2Test.setSex(2);
        Update<Db2Test> update = new Update<>(db2Test);
        update.setRemove("sex");
        int count = update.syncRun();
        System.out.println("更新行数：" + count);
        System.out.println(update);
    }


    @Test
    public void update2() {
        Update<Db2Test> update = new Update<Db2Test>() {
        };
        update.setKeyValue("1");
        update.putUpdate("name", "update2 更新");
        int count = update.syncRun();
        System.out.println("更新行数：" + count);
    }

    @Test
    public void update3() {
        Update<Db2Test> update = new Update<Db2Test>() {
        };
        update.setKeyValue("1");
        update.putUpdate("sex", "#{sex+1}");
        int count = update.syncRun();
        System.out.println("更新行数：" + count);
    }

    @Test
    public void update4() {
        Update<Db2Test> update = new Update<Db2Test>() {
        };
        update.setKeyValue("2");
        update.putUpdate("name", "#{id+1}");
        int count = update.syncRun();
        System.out.println("更新行数：" + count);
    }

    @Test
    public void update5() {
        Update<Db2Test> update = new Update<Db2Test>() {
        };
        update.setKeyValue("1");
        update.putUpdate("name", "#{id+1}");
        update.putUpdate("sex", "1");
        int count = update.syncRun();
        System.out.println("更新行数：" + count);
    }

    @Test
    public void update6() {
        Update<Db2Test> update = new Update<Db2Test>() {
        };
        update.putUpdate("name", "#{id+1}");
        update.putUpdate("sex", "1");
        update.where("1=1");
        int count = update.syncRun();
        System.out.println("更新行数：" + count);
    }

    @Test
    public void update7() {
        Update<Db2Test> update = new Update<Db2Test>() {
        };
        update.putUpdate("name", "#{id+1}");
        update.putUpdate("sex", "1");
        update.where("1=1");
        update.run();
        // 等待异步执行 防止程序关闭，实际代码不需要
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
