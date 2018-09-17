package cn.simplifydb.sequence;

import cn.simplifydb.system.DbLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 主键配置
 *
 * @author jiangzeyin
 * date 2018/6/20
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

    /**
     * 根据class 获取 接口对象
     *
     * @param sequence 实现 接口的class ,class 中必须包含instance 的静态方法来返回对应的单利对象
     * @return 对象
     * @see ISequence
     */
    public static ISequence parseSequence(Class<? extends ISequence> sequence) {
        if (sequence == null || sequence == ISequence.class) {
            return null;
        }
        int modifier = sequence.getModifiers();
        if (Modifier.isInterface(modifier)) {
            DbLog.getInstance().warn(sequence + " 生成主键class 不能是接口");
            return null;
        }
        if (Modifier.isAbstract(modifier)) {
            DbLog.getInstance().warn(sequence + " 生成主键class 不能是抽象类");
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
