package luckynoslacky.luckytask;

import java.util.Objects;

/**
 * Reports a scheduling rule violation without binding task logic to UI text.
 */
public class TaskSchedulingException extends RuntimeException {
    /** Identifies the scheduling rule that was violated. */
    public enum Reason {
        /** A task has no schedule to modify. */
        TODO_TASK,
        /** A deadline would be set before the current time. */
        PAST_DEADLINE,
        /** An event's end would be before its start. */
        END_BEFORE_START,
        /** A duration cannot be represented as a date and time. */
        TIME_OVERFLOW
    }

    /** Categorizes the scheduling rule violated by this exception. */
    private final Reason reason;

    /**
     * Creates a scheduling exception for a violated rule.
     *
     * @param reason scheduling rule that was violated
     */
    public TaskSchedulingException(Reason reason) {
        this(reason, null);
    }

    /**
     * Creates a scheduling exception with the cause of a violated rule.
     *
     * @param reason scheduling rule that was violated
     * @param cause underlying failure, if any
     */
    public TaskSchedulingException(Reason reason, Throwable cause) {
        super(getMessage(reason), cause);
        this.reason = reason;
    }

    /** Validates a reason and returns the exception message. */
    private static String getMessage(Reason reason) {
        return Objects.requireNonNull(reason, "Scheduling reason cannot be null.").name();
    }

    /**
     * Returns the scheduling rule that was violated.
     *
     * @return scheduling failure reason
     */
    public Reason getReason() {
        return reason;
    }
}
