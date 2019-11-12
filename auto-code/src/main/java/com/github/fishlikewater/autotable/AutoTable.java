package com.github.fishlikewater.autotable;

import scorpio.annotation.Column;
import scorpio.annotation.Id;
import scorpio.annotation.IdGenerator;
import scorpio.core.BaseModel;
import scorpio.core.Generator;
import scorpio.utils.NameUtils;
import scorpio.utils.StringUtils;

import java.lang.reflect.Field;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 14:16
 * @since
 **/
public interface AutoTable {


    public String getSqlTable(Class<?> type);

    public String getSqlType(int sqlType);

    public void excutor(Class<? extends BaseModel> c, String tableName, String dbName);

    default String getSql(Class<? extends BaseModel> clazz,  String tableName){
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
                sb.append(t.getSqlType()).append("(").append(t.getLength()).append(")").append("");
            }else {
                sb.append(t.getSqlType()).append("");
            }
            if(!t.isNull()){
                sb.append(" NOT NULL ");
            }
            if(t.isPrimaryyKey()){
                sb.append(" CONSTRAINT ").append(tableName).append("_pk ").append("PRIMARY KEY");
                if(t.isIncre()){
                    sb.append(" autoincrement");
                }
            }
            sb.append(",");
            sb.append("\n");
        });
        int index = sb.lastIndexOf(",");
        sb.replace(index, index+1, "");
        sb.append(")");
        return sb.toString();
    };
}
