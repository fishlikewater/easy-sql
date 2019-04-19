package com.github.fishlikewater.spring.boot.easysql2o.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年04月19日 16:27
 * @since
 **/
@Data
@ConfigurationProperties("sql2o")
public class Sql2oProperties {

    private boolean dev = false;

    private boolean activeRecord = false;

    private boolean openReadyAndWrite = false;



}
