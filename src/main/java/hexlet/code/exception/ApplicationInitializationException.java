package hexlet.code.exception;

public class ApplicationInitializationException extends RuntimeException {

    public ApplicationInitializationException(String message, Throwable e) {
        super(message, e);
    }

}
