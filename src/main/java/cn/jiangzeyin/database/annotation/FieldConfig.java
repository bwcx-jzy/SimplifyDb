package cn.jiangzeyin.database.annotation;

import java.lang.annotation.*;

/**
 * Created by jiangzeyin on 2017/12/9.
 * 实体属性配置
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
}
