package scorpio.core;

import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Query;
import scorpio.BaseUtils;
import scorpio.exception.BaseRuntimeException;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangx
 * @date 20171002
 * @email fishlikewater@126.com
 * 该类主要扩展操作(sql与代码分离，以.sqlmap为后缀的文件来存储sql语句，每句sql以K-V的方式)
 */
@Slf4j
public abstract class BaseMapper<T extends BaseModel> extends Model<T> implements Serializable {

    /**
     * 通过对象创建数据
     * @param t 保存对象
     * @return
     */
    public  Object save(T t) {
        String sql = new InsertModel().setTable(table).setMapping(mapping).getSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            return conn.createQuery(sql).bind(t).executeUpdate().getKey();
        }finally {
            close(conn);
        }
    }
    public Object saveIgnoreId(T t){
        String sql = new InsertModel().setIdName(idName).setIgnoreId(true).setTable(table).setMapping(mapping).getSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            return conn.createQuery(sql).bind(t).executeUpdate().getKey();
        }finally {
            close(conn);
        }
    }

    @Override
    protected void assetInit() {
        if (super.isInit.compareAndSet(1, 2)) {
            super.init();
        }
    }
    /**
     * 批量创建对象
     *
     * @param dtos
     */
    public void save(T[] dtos, boolean ignoreId) {
        String sql = new InsertModel()
                .setTable(super.table)
                .setMapping(super.mapping)
                .setIgnoreId(ignoreId)
                .getSql();

        log.debug("{}", sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            Query query = conn.createQuery(sql);
            for (T t : dtos) {
                query.bind(t).addToBatch();
            }
            query.executeBatch();
        } finally {
            close(conn);
        }
    }

    /**
     * 根据id删除数据
     *
     * @param ids
     */
    public int removeByIds(Object[] ids) {
        String sql = "";
        UpdateModel updateModel = new UpdateModel().setTable(super.table);
        sql = updateModel.in(super.idName, ids).getDeleteSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            Query query = conn.createQuery(sql);
            return query.executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }

    /**
     *  更新对象
     * @param t
     * @return
     */
    public int updateById(T t){
        return updateById(t, null);
    }

    /**
     *  更新对象
     * @param t
     * @return
     */
    public int updateById(T t, List notSet){
        UpdateModel updateModel = new UpdateModel().setTable(super.table);
        StringBuffer setSql = new StringBuffer();
        setSql.append(" ");
        super.mapping.forEach((k, v)->{
            if(!idName.equals(k)){
                if(notSet == null || !notSet.contains(k)){
                    setSql.append(k).append("=:").append(v).append(",");
                }
            }
        });
        setSql.deleteCharAt(setSql.length()-1);
        updateModel.set(setSql.toString());
        Object idValue = super.getIdValue(t);
        if(idValue == null){
            throw new BaseRuntimeException("not found id value");
        }
        updateModel.equal(idName, idValue);
        String sql = updateModel.getUpdateSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            Query query = conn.createQuery(sql);
            return query.bind(t).executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }

    /*@Override
    protected void loadSqlMap(){
        String path = "";
        Class<? extends BaseMapper> aClass = this.getClass();
        Map<String, String> sqlCahceMap = SqlMapUtils.getSqlCahceMap(tClass.getSimpleName());
        if(sqlCahceMap != null && !BaseUtils.getBuilder().getDev()){
            this.sqlMap.putAll(sqlCahceMap);
        }else{
            Table tbl = aClass.getAnnotation(Table.class);
            if(tbl != null){
                String table = tbl.table();
                if(StringUtils.isNotBlank(table)){
                    this.table = table;
                }
                if(StringUtils.isNotBlank(tbl.fileMapper())){
                    if(tbl.fileMapper().endsWith(".sqlmap")){
                        path = tbl.fileMapper();
                    }else{
                        path = tbl.fileMapper() + "/" + tClass.getSimpleName() + ".sqlmap";
                    }

                }else{
                    path =  tClass.getSimpleName() + ".sqlmap";
                }
            }else{
                path = tClass.getSimpleName() + ".sqlmap";
            }
            Map<String, String> sqlMap = SqlMapUtils.getSqlMap(path, tClass);
            this.sqlMap.putAll(sqlMap);
            SqlMapUtils.cacheSqlMap(tClass.getSimpleName(), sqlMap);
        }
    }*/
}
