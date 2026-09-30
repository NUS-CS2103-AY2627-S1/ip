package bean.exception;

/** Indicates that a requested task index is outside the task list. */
public class BeanListOutOfBoundsException extends RuntimeException {

    private final int invalidIndex;

    /** Creates an exception with a message and invalid index. */
    public BeanListOutOfBoundsException(String message, int invalidIndex) {
        super(message);
        this.invalidIndex = invalidIndex;
    }

    /** Creates an exception with the default out-of-bounds message. */
    public BeanListOutOfBoundsException(int invalidIndex) {
        super("Error out of bounds of the array!");
        this.invalidIndex = invalidIndex;
    }

    public int getInvalidIndex() {
        return invalidIndex;
    }
}
