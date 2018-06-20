package cn.jiangzeyin.sequence;

import java.util.ConcurrentModificationException;

/**
 * 主键配置
 * Created by jiangzeyin on 2018/6/20.
 */
public class SequenceConfig {
    private volatile static int workerId = -1;
    private volatile static int dataCenterId = -1;

    public static void config(int workerId, int dataCenterId) {
        if (workerId <= -1) {
            throw new IllegalArgumentException("workerId<=-1");
        }
        if (dataCenterId <= -1) {
            throw new IllegalArgumentException("dataCenterId<=-1");
        }
        if (SequenceConfig.workerId != -1) {
            throw new ConcurrentModificationException("workerId");
        }
        if (SequenceConfig.dataCenterId != -1) {
            throw new ConcurrentModificationException("dataCenterId");
        }
        SequenceConfig.workerId = workerId;
        SequenceConfig.dataCenterId = dataCenterId;
    }

    public static int getDataCenterId() {
        return dataCenterId;
    }

    public static int getWorkerId() {
        return workerId;
    }
}
