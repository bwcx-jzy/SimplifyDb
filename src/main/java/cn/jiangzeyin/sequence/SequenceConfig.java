package cn.jiangzeyin.sequence;

import cn.jiangzeyin.system.DbLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主键配置
 * Created by jiangzeyin on 2018/6/20.
 */
public class SequenceConfig {
    private volatile static int workerId = -1;
    private volatile static int dataCenterId = -1;

    private static final ConcurrentHashMap<Class<? extends ISequence>, ISequence> I_SEQUENCE_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();

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

    public static ISequence parseSequence(Class<? extends ISequence> sequence) {
        if (sequence == null)
            return null;
        if (sequence == ISequence.class) {
            return null;
        }
        return I_SEQUENCE_CONCURRENT_HASH_MAP.computeIfAbsent(sequence, aClass -> {
            Method method1;
            try {
                method1 = aClass.getMethod("instance");
                return (ISequence) method1.invoke(null);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                DbLog.getInstance().error("获取主键对象失败", e);
            }
            return null;
        });
    }
}
