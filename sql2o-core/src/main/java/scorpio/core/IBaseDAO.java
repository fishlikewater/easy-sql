/*
 * Copyright (c) 2006 HEER Software, Inc. All rights reserved.
 *
 * This software consists of contributions made by many individuals
 * on behalf of Heer R&D.  For more information,
 * please see <http://www.heerit.com/>.
 *
 * Author Heer InfoTech Co., Ltd.
 *
 * replace id: IBaseDAO.java,v 1.3 2006/12/06 01:44:52 zhangsh Exp $
 */
package scorpio.core;



import org.sql2o.Sql2o;

import java.util.List;
import java.util.Map;

/**
 * 对DAO的操作的抽象
 * Tips:
 * 支持bean嵌套对象 select name as teacher.name from ...
 * 其中的name将映射到pojo中的Teacher对象中的name属性,Bean.getTeacher().getName();
 *
 */
public interface IBaseDAO {

    /**
     * PARAM_*
     * 表示basedao在查询时可用的参数
     * 以key value 的形式传进来
     * queryByTPL()
     */
    static final String PARAM_CRITERIA = "criteria";

    static final String PARAM_ORDER_BY = "orderBy";

    static final String PARAM_END_NUM = "end";

    /**
     * 分页记录起始
     */
    static final String PARAM_BEGIN_NUM = "begin";

    static final String PARAM_INTERVAL = "interval";

    /**
     * parammap key模糊查询类型
     */
    static final String PARAM_QUERY_FUZZY_TYPE = "fuzzy";

    /**
     * parammap key排序字符串在parammap里的key
     */
    static String PARAM_ORDERBY = "orderBy";

    /**
     * 抓取pojo的属性名, 抓取子表记录, 适用于一多对查询
     * eg. "testList, left:demoList"
     */
    static String PARAM_FETCH_PROPERTIES = "PARAM_FETCH_PROPERTIES";

    /**=============================*/

    /**
     * 向数据库插入一个新的对象(忽略空值--包括null和空字符串)
     * 如果dto.getId()为空字符的话，会使用UUIDHexGenerator自动生成一个
     * @param dto
     */
    public Object create(BaseObject dto);

    /**
     * 向数据库插入一个新的对象(忽略空值--包括null和空字符串)
     * 如果dto.getId()为空字符的话，会使用UUIDHexGenerator自动生成一个
     * @param map
     */
    public Object create(Map<String, Object> map);

    /**
     * 向数据库插入一个新的对象，id从外部传入，不能为null
     * @param dto
     * @param id
     */
    public void create(BaseObject dto, String id);

    /**
     * 向数据库插入一批新的对象。
     * 如果dto[n].getId()为空字符的话，会使用UUIDHexGenerator自动生成一个
     * @param dtos
     */
    public void create(BaseObject[] dtos);

    /**
     * 向数据库插入一批新的对象。
     * 如果dto[n].getId()为空字符的话，会使用UUIDHexGenerator自动生成一个
     * @param dtos
     */
    public void create4Batch(List<? extends BaseObject> dtos);


    /**
     * 新增并返回主键的值
     * @param dto
     * @return
     */
    public String createAndId(BaseObject dto);

    /**
     * 删除一条记录
     * <p>
     * <b>注意：这个方法只支持具有主键约束的对象(不支持视图)</b>
     * @param id
     */
    public void removeById(Object id);

    /**
     * 删除id在参数ids中的记录
     * <p>
     * <b>注意：这个方法只支持具有主键约束的对象(不支持视图)</b>
     * @param ids
     */
    public void remove(String[] ids);

    /**
     * 删除id在参数ids中的记录
     * <p>
     * <b>注意：这个方法只支持具有主键约束的对象(不支持视图)</b>
     * @param ids
     */
    public void remove(Integer[] ids);
    /**
     * 删除符合条件的记录
     * 使用这个方法的时候需要注意，如果传入的dto里没有任何字段被赋值，则有可能删除整个
     * 表的记录
     * @param dto
     * @return 删除记录数
     */
    public int removeByCondition(BaseObject dto);

    /**
     * 删除符合条件的记录
     * 使用这个方法的时候需要注意，如果传入的dto里没有任何字段被赋值，则有可能删除整个
     * 表的记录
     * @param map 其中的key要与pojo属性值对应
     * @return 删除记录数
     */
    public int removeByCondition(Map<String, Object> map);

    /**
     * 删除符合条件的记录
     * 表的记录
     * @param criteria sql条件,如果criteria为blank则不进行删除
     * @return 删除记录数
     */
    public int removeByCriteria(String criteria);

    /**
     * 删除所有记录
     * @return
     */
    public int removeAll();

    /**
     * 根据id进行更新的接口
     * @param dto
     */
    public int update(BaseObject dto);

    /**
     * 根据id进行更新的接口
     * @param map
     */
    public int update(Map<String, Object> map);

    /**
     * 根据id进行更新的接口
     * @param dto
     */
    public int updateNotIgnoreNull(BaseObject dto);

    /**
     * 根据其他条件进行更新的接口
     * 使用这个方法的时候需要注意，如果传入的condition里没有任何字段被赋值，
     * 则有可能更新整个表的记录
     * @param data
     * @param condition
     */
    public int updateByCondtion(BaseObject data, BaseObject condition);

    /**
     * 根据id进行更新，但是忽略所有为null和空字符串的field
     * @param dto
     */
    public int updateIgnoreEmpty(BaseObject dto);

    /**
     * 根据其他条件进行更新，忽略所有为null和空字符串的field
     * 使用这个方法的时候需要注意，如果传入的condition里没有任何字段被赋值，
     * 则有可能更新整个表的记录
     * @param data
     * @param condition
     */
    public int updateByConditionIgnoreEmpty(BaseObject data, BaseObject condition);

    /**
     * 通过id查找对象
     * <p>
     * <b>使用此方法需要注意，如果数据库对象没有主键，系统默认利用属性中的id进行查询，需要保证属性中的id与数据库字段要对应</b>
     * @param id
     * @return
     */
    public Object findById(Object id);
    public List findByIds(List ids);

    /**
     * 查询单个对象
     * @param pojoOrMap
     * @return 查询结果，如果不存在则返回null
     */
    public BaseObject queryForBaseObject(BaseObject pojoOrMap);

    /**
     * 根据sql查询某个对象
     * @param criteria
     * @return 查询结果，如果不存在则返回null
     */
    public Object queryForBaseObjectByCriteria(String criteria);

    /*public BaseObject queryForBaseObject(BaseObject BaseObject, Map paramMap);*/

    /**
     * 通过传入的条件查找对象，支持翻页，支持排序
     * @param pojoOrMap
     * @param begin
     * @param interval
     * @param order
     * @return List 记录集合
     */
    public List query(Object pojoOrMap, int begin, int interval, String order);

    public List query();
    /**
     * 根据条件查找出符合条件的所有记录，可以排序
     * @param pojoOrMap
     * @param order
     * @return
     */
    public List query(Object pojoOrMap, String order);

    /**
     * 根据条件查找出符合条件的所有记录，可以排序
     * @param pojoOrMap
     * @param criteria sql查询条件
     * @param order 排序方式
     * @return
     */
    public List query(Object pojoOrMap, String criteria, String order);
    /**
     * 根据条件查找出符合条件的所有记录，可以排序
     * @param pojoOrMap
     * @param criteria sql查询条件
     * @param begin
     * @param interval
     * @param order 排序方式
     * @return
     */
    public List query(Object pojoOrMap, String criteria, int begin, int interval, String order);

   /* public List queryByObjectAndMap(BaseObject object, Map paramMap);*/

    /**
     * 根据条件查找符合条件的所有记录的总数
     * @param dto or map or sql查询条件
     * @return
     */
    public Integer queryCount(Object dto);

    /**
     * 同时根据查询对象和sql语句约束条件查询记录总数
     * @param beanOrMap 查询对象
     * @param criteria sql查询条件
     * @return
     */
    public Integer queryCount(Object beanOrMap, String criteria);

    /***********start 定义：扩展IBaseDAO接口的类可能需要调用的方法***********/

    /**
     * 批量更新<b>sqlKey</b>配置的sql语句
     * @param sqlKey 自定义的sql map对应的key
     * @param argsMap 参数Map
     * @return 更新记录数
     */
    public int executeUpdate(final String sqlKey, final Map argsMap);

    /**
     * 根据sql key查询列表
     * 配置sql语句时：使用对象属性时需要加前缀dto.<p>
     * 例如:  test_id=#${dto.testId}#
     * @param sqlKey 自定义的sql map对应的key
     * @param args 查询对象
     */
    public List queryForListByBaseObject(final String sqlKey, final BaseObject args);

    /**
     * 根据sql key查询列表,返回参数elementType的数据<p>
     * 配置sql语句时：使用对象属性时需要加前缀dto.<p>
     * 例如:  test_id=#${dto.testId}#
     * @param sqlKey 自定义的sql map对应的key
     * @param query 查询对象
     * @pram elementType
     * @return List nested elementType
     */
    public List queryForListByBaseObject(final String sqlKey, final BaseObject query, final Class elementType);

    /**
     * 根据sql key查询列表
     * @param sqlKey 自定义的sql map对应的key
     * @param queryMap 查询参数
     * @return List
     */
    public List queryForListByMap(final String sqlKey, final Map queryMap);

    /**
     * 根据sql key查询列表,返回参数elementType的数据<p>
     * 配置sql语句时：使用对象属性时需要加前缀dto.<p>
     * 例如:  test_id=#${dto.testId}#
     * @param sqlKey 自定义的sql map对应的key
     * @param queryMap 查询参数
     * @pram elementType
     * @return List nested elementType
     */
    public List queryForListByMap(final String sqlKey, final Map queryMap, final Class elementType);

    /**
     * 根据sql key查询某条记录,记录对应的类型为BaseObject
     * @param sqlKey 自定义的sql map对应的key
     * @param query 查询对象
     * @return BaseObject
     */
    public BaseObject queryForBaseObject(final String sqlKey, final BaseObject query);

    /**
     * 根据sql key查询某条记录,记录对应的类型为BaseObject
     * @param sqlKey 自定义的sql map对应的key
     * @param queryMap 查询参数
     * @return BaseObject
     */
    public BaseObject queryForBaseObject(final String sqlKey, final Map queryMap);

    /**
     * 根据sql key查询某个字段信息
     * @param sqlKey 自定义的sql map对应的key
     * @param query 查询对象
     * @param requiredType 返回结果对应的类类型
     * @return BaseObject
     */
    public Object queryForObject(final String sqlKey, final BaseObject query, Class requiredType);

    /**
     * 查询某个字段信息,返回参数requiredType的数据<p>
     * 配置sql语句时：使用对象属性时需要加前缀dto.<p>
     * 例如:  test_id=#${dto.testId}#
     * @param sqlKey 自定义的sql map对应的key
     * @param queryMap 查询参数
     * @pram elementType
     * @return List nested requiredType
     */
    public Object queryForObject(final String sqlKey, final Map queryMap, Class requiredType);


   /* public Integer queryCount(BaseObject dto, Map queryParamMap);*/

    public List<? extends BaseObject> queryByCriteria(String criteria);


    public List queryByTpl(String sqlTemplate);

    public List queryByTpl(String sqlTemplate, int begin, int interval);

    public List queryByTpl(String sqlTemplate, Object paramObject);

    public List queryByTpl(String sqlTemplate, Object paramObject, int begin, int end);

    /**
     * @param sqlTemplate
     * @param paramMap 查询参数定义Map
     * @return
     */
    public List queryByTpl(String sqlTemplate, Map paramMap);

   /* public List queryByTpl(String sqlTemplate, Map paramMap, int begin, int end);*/

    public Object queryForBaseObjectByTpl(String sqlTemplate);

    public Object queryForBaseObjectByTpl(String sqlTemplate, Object paramObject);

    public Object queryForObjectByTpl(String sqlTemplate, Object paramObject, Class requireType);

    /**
     * 查询得到基本类型, 用于一个字段的查询
     * @param sqlTemplate
     * @param paramObject
     * @param requireType
     * @return
     */
    public List queryForListByTpl(String sqlTemplate, Object paramObject, Class requireType);

    public Sql2o getSql2o(String sql);

}
