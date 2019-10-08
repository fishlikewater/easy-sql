package com.github.fishlikewater.autotable;

import org.apache.commons.lang.StringUtils;
import scorpio.annotation.Column;
import scorpio.annotation.Id;
import scorpio.annotation.IdGenerator;
import scorpio.core.BaseModel;
import scorpio.core.Generator;
import scorpio.enums.IEnum;
import scorpio.utils.NameUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 14:04
 * @since
 **/
public class SqliteTable implements AutoTable {


    @Override
    public String getSql(Class<? extends BaseModel> clazz, String tableName) {
        StringBuilder sb = new StringBuilder();
        List<TableField> list = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        Arrays.stream(declaredFields).forEach(f->{
            TableField tableField = new TableField();
            Id id = f.getAnnotation(Id.class);
            if(id != null){
                tableField.setPrimaryyKey(true);
                IdGenerator idGenerator = f.getAnnotation(IdGenerator.class);
                if(idGenerator != null && idGenerator.value() == Generator.AUTO){
                    tableField.setIncre(true);
                }
            }
            tableField.setFieldName(f.getName());
            Column column = f.getAnnotation(Column.class);
            if(column != null){
                if(StringUtils.isNotBlank(column.value())){
                    tableField.setTblKey(column.value());
                }else {
                    tableField.setTblKey(NameUtils.getUnderlineName(f.getName()));
                }
                if(StringUtils.isNotBlank(column.columnDefined())){
                    tableField.setSqlType(column.columnDefined());
                }
                if(column.type() != Types.NULL){
                    tableField.setSqlType(getSqlType(column.type()));
                }else {
                    tableField.setSqlType(getSqlTable(f.getType()));
                }
                tableField.setLength(column.length());
                if(StringUtils.isNotBlank(column.describe())){
                    tableField.setDescribe(column.describe());
                }
                tableField.setNull(column.nullable());
                tableField.setIndex(column.index());
                if(StringUtils.isNotBlank(column.defaultValue())){
                    tableField.setDefaultValue(column.defaultValue());
                }

            }else {
                tableField.setTblKey(NameUtils.getUnderlineName(f.getName()));
                tableField.setSqlType(getSqlTable(f.getType()));
                tableField.setLength(50);
                tableField.setNull(true);
                tableField.setIndex(false);
            }
            list.add(tableField);
        });
        sb.append("create table ").append(tableName).append(" (");
        sb.append("\n");
        list.forEach(t->{
            sb.append(t.getTblKey()).append(" ");
            if(t.getSqlType().equals("VARCHAR")){
                sb.append(t.getSqlType()).append("(").append(t.getLength()).append(")").append(" ");
            }else {
                sb.append(t.getSqlType()).append(" ");
            }
            if(!t.isNull()){
                sb.append("NOT NULL ");
            }
            if(t.isPrimaryyKey()){
                sb.append("CONSTRAINT ").append(tableName).append("_pk ").append("PRIMARY KEY");
            }
            sb.append("\n");
        });
        sb.append(")");
        return sb.toString();
    }


    private String getSqlTable(Class<?> type){
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
