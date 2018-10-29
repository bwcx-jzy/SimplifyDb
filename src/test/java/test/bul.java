package test;

import cn.simplifydb.sequence.impl.IdSequence;
import com.alibaba.fastjson.JSONArray;

/**
 * Created by jiangzeyin on 2018/8/6.
 */
public class bul {
    public static void main(String[] args) {
        IdSequence idSequence = (IdSequence) IdSequence.instance();
        System.out.println(idSequence.nextId());
        JSONArray jsonArray = new JSONArray();
    }
}
