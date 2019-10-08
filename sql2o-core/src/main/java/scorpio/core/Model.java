package scorpio.core;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import scorpio.BaseUtils;
import scorpio.annotation.Column;
import scorpio.annotation.Id;
import scorpio.annotation.Table;
import scorpio.annotation.Transient;
import scorpio.exception.BaseRuntimeException;
import scorpio.utils.NameUtils;
import scorpio.utils.SqlMapUtils;
import scorpio.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年04月19日 20:22
 * @since
 **/
@Slf4j
@EqualsAndHashCode(callSuper=false)
public abstract class Model<T> {

    @Transient
    protected Map<String, String> sqlMap = new HashMap<>(); //存储所有的sqlmap
    @Transient
    private String sqlmapPath;

    @Transient
    protected Map<String, String> mapping = new HashMap<>();
    @Transient
    protected String table;
    @Transient
    protected Class<T> tClass;
    @Transient
    protected String idName;

    @Transient
    protected Sql2o sql2o;
    @Transient
    protected AtomicInteger isInit = new AtomicInteger(1);

    protected Model(){
        assetInit();
    }

    public Object getIdValue(T t){
        try {
            Field idFiled = tClass.getDeclaredField(idName);
            idFiled.setAccessible(true);
            return idFiled.get(t);
        }catch (Exception e){
            log.error("不能获取主键值", e);
        }
        return null;

    }

    /**
     *  查询数量
     * @param queryModel
     * @return
     */
    public Integer count(QueryModel queryModel){
        queryModel.setFiledSet(mapping.keySet());
        queryModel.setTable(table);
        if(queryModel.isUseTpl()){
            queryModel.setSqlTemplate(getTpySql(queryModel.getTemplateNameOrSql()));
        }
        String sql = queryModel.getCountSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            return conn.createQuery(sql).executeScalar(Integer.class);
        }finally {
            close(conn);
        }
    }

    /**
     * 查询返回list集合
     * @param queryModel 条件构造
     * @return 查询list集合
     */
    public <V> List<V> list(QueryModel queryModel, Class<V> v){
        String sql = getQuery(queryModel);
        Connection conn = BaseUtils.getConn(sql);
        try{
            Query query = conn.createQuery(sql);
            if(queryModel.mappings != null){
                query.setColumnMappings(queryModel.mappings);
            }
            return query.executeAndFetch(v);
        }finally {
            close(conn);
        }
    }

    /**
     *  查询单个对象
     * @param queryModel 条件构造
     * @return 单个对象
     */
    public <V> V object(QueryModel queryModel, Class<V> v){
        String sql = getQuery(queryModel);
        Connection conn = BaseUtils.getConn(sql);
        try{
            Query query = conn.createQuery(sql);
            if(queryModel.mappings != null){
                query.setColumnMappings(queryModel.mappings);
            }
            return query.executeAndFetchFirst(v);
        }finally {
            close(conn);
        }
    }


    /**
     *
     * @param id id
     * @return 通过主键查询对象
     */
    public T findById(Object id){
        String sql = "";
        if(id instanceof String){
            sql = initQueryModel().equal(idName, id.toString()).getSql();
        }else{
            sql = initQueryModel().criteria(idName + "=" + id).getSql();
        }
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            Query query = conn.createQuery(sql);
            return query.setColumnMappings(mapping).executeAndFetchFirst(tClass);
        }finally {
            close(conn);
        }
    }

    /**
     *
     * @param id id
     * @return 通过主键删除对象
     */
    public int removeById(Object id){
        String sql = "";
        UpdateModel updateModel = new UpdateModel().setTable(table);
        if(id instanceof String){
            sql = updateModel.equal(idName, id.toString()).getDeleteSql();
        }else{
            sql = updateModel.criteria(idName + "=" + id).getDeleteSql();
        }
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            Query query = conn.createQuery(sql);
            return query.executeUpdate().getResult();
        }finally {
            close(conn);
        }
    }

    /**
     * 查询返回list集合
     * @param queryModel 条件构造
     * @return 查询list集合
     */
    public List<T> list(QueryModel queryModel){
        String sql = getQuery(queryModel);
        Connection conn = BaseUtils.getConn(sql);
        try{
            return conn.createQuery(sql).setColumnMappings(mapping).executeAndFetch(tClass);
        }finally {
            close(conn);
        }
    }

    /**
     * 查询返回List<Map<String, Object>>集合
     * @param queryModel 条件构造
     * @return 查询list集合
     */
    public List<Map<String, Object>> maps(QueryModel queryModel){
        String sql = getQuery(queryModel);
        Connection conn = BaseUtils.getConn(sql);
        try{
            return conn.createQuery(sql).executeAndFetchTable().asList();
        }finally {
            close(conn);
        }
    }

    /**
     *  查询单个对象
     * @param queryModel 条件构造
     * @return 单个对象
     */
    public T object(QueryModel queryModel){
        String sql = getQuery(queryModel);
        Connection conn = BaseUtils.getConn(sql);
        try{
            return conn.createQuery(sql).setColumnMappings(mapping).executeAndFetchFirst(tClass);
        }finally {
            close(conn);
        }
    }

    /**
     *  查询map对象
     * @param queryModel 条件构造
     * @return map对象
     */
    public Map map(QueryModel queryModel){
        List<Map<String, Object>> maps = maps(queryModel);
        if(maps.size() >1){
            throw new BaseRuntimeException("期望返回一条数据，实际查询到多条");
        }else if(maps.size() == 1){
            return maps.get(0);
        }else{
            return new HashMap();
        }
    }

    /**
     *  update操作
     * @param updateModel 更新条件构造
     * @return 更新数量
     */
    public int update(UpdateModel updateModel){
        String sql = getUpdateQuery(updateModel);
        Connection conn = BaseUtils.getConn(sql);
        try{
            return conn.createQuery(sql).executeUpdate().getResult();
        }finally {
            close(conn);
        }
    }

    /**
     *  delete操作
     * @param updateModel 删除条件构造
     * @return 删除数量
     */
    public int remove(UpdateModel updateModel){
        String sql = getDeleteQuery(updateModel);
        Connection conn = BaseUtils.getConn(sql);
        try{
            return conn.createQuery(sql).executeUpdate().getResult();
        }finally {
            close(conn);
        }

    }


    private QueryModel initQueryModel(){

        return  new QueryModel().setTable(table).setFiledSet(mapping.keySet());
    }

    private String getQuery(QueryModel queryModel){
        if(queryModel.isUseTpl()){
            queryModel.setSqlTemplate(getTpySql(queryModel.getTemplateNameOrSql()));
        }
        queryModel.setFiledSet(mapping.keySet());
        queryModel.setTable(table);
        String sql = queryModel.getSql();
        log.debug(sql);
        return sql;
    }

    private String getUpdateQuery(UpdateModel updateModel){
        if(updateModel.isUseTpl()){
            updateModel.setSqlTemplate(getTpySql(updateModel.getTemplateNameOrSql()));
        }
        updateModel.setTable(table);
        String sql = updateModel.getUpdateSql();
        log.debug(sql);
        return sql;
    }

    private String getDeleteQuery(UpdateModel updateModel){
        if(updateModel.isUseTpl()){
            updateModel.setSqlTemplate(getTpySql(updateModel.getTemplateNameOrSql()));
        }
        updateModel.setTable(table);
        String sql = updateModel.getDeleteSql();
        log.debug(sql);
        return sql;
    }

    /**
     * 释放链接
     *
     * @param conn
     */
    protected void close(Connection conn) {
        /** 不在事务中的时候才手动关闭 */
        if (BaseUtils.connectionThreadLocal.get() == null) {
            if (conn != null) {
                conn.close();
            }
        }
    }


    protected String getTpySql(String templateNameOrSql){
        String sql = null;
        if (!BaseUtils.getBuilder().getDev()){
            sql = this.sqlMap.get(templateNameOrSql);
        }else{
            Map<String, String> sqlMap = SqlMapUtils.getSqlMap(sqlmapPath, tClass);
            sql = sqlMap.get(templateNameOrSql);
        }
        if(sql == null){
            return templateNameOrSql;
            //throw new BaseRuntimeException("模板sql不存在...");
        }else {
            return sql;
        }
    }


    /**
     * 是否初始化
     */
    protected void assetInit() {
        if(!BaseUtils.getBuilder().getActiveRecord()){
            return;
        }
        if (isInit.compareAndSet(1, 2)) {
            init();
        }
    }

    protected void init(){
        if(!BaseUtils.getBuilder().getDev() && BaseUtils.getBuilder().getActiveRecord()){
            ModelCache.Model<T> cache = ModelCache.getCache(getClass());
            if(cache != null){
                this.sqlMap.putAll(cache.getSqlMap());
                this.idName = cache.getIdName();
                this.mapping = cache.getMapping();
                this.table = cache.getTableName();
                this.tClass = cache.getTClass();
                return;
            }
        }
        tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Transient aTransient = declaredField.getAnnotation(Transient.class);
            if(aTransient != null){
                continue;
            }
            Id id = declaredField.getAnnotation(Id.class);
            Column column = declaredField.getAnnotation(Column.class);
            if(id != null){
                idName = NameUtils.getUnderlineName(declaredField.getName());
                if(column != null && StringUtils.isNotBlank(column.value())){
                    idName = column.value();
                }
            }
            if(column != null && StringUtils.isNotBlank(column.value())){
                mapping.put(column.value(), declaredField.getName());
            }else {
                mapping.put(NameUtils.getUnderlineName(declaredField.getName()), declaredField.getName());
            }
        }
        this.table = NameUtils.getUnderlineName(tClass.getSimpleName());
        loadSqlMap();
    }


    protected void loadSqlMap(){
        /**  ActiveRecord 模式下 对新new的对象 取已经缓存过的sql文件*/
        /*if(BaseUtils.getBuilder().getActiveRecord() && !BaseUtils.getBuilder().getDev()){
            Map<String, String> sqlCahceMap = SqlMapUtils.getSqlCahceMap(tClass.getSimpleName());
            if(sqlCahceMap != null){
                this.sqlMap.putAll(sqlCahceMap);
                return;
            }
        }*/
        Table tbl = this.getClass().getAnnotation(Table.class);
        if(tbl != null){
            String table = tbl.table();
            if(StringUtils.isNotBlank(table)){
                this.table = table;
            }
            if(StringUtils.isNotBlank(tbl.fileMapper())){
                if(tbl.fileMapper().endsWith(".sqlmap")){
                    sqlmapPath = tbl.fileMapper();
                }else{
                    sqlmapPath = tbl.fileMapper() + "/" + tClass.getSimpleName() + ".sqlmap";
                }

            }else{
                sqlmapPath =  tClass.getSimpleName() + ".sqlmap";
            }
        }else{
            sqlmapPath = tClass.getSimpleName() + ".sqlmap";
        }
        Map<String, String> sqlMap = SqlMapUtils.getSqlMap(sqlmapPath, tClass);
        if(!BaseUtils.getBuilder().getDev() && BaseUtils.getBuilder().getActiveRecord()){
            this.sqlMap.putAll(sqlMap);
            ModelCache.Model<T> model = new ModelCache().new Model<T>();
            model.setIdName(this.idName);
            model.setMapping(this.mapping);
            model.setSqlMap(this.sqlMap);
            model.setTableName(this.table);
            model.setTClass(tClass);
            ModelCache.addCache(getClass(), model);

        }
       /* if(!BaseUtils.getBuilder().getDev()){

            this.sqlMap.putAll(sqlMap);
            if(BaseUtils.getBuilder().getActiveRecord()){//如果开启ActiveRecord 则缓存 则将sql缓存 避免每次new 对象时都加载一次sql文件
                SqlMapUtils.cacheSqlMap(tClass.getSimpleName(), sqlMap);
            }
        }*/
    }

}
