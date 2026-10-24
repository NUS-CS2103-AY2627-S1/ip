package shannon.exception;

/** Thrown when an {@code event} command is missing its {@code /from} or {@code /to} part. */
public class MissingEventTimeException extends ShannonException {

    /**
     * Builds the message showing a correctly formed {@code event} command.
     *
     * @param example a full, valid {@code event} command to show the user.
     */
    public MissingEventTimeException(String example) {
        super("An event needs a /from and a /to, each with a date after it, for example: " + example);
    }
}
