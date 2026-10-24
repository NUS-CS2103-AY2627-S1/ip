package shannon.exception;

/** Thrown when a {@code deadline} command has no {@code /by} part, or an empty one. */
public class MissingDeadlineByException extends ShannonException {

    /**
     * Builds the message showing a correctly formed {@code deadline} command.
     *
     * @param example a full, valid {@code deadline} command to show the user.
     */
    public MissingDeadlineByException(String example) {
        super("A deadline needs a /by with a date after it, for example: " + example);
    }
}
