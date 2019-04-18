package scorpio.core;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import scorpio.BaseUtils;
import scorpio.annotation.Column;
import scorpio.annotation.Id;
import scorpio.annotation.Mapping;
import scorpio.annotation.Transient;
import scorpio.utils.NameUtils;
import scorpio.utils.SqlMapUtils;
import scorpio.utils.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <p>fishlikewater@126.com</p>
 * @date 2019年04月17日 15:50
 * @since 1.6
 **/
@Slf4j
@EqualsAndHashCode(callSuper=false)
public abstract class BaseModel<T extends BaseModel> {

    @Transient
    protected Map<String, String> sqlMap = new HashMap<>(); //存储所有的sqlmap

    @Transient
    private Map<String, String> mapping = new HashMap<>();
    @Transient
    private String table;
    @Transient
    private Class<T> tClass;
    @Transient
    private String idName;

    @Transient
    protected Sql2o sql2o;
    @Transient
    private AtomicInteger isInit = new AtomicInteger(1);

    protected BaseModel(){
        assetInit();
    }

    /**
     *  查询数量
     * @param queryModel
     * @return
     */
    public Integer count(QueryModel queryModel){
        queryModel.setFileSet(mapping.keySet());
        queryModel.setTable(table);
        String sql = queryModel.getCountSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        return conn.createQuery(sql).executeScalar(Integer.class);
    }

    /**
     * 保存对象
     * @return
     */
    public Object save(){
        String sql = new InsertModel().setTable(table).setMapping(mapping).getSql();
        log.debug(sql);
        Query query = BaseUtils.getConn(sql).createQuery(sql);
        return query.bind(this).executeUpdate().getKey();
    }

    /**
     * 查询返回list集合
     * @param queryModel 条件构造
     * @return 查询list集合
     */
    public <V> List<V> list(QueryModel queryModel, Class<V> v){

        return getQuery(queryModel).setColumnMappings(mapping).executeAndFetch(v);
    }

    /**
     *  查询单个对象
     * @param queryModel 条件构造
     * @return 单个对象
     */
    public <V> V object(QueryModel queryModel, Class<V> v){

        return getQuery(queryModel).setColumnMappings(mapping).executeAndFetchFirst(v);
    }


    /**
     *
     * @param id id
     * @return 查询对象
     */
    public T findById(Object id){
        String sql = "";
        if(id instanceof String){
            sql = initQueryModel().equal(idName, id.toString()).getSql();
        }else{
            sql = initQueryModel().criteria(idName + "=" + id).getSql();
        }
        log.debug(sql);
        Query query = BaseUtils.getConn(sql).createQuery(sql);
        return query.setColumnMappings(mapping).executeAndFetchFirst(tClass);
    }

    /**
     * 查询返回list集合
     * @param queryModel 条件构造
     * @return 查询list集合
     */
    public List<T> list(QueryModel queryModel){

        return  getQuery(queryModel).setColumnMappings(mapping).executeAndFetch(tClass);
    }

    /**
     * 查询返回List<Map<String, Object>>集合
     * @param queryModel 条件构造
     * @return 查询list集合
     */
    public List<Map<String, Object>> maps(QueryModel queryModel){

        return  getQuery(queryModel).executeAndFetchTable().asList();
    }

    /**
     *  查询单个对象
     * @param queryModel 条件构造
     * @return 单个对象
     */
    public T object(QueryModel queryModel){

        return getQuery(queryModel).setColumnMappings(mapping).executeAndFetchFirst(tClass);
    }

    /**
     *  查询map对象
     * @param queryModel 条件构造
     * @return map对象
     */
    public Map map(QueryModel queryModel){

        return getQuery(queryModel).executeAndFetchFirst(Map.class);
    }

    public int update(UpdateModel updateModel){

        return getUpdateQuery(updateModel).executeUpdate().getResult();
    }

    public int delete(UpdateModel updateModel){

        return getDeleteQuery(updateModel).executeUpdate().getResult();
    }


    private QueryModel initQueryModel(){

        return  new QueryModel().setTable(table).setFileSet(mapping.keySet());
    }

    private Query getQuery(QueryModel queryModel){
        if(queryModel.isUseTpl()){
            queryModel.setSqlTemplate(this.sqlMap.get(queryModel.getTemplateName()));
        }
        queryModel.setFileSet(mapping.keySet());
        queryModel.setTable(table);
        String sql = queryModel.getSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        return conn.createQuery(sql);
    }

    private Query getUpdateQuery(UpdateModel updateModel){
        if(updateModel.isUseTpl()){
            updateModel.setSqlTemplate(this.sqlMap.get(updateModel.getTemplateName()));
        }
        updateModel.setTable(table);
        String sql = updateModel.getUpdateSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        return conn.createQuery(sql);
    }

    private Query getDeleteQuery(UpdateModel updateModel){
        if(updateModel.isUseTpl()){
            updateModel.setSqlTemplate(this.sqlMap.get(updateModel.getTemplateName()));
        }
        updateModel.setTable(table);
        String sql = updateModel.getDeleteSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        return conn.createQuery(sql);
    }

    /**
     * 是否初始化
     */
    private void assetInit() {
        if (isInit.compareAndSet(1, 2)) {
            init();
        }
    }

    private void init(){
        Class<? extends BaseModel> aClass = getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Transient aTransient = declaredField.getAnnotation(Transient.class);
            if(aTransient != null){
                continue;
            }
            Id id = declaredField.getAnnotation(Id.class);
            Column column = declaredField.getAnnotation(Column.class);
            if(id != null){
                idName = NameUtils.getUnderlineName(declaredField.getName());
                if(column != null){
                    idName = column.value();
                }
            }
            if(column != null){
                mapping.put(column.value(), declaredField.getName());
            }else {
                mapping.put(NameUtils.getUnderlineName(declaredField.getName()), declaredField.getName());
            }
        }
        this.table = NameUtils.getUnderlineName(aClass.getSimpleName());
        tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        loadSqlMap();
    }


    private void loadSqlMap(){
        String path = "";
        Map<String, String> sqlCahceMap = SqlMapUtils.getSqlCahceMap(tClass.getSimpleName());
        if(sqlCahceMap != null){
            this.sqlMap.putAll(sqlCahceMap);
        }else{
            Mapping mapping = tClass.getAnnotation(Mapping.class);
            if(mapping != null){
                String table = mapping.table();
                if(StringUtils.isNotBlank(table)){
                    this.table = table;
                }
                if(StringUtils.isNotBlank(mapping.fileMapper())){
                    if(mapping.fileMapper().endsWith(".sqlmap")){
                        path = mapping.fileMapper();
                    }else{
                        path = mapping.fileMapper() + File.separator + tClass.getSimpleName() + ".sqlmap";
                    }

                }else{
                    path = tClass.getSimpleName() + ".sqlmap";
                }
            }else{
                path = tClass.getSimpleName() + ".sqlmap";
            }
            Map<String, String> sqlMap = SqlMapUtils.getSqlMap(path, tClass);
            this.sqlMap = sqlMap;
            SqlMapUtils.cacheSqlMap(tClass.getSimpleName(), sqlMap);
        }
    }

}
