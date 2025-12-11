package persistence;

public class BadIceException extends Exception {

    public BadIceException(String msg) {
        super(msg);
    }

    public BadIceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
