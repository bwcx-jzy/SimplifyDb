package cn.jiangzeyin.database.annotation;

import java.lang.annotation.*;

/**
 * Created by jiangzeyin on 2017/10/24.
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityConfig {
    boolean active() default true;

    boolean update() default true;
}
