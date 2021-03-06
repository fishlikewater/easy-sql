package scorpio.core;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <p>fishlikewater@126.com</p>
 * @date 2019年04月18日 11:12
 * @since 1.6
 **/
@Data
@Accessors(chain = true)
public class InsertModel {

    private String table;

    private Map<String, String> mapping = new HashMap<>();

    private boolean ignoreId = false;

    private String idName;


    public String getSql() {
        StringBuffer insertStr = new StringBuffer();
        StringBuffer value = new StringBuffer();
        insertStr
                .append("insert into ")
                .append(table)
                .append("(");
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            if (entry.getKey().equals(idName)) {
                if (ignoreId) {
                    continue;
                }
            }
            insertStr.append(entry.getKey()).append(",");
            value.append(":").append(entry.getValue()).append(",");
        }
        insertStr.deleteCharAt(insertStr.length() - 1);
        value.deleteCharAt(value.length() - 1);
        value.append(")");
        insertStr.append(") values(").append(value);
        return insertStr.toString();
    }


}
