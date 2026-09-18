package sozius.exception;

/**
 * SoziusException represents errors that can be anticipated during normal usage
 * of the application, such as invalid command formats, invalid dates,
 * or problems reading and writing the tasks file.
 * The message is always safe to show directly to the user.
 */
public class SoziusException extends Exception {
    /**
     * Creates a SoziusException instance
     * @param message message for the exception
     */
    public SoziusException(String message) {
        super(message);
    }
}
