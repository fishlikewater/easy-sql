package scorpio.utils;

import lombok.Data;
import scorpio.core.BaseObject;
import scorpio.core.Generator;
import scorpio.core.IdDefined;
import scorpio.core.Mapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
public class TableInfo {

    private String tblName;

    private String pkName;

    /** 主键生成方式*/
    private Generator generator;

    /**自定义主键构造类 */
    private IdDefined idDefined ;

    /** 映射文件路径 */
    private String fileMapper;

    private Class<? extends BaseObject> pojoClass;

    /** 值不为null的属性名*/
    private List columnsWithNotEmptyValue = new LinkedList();
    /** 所有的列名*/
    private List dbColumnList = new LinkedList();
    /** 值不为null的列名*/
    private List notEmptyDBColumnList = new LinkedList();
    /** 所有的属性名*/
    private List dtoAllValueName = new LinkedList();
    /** 不为空列与属性的映射关系*/
    private List<Mapper> mapperList = new ArrayList();
    /** 列与属性的映射关系*/
    private List<Mapper> mapperForAllList = new ArrayList();
    /** 不为空列与属性的映射关系*/
    private List<Mapper> conditon = new ArrayList();

}

