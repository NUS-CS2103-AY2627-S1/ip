package max.maxexception;

public class InvalidTaskIDException extends MaxException {
    public InvalidTaskIDException() {
        super("Invalid task ID. Enter a number.");
    }
}
