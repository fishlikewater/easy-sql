package com.github.fishlikewater.test;

import lombok.Data;
import scorpio.annotation.Column;
import scorpio.annotation.Id;
import scorpio.annotation.Table;
import scorpio.core.BaseModel;

/**
 * @author zhangx
 * @version V1.0
 * @mail fishlikewater@126.com
 * @ClassName Resources
 * @Description
 * @Date 2019年04月18日 11:53
 * @since
 **/
@Data
@Table(fileMapper = "/Resources.sqlmap")
public class Resources extends BaseModel<Resources> {
    @Id
    private int id;

    /**
     * 资源名称
     */
    @Column(length = 100)
    private String name;


    /**
     * 资源url
     */
    private String resUrl;

    /**
     * 资源类型   1:菜单    2：按钮
     */
    private String type;

    /**
     * 父资源
     */
    private int parentId;

    /**
     * 排序
     */
    private boolean sort;
}
