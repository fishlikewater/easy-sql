package com.github.fishlikewater.autotable;

import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import scorpio.BaseUtils;
import scorpio.core.BaseModel;
import scorpio.enums.IEnum;

import java.lang.reflect.ParameterizedType;
import java.sql.Types;
import java.time.LocalDateTime;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 14:04
 * @since
 **/
@Slf4j
public class SqliteTable implements AutoTable {


    public void excutor(Class<? extends BaseModel> c, String tableName, String dbName){
        String queryTable = "select count(*) from sqlite_master where tbl_name='"+tableName+"'";
        Connection conn = BaseUtils.sql2o.open();
        Integer count = conn.createQuery(queryTable).executeScalar(int.class);
        if(count == 0){
            String sql = getSql(c, tableName);
            log.info(sql);
            conn.createQuery(sql).executeUpdate();
        }
        conn.close();
    }


    public String getSqlTable(Class<?> type){
        if(Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)){
            return "TINYINT";
        }else if(Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type) ||
                Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)){
            return "INTEGER";
        }else if(Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type) ||
                Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)){
            return "REAL";
        }else if(String.class.isAssignableFrom(type)){
            return "VARCHAR";
        }else if(LocalDateTime.class.isAssignableFrom(type)){
            return "DATETIME";
        }else if(IEnum.class.isAssignableFrom(type)){
            Class tClass = (Class) ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments()[0];
            if(String.class.isAssignableFrom(tClass)){
                return "VARCHAR";
            }
            return "TINYINT";
        }else {
            return "VARCHAR";
        }
    }

    public String getSqlType(int sqlType){

        if(sqlType == Types.INTEGER){
            return "INTEGER";
        }else if(sqlType == Types.DOUBLE || sqlType == Types.FLOAT){
            return "REAL";
        }else if(sqlType == Types.VARCHAR){
            return "VARCHAR";
        }else if(sqlType == Types.BOOLEAN){
            return "TINYINT";
        }else if(sqlType == Types.DECIMAL){
            return "NUMERIC";
        }else if(sqlType == Types.DATE){
            return "DATE";
        }else if(sqlType == Types.TIME || sqlType == Types.TIMESTAMP){
            return "DATETIME";
        }else {
            return "VARCHAR";
        }
    }
}
