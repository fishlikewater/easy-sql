package scorpio.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * @author <p>fishlikewater@126.com</p>
 * @date 2019年04月17日 15:50
 * @since 1.6
 **/
@Accessors(chain = true)
public class QueryModel {

    @Setter(AccessLevel.PROTECTED)
    private Set<String> filedSet = new HashSet<>();

    private List<String> selectList = new ArrayList<>();

    protected Map<String, String> mappings = null;

    @Getter
    private String lastSql = "";

    @Getter
    private StringBuffer queryStr = new StringBuffer();

    private StringBuffer pageStr = new StringBuffer();

    private StringBuffer orderBy = new StringBuffer();

    private String table;

    @Getter
    private boolean useTpl;
    @Getter
    private String templateName;

    private String sqlTemplate;

    private Map<String, Object> argMap;

    protected String getCountSql() {
        if (useTpl) {
            if (sqlTemplate == null) {
                throw new IllegalArgumentException("sqlTemplate can't be empty");
            }
            return ParseTpl.parseSqlTemplate(sqlTemplate, argMap);
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) ");
        sql.append("from ");
        sql.append(table).append(" ");
        if (queryStr.length() != 0) {
            sql.append("where");
            return sql.append(queryStr).toString();
        }
        return sql.append(lastSql).toString();
    }

    /**
     * 获取sql
     *
     * @return
     */
    protected String getSql() {
        if (useTpl) {
            if (sqlTemplate == null) {
                throw new IllegalArgumentException("sqlTemplate can't be empty");
            }
            String template = ParseTpl.parseSqlTemplate(sqlTemplate, argMap);
            if (pageStr.length() != 0) {
                template = template + pageStr;
            }
            return template;
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select ");
        if (selectList.size() == 0) {
            filedSet.forEach(t -> {
                sql.append(t);
                sql.append(",");
            });
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" ");
        } else {
            for (int i = 0; i < selectList.size(); i++) {
                sql.append(selectList.get(i));
                if (i != selectList.size() - 1) {
                    sql.append(",");
                } else {
                    sql.append(" ");
                }
            }
        }
        sql.append("from ");
        sql.append(table).append(" ");
        if (queryStr.length() == 0) {
            return sql.append(lastSql).append(pageStr).toString();
        } else {
            sql.append("where");
            sql.append(queryStr).append(lastSql);
            if(orderBy.length() != 0){
                orderBy.deleteCharAt(orderBy.length()-1);
                sql.append(orderBy);
            }
            return sql.append(pageStr).toString();
        }

    }

    public QueryModel tpl(String templateName, Map<String, Object> argMap) {
        this.useTpl = true;
        this.templateName = templateName;
        this.argMap = argMap;
        return this;
    }

    /**
     * 添加like查询条件
     *
     * @param column
     * @param value
     * @return
     */
    public QueryModel like(String column, String value) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr
                .append(column)
                .append(" like ")
                .append("'")
                .append(value)
                .append("'");


        return this;
    }

    /**
     * 添加>=查询条件
     *
     * @param column
     * @param value
     * @return
     */
    public QueryModel lte(String column, Object value) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr
                .append(column)
                .append("<=");
        if (value instanceof String) {
            queryStr.append("'")
                    .append(value)
                    .append("'");
        } else {
            queryStr.append(value);

        }
        return this;
    }

    /**
     * 添加< 查询条件
     *
     * @param column
     * @param value
     * @return
     */
    public QueryModel lt(String column, Object value) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr
                .append(column)
                .append("<");
        if (value instanceof String) {
            queryStr.append("'")
                    .append(value)
                    .append("'");
        } else {
            queryStr.append(value);

        }
        return this;
    }

    /**
     * 添加>=查询条件
     *
     * @param column
     * @param value
     * @return
     */
    public QueryModel gte(String column, Object value) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr
                .append(column)
                .append(">=");
        if (value instanceof String) {
            queryStr.append("'")
                    .append(value)
                    .append("'");
        } else {
            queryStr.append(value);

        }
        return this;
    }

    /**
     * 添加>查询条件
     *
     * @param column
     * @param value
     * @return
     */
    public QueryModel gt(String column, Object value) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr
                .append(column)
                .append(">");
        if (value instanceof String) {
            queryStr.append("'")
                    .append(value)
                    .append("'");
        } else {
            queryStr.append(value);

        }
        return this;
    }

    /**
     * 添加=查询条件
     *
     * @param column
     * @param value
     * @return
     */
    public QueryModel equal(String column, Object value) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr
                .append(column)
                .append("=");
        if (value instanceof String) {
            queryStr.append("'")
                    .append(value)
                    .append("'");
        } else {
            queryStr.append(value);
        }
        return this;
    }

    /**
     * 添加in查询条件
     *
     * @param column
     * @param arr
     * @return
     */
    public QueryModel in(String column, Object[] arr) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr
                .append(column)
                .append(" in")
                .append("(");
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] instanceof String) {
                queryStr.append("'").append(arr[i]).append("'");
            } else {
                queryStr.append(arr[i]);
            }
            if (i < arr.length - 1) {
                queryStr.append(",");
            } else {
                queryStr.append(")");
            }

        }
        return this;
    }

    public QueryModel page(int bedin, int limit) {
        pageStr.append(" limit ").append(bedin).append(", ").append(limit);
        return this;
    }

    public QueryModel orderBy(String column, boolean isAsc){
        orderBy.append(" ").append(column);
        if(isAsc){
            orderBy.append(" asc");
        }else{
            orderBy.append(" desc");
        }
        orderBy.append(",");
        return this;
    }

    /**
     * 添加自定义语句
     *
     * @param sql 自定义sql
     * @return
     */
    protected QueryModel criteria(String sql) {
        if (queryStr.length() != 0) {
            queryStr.append(" and ");
        }else{
            queryStr.append(" ");
        }
        queryStr.append(sql);
        return this;
    }


    /**
     * 自定义返回字段
     *
     * @param arg
     * @return
     */
    public QueryModel select(String... arg) {
        this.selectList = Arrays.asList(arg);
        return this;
    }

    /**
     * 自定义返回字段映射
     *
     * @param mappings
     * @return
     */
    public QueryModel columnMappings(Map<String, String> mappings) {
        this.mappings = new HashMap<>();
        this.mappings.putAll(mappings);
        return this;
    }

    /**
     *  添加到最后
     * @param sql
     * @return
     */
    public QueryModel last(String sql){
        this.lastSql = sql;
        return this;
    }

    protected QueryModel setTable(String table) {
        this.table = table;
        return this;
    }

    protected QueryModel setSqlTemplate(String sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
        return this;
    }
}
