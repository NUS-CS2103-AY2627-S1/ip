package auto.exception;

/** Represents a user-facing error caused by an invalid command or argument. */
public class AutoException extends Exception {
    private static final String PREFIX = "Paiseh, ";

    private AutoException(String message) {
        super(PREFIX + message);
    }

    /** Returns an error for input that does not match a supported command. */
    public static AutoException unknownCommand() {
        return new AutoException("I don't understand this command lah.");
    }

    /** Returns an error for a task number outside the current task list. */
    public static AutoException noSuchTask(int taskNumber) {
        return new AutoException(String.format("no task %d leh. Type list to check the numbers.", taskNumber));
    }

    /** Returns an error for an argument that cannot be parsed as a task number. */
    public static AutoException notATaskNumber(String argument) {
        return new AutoException(String.format("'%s' is not a task number lah. Use a whole number.", argument));
    }

    /** Returns an error for a find command without a keyword. */
    public static AutoException findNeedsKeyword() {
        return new AutoException("find what ah? Try find book.");
    }

    /** Returns an error for a todo, deadline, or event without a description. */
    public static AutoException taskNeedsDescription() {
        return new AutoException("description cannot be blank leh. Tell me what the task is.");
    }

    /** Returns an error for a deadline without its required {@code /by} value. */
    public static AutoException deadlineNeedsBy() {
        return new AutoException("by when ah? Add /by followed by a date in dd/MM/yyyy.");
    }

    /** Returns an error for an event without both required range values. */
    public static AutoException eventNeedsFromAndTo() {
        return new AutoException("when start, when end? Add /from and /to dates in dd/MM/yyyy.");
    }

    /** Returns an error for an event whose end date precedes its start date. */
    public static AutoException eventEndsBeforeItStarts() {
        return new AutoException("end before start cannot lah. Check your /from and /to dates.");
    }

    /** Returns an error for a date that fails format or calendar validation. */
    public static AutoException invalidDate(String value) {
        return new AutoException(String.format(
                "'%s' is not a valid date leh. Use dd/MM/yyyy.", value));
    }
}
