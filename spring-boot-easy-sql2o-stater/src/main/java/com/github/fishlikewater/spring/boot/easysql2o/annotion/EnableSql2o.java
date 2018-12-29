package com.github.fishlikewater.spring.boot.easysql2o.annotion;

import com.github.fishlikewater.spring.boot.easysql2o.config.Sql2oConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName EnableSql2o
 * @Description
 * @date 2018年12月29日 22:50
 **/

@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(Sql2oConfiguration.class)
@Documented
@Configuration
public @interface EnableSql2o {
}
