package cn.simplifydb.sequence;

import cn.jiangzeyin.StringUtil;
import cn.jiangzeyin.SystemClock;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * 公共的
 *
 * @author jiangzeyin
 * date 2018/6/20
 */
public abstract class BaseSequence implements IQuietSequence {
    /**
     * 机器编号
     */
    protected final int workerId;
    /**
     * 数据中心编号
     */
    protected final int dataCenterId;
    /**
     * 用mask防止溢出:位与运算保证计算
     */
    protected final long sequenceMask;

    protected final int workerIdShift;
    protected final int dataCenterIdShift;

    protected final int workerIdBits;
    protected final int dataCenterIdBits;
    protected final int sequenceBits;

    protected long sequence = 0L;
    /**
     * 最后一次计算的时间
     */
    protected long lastTimestamp = -1L;


    public BaseSequence(int workerId, int dataCenterId, int workerIdBits, int dataCenterIdBits, int sequenceBits) {
        // 判断位移量是否合法
        if (workerIdBits <= 0) {
            throw new IllegalArgumentException("workerIdBits");
        }
        this.workerIdBits = workerIdBits;
        if (dataCenterIdBits <= 0) {
            throw new IllegalArgumentException("dataCenterIdBits");
        }
        this.dataCenterIdBits = dataCenterIdBits;

        // 计算最大的值
        long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
        if (dataCenterId < 0) {
            dataCenterId = (int) getDataCenterId(maxDataCenterId);
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        // 机器编号
        long maxWorkerId = -1L ^ (-1L << workerIdBits);
        if (workerId < 0) {
            workerId = (int) getWorkerId(dataCenterId, maxWorkerId);
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
        if (sequenceBits <= 0) {
            throw new IllegalArgumentException("sequenceBits");
        }
        this.sequenceBits = sequenceBits;
        // 计算偏移量
        this.workerIdShift = sequenceBits;
        this.dataCenterIdShift = sequenceBits + workerIdBits;

        this.sequenceMask = -1L ^ (-1L << sequenceBits);
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return SystemClock.now();
    }

    /**
     * 获取 maxWorkerId
     *
     * @param dataCenterId 数据中心id
     * @param maxWorkerId  机器id
     * @return maxWorkerId
     */
    private static long getWorkerId(long dataCenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(dataCenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!StringUtil.isEmpty(name)) {
            // GET jvmPid
            mpid.append(name.split("@")[0]);
        }
        //dataCenterId + PID 的 hashcode 获取16个低位
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * <p>
     * 数据标识id部分
     * </p>
     *
     * @param maxDataCenterId 最大的数据中心编号
     * @return 获取到的
     */
    private static long getDataCenterId(long maxDataCenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = ((0x000000FF & (long) mac[mac.length - 1]) | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                    id = id % (maxDataCenterId + 1);
                }
            }
        } catch (Exception e) {
            System.err.println(" getDataCenterId: " + e.getMessage());
        }
        return id;
    }
}
