package hexlet.code.exception;

public class ResourceInUseDeleteException extends RuntimeException {

    public ResourceInUseDeleteException(String message) {
        super(message);
    }

}
