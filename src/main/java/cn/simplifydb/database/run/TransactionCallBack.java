package cn.simplifydb.database.run;

import cn.simplifydb.database.run.write.Transaction;
import cn.simplifydb.system.DbLog;

/**
 * 默认事物回调实现类
 *
 * @author jiangzeyin
 * @date 2019/1/9
 */
public class TransactionCallBack implements Transaction.Callback {

    @Override
    public boolean start(Transaction.Operate operate) {
        return true;
    }

    @Override
    public void error(Exception e) {
        DbLog.getInstance().error("事物异常", e);
    }
}
