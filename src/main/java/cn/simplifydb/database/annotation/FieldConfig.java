package cn.simplifydb.database.annotation;

import cn.simplifydb.sequence.ISequence;

import java.lang.annotation.*;

/**
 * 实体属性配置
 * Created by jiangzeyin on 2017/12/9.
 */
@Documented
@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldConfig {
    /**
     * insert 是默认值
     *
     * @return str
     */
    String insertDefValue() default "";

    /**
     * 主键生成器
     *
     * @return class
     */
    Class<? extends ISequence> sequence() default ISequence.class;
}
