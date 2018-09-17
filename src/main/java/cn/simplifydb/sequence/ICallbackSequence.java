package cn.simplifydb.sequence;

/**
 * 回调cls 和name 生成主键
 *
 * @author jiangzeyin
 * date 2018/6/22
 */
public interface ICallbackSequence extends ISequence {
    /**
     * 生成主键
     *
     * @param cls  需要生成的class
     * @param name 主键的列名
     * @return 主键
     */
    String nextId(Class cls, String name);
}
