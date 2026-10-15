package heisenberg;

/** Indicates that the given task number does not refer to an existing task. */
public class InvalidTaskNumberException extends RuntimeException {
    public InvalidTaskNumberException(String message) {
        super(message);
    }
}
