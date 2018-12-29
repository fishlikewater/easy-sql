package scorpio.migrate.generators;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class GeneratorFactory {

    public static Generator getGenerator(Connection connection) throws SQLException {
        String dbName = connection.getMetaData().getDatabaseProductName();

        if ("MySQL".equalsIgnoreCase(dbName)) {
            return new MySQLGenerator();
        } else if ("Apache Derby".equalsIgnoreCase(dbName)) {
        	return new DerbyGenerator();
        } else {
        	if (!dbName.equals("H2")) {
        		log.warn("No DDLGenerator found for \"" + dbName + "\".  You may need to write your own!  Defaulting to GenericGenerator.");
        	}
            return new GenericGenerator();
        }

    }
}
