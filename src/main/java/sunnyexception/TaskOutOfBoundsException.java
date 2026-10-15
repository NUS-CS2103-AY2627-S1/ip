package sunnyexception;

/**
 * Indicates that the user requested a task at an index of the taskboard that doesn't yet exist.
 * This class extends {@link SunnyException}.
 */
public class TaskOutOfBoundsException extends SunnyException{
    /**
     * Constructs the exception with the specified message.
     *
     * @param message the message describing the exception.
     */
    public TaskOutOfBoundsException(String message){
        super(message);
    }
}
