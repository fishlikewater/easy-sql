package scorpio.utils;

import scorpio.annotation.Column;
import scorpio.annotation.Transient;
import scorpio.core.BaseObject;
import scorpio.core.Mapper;

import java.lang.reflect.Field;
import java.util.*;

public class TableUtils {

    /**
     * 获取不为空的列list 与属性list
     * @param clazz
     * @param dto
     * @return
     */
    public static Map<String, List> getNotEmptyColumn(Class<? extends BaseObject> clazz, BaseObject dto){
        String dtoName = dto.getClass().getSimpleName();
        String clazzName = clazz.getSimpleName();
        if(!dtoName.equals(clazzName)){
            throw  new RuntimeException("创建对象不匹配DAO映射对象");
        }
        Field[] fields = clazz.getDeclaredFields();
        if(fields.length>0){
            Map<String, List> map = new HashMap<>();
            List<String> dbColumnList = new LinkedList<>();
            List<String> dtoColumnList = new LinkedList<>();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                f.setAccessible(true);
                if(f.getAnnotation(Transient.class) != null){
                    continue;
                }
                try {
                    Object o = f.get(dto);
                    if(o != null){
                        dtoColumnList.add(f.getName());
                        Column columnAnnotation = f.getAnnotation(Column.class);
                        if(columnAnnotation !=null){
                            dbColumnList.add(columnAnnotation.value());
                        }else{
                            dbColumnList.add(NameUtils.getUnderlineName(f.getName()));
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            map.put("dbColumn",dbColumnList);
            map.put("dtoColumn",dtoColumnList);
            return map;
        }
        return null;
    }

    /**
     *获取不为空的列与属性的映射关系
     * @param clazz
     * @param dto
     * @return
     */
    public static List<Mapper> getMapper(Class<? extends BaseObject> clazz, BaseObject dto){
        String dtoName = dto.getClass().getSimpleName();
        String clazzName = clazz.getSimpleName();
        if(!dtoName.equals(clazzName)){
            throw  new RuntimeException("创建对象不匹配DAO映射对象");
        }
        Field[] fields = clazz.getDeclaredFields();
        if(fields.length>0){
            List<Mapper> mapperList = new ArrayList<>();
            for (int i = 0; i < fields.length; i++) {

                Field f = fields[i];
                f.setAccessible(true);
                if(f.getAnnotation(Transient.class) != null){
                    continue;
                }
                try {
                    Object o = f.get(dto);
                    if(o != null){
                        Column columnAnnotation = f.getAnnotation(Column.class);
                        String columnName;
                        if(columnAnnotation !=null){
                            columnName = columnAnnotation.value();
                        }else{
                            columnName = NameUtils.getUnderlineName(f.getName());
                        }
                        Mapper mapper = new Mapper(columnName, f.getName());
                        mapperList.add(mapper);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return mapperList;
        }
        return null;
    }

    /**
     *
     * @param list
     * @return
     */
    public static Map<String, String> columnMap(List<Mapper> list){
        Map<String, String> columnMap = new HashMap<>();
        list.forEach(item ->{
            columnMap.put(item.getColumn(), item.getProperty());
        });

        return columnMap;
    }
}
