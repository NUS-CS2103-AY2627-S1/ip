package chattingheads.exception;

/**
 * Represents an error caused by doing a task type-specific operation on the wrong task type.
 */
public class InvalidTaskTypeException extends ChattingHeadsException {
    public InvalidTaskTypeException(String correctTaskType) {
        super(String.format("Selected task is not %s\nLooks like I can't change you", correctTaskType));
    }
}
