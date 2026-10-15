package sunnyexception;

/**
 * Indicates that the user provided insufficient info for a task.
 * This class extends {@link SunnyException}.
 */
public class InsufficientInfoException extends SunnyException{
    /**
     * Constructs the exception with the specified message.
     *
     * @param message the message describing the exception.
     */
    public InsufficientInfoException(String message){
        super(message);
    }
}