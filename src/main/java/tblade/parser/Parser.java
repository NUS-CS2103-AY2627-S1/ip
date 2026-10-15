package tblade.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import tblade.exception.TBladeException;

/**
 * Makes sense of the raw command text entered by the user: extracts the arguments for
 * each command and validates that they are well-formed, before TBlade acts on them.
 */
public class Parser {
    private static final String TODO_COMMAND_WORD = "todo";
    private static final String FIND_COMMAND_WORD = "find";
    private static final String DEADLINE_COMMAND_WORD = "deadline";
    private static final String EVENT_COMMAND_WORD = "event";
    private static final String BY_MARKER = "/by";
    private static final String BY_DELIMITER = " " + BY_MARKER;
    private static final String FROM_MARKER = "/from";
    private static final String FROM_DELIMITER = " " + FROM_MARKER;
    private static final String TO_MARKER = "/to";
    private static final String TO_DELIMITER = " " + TO_MARKER;

    /**
     * The description and due date parsed from a {@code deadline} command.
     *
     * @param description text that describes the deadline task
     * @param by the date by which the task must be completed
     */
    public record DeadlineArgs(String description, LocalDate by) {
    }

    /**
     * The description, start, and end parsed from an {@code event} command.
     *
     * @param description text that describes the event task
     * @param from the event start text entered by the user
     * @param to the event end text entered by the user
     */
    public record EventArgs(String description, String from, String to) {
    }

    /**
     * Prevents instantiation; Parser only exposes static methods.
     */
    private Parser() {
    }

    /**
     * Extracts and validates the description of a {@code todo} command.
     *
     * @param command full command entered by the user
     * @return the todo description
     * @throws TBladeException if the description is empty
     */
    public static String getTodoDescription(String command) throws TBladeException {
        String description = command.substring(TODO_COMMAND_WORD.length()).trim();
        if (description.isEmpty()) {
            throw new TBladeException("The description of a todo cannot be empty. Use: todo DESCRIPTION");
        }
        return description;
    }

    /**
     * Extracts and validates the search keyword of a {@code find} command.
     *
     * @param command full command entered by the user
     * @return the search keyword
     * @throws TBladeException if the keyword is empty
     */
    public static String getFindKeyword(String command) throws TBladeException {
        String keyword = command.substring(FIND_COMMAND_WORD.length()).trim();
        if (keyword.isEmpty()) {
            throw new TBladeException("The search keyword cannot be empty. Use: find KEYWORD");
        }
        return keyword;
    }

    /**
     * Extracts and validates the description and {@code /by} date of a {@code deadline} command.
     *
     * @param command full command entered by the user
     * @return the parsed deadline description and date
     * @throws TBladeException if the description, date, or date format is invalid
     */
    public static DeadlineArgs parseDeadlineArgs(String command) throws TBladeException {
        String details = command.substring(DEADLINE_COMMAND_WORD.length()).trim();
        if (details.isEmpty() || details.startsWith(BY_MARKER)) {
            throw new TBladeException("The description of a deadline cannot be empty. "
                    + "Use: deadline DESCRIPTION /by DATE");
        }
        int byIndex = details.indexOf(BY_DELIMITER);
        if (byIndex < 0) {
            throw new TBladeException("A deadline needs a /by date. "
                    + "Use: deadline DESCRIPTION /by DATE");
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + BY_DELIMITER.length()).trim();
        if (by.isEmpty()) {
            throw new TBladeException("The /by date cannot be empty. "
                    + "Use: deadline DESCRIPTION /by DATE");
        }
        return new DeadlineArgs(description, parseDeadlineDate(by));
    }

    /**
     * Extracts and validates the description, {@code /from}, and {@code /to} of an {@code event} command.
     *
     * @param command full command entered by the user
     * @return the parsed event description, start, and end
     * @throws TBladeException if the description, start, or end is missing
     */
    public static EventArgs parseEventArgs(String command) throws TBladeException {
        String details = command.substring(EVENT_COMMAND_WORD.length()).trim();
        if (details.isEmpty() || details.startsWith(FROM_MARKER)) {
            throw new TBladeException("The description of an event cannot be empty. "
                    + "Use: event DESCRIPTION /from START /to END");
        }
        int fromIndex = details.indexOf(FROM_DELIMITER);
        if (fromIndex < 0) {
            throw new TBladeException("An event needs a /from start time. "
                    + "Use: event DESCRIPTION /from START /to END");
        }
        String description = details.substring(0, fromIndex).trim();
        String times = details.substring(fromIndex + FROM_DELIMITER.length()).trim();
        if (times.isEmpty()) {
            throw new TBladeException("The /from start time cannot be empty. "
                    + "Use: event DESCRIPTION /from START /to END");
        }
        int toIndex = times.indexOf(TO_DELIMITER);
        if (toIndex < 0) {
            throw new TBladeException("An event needs a /to end time. "
                    + "Use: event DESCRIPTION /from START /to END");
        }
        String from = times.substring(0, toIndex).trim();
        String to = times.substring(toIndex + TO_DELIMITER.length()).trim();
        if (from.isEmpty()) {
            throw new TBladeException("The /from start time cannot be empty. "
                    + "Use: event DESCRIPTION /from START /to END");
        }
        if (to.isEmpty()) {
            throw new TBladeException("The /to end time cannot be empty. "
                    + "Use: event DESCRIPTION /from START /to END");
        }
        return new EventArgs(description, from, to);
    }

    /**
     * Converts a command's task number to a valid zero-based list index.
     *
     * @param command full command entered by the user
     * @param commandWord the command word before its number
     * @param taskCount number of tasks currently stored
     * @return the zero-based index of the requested task
     * @throws TBladeException if the command does not contain a valid task number
     */
    public static int parseTaskIndex(String command, String commandWord, int taskCount) throws TBladeException {
        String taskNumberText = command.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new TBladeException("Please provide a task number. Use: " + commandWord + " TASK_NUMBER");
        }
        if (taskCount == 0) {
            throw new TBladeException("There are no tasks to " + commandWord + ". Add a task first.");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new TBladeException("Task number must be between 1 and " + taskCount + ". "
                        + "Use: " + commandWord + " TASK_NUMBER");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new TBladeException("Task number must be a whole number. "
                    + "Use: " + commandWord + " TASK_NUMBER");
        }
    }

    /**
     * Parses an ISO-8601 deadline date supplied with the {@code /by} argument.
     *
     * @param dateText date entered by the user
     * @return the parsed deadline date
     * @throws TBladeException if the date is not in {@code yyyy-MM-dd} format
     */
    private static LocalDate parseDeadlineDate(String dateText) throws TBladeException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new TBladeException("The /by date must use yyyy-MM-dd. Use: deadline DESCRIPTION /by yyyy-MM-dd");
        }
    }
}
