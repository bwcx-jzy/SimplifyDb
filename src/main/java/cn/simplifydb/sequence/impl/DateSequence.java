package cn.simplifydb.sequence.impl;

import cn.jiangzeyin.DateUtil;
import cn.jiangzeyin.StringUtil;
import cn.simplifydb.sequence.BaseSequence;
import cn.simplifydb.sequence.ISequence;
import cn.simplifydb.sequence.SequenceConfig;
import com.alibaba.fastjson.JSONObject;

/**
 * 与snowflake算法区别,返回字符串id,占用更多字节,但直观从id中看出生成时间
 *
 * @author jiangzeyin
 * date 2018/6/20
 */
public class DateSequence extends BaseSequence {

    private static class InstanceHolder {
        static final ISequence INSTANCE = new DateSequence(SequenceConfig.getWorkerId(), SequenceConfig.getDataCenterId());
    }

    public static ISequence instance() {
        return InstanceHolder.INSTANCE;
    }

    private DateSequence(int workerId, int dataCenterId) {
        super(workerId, dataCenterId, 8, 5, 12);
    }

    @Override
    public synchronized String nextId() {
        //获取当前毫秒数
        long timestamp = timeGen();
        //如果服务器时间有问题(时钟后退) 报错。
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = (sequence + 1) & sequenceMask;
            //判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0) {
                //自旋等待到下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        long suffix = (dataCenterId << dataCenterIdShift) | (workerId << workerIdShift) | sequence;

        String datePrefix = DateUtil.formatTime("yyyyMMddHHmmssSSS", timestamp);

        return datePrefix + suffix;
    }

    @Override
    public JSONObject parseInfo(String id) {
        if (StringUtil.isEmpty(id)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", "id empty");
            return jsonObject;
        }
        if (id.length() < 17) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error", "id error");
            return jsonObject;
        }
        String date = id.substring(0, 17);
        id = id.substring(17);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("date", date);
        id = Long.toBinaryString(Long.parseLong(id));
        if (StringUtil.parseLong(id) == 0L) {
            jsonObject.put("sequence", 0);
            jsonObject.put("workerId", 0);
            jsonObject.put("dataCenter", 0);
            return jsonObject;
        }
        int len = id.length();
        int sequenceStart = len < workerIdShift ? 0 : len - workerIdShift;
        String sequence = id.substring(sequenceStart, len);
        int workerStart = len < dataCenterIdShift ? 0 : len - dataCenterIdShift;
        String workerId = sequenceStart == 0 ? "0" : id.substring(workerStart, sequenceStart);
        String dataCenterId = workerStart == 0 ? "0" : id.substring(0, workerStart);
        int sequenceInt = Integer.valueOf(sequence, 2);
        jsonObject.put("sequence", sequenceInt);
        int workerIdInt = Integer.valueOf(workerId, 2);
        jsonObject.put("workerId", workerIdInt);
        int dataCenterIdInt = Integer.valueOf(dataCenterId, 2);
        jsonObject.put("dataCenter", dataCenterIdInt);
        return jsonObject;
    }
}
