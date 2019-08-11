package scorpio.core;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年08月11日 11:01
 * @since
 **/
public class ModelCache {

    private static  Map<Class<? extends scorpio.core.Model>, Model> map = new ConcurrentHashMap<>();

    public static Model getCache(Class<? extends scorpio.core.Model> clazz){
        if(map.containsKey(clazz)){
            return map.get(clazz);
        }
        return null;
    }

    public static void addCache(Class<? extends scorpio.core.Model> clazz, Model model){
        if(!map.containsKey(clazz)){
            map.put(clazz, model);
        }
    }

    @Data
    class Model{

        private String idName;

        private String tableName;

        private Map<String, String> mapping = new HashMap<>();

        private Map<String, String> sqlMap = new HashMap<>(); //存储所有的sqlmap

    }
}
