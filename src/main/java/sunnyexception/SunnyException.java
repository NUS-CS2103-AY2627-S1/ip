package sunnyexception;

/**
 * Represents a general exception used by the Sunny application.
 * This class serves as the parent class for other SunnyException types.
 */
public class SunnyException extends Exception{
    /**
     * Constructs the exception with the specified message.
     *
     * @param message the message describing the exception.
     */
    public SunnyException (String message){
        super(message);
    }
}