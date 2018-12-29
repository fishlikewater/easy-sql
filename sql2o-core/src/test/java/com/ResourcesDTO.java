package com;

import lombok.Builder;
import scorpio.annotation.Transient;
import scorpio.core.BaseObject;


@Builder
public class ResourcesDTO extends BaseObject {

    private Integer id;

    /**
     * 资源名称
     */
    private String name;


    /**
     * 资源url
     */
    private String resUrl;

    /**
     * 资源类型   1:菜单    2：按钮
     */
    private Integer type;

    /**
     * 父资源
     */
    private Integer parentId;

    /**
     * 排序
     */
    private Integer sort;

    @Transient
    private String checked;//是否选中
}
