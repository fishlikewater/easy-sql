package scorpio.migrate;

import lombok.extern.slf4j.Slf4j;
import scorpio.exception.SchemaMigrationException;


@Slf4j
public class Validator {

	public static void notNull(Object object, String message) {
		if (object == null) {
			throwException(message);
		}
	}

	public static void isTrue(boolean trueExpression, String message) {
		
		if (!trueExpression) {
			throwException(message);
		}
	}
	
	private static void throwException(String message) {
		SchemaMigrationException exception = new SchemaMigrationException(message);
		log.error("Required object was null", exception);
		throw exception;
	}
	
}
