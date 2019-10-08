package com.github.fishlikewater.autotable;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 14:18
 * @since
 **/
public class AutoTableFactory {

    public static AutoTable getInstance(String dataType){
        if(dataType.toLowerCase().equals("sqlite")){
            return new SqliteTable();
        }else {
            return new SqliteTable();
        }
    }
}
