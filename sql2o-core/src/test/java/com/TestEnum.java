package com;

import scorpio.enums.IEnum;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年08月13日 12:01
 * @since
 **/
public enum TestEnum implements IEnum<String> {

    app("1", "第一"), pc("2", "第二");

    private String code;

    private String desc;

    TestEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getDesc(){
        return desc;
    }


    @Override
    public String getSaveField() {
        return code;
    }

}
