package scorpio.exception;

public class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public BaseRuntimeException(String msg) {
        super(msg);
    }
    public BaseRuntimeException() {
        super();
    }
}
