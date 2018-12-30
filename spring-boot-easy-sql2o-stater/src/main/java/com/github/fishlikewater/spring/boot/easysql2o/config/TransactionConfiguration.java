package com.github.fishlikewater.spring.boot.easysql2o.config;

import com.github.fishlikewater.spring.boot.easysql2o.aop.TransactionAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.Advice;
import org.aspectj.weaver.AnnotatedElement;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName TransactionConfiguration
 * @Description
 * @date 2018年12月30日 12:18
 **/
@Configuration
@ConditionalOnClass({EnableAspectJAutoProxy.class, Aspect.class, Advice.class, AnnotatedElement.class})
public class TransactionConfiguration {

    @Bean
    public TransactionAspect transactionAspect(){
        return new TransactionAspect();
    }
}
