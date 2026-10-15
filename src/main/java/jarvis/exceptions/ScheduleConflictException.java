package jarvis.exceptions;

/**
 * Represents the to-be-added event clashing with another event.
 */
public class ScheduleConflictException extends JarvisException {

    /**
     * Creates an exception with the specified message.
     *
     * @param message Error message to display.
     */
    public ScheduleConflictException(String message) {
        super(message);
    }
}
