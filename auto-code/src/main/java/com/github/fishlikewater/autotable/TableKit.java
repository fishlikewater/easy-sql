package com.github.fishlikewater.autotable;

import com.fishlikewater.kit.core.ScannerKit;
import org.apache.commons.lang.StringUtils;
import org.sql2o.Connection;
import scorpio.BaseUtils;
import scorpio.annotation.Table;
import scorpio.core.BaseModel;
import scorpio.utils.NameUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author <p>fishlikewater@126.com</p>
 * @version V1.0
 * @date 2019年10月08日 14:06
 * @since
 **/
public class TableKit {

    /**
     * 首先扫描需要创建table 的实体
     */
    public static void createTable(DataSource dataSource, String... packages) {
        BaseUtils.open(dataSource);
        List<Class<? extends BaseModel>> classes = ScannerKit.scannerPackage(BaseModel.class, packages);
        classes.forEach(c -> {
            Field[] fields = c.getDeclaredFields();
            String dataType = BaseUtils.getDataType();
            String tableName = getTableName(c);
            String queryTable = "select count(*) from sqlite_master where tbl_name='"+tableName+"'";
            Connection conn = BaseUtils.sql2o.open();
            Integer count = conn.createQuery(queryTable).executeScalar(int.class);
            if(count == 0){
                AutoTable autoTable = AutoTableFactory.getInstance(dataType);
                String sql = autoTable.getSql(c, tableName);
                conn.createQuery(sql).executeUpdate();
                if (conn != null) {
                    conn.close();
                }
            }
        });
    }

    private static String getTableName(Class<? extends BaseModel> clazz){
        Table table = clazz.getAnnotation(Table.class);
        String tableName;
        if(table == null && StringUtils.isNotBlank(table.table())){
            tableName = table.table();
        }else {
            tableName = NameUtils.getUnderlineName(clazz.getSimpleName());
        }
        return tableName;
    }

}
