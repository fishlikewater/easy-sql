package scorpio.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author <p><a>fishlikewater@126.com</a></p>
 * @date 2019年04月18日 16:26
 * @since 1.6
 **/
@Accessors(chain = true)
public class UpdateModel{

    private QueryModel queryModel = new QueryModel();

    @Setter(AccessLevel.PROTECTED)
    private StringBuffer setSql = new StringBuffer();

    private String table;
    @Getter
    private boolean useTpl;
    @Getter
    private String templateNameOrSql;
    @Getter
    private String sqlTemplate;
    @Getter
    private Map<String, Object> argMap;


    public String getDeleteSql(){
        if(useTpl){
            if(sqlTemplate == null){
                throw new IllegalArgumentException("sqlTemplate can't be empty");
            }
            return ParseTpl.parseSqlTemplate(sqlTemplate, argMap);
        }
        StringBuffer deleteSql = new StringBuffer();
        StringBuffer queryStr = queryModel.getQueryStr();
        deleteSql.append("delete from ").append(table);
        if(queryStr.length() != 0){
            deleteSql.append(" where").append(queryStr);
        }
        return deleteSql.append(queryModel.getLastSql()).toString();
    }

    public String getUpdateSql(){
        if(useTpl){
            if(sqlTemplate == null){
                throw new IllegalArgumentException("sqlTemplate can't be empty");
            }
            return ParseTpl.parseSqlTemplate(sqlTemplate, argMap);
        }
        StringBuffer updateSql = new StringBuffer();
        StringBuffer queryStr = queryModel.getQueryStr();
        updateSql.append("update ").append(table).append(" set");
        setSql.deleteCharAt(setSql.length() - 1);
        updateSql.append(setSql);
        if(queryStr.length() != 0){
            updateSql.append(" where").append(queryStr);
        }
        return updateSql.append(queryModel.getLastSql()).toString();
    }

    /**
     *  设置修改字段
     * @param column 列名
     * @param value 修改为
     * @return
     */
    public UpdateModel set(String column, Object value){
        if(value instanceof String){
            setSql.append(" ").append(column).append("=").append("'").append(value).append("',");
        }else{
            setSql.append(" ").append(column).append("=").append(value).append(",");
        }
        return this;
    }

    /**
     *  设置修改字段
     * @param sqlSet set -where中间sql语句
     * @return
     */
    public UpdateModel set(String sqlSet){
        this.setSql.append(sqlSet).append(",");
        return this;
    }

    /**
     *  使用模板
     * @param templateNameOrSql 模板key
     * @param argMap 参数
     * @return
     */
    public UpdateModel tpl(String templateNameOrSql, Map<String, Object> argMap){
        this.useTpl = true;
        this.templateNameOrSql = templateNameOrSql;
        this.argMap = argMap;
        return this;
    }

    /**
     * 添加like查询条件
     * @param column
     * @param value
     * @return
     */
    public UpdateModel like(String column, String value){
        queryModel.like(column, value);
        return this;
    }

    /**
     * 添加>=查询条件
     * @param column
     * @param value
     * @return
     */
    public UpdateModel lte(String column, Object value){
        queryModel.lte(column, value);
        return this;
    }

    /**
     * 添加< 查询条件
     * @param column
     * @param value
     * @return
     */
    public UpdateModel lt(String column, Object value){
        queryModel.lt(column, value);
        return this;
    }

    /**
     * 添加>=查询条件
     * @param column
     * @param value
     * @return
     */
    public UpdateModel gte(String column, Object value){
        queryModel.gte(column, value);
        return this;
    }

    /**
     * 添加>查询条件
     * @param column
     * @param value
     * @return
     */
    public UpdateModel gt(String column, Object value){
        queryModel.gt(column, value);
        return this;
    }

    /**
     * 添加=查询条件
     * @param column
     * @param value
     * @return
     */
    public UpdateModel equal(String column, Object value){
        queryModel.equal(column, value);
        return this;
    }

    /**
     * 添加in查询条件
     * @param column
     * @param arr
     * @return
     */
    public UpdateModel in(String column, Object[] arr){
        queryModel.in(column, arr);
        return this;
    }

    /**
     *  添加自定义语句
     * @param sql 自定义sql
     * @return
     */
    protected UpdateModel criteria(String sql){
        queryModel.criteria(sql);
        return this;
    }

    /**
     *  添加到最后
     * @param sql
     * @return
     */
    public UpdateModel last(String sql){
        queryModel.last(sql);
        return this;
    }

    protected UpdateModel setSqlTemplate(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
        return this;
    }

    protected UpdateModel setTable(String table) {
        this.table = table;
        return this;
    }
}


