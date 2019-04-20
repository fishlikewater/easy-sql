/*
package scorpio.utils;

import cn.hutool.core.bean.BeanUtil;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanUtils {

    */
/**
     * map转换为对象(这里将数据库中下划线字段转为驼峰形式)
     * @param map
     * @param beanClass
     * @return
     * @throws Exception
     *//*

    public static Object mapToObject(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null)
            return null;

        Object obj = beanClass.newInstance();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            Method setter = property.getWriteMethod();
            if (setter != null) {
                setter.invoke(obj, map.get(NameUtils.getUnderlineName(property.getName())));
            }
        }

        return obj;
    }

    */
/**
     * 对象转换为map
     * @param obj
     * @return
     * @throws Exception
     *//*

    public static Map<String, Object> objectToMap(Object obj) throws Exception {
        if(obj == null)
            return null;

        Map<String, Object> map = new HashMap<String, Object>();

        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (key.compareToIgnoreCase("class") == 0) {
                continue;
            }
            Method getter = property.getReadMethod();
            Object value = getter!=null ? getter.invoke(obj) : null;
            map.put(key, value);
        }

        return map;
    }


    */
/**
     * 将对象装换为map
     * @param bean
     * @return
     *//*

    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = new HashMap<>();
        if (bean != null) {
            Map beanMap = BeanUtil.beanToMap(bean);
            for (Object key : beanMap.keySet()) {
                map.put(NameUtils.getUnderlineName(key+""), beanMap.get(key));
            }
        }
        return map;
    }

    */
/**
     * 将map装换为javabean对象
     * @param map
     * @param bean
     * @return
     *//*

    public static <T> T mapToBean(Map<String, Object> map,T bean) {
        if(map == null){
            return null;
        }
        Map beanMap = BeanUtil.beanToMap(bean);
        beanMap.putAll(map);
        return bean;
    }

    */
/**
     * 将List<T>转换为List<Map<String, Object>>
     * @param objList
     * @return
     *//*

    public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (objList != null && objList.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0,size = objList.size(); i < size; i++) {
                bean = objList.get(i);
                map = beanToMap(bean);
                list.add(map);
            }
        }
        return list;
    }

    */
/**
     * 将List<Map<String,Object>>转换为List<T>
     * @param maps
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     *//*

    public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps,Class<T> clazz) throws InstantiationException, IllegalAccessException {
        List<T> list = new ArrayList<>();
        if (maps != null && maps.size() > 0) {
            Map<String, Object> map = null;
            T bean = null;
            for (int i = 0,size = maps.size(); i < size; i++) {
                bean = clazz.newInstance();
                mapToBean(maps.get(i), bean);
                list.add(bean);
            }
        }
        maps = null;
        return list;
    }

    */
/**
     * key转换驼峰
     * @param map
     * @return
     *//*

    public static Map<String, Object> mapKeyCovertion(Map<String, Object> map){
        if(map == null){
            return null;
        }
        Map<String, Object> nMap = new HashMap<>();
        map.forEach((k, v)->{nMap.put(NameUtils.getCamelName(k), v);});
        map = null;
        return nMap;
    }

    */
/**
     * 获取sql类型
     * @param clazz
     * @return
     *//*

    public static int getSqlType(Class clazz){
        if(clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(int.class)){

            return Types.INTEGER;
        }else if(clazz.isAssignableFrom(long.class) || clazz.isAssignableFrom(Long.class)){

            return Types.BIGINT;
        }else{
            return Types.VARCHAR;
        }
    }


}
*/
