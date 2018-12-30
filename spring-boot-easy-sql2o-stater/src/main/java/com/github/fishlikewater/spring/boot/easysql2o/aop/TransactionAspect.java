package com.github.fishlikewater.spring.boot.easysql2o.aop;

import com.github.fishlikewater.spring.boot.easysql2o.annotion.Tranctional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.sql2o.Connection;
import scorpio.BaseUtils;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName TransactionAspect
 * @Description
 * @date 2018年12月30日 12:14
 **/
@Slf4j
@Aspect
public class TransactionAspect {


    /*
     * 定义一个切入点
     */
    @Pointcut(value = "@annotation(com.github.fishlikewater.spring.boot.easysql2o.annotion.Tranctional)")
    public void anyRequestTranctional() {
    }

    @Pointcut(value = "@within(com.github.fishlikewater.spring.boot.easysql2o.annotion.Tranctional)")
    public void anyClassTranctional(){

    }

    @Around(value = "anyRequestTranctional() && @annotation(tranctional)")
    public Object aroundAdvice4Method(ProceedingJoinPoint pjp, Tranctional tranctional) throws Throwable {
        Object result = null;
        Connection conn = BaseUtils.connectionThreadLocal.get();
        if(conn != null){
            result = pjp.proceed();
            return result;
        }
        try {
            int level = tranctional.value();
            Connection connection = BaseUtils.sql2o.beginTransaction(level);
            BaseUtils.connectionThreadLocal.set(connection);
            result = pjp.proceed();
            connection.commit();
        } catch (RuntimeException e) {
            log.warn("Transaction rollback");
            log.error("操作失败",e);
            ((Connection) BaseUtils.connectionThreadLocal.get()).rollback();
            throw e;
        } finally {
            BaseUtils.connectionThreadLocal.remove();
            return result;
        }
    }

    @Around(value = "anyClassTranctional() && !@annotation(com.github.fishlikewater.spring.boot.easysql2o.annotion.Tranctional)")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        Connection conn = BaseUtils.connectionThreadLocal.get();
        if(conn != null){
            result = pjp.proceed();
            return result;
        }
        try {
            Tranctional tranctional = pjp.getTarget().getClass().getAnnotation(Tranctional.class);
            int level = tranctional.value();
            Connection connection = BaseUtils.sql2o.beginTransaction(level);
            BaseUtils.connectionThreadLocal.set(connection);
            result = pjp.proceed();
            connection.commit();
        } catch (RuntimeException e) {
            log.warn("Transaction rollback");
            log.error("操作失败",e);
            ((Connection) BaseUtils.connectionThreadLocal.get()).rollback();
            throw e;
        } finally {
            BaseUtils.connectionThreadLocal.remove();
            return result;
        }
    }
}
