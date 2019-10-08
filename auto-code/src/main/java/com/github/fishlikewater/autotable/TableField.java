package com.github.fishlikewater.autotable;

import lombok.Data;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 16:27
 * @since
 **/
@Data
public class TableField {

    private String fieldName;

    private String tblKey;

    private String sqlType;

    private int length;

    private boolean isPrimaryyKey = false;

    private boolean isIncre = false;//主键是否自增

    private boolean isNull = true;

    private boolean isIndex = false;

    private String defaultValue;

    private String describe;
}
