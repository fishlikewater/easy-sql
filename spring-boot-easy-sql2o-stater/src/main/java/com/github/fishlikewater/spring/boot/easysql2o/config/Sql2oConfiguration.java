package com.github.fishlikewater.spring.boot.easysql2o.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(Sql2oProperties.class)
public class Sql2oConfiguration {

    @Autowired
    private Sql2oProperties sql2oProperties;

    @Bean
    public Sql2o sql2o(DataSource dataSource){
        BaseUtils.getBuilder()
                .setDev(sql2oProperties.isDev())
                .setOpenReadyAndWrite(sql2oProperties.isOpenReadyAndWrite())
                .setActiveRecord(sql2oProperties.isActiveRecord());
        return BaseUtils.open(dataSource);
    }

}
