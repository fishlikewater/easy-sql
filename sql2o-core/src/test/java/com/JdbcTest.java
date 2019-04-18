

package com;

import org.junit.Before;
import org.junit.Test;
import scorpio.BaseUtils;
import scorpio.core.QueryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
                    .setCreate(true)
                    .setDev(false);
            BaseUtils.open("jdbc:sqlite:scorpio-jdbc.db", null, null);
        } catch (Exception e) {
        }
    }

    @Test
    public void insert() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto = new TestDTO();
        dto.setaId("1");
        dto.setName("李四");
        testDAO.create(dto);
    }

    @Test
    public void insertForMap() {
        TestMapper testDAO = new TestMapper();
        Map map = new HashMap<>();
        map.put("aId", "6");
        map.put("name", "玩物");
        testDAO.create(map);
    }

    @Test
    public void insertNoId() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto = new TestDTO();
        dto.setName("李四");
        testDAO.create(dto, "7");
    }

    @Test
    public void insertBath() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto1 = new TestDTO();
        dto1.setaId("8");
        dto1.setName("李四");
        TestDTO dto2 = new TestDTO();
        dto2.setaId("9");
        dto2.setName("玩物");
        TestDTO[] dtos = {dto1, dto2};
        testDAO.create(dtos);
    }

    //mysql才支持
    @Test
    public void insert4Batch() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto1 = new TestDTO();
        dto1.setaId("10");
        dto1.setName("李四");
        TestDTO dto2 = new TestDTO();
        dto2.setaId("11");
        dto2.setName("玩物");
        List list = new ArrayList();
        list.add(dto1);
        list.add(dto2);
        testDAO.create4Batch(list);
    }

    @Test
    public void createAndId() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto1 = new TestDTO();
        dto1.setName("李四");
        testDAO.createAndId(dto1);
    }

    @Test
    public void remove() {
        TestMapper testDAO = new TestMapper();
        testDAO.remove("6");
    }

    @Test
    public void removeBath() {
        TestMapper testDAO = new TestMapper();
        String[] ids = {"2", "3"};
        Integer[] idss = {2, 3};
        testDAO.remove(ids);
    }

    @Test
    public void removeByObj() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto1 = new TestDTO();
        //dto1.setId("2");
        dto1.setName("李四");
        int i = testDAO.removeByCondition(dto1);
        System.out.println(i);
    }

    @Test
    public void removeByMap() {
        TestMapper testDAO = new TestMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("id", "5");
        map.put("name", "玩物");
        testDAO.removeByCondition(map);
    }

    @Test
    public void removeByCriteria() {
        TestMapper testDAO = new TestMapper();
        int i = testDAO.removeByCriteria("name='李四'");
        System.out.println(i);
    }

    @Test
    public void removeAll() {
        TestMapper testDAO = new TestMapper();
        testDAO.removeAll();
    }

    @Test
    public void update() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto1 = new TestDTO();
        dto1.setaId("6");
        dto1.setName("张三");
        testDAO.update(dto1);
    }

    @Test
    public void updateForMap() {
        TestMapper testDAO = new TestMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("aId", "1");
        map.put("name", "玩物");
        testDAO.update(map);
    }

    @Test
    public void updateNotIgnoreNull() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto1 = new TestDTO();
        testDAO.updateNotIgnoreNull(dto1);
    }

    @Test
    public void findById() {
        TestMapper testDAO = new TestMapper();
        Integer id = 1;
        System.out.println(testDAO.findById(id));
    }

    @Test
    public void findByIds() {
        TestMapper testDAO = new TestMapper();
        List list = new ArrayList();
        list.add(1);
        list.add(6);
        List<TestDTO> byIds = testDAO.findByIds(list);
        System.out.println(byIds);
    }

    @Test
    public void queryForBaseObject() {
        TestMapper testDAO = new TestMapper();
        List list = new ArrayList();
        TestDTO dto1 = new TestDTO();
        dto1.setName("张三");
        System.out.println(testDAO.queryForBaseObject(dto1));
    }

    @Test
    public void queryForBaseObjectByCriteria() {
        TestMapper testDAO = new TestMapper();
        System.out.println(testDAO.queryForBaseObjectByCriteria("name='玩物'"));
    }

    @Test
    public void query() {
        TestMapper testDAO = new TestMapper();
        List list = new ArrayList();
        TestDTO dto1 = new TestDTO();
        dto1.setName("玩物");
        System.out.println(testDAO.query(null, 0, 3, "scrq desc"));
    }

    @Test
    public void queryCount() {
        TestMapper testDAO = new TestMapper();
        System.out.println(testDAO.queryCount("name='张三'"));
    }

    @Test
    public void queryCount2() {
        TestMapper testDAO = new TestMapper();
        System.out.println(testDAO.queryCount());
    }

    @Test
    public void queryCountByTpl() {
        TestMapper testDAO = new TestMapper();
        System.out.println(testDAO.queryCountByTpl("queryCount", null));
    }

    @Test
    public void queryForListByBaseObject() {
        TestMapper testDAO = new TestMapper();
        System.out.println(testDAO.queryForListByBaseObject("query", null));
    }

    @Test
    public void queryForObject() {
        TestMapper testDAO = new TestMapper();
        TestDTO dto1 = new TestDTO();
        dto1.setName("玩物");
        System.out.println(testDAO.queryForObject("query", dto1, TestDTO.class));
    }

    @Test
    public void queryAll() {
        TestMapper testDAO = new TestMapper();
        List<TestDTO> query = testDAO.query();
        System.out.println(query);
    }

    @Test
    public void queryAll2() {
        long t1 = System.currentTimeMillis();
        ResourcesMapper testDAO = new ResourcesMapper();
        List<ResourcesDTO> query = testDAO.query();
        // List<ResourcesDTO> resourcesDTOS = BaseUtils.sql2o.open().createQuery("select * from resources").executeAndFetch(ResourcesDTO.class);
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        System.out.println(query.toString());
    }

    @Test
    public void testModel(){
        Resources resources = new Resources();
        long t1 = System.currentTimeMillis();
        Resources queryCount = resources.object(new QueryModel().tpl("queryCount", null));
        System.out.println(queryCount);
        //resources.update(new UpdateModel().set("res_url", "https://baidu.com"));
        List<Map<String, Object>> list = resources.maps(new QueryModel().equal("id", "1"));
        long t2 = System.currentTimeMillis();
        System.out.println(t2-t1);
        System.out.println(list);
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

