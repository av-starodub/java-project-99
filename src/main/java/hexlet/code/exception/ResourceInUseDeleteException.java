package hexlet.code.exception;

public class TaskStatusInUseException extends RuntimeException {

    public TaskStatusInUseException(String message) {
        super(message);
    }

}
