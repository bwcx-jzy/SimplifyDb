package cn.simplifydb.test;

import cn.jiangzeyin.RandomUtil;
import cn.simplifydb.Init;
import cn.simplifydb.database.run.write.Insert;
import cn.simplifydb.entity.test1.TestCreateUser;
import cn.simplifydb.system.SystemSessionInfo;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by jiangzeyin on 2018/9/20.
 */
public class TestCreateUserFn {

    @Before
    public void init() throws Exception {
        Init.init();
        SystemSessionInfo.setSessionUser(new SystemSessionInfo.SessionUser() {
            @Override
            public String getUserName() {
                return "1号人员";
            }

            @Override
            public int getUserId() {
                return 1;
            }

            @Override
            public String userIdGetName(int userId) {
                return "请自行转换";
            }
        });
    }

    @Test
    public void insert() {
        TestCreateUser test = new TestCreateUser();
        test.setName("测试：" + RandomUtil.getRandomCode(2));
        // 同步执行
        Object key = new Insert<>(test).syncRun();
        System.out.println("执行结果：" + key);
    }
}
