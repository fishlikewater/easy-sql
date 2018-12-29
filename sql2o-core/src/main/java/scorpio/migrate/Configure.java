package scorpio.migrate;

import lombok.extern.slf4j.Slf4j;
import scorpio.BaseUtils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Tells migrate4j where migration classes are located
 * and how to connect to the database.
 *
 */
@Slf4j
public class Configure {

	public static Connection getConnection() throws SQLException {
		
		return BaseUtils.sql2o.getConnectionSource().getConnection();
	}

	public static void close(Connection connection){
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
               log.error("don't close connection",e);
               return;
            }
        }

    }
	
}
