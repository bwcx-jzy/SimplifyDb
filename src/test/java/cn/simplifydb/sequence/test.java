package cn.simplifydb.sequence;

import cn.simplifydb.sequence.impl.DateSequence;
import cn.simplifydb.sequence.impl.IdSequence;
import cn.simplifydb.system.DbLog;

/**
 * Created by jiangzeyin on 2018/11/26.
 */
public class test {
    public static void main(String[] args) {
//        IdSequence idSequence = new IdSequence(1, 40);
        DbLog.setDbLogInterface(new DbLog.DbLogInterface() {
            @Override
            public void info(Object object) {

            }

            @Override
            public void error(String msg, Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void warn(Object msg) {

            }

            @Override
            public void warn(String msg, Throwable t) {

            }
        });
        System.out.println(((IdSequence) IdSequence.instance()).nextId());

        SequenceConfig.config(30, 64);
        System.out.println(((DateSequence) SequenceConfig.parseSequence(DateSequence.class)).nextId());
    }
}
