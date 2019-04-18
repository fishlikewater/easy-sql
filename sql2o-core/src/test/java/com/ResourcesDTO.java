package com;

import lombok.Builder;
import scorpio.annotation.Column;
import scorpio.annotation.Id;
import scorpio.annotation.Transient;
import scorpio.core.BaseObject;

import java.sql.Types;


@Builder
public class ResourcesDTO implements BaseObject {

    @Id
    @Column("id")
    private Integer id;

    /**
     * 资源名称
     */
    @Column(value = "name")
    private String name;


    /**
     * 资源url
     */
    @Column("res_url")
    private String resUrl;

    /**
     * 资源类型   1:菜单    2：按钮
     */
    @Column(value = "type", type = Types.INTEGER)
    private Integer type;

    /**
     * 父资源
     */
    @Column(value = "parent_id", type = Types.INTEGER)
    private Integer parentId;

    /**
     * 排序
     */
    @Column(value = "sort", type = Types.INTEGER)
    private Integer sort;

    @Transient
    private String checked;//是否选中
}
