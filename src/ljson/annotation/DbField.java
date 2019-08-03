package ljson.annotation;

import java.lang.annotation.*;

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
