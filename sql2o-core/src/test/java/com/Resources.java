package com;

import lombok.Data;
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
    private String id;

    /**
     * 资源名称
     */
    private TestEnum name;


    /**
     * 资源url
     */
    private String resUrl;

    /**
     * 资源类型   1:菜单    2：按钮
     */
    private TypeEnum type;

    /**
     * 父资源
     */
    private int parentId;

    /**
     * 排序
     */
    private boolean sort;
}
