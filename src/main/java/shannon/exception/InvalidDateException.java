package shannon.exception;

/**
 * Thrown when a date, or the time of day that goes with it, was given in a form we cannot read,
 * e.g. {@code deadline report /by tomorrow}, or names one that does not exist, e.g.
 * {@code 2026-02-30}.
 * <p>
 * Like {@link StorageException}, each case is built by a named factory method rather than a
 * constructor, since several of them take the same kinds of argument.
 */
public class InvalidDateException extends ShannonException {

    /**
     * Creates an exception with an already-worded message.
     *
     * @param message the text to show the user.
     */
    private InvalidDateException(String message) {
        super(message);
    }

    /**
     * Creates the exception for text that is not in {@code yyyy-mm-dd} form at all.
     *
     * @param text the date as the user typed it, echoed back so it is clear which part was wrong.
     * @return the exception, ready to be thrown.
     */
    public static InvalidDateException ofUnreadableDate(String text) {
        return new InvalidDateException("I couldn't understand the date \"" + text + "\"."
                + " Please write it as yyyy-mm-dd, for example: 2026-08-09");
    }

    /**
     * Creates the exception for a date in the right form that names a day that does not exist.
     *
     * @param text   the date as the user typed it.
     * @param reason why it does not exist, e.g. {@code February 2026 only has 28 days}.
     * @return the exception, ready to be thrown.
     */
    public static InvalidDateException ofNonExistentDate(String text, String reason) {
        return new InvalidDateException("\"" + text + "\" isn't a real date: " + reason + ".");
    }

    /**
     * Creates the exception for an event's start or end that is not a date with an optional time.
     *
     * @param text the start or end as the user typed it.
     * @return the exception, ready to be thrown.
     */
    public static InvalidDateException ofUnreadableDateTime(String text) {
        return new InvalidDateException("I couldn't understand \"" + text + "\" as a date and time."
                + " Please write it as yyyy-mm-dd, or yyyy-mm-dd HH:mm to include a time,"
                + " for example: 2026-08-09 14:00");
    }

    /**
     * Creates the exception for a time of day that is not in 24-hour {@code HH:mm} form, or does
     * not exist, e.g. {@code 2pm} or {@code 25:00}.
     *
     * @param text the time as the user typed it.
     * @return the exception, ready to be thrown.
     */
    public static InvalidDateException ofUnreadableTime(String text) {
        return new InvalidDateException("I couldn't understand the time \"" + text + "\"."
                + " Please write it in 24-hour form as HH:mm, from 00:00 to 23:59, for example: 14:00");
    }
}
