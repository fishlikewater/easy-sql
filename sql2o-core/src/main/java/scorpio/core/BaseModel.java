package scorpio.core;

import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import scorpio.BaseUtils;

/**
 * @author <p>fishlikewater@126.com</p>
 * @date 2019年04月17日 15:50
 * @since 1.6
 **/
@Slf4j
@EqualsAndHashCode(callSuper=false)
public abstract class BaseModel<T extends BaseModel> extends Model<T> {

    /**
     * 保存对象
     * @return
     */
    public Object save(){
        String sql = new InsertModel().setTable(table).setMapping(mapping).getSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            return conn.createQuery(sql).bind(this).executeUpdate().getKey();
        }finally {
            close(conn);
        }
    }

    /**
     * 保存对象
     * @return
     */
    public Object saveIgnoreId(){
        String sql = new InsertModel().setIdName(idName).setIgnoreId(true).setTable(table).setMapping(mapping).getSql();
        log.debug(sql);
        Connection conn = BaseUtils.getConn(sql);
        try {
            return conn.createQuery(sql).bind(this).executeUpdate().getKey();
        }finally {
            close(conn);
        }
    }

}
