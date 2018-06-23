package cn.jiangzeyin.sequence;

import com.alibaba.fastjson.JSONObject;

/**
 * 静默不回调生成主键
 * Created by jiangzeyin on 2018/6/23.
 */
public interface IQuietSequence extends ISequence {
    /**
     * 生成唯一主键
     *
     * @return 主键
     */
    String nextId();

    /**
     * 根据主键获取相关信息
     *
     * @param id 主键
     * @return json
     */
    JSONObject parseInfo(String id);
}
