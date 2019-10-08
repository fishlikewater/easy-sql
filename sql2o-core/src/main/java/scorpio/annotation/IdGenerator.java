package scorpio.annotation;

import scorpio.core.Generator;
import scorpio.core.IdDefined;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdGenerator {

    Generator value() default Generator.AUTO;

    Class idclass() default IdDefined.class;
}
