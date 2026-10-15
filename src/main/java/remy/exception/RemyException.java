package remy.exception;

/**
 * Represents an error caused by an invalid user command.
 */
public class RemyException extends RuntimeException {
    /** Divider used to frame messages shown to the user. */
    private static final String MESSAGE_DIVIDER = "____________________________________________________________\n";

    /** Creates an exception for an unknown command. */
    public RemyException() {
        super(formatMessage("Wha-? My bad bruh, I have no clue what you're talking about :/\n"));
    }

    /**
     * Creates an exception containing the given message.
     *
     * @param message Message to show to the user.
     */
    public RemyException(String message) {
        super(formatMessage(message + "\n"));
    }

    /**
     * Creates an exception for a to-do command with no description.
     *
     * @return An exception describing the missing to-do description.
     */
    public static RemyException createForMissingTodoDescription() {
        return new RemyException(formatTodoDetails());
    }

    /**
     * Creates an exception for a deadline command with missing details.
     *
     * @param hasDescription Whether a description was supplied.
     * @param hasDeadline Whether a deadline was supplied.
     * @return An exception describing the missing deadline details.
     */
    public static RemyException createForMissingDeadlineDetails(boolean hasDescription,
            boolean hasDeadline) {
        return new RemyException(formatDeadlineDetails(hasDescription, hasDeadline));
    }

    /**
     * Creates an exception for an event command with missing details.
     *
     * @param hasDescription Whether a description was supplied.
     * @param hasStart Whether a start endpoint was supplied.
     * @param hasEnd Whether an end endpoint was supplied.
     * @return An exception describing the missing event details.
     */
    public static RemyException createForMissingEventDetails(boolean hasDescription, boolean hasStart,
            boolean hasEnd) {
        return new RemyException(formatEventDetails(hasDescription, hasStart, hasEnd));
    }

    /**
     * Returns a message framed with the standard divider.
     *
     * @param details Details to display inside the message frame.
     * @return The formatted error message.
     */
    private static String formatMessage(String details) {
        return MESSAGE_DIVIDER + details + MESSAGE_DIVIDER;
    }

    /**
     * Returns the details for a to-do command without a description.
     *
     * @return The error details.
     */
    private static String formatTodoDetails() {
        return "Bruh :/, you need to fill in the description of your todo task, "
                + "unless you're doing nothing :/.\n"
                + "\nYour todo task should look something like this, please don't mess it up again -_-: \n"
                + "todo borrow book";
    }

    /**
     * Returns the details for a deadline command with missing details.
     *
     * @param hasDescription Whether a description was supplied.
     * @param hasDeadline Whether a deadline was supplied.
     * @return The error details.
     */
    private static String formatDeadlineDetails(boolean hasDescription, boolean hasDeadline) {
        String details = "Bruh :/, your deadline task is missing the following: \n";

        if (!hasDescription) {
            details += "- description. \n";
        }

        if (!hasDeadline) {
            details += "- DEADLINE of your DEADLINE task -_-. \n";
        }

        details += "\nYour deadline task should look something like this, "
                + "please don't mess it up again -_-: \n"
                + "deadline return book /by Sunday";
        return details;
    }

    /**
     * Returns the details for an event command with missing details.
     *
     * @param hasDescription Whether a description was supplied.
     * @param hasStart Whether a start endpoint was supplied.
     * @param hasEnd Whether an end endpoint was supplied.
     * @return The error details.
     */
    private static String formatEventDetails(boolean hasDescription, boolean hasStart, boolean hasEnd) {
        String details = "Bruh :/, your event task is missing the following: \n";

        if (!hasDescription) {
            details += "- description. \n";
        }

        if (!hasStart) {
            details += "- when your event task starts. \n";
        }

        if (!hasEnd) {
            details += "- when your event task ends. \n";
        }

        details += "\nYour event task should look something like this, "
                + "please don't mess it up again -_-: \n"
                + "event project meeting /from Mon 2pm /to 4pm";
        return details;
    }
}
