package scorpio.core;

import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import org.sql2o.Query;
import scorpio.BaseUtils;

import java.io.Serializable;

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
    private int removeByIds(Object[] ids) {
        String sql = "";
        UpdateModel updateModel = new UpdateModel().setTable(super.table);
        if(ids instanceof String[]){
            sql = updateModel.in(super.idName, (String[]) ids).getDeleteSql();
        }else{
            sql = updateModel.criteria(super.idName + "in(" + ids + ")").getDeleteSql();
        }
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            Query query = conn.createQuery(sql);
            return query.executeUpdate().getResult();
        } finally {
            close(conn);
        }
    }
}
