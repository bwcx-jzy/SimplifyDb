package cn.simplifydb.sequence;

import cn.simplifydb.sequence.impl.IdSequence;

/**
 * Created by jiangzeyin on 2018/11/26.
 */
public class test {
    public static void main(String[] args) {
//        IdSequence idSequence = new IdSequence(1, 40);
        System.out.println(((IdSequence) IdSequence.instance()).nextId());
    }
}
