//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package scorpio;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Getter
public final class BaseUtils {
    public static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    public static final List<Sql2o> onlyRead = new ArrayList<>();

    public static final List<Sql2o> readWrite = new ArrayList<>();

    @Getter
    private static String dataType = null;

    @Setter
    @Accessors(chain = true)
    private Boolean isClearCache = false;
    @Setter
    @Accessors(chain = true)
    private Boolean dev = false;
    @Setter
    @Accessors(chain = true)
    private Boolean create = false;
    @Setter
    @Accessors(chain = true)
    private Boolean openReadyAndWrite = false;

    @Setter
    @Accessors(chain = true)
    private Boolean activeRecord = false;

    private static final BaseUtils builder = new BaseUtils();

    public static BaseUtils getBuilder(){
        return builder;
    }


    public static Sql2o sql2o;

    public static Sql2o open(String url, String user, String password) {
        sql2o = new Sql2o(url, user, password);
        log.info("⬢ scorpio-JDBC initializing");
        try {
            dataType = sql2o.getConnectionSource().getConnection().getMetaData().getDatabaseProductName();
            log.info("⬢ 连接数据库是:"+dataType+"");
        } catch (SQLException e) {
            log.error("获取连接数据库类型失败",e);
        }
        return sql2o;
    }

    public static Sql2o open(DataSource dataSource) {
        sql2o = new Sql2o(dataSource);
        log.info("⬢ scorpio-JDBC initializing");
        try {
            dataType = dataSource.getConnection().getMetaData().getDatabaseProductName();
            log.info("⬢ 连接数据库是:"+dataType+"");
        } catch (SQLException e) {
            log.error("获取连接数据库类型失败",e);
        }
        return sql2o;
    }

    public static Sql2o open(Sql2o sql2o_) {
        sql2o = sql2o_;
        log.info("⬢ scorpio-JDBC initializing");
        try {
            dataType = sql2o_.getConnectionSource().getConnection().getMetaData().getDatabaseProductName();
            System.out.println("⬢ 连接数据库是:"+dataType+"");
        } catch (SQLException e) {
            log.error("获取连接数据库类型失败",e);
        }
        return sql2o;
    }

    public static void openOnlyReady(DataSource... dataSource){
        int index = 0;
        for(DataSource ds:dataSource){
            try {
                dataType = ds.getConnection().getMetaData().getDatabaseProductName();
                onlyRead.add(new Sql2o(ds));
                log.info("{}","⬢ only ready database " + ++index +",and database type: "+dataType);
            }catch (SQLException e) {
                log.error("获取连接数据库类型失败",e);
            }

        }
    }

    public static void openWrite(DataSource... dataSource){
        int index = 0;
        for(DataSource ds:dataSource){
            try {
                dataType = ds.getConnection().getMetaData().getDatabaseProductName();
                readWrite.add(new Sql2o(ds));
                log.info("{}","⬢ write database " + ++index +",and database type: "+dataType);
            }catch (SQLException e) {
                log.error("获取连接数据库类型失败",e);
            }

        }
    }

    private static Sql2o getSql2o(String sql) {
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
            return sql2o;
        }
    }


    public static Connection getConn(String sql) {
        Connection connection = BaseUtils.connectionThreadLocal.get();
        if (null != connection) {
            return connection;
        }
        return getSql2o(sql).open();
    }
    /**
     * 原子提交
     *
     * @param supplier
     * @param <T>
     */
    public static <T> T atomic(Supplier<T> supplier) {
        T result = null;
        try {
            Connection connection = sql2o.beginTransaction();
            connectionThreadLocal.set(connection);
            result = supplier.get();
            connection.commit();
        } catch (RuntimeException e) {
            log.warn("Transaction rollback");
            connectionThreadLocal.get().rollback();
            throw e;
        } finally {
            connectionThreadLocal.remove();
            return result;
        }
    }
}
