package max.maxexception;

public class EmptyDescriptionException extends MaxException {
    public EmptyDescriptionException() {
        super("Task description required.");
    }
}
