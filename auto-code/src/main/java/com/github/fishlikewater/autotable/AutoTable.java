package com.github.fishlikewater.autotable;

import scorpio.core.BaseModel;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 14:16
 * @since
 **/
public interface AutoTable {

    public String getSql(Class<? extends BaseModel> clazz,  String tableName);
}
