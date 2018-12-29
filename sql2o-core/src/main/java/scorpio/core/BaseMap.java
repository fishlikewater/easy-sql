package scorpio.core;

import java.util.HashMap;
import java.util.Map;

public class BaseMap {

    private static Map<String, String> basicMap = new HashMap<String, String>();

    static {
        basicMap.put("findById", "select * from ${tbl.tblName} where  ${tbl.pkName} = :id");
        basicMap.put("removeById", "delete from ${tbl.tblName} where  ${tbl.pkName} = :id");
        basicMap.put("create", "insert into ${tbl.tblName} (" +
                    "<#list tbl.notEmptyDBColumnList as x>" +
                    "${x}<#if x_has_next>,</#if>" +
                    "</#list>" +
                    ")values(" +
                    "<#list tbl.columnsWithNotEmptyValue as x> " +
                    ":${x}<#if x_has_next>,</#if>" +
                    "</#list> "
                    +") ");
        basicMap.put("create4Batch", "LOAD DATA LOCAL INFILE 'sql.csv' IGNORE INTO TABLE ${tbl.tblName} (" +
                    "<#list tbl.notEmptyDBColumnList as x>" +
                    "${x}<#if x_has_next>,</#if>" +
                    "</#list>" +
                    ") ");
        basicMap.put("createsForMysql", "insert into ${tbl.dbo.name} ( " +
                    "<#list tbl.columnsWithNotNullValue as x> " +
                    "${x.name}<#if x_has_next>,</#if> " +
                    "</#list>  " +
                    ")values " +
                    "<#list 1..valuescount as v> " +
                    "(" +
                    " <#list tbl.columnsWithNotNullValue as x>? <#if x_has_next>,</#if></#list> " +
                    ")<#if v_has_next>,</#if> " +
                    "</#list> " +
                    "");
        basicMap.put("update", "update ${tbl.tblName} " +
                "  set " +
                "  <#list tbl.mapperList as x> " +
                "  ${x.column} = :${x.property}<#if x_has_next>,</#if> " +
                "  </#list>  " +
                "  where ${tbl.pkName} = '${id}' " +
                "   ");
        basicMap.put("updateByCondition", "update ${tbl.dbo.name} " +
                " set  " +
                " <#list tbl.mapperList as x> " +
                " ${x.name} = :${x.property}<#if x_has_next>,</#if> " +
                " </#list>  " +
                " where 1=1 " +
                " <#list tbl.condition as y> " +
                " and ${y.name} = :{y.property} " +
                " </#list> " +
                " ");
        basicMap.put("updateByConditionIgnoreEmpty", "select * from ${tbl.tblName} where 1=1 " +
                " <#list tbl.columnsWithNotEmptyValue as x> " +
                " <#if fuzzy??> " +
                " and ${x.name} like ? " +
                " <#else> " +
                " and ${x.name} = ? " +
                " </#if> " +
                " </#list> " +
                " ${criteria?if_exists} " +
                " ${orderBy?if_exists} " +
                " ");
        basicMap.put("query", "select * from ${tbl.tblName} where 1=1 " +
                " <#list tbl.mapperList as x> " +
                " <#if fuzzy??> " +
                " and ${x.name} like %:${x.property}% " +
                " <#else> " +
                " and ${x.column} =:${x.property}" +
                " </#if> " +
                " </#list> " +
                " ${criteria?if_exists} " +
                " ${orderBy?if_exists} " +
                " ");
        basicMap.put("query4Oracle", "select * from (select a.*,ROWNUM RN from(select * from ${tbl.tblName}) a where ROWNUM<${end}) where 1=1 " +
                " <#list tbl.mapperList as x> " +
                " <#if fuzzy??> " +
                " and ${x.name} like %:${x.property}% " +
                " <#else> " +
                " and ${x.column} =:${x.property}" +
                " </#if> " +
                " </#list> " +
                " ${criteria?if_exists} " +
                " ${orderBy?if_exists} " +
                "  and RN >= ${begin}");
        basicMap.put("queryCount", "select count(*) from ${tbl.tblName} " +
                " where 1=1 " +
                " <#list tbl.mapperList as x> " +
                " <#if fuzzy??> " +
                " and ${x.column} like %:${x.property}% " +
                " <#else> " +
                " and ${x.column} = :${x.property} " +
                " </#if> " +
                " </#list> " +
                " ${criteria?if_exists} " +
                " ");
        basicMap.put("queryCountByCriteria", "select count(*) from ${tbl.dbo.name} " +
                " where 1=1 " +
                " <#list tbl.columnsWithNotEmptyValue as x> " +
                " and ${x.name} = ? " +
                " </#list> " +
                " ${criteria?if_exists} " +
                " ");
        basicMap.put("findByIds", "select * from ${tbl.tblName} " +
                " where ${tbl.pkName} in(" +
                " <#list ids as x>" +
                " '${x}'"+
                " <#if x_has_next>,</#if>" +
                " </#list> " +
                " ) "+
                " ${criteria?if_exists} " +
                " ");
        basicMap.put("remove", "delete from ${tbl.tblName} " +
                     "where 1=1 " +
                     "<#list tbl.mapperList as x> " +
                     "and ${x.column} =:${x.property} " +
                     "</#list> " +
                     "${criteria?if_exists} " +
                     " ");
    }

    public static Map<String, String> getBasicMap(){

        return basicMap;
    }
}
