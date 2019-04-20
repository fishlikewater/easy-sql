

package com;

import org.junit.Before;
import org.junit.Test;
import scorpio.BaseUtils;
import scorpio.core.QueryModel;

import java.util.Map;

public class JdbcTest {
    public static final String DB_NAME = "jdbc.db";
    public static String DB_PATH;
    public static String DB_SRC;

    @Before
    public void before() {
        try {
            //String url = "jdbc:mysql://localhost:3306/security?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useSSL=false";
            Class.forName("org.sqlite.JDBC");
            // BaseUtils.open(url,"root", "ROOT");
            BaseUtils.getBuilder()
                    .setDev(false);
            BaseUtils.open("jdbc:sqlite:scorpio-jdbc.db", null, null);
        } catch (Exception e) {
        }
    }


    @Test
    public void testModel(){
        ResourcesMapper mapper = new ResourcesMapper();
        Integer count = mapper.count(new QueryModel());
        System.out.println(count);
//        long t1 = System.currentTimeMillis();
//        Integer queryCount = resources.object(new QueryModel().tpl("queryCount", null), Integer.class);
//        System.out.println(queryCount);
//        //resources.update(new UpdateModel().set("res_url", "https://baidu.com"));
//        List<Map<String, Object>> list = resources.maps(new QueryModel().equal("id", "1"));
//        long t2 = System.currentTimeMillis();
//        System.out.println(t2-t1);
//        System.out.println(list);
    }

    @Test
    public void test11() {
        System.out.println(String.class.isAssignableFrom(Map.class));
    }

    @Test
    public void testPort() {
        TestMapper testDAO = new TestMapper();
        BaseUtils.atomic(() -> {
            return testDAO.findById(6);
        });

    }

}

