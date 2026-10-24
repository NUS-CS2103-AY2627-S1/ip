package shannon.exception;

/**
 * Thrown when a marker such as {@code /by}, {@code /from} or {@code /to} is used in a way that
 * would put part of a command in the wrong place, e.g. {@code /by} typed twice.
 * <p>
 * Like {@link StorageException}, each case is built by a named factory method rather than a
 * constructor, because several of them take the same kinds of argument.
 */
public class InvalidMarkerException extends ShannonException {

    /**
     * Creates an exception with an already-worded message.
     *
     * @param message the text to show the user.
     */
    private InvalidMarkerException(String message) {
        super(message);
    }

    /**
     * Creates the exception for a marker that belongs to a different command,
     * e.g. {@code todo read book /by 2026-08-09}.
     *
     * @param command    the command word the user typed.
     * @param marker     the marker that command does not take.
     * @param suggestion an example of the command that does take the marker.
     * @return the exception, ready to be thrown.
     */
    public static InvalidMarkerException ofUnexpected(String command, String marker, String suggestion) {
        return new InvalidMarkerException("The " + command + " command doesn't take " + marker
                + ". Did you mean something like: " + suggestion);
    }

    /**
     * Creates the exception for a marker given more than once,
     * e.g. {@code deadline report /by 2026-08-09 /by 2026-08-10}.
     *
     * @param marker  the repeated marker.
     * @param example a full, valid example of the command.
     * @return the exception, ready to be thrown.
     */
    public static InvalidMarkerException ofRepeated(String marker, String example) {
        return new InvalidMarkerException("You gave " + marker + " more than once."
                + " Give it just once, for example: " + example);
    }

    /**
     * Creates the exception for markers in the wrong order, e.g. a {@code /to} before a
     * {@code /from}.
     *
     * @param earlier the marker that should have come first.
     * @param later   the marker that should have come after it.
     * @param example a full, valid example of the command.
     * @return the exception, ready to be thrown.
     */
    public static InvalidMarkerException ofWrongOrder(String earlier, String later, String example) {
        return new InvalidMarkerException("Put " + earlier + " before " + later
                + ", for example: " + example);
    }

    /**
     * Creates the exception for a marker typed without a space on each side,
     * e.g. {@code deadline report/by 2026-08-09}, which would otherwise look as though the
     * marker were missing altogether.
     *
     * @param marker  the marker that is joined to the words around it.
     * @param example a full, valid example of the command.
     * @return the exception, ready to be thrown.
     */
    public static InvalidMarkerException ofMissingSpace(String marker, String example) {
        return new InvalidMarkerException("Put a space on each side of " + marker
                + ", for example: " + example);
    }
}
