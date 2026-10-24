package shannon.exception;

/**
 * Thrown when an event does not end after it starts,
 * e.g. {@code event meeting /from 2026-08-09 16:00 /to 2026-08-09 14:00}.
 */
public class InvalidEventTimesException extends ShannonException {

    /**
     * Builds the message for an event whose end is not after its start.
     *
     * @param from the start, as it would be shown to the user.
     * @param to   the end, as it would be shown to the user.
     */
    public InvalidEventTimesException(String from, String to) {
        // Equal text means the same date and time. Two equal dates without a time never get
        // here, because such an event lasts the whole of that day.
        super(from.equals(to)
                ? "The event starts and ends at the same moment (" + from + ")."
                        + " Please give it a /to that is later than its /from."
                : "The event ends before it starts: from " + from + " to " + to + "."
                        + " Please give it a /to that is later than its /from.");
    }
}
