package cn.jiangzeyin.sequence;

import com.alibaba.fastjson.JSONObject;

/**
 * 序列化枚举
 * Created by jiangzeyin on 2018/6/20.
 */
public enum SequenceEnum implements ISequence {
    Date(DateSequence.instance()),
    Id(IdSequence.instance());
    private ISequence iSequence;

    SequenceEnum(ISequence iSequence) {
        this.iSequence = iSequence;
    }

    @Override
    public String nextId() {
        return iSequence.nextId();
    }

    @Override
    public JSONObject parseInfo(String id) {
        return iSequence.parseInfo(id);
    }
}
