

package com;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.Before;
import org.junit.Test;
import scorpio.BaseUtils;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class JdbcTest {
    public static final String DB_NAME = "jdbc.db";
    public static String DB_PATH;
    public static String DB_SRC;

    @Before
    public void before() {
        try {
            InputStream inputStream = JdbcTest.class.getResourceAsStream("/druid.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
            BaseUtils.open(dataSource);
            /*Class.forName("org.sqlite.JDBC");
            BaseUtils.getBuilder()
                    .setDev(false);
            BaseUtils.open("jdbc:sqlite:scorpio-jdbc.db", null, null);*/
        } catch (Exception e) {
        }
    }


    @Test
    public void testModel(){
        ResourcesMapper mapper = new ResourcesMapper();
        //Integer object = mapper.object(new QueryModel().tpl("select count(*) from resources", null), Integer.class);
        //System.out.println(object);
         Resources resources = new Resources();
        resources.setSort(false);
        resources.setType(TypeEnum.app);
        //resources.setName(TestEnum.app);
        resources.setResUrl("baaa");
        resources.setParentId(22);
        //resources.setName(TestEnum.pc);
        mapper.save(resources, true);
        //Resources map = mapper.object(new QueryModel());
        //System.out.println(map);
  //     Integer count = mapper.count(new QueryModel());
  //      System.out.println(count);
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
        BaseUtils.getBuilder().setActiveRecord(true);
        TestDTO dto1 = new TestDTO();
        TestDTO dto2 = new TestDTO();
    }

}

