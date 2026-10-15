package michael;

import task.Deadline;
import task.Todo;
import task.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Handles parsing and validation of user commands and parameters.
 */
public final class Parser {

    private Parser() {
        // Utility class; do not instantiate.
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    /**
     * Parses a string representation of a task index into a 0-based integer index.
     *
     * @param args The input string containing the 1-based task number.
     * @param commandName The name of the command invoking this parser, used for error messages.
     * @return The 0-based index of the task.
     * @throws MichaelException If {@code args} is empty or cannot be parsed as an integer.
     */
    public static int parseTaskIndex(String args, String commandName) throws MichaelException {
        if (args.isEmpty()) {
            throw new MichaelException("Please provide a task number after " + commandName + ".");
        }
        try {
            int index = Integer.parseInt(args) - 1;
            if (index < 0) {
                throw new MichaelException("Task numbers must be positive.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new MichaelException("Task numbers must be whole numbers.");
        }
    }

    /**
     * Parses arguments to construct a {@link Todo} task.
     *
     * @param args The input string containing the description of the todo.
     * @return A new {@code Todo} task with the specified description.
     * @throws MichaelException If the description is empty.
     */
    public static Todo parseTodo(String args) throws MichaelException {
        if (args.isEmpty()) {
            throw new MichaelException("The description of a todo cannot be empty.");
        }
        return new Todo(args);
    }

    /**
     * Parses arguments to construct a {@link Deadline} task.
     * Expects a description followed by the {@code /by} flag and a date-time string in "yyyy-MM-dd HHmm" format.
     *
     * @param args The input string containing the description and deadline date-time.
     * @return A new {@code Deadline} task with the specified description and date-time.
     * @throws MichaelException If the {@code /by} flag is missing, description/date is empty,
     *                          or the date string does not match the expected pattern.
     */
    public static Deadline parseDeadline(String args) throws MichaelException {
        int byIndex = args.indexOf("/by");
        if (byIndex == -1) {
            throw new MichaelException("Please include a deadline using /by.");
        }

        assert byIndex >= 0 && byIndex + 3 <= args.length()
                : "The /by marker must identify a valid substring boundary";

        String description = args.substring(0, byIndex).trim();
        String by = args.substring(byIndex + 3).trim();

        if (description.isEmpty()) {
            throw new MichaelException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new MichaelException("Please provide a time after /by.");
        }

        try {
            LocalDateTime deadline = LocalDateTime.parse(by, DATE_TIME_FORMATTER);
            return new Deadline(description, deadline);
        } catch (DateTimeParseException e) {
            throw new MichaelException(
                    "Please enter the deadline in the format yyyy-MM-dd HHmm, e.g. 2019-12-02 1800."
            );
        }
    }

    /**
     * Parses arguments to construct an {@link Event} task.
     * Expects a description followed by {@code /from} and {@code /to} flags specifying the duration.
     *
     * @param args The input string containing the description, start time, and end time.
     * @return A new {@code Event} task with the specified description, start time, and end time.
     * @throws MichaelException If either flag is missing, ordered incorrectly, or if any component is empty.
     */
    public static Event parseEvent(String args) throws MichaelException {
        int fromIndex = args.indexOf("/from");
        int toIndex = args.indexOf("/to");

        if (fromIndex == -1 || toIndex == -1 || toIndex <= fromIndex) {
            throw new MichaelException("Please include an event time using /from and /to.");
        }

        assert fromIndex >= 0 && toIndex > fromIndex && fromIndex + 5 <= toIndex
                && toIndex + 3 <= args.length()
                : "Event markers must identify valid, ordered substring boundaries";

        String description = args.substring(0, fromIndex).trim();
        String from = args.substring(fromIndex + 5, toIndex).trim();
        String to = args.substring(toIndex + 3).trim();

        if (description.isEmpty()) {
            throw new MichaelException("The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new MichaelException("Please provide times after /from and /to.");
        }

        return new Event(description, from, to);
    }
    /**
     * Parses the arguments for the find command.
     *
     * @param args The input string containing the search keyword.
     * @return The validated search keyword.
     * @throws MichaelException If the keyword is empty.
     */
    public static String parseFind(String args) throws MichaelException {
        if (args.isEmpty()) {
            throw new MichaelException("Please enter a keyword to search for.");
        }
        return args;
    }
}
