package scorpio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String value();

    int type() default Types.VARCHAR;

    int length() default 255;

    boolean nullable() default false;

    String describe() default "";

    boolean index() default false;

    String columnDefined() default "";

    String defaultValue() default "";

}
