package cn.simplifydb.entity.test;

import cn.simplifydb.database.annotation.FieldConfig;
import cn.simplifydb.sequence.impl.IdSequence;

/**
 * testSQl 表
 * Created by jiangzeyin on 2018/9/17.
 */
public class IdTest {

    /**
     * id 生成
     */
    @FieldConfig(sequence = IdSequence.class)
    private long keyId;
    private String name;

    public IdTest() {
    }

    @Override
    public String toString() {
        return "IdTest{" +
                "keyId=" + keyId +
                ", name='" + name + '\'' +
                '}';
    }

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
