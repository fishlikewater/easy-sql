package scorpio.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年04月18日 15:06
 * @since
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    String table() default "";

    String fileMapper() default "";
}
