package com;

import scorpio.enums.IEnum;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年08月13日 12:01
 * @since
 **/
public enum TypeEnum implements IEnum<Integer> {

    app(1, "类型一"), pc(2, "类型二");

    private Integer code;

    private String desc;

    TypeEnum(Integer code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getDesc(){
        return desc;
    }


    @Override
    public Integer getSaveFieid() {
        return code;
    }
}
