package com;

import lombok.Data;
import lombok.EqualsAndHashCode;
import scorpio.core.BaseModel;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName BaseModelTest
 * @Description
 * @Date 2019年04月17日 16:09
 * @since
 **/
@Data
@EqualsAndHashCode(callSuper=true)
public class BaseModelTest extends BaseModel<BaseModelTest> {

    private String id;

    private String name;

}
