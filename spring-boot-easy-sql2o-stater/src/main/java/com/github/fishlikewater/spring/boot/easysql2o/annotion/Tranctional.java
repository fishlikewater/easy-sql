package com.github.fishlikewater.spring.boot.easysql2o.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Connection;

@Target({ElementType.TYPE,ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Tranctional {

    int value() default Connection.TRANSACTION_READ_COMMITTED;
}
