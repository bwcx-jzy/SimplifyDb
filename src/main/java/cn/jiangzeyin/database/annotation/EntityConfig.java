package cn.jiangzeyin.database.annotation;

import java.lang.annotation.*;

/**
 * 实体配置
 * Created by jiangzeyin on 2017/10/24.
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityConfig {
    /**
     * 是否记录数据状态
     *
     * @return boolean
     */
    boolean active() default true;

    /**
     * 是否记录update  时间信息
     *
     * @return boolean
     */
    boolean update() default true;

    /**
     * baseEntity mark 字段
     *
     * @return 是否存在mark 字段
     */
    boolean baseMark() default true;

    /**
     * 插入的默认值
     *
     * @return String
     */
    String insertColumns() default "";
}
