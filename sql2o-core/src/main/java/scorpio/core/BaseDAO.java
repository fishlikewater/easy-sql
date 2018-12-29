package scorpio.core;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import scorpio.BaseUtils;
import scorpio.annotation.*;
import scorpio.exception.BaseRuntimeException;
import scorpio.migrate.Execute;
import scorpio.utils.BeanUtils;
import scorpio.utils.NameUtils;
import scorpio.utils.TableInfo;
import scorpio.utils.TableUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangx
 * @date 20171002
 * @email fishlikewater@126.com
 * 该类主要扩展操作(sql与代码分离，以.sqlmap为后缀的文件来存储sql语句，每句sql以K-V的方式)
 */
@Slf4j
public class BaseDAO<T extends BaseObject> implements IBaseDAO, Serializable {

    /**
     * 存储当前pojo 对应.sqlmap文件中的sql语句
     */
    private Map<String, String> sqlMap;
    /**
     * 文件最后修改时间(判断文件是否更新使用)
     */
    private AtomicLong lastModifyTime = null;
    /**
     * 锁
     */
    ReentrantLock lock = new ReentrantLock();
    /**
     * 表信息
     */
    private TableInfo tableInfo;
    /**
     * 分页记录起始
     */
    static final String PARAM_BEGIN_NUM = "begin";
    static final String PARAM_INTERVAL = "limit";
    @Transient
    @Setter
    protected Sql2o sql2o;

    private AtomicInteger isInit = new AtomicInteger(1);

    /**
     * 通过对象创建数据
     *
     * @param dto
     * @return
     */
    @Override
    public Object create(BaseObject dto) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        getNotEmptyColumn(dto);
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("create", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            Object key = conn.createQuery(template).bind(dto).executeUpdate().getKey();
            return key;
        } finally {
            close(conn);
        }
    }

    /**
     * 通过map创建对象,map中的key为对应对象的属性
     *
     * @param map
     */
    @Override
    public Object create(Map<String, Object> map) {
        assetInit();
        BaseObject dto = toConvertion(map);
        return create(dto);
    }

    /**
     * 通过传入的id 创建对象
     *
     * @param dto
     * @param id
     */
    @Override
    public void create(BaseObject dto, String id) {
        /**先获取主键列 */
        assetInit();
        if (StringUtils.isBlank(id) && tableInfo.getGenerator() != Generator.AUTO) {
            log.error("Primary key cannot be empty");
            throw new BaseRuntimeException();
        }
        Field pkCloumn = null;
        try {
            pkCloumn = dto.getClass().getDeclaredField(NameUtils.getCamelName(tableInfo.getPkName()));
            pkCloumn.setAccessible(true);
            pkCloumn.set(dto, id);
            create(dto);
        } catch (NoSuchFieldException e) {
            log.error("No primary key column mappings in :"+ dto.getClass ().getSimpleName(), e);
            throw new BaseRuntimeException();
        } catch (IllegalAccessException e) {
            log.error("Primary key cannot be empty", e);
            throw new BaseRuntimeException();
        }
    }

    /**
     * 批量创建对象
     *
     * @param dtos
     */
    @Override
    public void create(BaseObject[] dtos) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        tableInfo.setColumnsWithNotEmptyValue(tableInfo.getDtoAllValueName());
        tableInfo.setNotEmptyDBColumnList(tableInfo.getDbColumnList());
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("create", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            Query query = conn.createQuery(template);
            for (BaseObject dto : dtos) {
                query.bind(dto).addToBatch();
            }
            query.executeBatch();
        } finally {
            close(conn);
        }
    }

    /**
     * mysql 专用的高效插入方法，是适用于mysql数据库(插入效率，测试[读取三十万数据，插入新表共耗时5s])
     *
     * @param dtos
     */
    public void create4Batch(List<? extends BaseObject> dtos) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List dbColumnList = tableInfo.getDbColumnList();
        if (tableInfo.getGenerator() == Generator.AUTO) {
            dbColumnList.remove(tableInfo.getPkName());
        }
        tableInfo.setNotEmptyDBColumnList(dbColumnList);
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("create4Batch", paramMap);
        log.debug("{}", template);
        InputStream dataStream = getTestDataInputStream(dtos, tableInfo.getNotEmptyDBColumnList());
        try {
            int rows = bulkLoadFromInputStream(template, dataStream);
        } catch (SQLException e) {
            log.error("SQL data injection exception", e);
            throw new BaseRuntimeException();
        }
    }

    /**
     * 创建数据 生成id
     *
     * @param dto
     * @return
     */
    @Override
    public String createAndId(BaseObject dto) {
        assetInit();
        String id = String.valueOf(IdFactory.getId(tableInfo.getGenerator(), tableInfo.getIdDefined()));
        create(dto, id);
        return id;
    }


    /**
     * 根据id删除数据
     *
     * @param id
     */

    public void remove(String id) {
        removeById(id);
    }

    public void remove(Integer id) {
        removeById(id);
    }

    @Override
    public void removeById(Object id) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("removeById", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            int c = conn.createQuery(template).addParameter("id", id).executeUpdate().getResult();
            if (c == 0) {
                //throw new BaseRuntimeException("数据异常，删除失败");
                log.warn("{}", "No data is deleted to detect whether the primary key "+ id +" exists");
            }
        } finally {
            close(conn);
        }
    }

    @Override
    public void remove(String[] ids) {
        removeByIds(ids);
    }

    @Override
    public void remove(Integer[] ids) {
        removeByIds(ids);
    }

    /**
     * 根据id删除数据
     *
     * @param ids
     */
    private void removeByIds(Object[] ids) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("removeById", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            Query query = conn.createQuery(template);
            for (int i = 0; i < ids.length; i++) {
                query.addParameter("id", ids[i]).addToBatch();
            }
            query.executeBatch();
        } finally {
            close(conn);
        }
    }

    /**
     * 根据条件删除数据
     *
     * @param dto
     * @return
     */
    @Override
    public int removeByCondition(BaseObject dto) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        tableInfo.setMapperList(TableUtils.getMapper(tableInfo.getPojoClass(), dto));
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("remove", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            return conn.createQuery(template).bind(dto).executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }

    /**
     * 获取不为空的列
     *
     * @param dto
     */
    private void getNotEmptyColumn(BaseObject dto) {
        Map<String, List> map = TableUtils.getNotEmptyColumn(tableInfo.getPojoClass(), dto);
        List columnsWithNotEmptyValue = map.get("dtoColumn");
        List notEmptyDBColumnList = map.get("dbColumn");
        tableInfo.setColumnsWithNotEmptyValue(columnsWithNotEmptyValue);
        tableInfo.setNotEmptyDBColumnList(notEmptyDBColumnList);
    }

    /**
     * map 转换为对象
     *
     * @param map
     * @return
     */
    private BaseObject toConvertion(Map<String, Object> map) {
        try {
            map.forEach((k, v) -> {
                if (!tableInfo.getDtoAllValueName().contains(k)) {
                    log.error("Attribute in map: " + k + "is not within the database mapping object");
                    throw new BaseRuntimeException();
                }
            });
            return BeanUtils.mapToBean(map, tableInfo.getPojoClass().newInstance());
        } catch (InstantiationException e) {
            log.error("Instantiation abnormity", e);
            throw new BaseRuntimeException();
        } catch (IllegalAccessException e) {
            log.error("Parameter anomaly", e);
            throw new BaseRuntimeException();
        }
    }

    /**
     * BaseObject 转换为Map
     *
     * @param dto
     * @return
     */
    private Map<String, Object> toConvertion(BaseObject dto) {

        return BeanUtils.beanToMap(dto);
    }

    /**
     * 删除符合条件的记录
     * 使用这个方法的时候需要注意，如果传入的dto里没有任何字段被赋值，则有可能删除整个
     * 表的记录
     *
     * @param map 其中的key要与pojo属性值对应
     * @return
     */
    @Override
    public int removeByCondition(Map<String, Object> map) {
        assetInit();
        BaseObject baseObject = toConvertion(map);
        return removeByCondition(baseObject);
    }

    /**
     * @param criteria sql条件,如果criteria为blank则不进行删除
     * @return
     */
    @Override
    public int removeByCriteria(String criteria) {
        if (StringUtils.isBlank(criteria)) {
            log.error("The deletion condition is not empty, which removes all data, and if you need to delete all the data, use the removeAll method");
            throw new BaseRuntimeException();
        }
        assetInit();
        String sqlTemplate = "delete from " + tableInfo.getTblName() + " where " + criteria + "";
        Connection conn = getConn(sqlTemplate);
        try {
            return conn.createQuery(sqlTemplate).executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }

    /**
     * @return
     */
    @Override
    public int removeAll() {
        assetInit();
        String sqlTemplate = "delete from " + tableInfo.getTblName() + "";
        Connection conn = getConn(sqlTemplate);
        try {
            return conn.createQuery(sqlTemplate).executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }

    /**
     * 更新对象
     *
     * @param dto
     * @return
     */
    @Override
    public int update(BaseObject dto) {
        assetInit();
        String camelName = NameUtils.getCamelName(tableInfo.getPkName());
        try {
            Field field = tableInfo.getPojoClass().getDeclaredField(camelName);
            field.setAccessible(true);
            Object o = field.get(dto);
            if (o == null) {
                log.error("The object does not have the primary key value, and the update fails!!!");
                throw new BaseRuntimeException();
            }
            getNotEmptyColumn(dto);
            Map<String, Object> paramMap = new HashMap<String, Object>();
            tableInfo.setMapperList(TableUtils.getMapper(tableInfo.getPojoClass(), dto));
            paramMap.put("tbl", tableInfo);
            paramMap.put("id", o);
            String template = parseSqlTemplate("update", paramMap);
            log.debug("{}", template);
            Connection conn = getConn(template);
            try {
                int result = conn.createQuery(template).bind(dto).executeUpdate().getResult();
                if (result == 0) {
                    log.error("No data, update failure");
                    //throw new BaseRuntimeException();
                }
                return result;
            } finally {
                close(conn);
            }

        } catch (NoSuchFieldException e) {
            log.error("Getting the object primary key attribute failure", e);
            throw new BaseRuntimeException();
        } catch (IllegalAccessException e) {
            log.error("Getting the object primary key attribute failure", e);
            throw new BaseRuntimeException();
        }
    }

    @Override
    public int update(Map<String, Object> map) {
        assetInit();
        BaseObject baseObject = toConvertion(map);
        return update(baseObject);
    }

    @Override
    public int updateNotIgnoreNull(BaseObject dto) {
        assetInit();
        String camelName = NameUtils.getCamelName(tableInfo.getPkName());
        try {
            Field field = tableInfo.getPojoClass().getDeclaredField(camelName);
            field.setAccessible(true);
            Object o = field.get(dto);
            if (o == null) {
                log.error("The object does not have the primary key value, and the update fails!!!");
                throw new BaseRuntimeException();
            }
            Map<String, Object> paramMap = new HashMap<String, Object>();
            tableInfo.setMapperList(tableInfo.getMapperForAllList());
            paramMap.put("tbl", tableInfo);
            paramMap.put("id", o);
            String template = parseSqlTemplate("update", paramMap);
            log.debug("{}", template);
            Connection conn = getConn(template);
            try {
                return conn.createQuery(template).bind(dto).executeUpdate().getResult();
            } finally {
                close(conn);
            }

        } catch (NoSuchFieldException e) {
            log.error("Getting the object primary key attribute failure", e);
            throw new BaseRuntimeException();
        } catch (IllegalAccessException e) {
            log.error("Getting the object primary key attribute failure", e);
            throw new BaseRuntimeException();
        }
    }

    @Override
    public int updateByCondtion(BaseObject data, BaseObject condition) {
        assetInit();
        String camelName = NameUtils.getCamelName(tableInfo.getPkName());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        tableInfo.setMapperList(tableInfo.getMapperForAllList());
        tableInfo.setConditon(tableInfo.getMapperForAllList());
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("update", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            return conn.createQuery(template).bind(data).bind(condition).executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }

    @Override
    public int updateIgnoreEmpty(BaseObject dto) {
        assetInit();
        return update(dto);
    }

    @Override
    public int updateByConditionIgnoreEmpty(BaseObject data, BaseObject condition) {
        assetInit();
        String camelName = NameUtils.getCamelName(tableInfo.getPkName());
        Map<String, Object> paramMap = new HashMap<String, Object>();
        tableInfo.setMapperList(TableUtils.getMapper(tableInfo.getPojoClass(), data));
        tableInfo.setConditon(TableUtils.getMapper(tableInfo.getPojoClass(), condition));
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("update", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            return conn.createQuery(template).bind(data).bind(condition).executeUpdate().getResult();
        } finally {
            close(conn);
        }


    }

    @Override
    public T findById(Object id) {
        assetInit();
        List<Object> list = new ArrayList<>();
        list.add(id);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ids", list);
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("findByIds", paramMap);
        log.debug("{}", template);
        List lists = returnList(template, null);
        return (T) filterMuilt(lists);
    }

    @Override
    public List<T> findByIds(List ids) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("ids", ids);
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("findByIds", paramMap);
        log.debug("{}", template);
        return getListObject(template);
    }

    @Override
    public T queryForBaseObject(BaseObject dto) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        tableInfo.setMapperList(TableUtils.getMapper(tableInfo.getPojoClass(), dto));
        paramMap.put("tbl", tableInfo);
        String template = parseSqlTemplate("query", paramMap);
        log.debug("{}", template);
        List list = returnList(template, dto);
        return (T) filterMuilt(list);
    }

    @Override
    public T queryForBaseObjectByCriteria(String criteria) {
        assetInit();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        tableInfo.setMapperList(new ArrayList<>());
        paramMap.put("tbl", tableInfo);
        if (StringUtils.isNotBlank(criteria)) {
            paramMap.put(PARAM_CRITERIA, "and " + criteria);
        }
        String template = parseSqlTemplate("query", paramMap);
        log.debug("{}", template);
        List list = returnList(template, null);
        return (T) filterMuilt(list);
    }

    private List returnList(String template, BaseObject dto) {
        Connection conn = getConn(template);
        try {
            Query query = conn.createQuery(template);
            for (Mapper mapper : tableInfo.getMapperForAllList()) {
                query.addColumnMapping(mapper.getColumn(), mapper.getProperty());
            }
            List lists;
            if (dto != null) {
                lists = query.bind(dto).executeAndFetch(tableInfo.getPojoClass());
            } else {
                lists = query.executeAndFetch(tableInfo.getPojoClass());
            }
            return lists;
        } finally {
            close(conn);
        }
    }

    ;


    @Override
    public List query() {
        assetInit();
        String template = "select * from " + tableInfo.getTblName();
        log.debug("{}", template);
        return getListObject(template);
    }

    private List<T> getListObject(String template) {
        Connection conn = getConn(template);
        try {
            Query query = conn.createQuery(template);
            for (Mapper mapper : tableInfo.getMapperForAllList()) {
                query.addColumnMapping(mapper.getColumn(), mapper.getProperty());
            }
            return query.executeAndFetch((Class<T>) tableInfo.getPojoClass());
        } finally {
            close(conn);
        }
    }

    /**
     * 支持mysql及oracle
     *
     * @param pojoOrMap
     * @param begin
     * @param limit
     * @param order
     * @return
     */
    @Override
    public List query(Object pojoOrMap, int begin, int limit, String order) {
        return query(pojoOrMap, null, begin, limit, order);
    }

    @Override
    public List query(Object pojoOrMap, String order) {
        return query(pojoOrMap, 0, 0, order);
    }

    @Override
    public List query(Object pojoOrMap, String criteria, String order) {
        return query(pojoOrMap, criteria, 0, 0, order);
    }

    @Override
    public List<T> query(Object pojoOrMap, String criteria, int begin, int limit, String order) {
        assetInit();
        BaseObject baseObject = null;
        if (pojoOrMap instanceof Map) {
            baseObject = toConvertion((Map) pojoOrMap);
        } else if (pojoOrMap instanceof BaseObject) {
            baseObject = (BaseObject) pojoOrMap;
        }
        Map<String, Object> paramMap = new HashMap();
        tableInfo.setMapperList(baseObject == null ? new ArrayList<>() : TableUtils.getMapper(tableInfo.getPojoClass(), baseObject));
        paramMap.put("tbl", tableInfo);
        if (StringUtils.isNotBlank(order)) {
            paramMap.put(PARAM_ORDER_BY, " order by " + order);
        }
        if (StringUtils.isNotBlank(criteria)) {
            paramMap.put(PARAM_CRITERIA, "and " + criteria);
        }
        String template = "";
        if (BaseUtils.getDataType().indexOf("Oracle") != -1) {
            paramMap.put(PARAM_BEGIN_NUM, begin);
            paramMap.put(PARAM_END_NUM, begin + limit);
            template = parseSqlTemplate("query4Oracle", paramMap);

        } else {
            template = parseSqlTemplate("query", paramMap);
            if (begin >= 0 && limit > 0) {
                template = template + " limit " + begin + ", " + limit + " ";
            }
        }
        log.debug("{}", template);
        return returnList(template, baseObject);
    }

    public Integer queryCount() {
        return queryCount(null, null);
    }

    public Integer queryCount(String criteria) {
        return queryCount(null, criteria);
    }

    public Integer queryCountByTpl(String sqlKey, Map argsMap) {
        assetInit();
        String sql = getSqlMap().get(sqlKey);
        String template = parseSqlTemplate(sql, argsMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            return conn.createQuery(template).executeScalar(Integer.class);
        } finally {
            close(conn);
        }
    }

    @Override
    public Integer queryCount(Object dto) {
        assetInit();
        return queryCount(dto, null);
    }

    @Override
    public Integer queryCount(Object beanOrMap, String criteria) {
        assetInit();
        BaseObject dto;
        if(beanOrMap == null){
            dto = null;
        } else if (beanOrMap instanceof BaseObject) {
            dto = (BaseObject) beanOrMap;
        } else if (beanOrMap instanceof Map) {
            dto = toConvertion((Map) beanOrMap);
        } else if (beanOrMap != null) {
            log.error("beanOrMap Only to inherit the object of BasicObject or Map");
            throw new BaseRuntimeException();
        } else {
            dto = null;
        }
        Map<String, Object> paramMap = new HashMap();
        if (dto == null) {
            tableInfo.setMapperList(new ArrayList<>());

        } else {
            tableInfo.setMapperList(TableUtils.getMapper(tableInfo.getPojoClass(), dto));

        }
        paramMap.put("tbl", tableInfo);
        if (StringUtils.isNotBlank(criteria)) {
            paramMap.put(PARAM_CRITERIA, "and " + criteria);
        }
        String template = parseSqlTemplate("queryCount", paramMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            if (dto != null) {
                return conn.createQuery(template).bind(dto).executeScalar(Integer.class);
            } else {
                return conn.createQuery(template).executeScalar(Integer.class);

            }
        } finally {
            close(conn);
        }
    }

    @Override
    public int executeUpdate(String sqlTemplate, Map argsMap) {
        assetInit();
        if (StringUtils.isBlank(sqlTemplate)) {
            throw new IllegalArgumentException("sqlkey can't be empty");
        }
        String sql = getSqlMap().get(sqlTemplate);
        if (StringUtils.isBlank(sql)) {
            sql = sqlTemplate;
        }
        String template = parseSqlTemplate(sql, argsMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            return conn.createQuery(template).executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }

    @Override
    public List queryForListByBaseObject(String sqlTemplate, BaseObject args) {
        return queryForListByBaseObject(sqlTemplate, args, Map.class);
    }

    @Override
    public List queryForListByBaseObject(String sqlTemplate, BaseObject query, Class elementType) {
        Map<String, Object> paraMap = toConvertion(query);
        return queryForListByMap(sqlTemplate, paraMap, elementType);
    }

    @Override
    public List queryForListByMap(String sqlTemplate, Map queryMap) {
        return queryForListByMap(sqlTemplate, queryMap, Map.class);
    }

    @Override
    public List queryForListByMap(String sqlTemplate, Map queryMap, Class elementType) {
        assetInit();
        if (StringUtils.isBlank(sqlTemplate)) {
            throw new IllegalArgumentException("sqlkey can't be empty");
        }
        String sql = getSqlMap().get(sqlTemplate);
        if (StringUtils.isBlank(sql)) {
            sql = sqlTemplate;
        }
        String template = parseSqlTemplate(sql, queryMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            if (elementType.isAssignableFrom(Map.class)) {
                return conn.createQuery(template).executeAndFetchTable().asList();
            } else if(elementType.isAssignableFrom(tableInfo.getPojoClass())){
                Query query = conn.createQuery(template);
                for (Mapper mapper : tableInfo.getMapperForAllList()) {
                    query.addColumnMapping(mapper.getColumn(), mapper.getProperty());
                }
                return query.executeAndFetch(elementType);
            }else{
                return conn.createQuery(template).executeAndFetch(elementType);
            }

        } finally {
            close(conn);
        }
    }

    @Override
    public T queryForBaseObject(String sqlTemplate, BaseObject query) {
        Map<String, Object> paraMap = toConvertion(query);
        return (T) queryForObject(sqlTemplate, paraMap, tableInfo.getPojoClass());
    }

    @Override
    public T queryForBaseObject(String sqlTemplate, Map queryMap) {
        return (T) queryForObject(sqlTemplate, queryMap, tableInfo.getPojoClass());
    }

    @Override
    public Object queryForObject(String sqlTemplate, BaseObject query, Class requiredType) {
        Map<String, Object> paraMap = toConvertion(query);
        return queryForObject(sqlTemplate, paraMap, requiredType);
    }

    @Override
    public Object queryForObject(String sqlTemplate, Map queryMap, Class requiredType) {
        assetInit();
        if (StringUtils.isBlank(sqlTemplate)) {
            throw new IllegalArgumentException("sqlTemplate can't be empty");
        }
        String sql = getSqlMap().get(sqlTemplate);
        if (StringUtils.isBlank(sql)) {
            sql = sqlTemplate;
        }
        String template = parseSqlTemplate(sql, queryMap);
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            List lists;
            if (requiredType.isAssignableFrom(Map.class)) {
                lists = conn.createQuery(template).executeAndFetchTable().asList();
            } else if (requiredType.isAssignableFrom(tableInfo.getPojoClass())) {
                Query query = conn.createQuery(template);
                for (Mapper mapper : tableInfo.getMapperForAllList()) {
                    query.addColumnMapping(mapper.getColumn(), mapper.getProperty());
                }
                lists = query.executeAndFetch(tableInfo.getPojoClass());
            } else {
                lists = conn.createQuery(template).executeAndFetch(requiredType);
            }
            return filterMuilt(lists);
        } finally {
            close(conn);
        }
    }

    @Override
    public List<T> queryByCriteria(String criteria) {
        assetInit();
        Map<String, Object> paramMap = new HashMap();
        tableInfo.setMapperList(new ArrayList<>());
        paramMap.put("tbl", tableInfo);
        if (StringUtils.isNotBlank(criteria)) {
            paramMap.put(PARAM_CRITERIA, "and " + criteria);
        }
        String template = parseSqlTemplate("query", paramMap);
        log.debug("{}", template);
        return returnList(template, null);
    }

    @Override
    public List<Map<String, Object>> queryByTpl(String sqlTemplate) {
        return queryByTpl(sqlTemplate, null, 0, 0);
    }

    @Override
    public List<Map<String, Object>> queryByTpl(String sqlTemplate, int begin, int limit) {
        return queryByTpl(sqlTemplate, null, begin, limit);
    }

    @Override
    public List<Map<String, Object>> queryByTpl(String sqlTemplate, Object paramObject) {
        return queryByTpl(sqlTemplate, paramObject, -1, -1);
    }

    @Override
    public List<Map<String, Object>> queryByTpl(String sqlTemplate, Object paramObject, int begin, int limit) {
        assetInit();
        Map<String, Object> paramMap = new HashMap();
        if (paramObject instanceof Map) {
            paramMap.putAll((Map) paramObject);
        } else if (paramObject instanceof BaseObject) {
            paramMap.putAll(toConvertion((BaseObject) paramObject));
        }
        if (StringUtils.isBlank(sqlTemplate)) {
            throw new IllegalArgumentException("sqlkey can't be empty");
        }
        String sql = getSqlMap().get(sqlTemplate);
        if (StringUtils.isBlank(sql)) {
            sql = sqlTemplate;
        }
        String template = parseSqlTemplate(sql, paramMap);
        if (begin >= 0 && limit > 0) {
            template = template + " limit " + begin + ", " + limit + " ";
        }
        log.debug("{}", template);
        Connection conn = getConn(template);
        try {
            return conn.createQuery(template).executeAndFetchTable().asList();
        } finally {
            close(conn);
        }
    }

    @Override
    public List<Map<String, Object>> queryByTpl(String sqlTemplate, Map paramMap) {
        return queryForListByTpl(sqlTemplate, paramMap, Map.class);
    }


    @Override
    public Object queryForBaseObjectByTpl(String sqlTemplate) {
        return queryForBaseObjectByTpl(sqlTemplate, null);
    }

    @Override
    public Object queryForBaseObjectByTpl(String sqlTemplate, Object paramObject) {
        return queryForObjectByTpl(sqlTemplate, paramObject, Map.class);
    }

    @Override
    public Object queryForObjectByTpl(String sqlTemplate, Object paramObject, Class requireType) {
        List list = queryForListByTpl(sqlTemplate, paramObject, requireType);
        return filterMuilt(list);
    }

    @Override
    public List queryForListByTpl(String sqlTemplate, Object paramObject, Class requireType) {
        return queryForListByTpl(sqlTemplate, paramObject, requireType, -1, -1);
    }

    public List queryForListByTpl(String sqlTemplate, Object paramObject, Class requireType, int begin, int limit) {
        assetInit();
        Map<String, Object> paramMap = new HashMap();
        if (paramObject instanceof Map) {
            paramMap.putAll((Map) paramObject);
        } else if (paramObject instanceof BaseObject) {
            paramMap.putAll(toConvertion((BaseObject) paramObject));
        }
        if (StringUtils.isBlank(sqlTemplate)) {
            throw new IllegalArgumentException("sqlTemplate can't be empty");
        }
        String sql = getSqlMap().get(sqlTemplate);
        if (StringUtils.isBlank(sql)) {
            sql = sqlTemplate;
        }
        String template = parseSqlTemplate(sql, paramMap);
        if (begin >= 0 && limit > 0) {
            template = template + " limit " + begin + ", " + limit + " ";
        }
        log.debug("{}", sql);
        Connection conn = getConn(sql);
        try {
            if (requireType.isAssignableFrom(Map.class)) {
                return conn.createQuery(template).executeAndFetchTable().asList();
            } else if (requireType.isAssignableFrom(tableInfo.getPojoClass())) {
                Query query = conn.createQuery(template);
                for (Mapper mapper : tableInfo.getMapperForAllList()) {
                    query.addColumnMapping(mapper.getColumn(), mapper.getProperty());
                }
                return query.executeAndFetch(tableInfo.getPojoClass());
            } else {
                return conn.createQuery(template).executeAndFetch(requireType);
            }
        } finally {
            close(conn);
        }
    }


    public Sql2o getSql2o(String sql) {
        if(BaseUtils.getBuilder().getOpenReadyAndWrite()){
            if(StringUtils.startsWithIgnoreCase(sql, "select")){
               return BaseUtils.onlyRead.get(RandomUtils.nextInt(BaseUtils.onlyRead.size()));
            }else{
                return BaseUtils.readWrite.get(RandomUtils.nextInt(BaseUtils.readWrite.size()));
            }
        }else{
            if (null != sql2o) {
                return sql2o;
            }
            this.sql2o = BaseUtils.sql2o;
            return sql2o;
        }
    }


    public Connection getConn(String sql) {
        Connection connection = BaseUtils.connectionThreadLocal.get();
        if (null != connection) {
            return connection;
        }
        return getSql2o(sql).open();
    }

    /**
     * 初始化
     */
    private void init() {
        Boolean isExiste = false;
        Boolean checkTable = true;
        tableInfo = new TableInfo();
        Table table = getClass().getAnnotation(Table.class);
        tableInfo.setTblName(table.table());
        tableInfo.setPkName(table.pk());
        tableInfo.setPojoClass(table.pojo());
        tableInfo.setFileMapper(table.fileMapper());
        List<scorpio.migrate.Column> columns = new ArrayList<>();
        /** 添加映射表列结构*/
        Field[] fields = table.pojo().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            f.setAccessible(true);
            Transient transients = f.getAnnotation(Transient.class);
            if (transients != null) {
                continue;
            }
            IdGenerator idGenerator = f.getAnnotation(IdGenerator.class);
            if (idGenerator != null) {
                tableInfo.setGenerator(idGenerator.value());
                /**自定义id生成*/
                if(idGenerator.value() == Generator.DEFINED){
                    try {
                        tableInfo.setIdDefined((IdDefined)idGenerator.idclass().newInstance());

                    }catch (Exception e){
                        log.error("Iddefined error", e);
                    }
                }
            }
            Column columnAnnotation = f.getAnnotation(Column.class);
            String columnName;
            if (columnAnnotation != null) {
                columnName = columnAnnotation.value();
            } else {
                columnName = NameUtils.getUnderlineName(f.getName());
            }
            Id id = f.getAnnotation(Id.class);
            if (id != null) {
                tableInfo.setPkName(columnName);
            }
            tableInfo.getDbColumnList().add(columnName);
            tableInfo.getDtoAllValueName().add(f.getName());
            Mapper mapper = new Mapper(columnName, f.getName());
            tableInfo.getMapperForAllList().add(mapper);
            if (BaseUtils.getBuilder().getCreate()) {
                if(checkTable){
                    isExiste = Execute.tableExists(table.table());
                    checkTable = false;
                }
                if(!isExiste){
                    createColumn(table, columns, f, idGenerator, columnAnnotation, columnName, id);
                }
            }
        }
       if(columns.size()>0){
            log.info("{}","begin create table-----"+table.table()+"");
           scorpio.migrate.Table table1 = new scorpio.migrate.Table(table.table(), columns.toArray(new scorpio.migrate.Column[0]));
           Execute.createTable(table1);
           log.info("{}","create table-----"+table.table()+" success");
       }
        columns = null;

    }

    private void createColumn(Table table, List<scorpio.migrate.Column> columns, Field f, IdGenerator idGenerator, Column columnAnnotation, String columnName, Id id) {
        Boolean primaryKey = false;
        Boolean autoincrement = false;
        if (StringUtils.equals(table.pk(), columnName) || id != null) {
            primaryKey = true;

        }
        IdGenerator idGenerator1 = f.getAnnotation(IdGenerator.class);
        if (idGenerator1 != null && idGenerator1.value() == Generator.AUTO) {
            autoincrement = true;
        }
        Class javaType = f.getType();
        scorpio.migrate.Column column = null;
        int type = Types.VARCHAR;
        if (columnAnnotation != null) {
            String columnDefined = columnAnnotation.columnDefined();
            Boolean nullable = columnAnnotation.nullable();
            type = columnAnnotation.type();
            int length = columnAnnotation.length();
            String describe = columnAnnotation.describe();
            String defaultValue = columnAnnotation.defaultValue();
            if(StringUtils.isBlank(defaultValue)){
                defaultValue = null;
            }
            column = new scorpio.migrate.Column(columnName, type, length, primaryKey, nullable, defaultValue, columnDefined, autoincrement,describe);
            columns.add(column);
        }else if(id != null || idGenerator != null){
            type = BeanUtils.getSqlType(javaType);
            int length = 11;
            if(type == Types.VARCHAR){
                length = 32;//default this length of id is 32
            }
            column = new scorpio.migrate.Column(columnName, type, length, primaryKey, false, null, null, autoincrement,null);
            columns.add(column);
        }
    }

    /**
     * 是否初始化
     */
    private void assetInit() {
        if (isInit.compareAndSet(1, 2)) {
            init();
        }
    }


    private Map<String, String> getSqlMap() {
        if (lastModifyTime != null && this.sqlMap != null && !BaseUtils.getBuilder().getIsClearCache() && !BaseUtils.getBuilder().getDev()) {
            return this.sqlMap;
        }
        try {
            lock.lock();
            if (lastModifyTime != null && this.sqlMap != null && !BaseUtils.getBuilder().getIsClearCache() && !BaseUtils.getBuilder().getDev()) {
                return this.sqlMap;
            }
            URL url;
            String path = "";
            if (StringUtils.isNotBlank(tableInfo.getFileMapper()) && tableInfo.getFileMapper().endsWith(".sqlmap")) {
                path = tableInfo.getFileMapper();
                url = getClass().getResource(path);

            } else if (StringUtils.isNotBlank(tableInfo.getFileMapper()) && !tableInfo.getFileMapper().endsWith(".sqlmap")) {
                path = tableInfo.getFileMapper() + File.separator + getClass().getSimpleName() + ".sqlmap";
                url = getClass().getResource(path);

            } else {
                path = getClass().getSimpleName() + ".sqlmap";
                url = getClass().getResource(path);
            }
            /** 获取sqlmap 文件url */
            if (url == null) {
                log.debug("{}", " no sqlmapping file find! ");
                return new HashMap<String, String>();
            }
            //如果不在jar中
            InputStream in = null;
            long currentLastmodify;
            if (null != url && url.getFile().indexOf(".jar") < 0) {
                try {
                    in = new FileInputStream(url.toURI().getPath());
                } catch (FileNotFoundException e) {
                    log.error("{}", "not find" + url.toURI().getPath(), e);
                    return new HashMap<String, String>();
                }
                File file = new File(url.toURI());
                currentLastmodify = file.lastModified();
            } else {
                in = this.getClass().getResourceAsStream(path);
                currentLastmodify = 0l;
            }

            if (lastModifyTime == null) {
                /** 初始化加载sqlmap */
                loadSqlMap(url, in);
                lastModifyTime = new AtomicLong(currentLastmodify);

            } else {
                if (currentLastmodify > 0 && currentLastmodify != lastModifyTime.longValue()) {
                    /** sqlmap 有更新，需重新加载 */
                    loadSqlMap(url, in);
                    lastModifyTime = new AtomicLong(currentLastmodify);
                }
            }
        } catch (URISyntaxException e) {
            log.error("{}", "file" + getClass().getSimpleName() + ".sqlmap not find");
            return null;
        } finally {
            BaseUtils.getBuilder().setIsClearCache(false);
            lock.unlock();
        }
        return this.sqlMap;
    }

    private void loadSqlMap(URL url, InputStream fo) {
        Map<String, String> tempUserSqlMap = new HashMap<String, String>();
        try {
            char[] chars = IOUtils.toCharArray(fo, "UTF-8");
            StringBuffer s = new StringBuffer();

            char status = '=';
            String key = "";
            Pattern p = Pattern.compile("\\s+");
            boolean commetStatus = false;
            for (int i = 0; i < chars.length; i++) {


                if (!commetStatus && '/' == chars[i] && '*' == chars[i + 1]) {

                    commetStatus = true;

                } else if (commetStatus && '/' == chars[i] && '*' == chars[i - 1]) {

                    commetStatus = false;

                } else if (!commetStatus) {
                    if ('=' == status && chars[i] == status) {
                        status = ';';
                        key = s.toString();
                        s = new StringBuffer();
                    } else if (';' == status && chars[i] == status) {
                        status = '=';
                        s.toString().replaceAll("\r|\n", "");
                        Matcher m = p.matcher(s);
                        String sql = m.replaceAll(" ");
                        tempUserSqlMap.put(StringUtils.trim(key), StringUtils.trim(sql));
                        s = new StringBuffer();
                    } else {
                        s.append(chars[i]);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            log.error("{}", "not find: " + url, e);

        } catch (IOException e) {
            log.debug("{}", " load sqlmapping file error! ");
        } finally {
            IOUtils.closeQuietly(fo);
        }
        this.sqlMap = tempUserSqlMap;
        tempUserSqlMap = null;
    }

    /**
     * 解析sqlTemplate
     */
    private String parseSqlTemplate(String sqlTemplate, Map paramMap) {
        return ParseTpl.parseSqlTemplate(sqlTemplate, paramMap);
    }

    private Object filterMuilt(List<Object> list) {
        if (list.size() > 1) {
            throw new BaseRuntimeException("Expect to get a result and get multiple results");
        } else if (list == null || list.size() == 0) {
            return null;
        } else {
            return list.get(0);
        }

    }

    /**
     * 链接关闭
     *
     * @param conn
     */
    private void close(Connection conn) {
        /** 不在事务中的时候才手动关闭 */
        if (BaseUtils.connectionThreadLocal.get() == null) {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private InputStream getTestDataInputStream(List<? extends BaseObject> list, List dtoAllValueName) {
        StringBuilder builder = new StringBuilder();
        Object last = dtoAllValueName.get(dtoAllValueName.size() - 1);
        for (BaseObject object : list) {
            for (Object fName : dtoAllValueName) {
                try {
                    Field field = object.getClass().getField(fName + "");
                    field.setAccessible(true);
                    Object o = field.get(object);
                    builder.append(o + "");
                    if (!(last + "").equals(o + "")) {
                        builder.append("\t");
                    }
                } catch (NoSuchFieldException e) {
                    log.error("Get property failure!!!", e);
                } catch (IllegalAccessException e) {
                    log.error("Get property failure!!!", e);
                }

            }
            builder.append("\n");
        }
        byte[] bytes = builder.toString().getBytes();
        InputStream is = new ByteArrayInputStream(bytes);
        return is;
    }

    /**
     * mysql 高效插入方法方法 需要引入mysql驱动
     *
     * @param loadDataSql
     * @param dataStream
     * @return
     * @throws SQLException
     */
    private int bulkLoadFromInputStream(String loadDataSql, InputStream dataStream) throws SQLException {
        if (dataStream == null) {
            log.info("InputStream is null ,No data is imported");
            return 0;
        }
        try (java.sql.Connection con = getSql2o(loadDataSql).getConnectionSource().getConnection();) {

            PreparedStatement statement = con.prepareStatement(loadDataSql);
            int result = 0;

            if (statement.isWrapperFor(com.mysql.jdbc.Statement.class)) {

                com.mysql.jdbc.PreparedStatement mysqlStatement = statement
                        .unwrap(com.mysql.jdbc.PreparedStatement.class);

                mysqlStatement.setLocalInfileInputStream(dataStream);
                result = mysqlStatement.executeUpdate();
                return result;
            }
        } finally {
            if (dataStream != null) {
                try {
                    dataStream.close();
                } catch (IOException e) {
                    log.error("close failure", e);
                }
            }
        }
        return 0;
    }
}
