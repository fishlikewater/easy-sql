package com.github.fishlikewater.autotable;

import com.fishlikewater.kit.core.ScannerKit;
import org.sql2o.Connection;
import scorpio.BaseUtils;
import scorpio.core.BaseModel;

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
            AutoTable autoTable = AutoTableFactory.getInstance(dataType);
            String sql = autoTable.getSql(c);
            Connection conn = BaseUtils.getConn(sql);
            try {
                conn.createQuery(sql).executeUpdate();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        });
    }


}
