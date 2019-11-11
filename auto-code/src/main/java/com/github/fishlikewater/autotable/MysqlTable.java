package com.github.fishlikewater.autotable;

import lombok.extern.slf4j.Slf4j;
import org.sql2o.Connection;
import scorpio.BaseUtils;
import scorpio.core.BaseModel;
import scorpio.enums.IEnum;

import java.lang.reflect.ParameterizedType;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 14:04
 * @since
 **/
@Slf4j
public class MysqlTable implements AutoTable {


    public void excutor(Class<? extends BaseModel> c, String tableName, String dbName){
        String queryTable = "select count(*) from information_schema.TABLES where TABLE_SCHEMA='"+dbName+"' and table_name ='"+tableName+"'";
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
        if(Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type) ||
                Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)){
            return "TINYINT";
        }else if(Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)){
            return "INT";
        }else if(Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)){
            return "SMALLINT";
        }else if(Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)){
            return "DOUBLE";
        }else if(Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)){
            return "FLOAT";
        }else if(Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)){
            return "BIGINT";
        }else if(String.class.isAssignableFrom(type)){
            return "VARCHAR";
        }else if(LocalDate.class.isAssignableFrom(type)){
            return "DATE";
        }else if(LocalTime.class.isAssignableFrom(type)){
            return "TIME";
        }else if(LocalDateTime.class.isAssignableFrom(type)){
            return "DATETIME";
        }else if(IEnum.class.isAssignableFrom(type)){
            Class tClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
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
        }else if(sqlType == Types.DOUBLE){
            return "DOUBLE";
        }else if(sqlType == Types.FLOAT){
            return "FLOAT";
        }else if(sqlType == Types.BIGINT){
            return "BIGINT";
        }else if(sqlType == Types.SMALLINT){
            return "SMALLINT";
        }else if(sqlType == Types.TINYINT){
            return "TINYINT";
        }else if(sqlType == Types.VARCHAR){
            return "VARCHAR";
        }else if(sqlType == Types.BOOLEAN){
            return "TINYINT";
        }else if(sqlType == Types.DECIMAL){
            return "DECIMAL";
        }else if(sqlType == Types.DATE){
            return "DATE";
        }else if(sqlType == Types.TIMESTAMP){
            return "DATETIME";
        }else if(sqlType == Types.TIME){
            return "TIME";
        }else {
            return "VARCHAR";
        }
    }
}
