package lidaye.ljson.annotation;

import java.lang.annotation.*;

/**
 * 表名注解
 * @author lidaye
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value();
}
