package ${package}.mapper;

import ${package}.entity.${className};
import scorpio.annotation.Table;
import scorpio.core.BaseMapper;

@Table(table = "${table}"<#if fileMapper??>, fileMapper = "${fileMapper}" </#if>)
public class ${className}Mapper extends BaseMapper<${className}> {


}