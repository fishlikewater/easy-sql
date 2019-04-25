package com;

import org.junit.Test;
import scorpio.core.CreateTemplate;

import java.util.Properties;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年04月23日 16:46
 * @since
 **/
public class TestTemplate {

    @Test
    public void testTpy(){
        Properties p = new Properties();
        p.setProperty("jdbc.url", "jdbc:sqlite:scorpio-jdbc.db");
        p.setProperty("jdbc.username", "");
        p.setProperty("jdbc.password", "");
        p.setProperty("jdbc.driver", "org.sqlite.JDBC");
        p.setProperty("table", "resources");
        p.setProperty("pack", "com.test");
        p.setProperty("fileMapper", "/mapper");
        p.setProperty("basePath", "F:\\IdeaProjects\\easy-sql\\sql2o-core");
        CreateTemplate.init(p);
        CreateTemplate.createMapper();
        CreateTemplate.createModel();
        CreateTemplate.createService();
    }
}
