package com.github.fishlikewater.spring.boot.easysql2o.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sql2o.Sql2o;
import scorpio.BaseUtils;

import javax.sql.DataSource;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName Sql2oConfiguration
 * @Description
 * @date 2018年12月29日 22:46
 **/

@Configuration
@ConditionalOnBean(DataSource.class)
public class Sql2oConfiguration {

    @Bean
    public Sql2o sql2o(DataSource dataSource){
        return BaseUtils.open(dataSource);
    }

}
