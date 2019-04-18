package com;

import lombok.Data;
import scorpio.annotation.Id;
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
public class Resources extends BaseModel<Resources> {
    @Id
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
}
