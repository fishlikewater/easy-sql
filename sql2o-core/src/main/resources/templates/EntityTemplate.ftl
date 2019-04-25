package ${package}.entity;

import lombok.Data;
import scorpio.annotation.Id;
import scorpio.core.BaseModel;

@Data
public class ${className} extends BaseModel {

<#if id??>
    @Id
    private ${id};

</#if>
<#list list as p>
    private ${p};

</#list>

}