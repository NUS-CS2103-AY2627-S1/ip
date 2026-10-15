package sunnyexception;

/**
 * Indicates that the task the user requested is not recognized.
 * This class extends {@link SunnyException}.
 */
public class UnrecognisedTaskException extends SunnyException{
    /**
     * Constructs the exception with the specified message.
     *
     * @param message the message describing the exception.
     */
    public UnrecognisedTaskException (String message){
        super(message);
    }
}