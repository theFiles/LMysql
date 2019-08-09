package lidaye.ljson.annotation;

import java.lang.annotation.*;

/**
 * 字段注解
 * @author lidaye
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DbField {
    /**
     * 字段名
     */
    String value();

    /**
     * 约束级别
     */
    Constraint cst() default Constraint.NORMAL;
}
