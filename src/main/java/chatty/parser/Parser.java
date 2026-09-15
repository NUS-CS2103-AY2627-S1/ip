package chatty.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import chatty.exception.ChattyException;
import chatty.task.Deadline;
import chatty.task.Event;
import chatty.task.RecurrenceUnit;
import chatty.task.RecurringTask;
import chatty.task.Task;
import chatty.task.Todo;

/**
 * Interprets user input as commands, tasks, and task numbers.
 */
public class Parser {
    /** Delimiter separating a deadline description from its due date. */
    private static final String DEADLINE_BY_DELIMITER = "/by";

    /** Delimiter separating an event description from its start value. */
    private static final String EVENT_FROM_DELIMITER = "/from";

    /** Delimiter separating an event start value from its end value. */
    private static final String EVENT_TO_DELIMITER = "/to";

    /** Delimiter separating a recurring-task description from its next date. */
    private static final String RECURRING_ON_DELIMITER = "/on";

    /** Delimiter separating a recurring-task date from its interval. */
    private static final String RECURRING_EVERY_DELIMITER = "/every";

    /** Prevents instantiation of this command-parsing utility class. */
    private Parser() {
    }

    /**
     * Returns the command type represented by the normalized input.
     *
     * @param input normalized user input.
     * @return matching command type, or {@link CommandType#UNKNOWN} when no command matches.
     */
    public static CommandType parseCommand(String input) {
        for (CommandType command : CommandType.values()) {
            boolean isExactMatch = input.equals(command.getKeyword());
            boolean hasAcceptedArguments = command.acceptsArguments()
                    && input.startsWith(command.getKeyword() + " ");
            if (isExactMatch || hasAcceptedArguments) {
                return command;
            }
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Creates a task from an add command and its arguments.
     *
     * @param input normalized user input.
     * @param command type of task to create.
     * @return task described by the command.
     * @throws ChattyException if required task details are missing or invalid.
     */
    public static Task parseTask(String input, CommandType command) throws ChattyException {
        switch (command) {
            case TODO:
                return parseTodo(input);
            case DEADLINE:
                return parseDeadline(input);
            case EVENT:
                return parseEvent(input);
            case RECURRING:
                return parseRecurringTask(input);
            default:
                throw new ChattyException("OOPS!!! That command does not create a task.");
        }
    }

    /**
     * Returns the one-based task number supplied to a task operation.
     *
     * @param input normalized user input.
     * @param command task operation being parsed.
     * @return one-based task number.
     * @throws ChattyException if the task number is absent or not a whole number.
     */
    public static int parseTaskNumber(String input, CommandType command) throws ChattyException {
        String indexText = input.substring(command.getKeyword().length()).strip();
        if (indexText.isEmpty()) {
            throw new ChattyException("OOPS!!! Tell me which task to " + command.getKeyword() + ".");
        }

        try {
            return Integer.parseInt(indexText);
        } catch (NumberFormatException exception) {
            throw new ChattyException("OOPS!!! The task number must be a whole number.");
        }
    }

    /**
     * Returns the keyword supplied to a find command.
     *
     * @param input normalized find command.
     * @return keyword to search for.
     * @throws ChattyException if the keyword is absent.
     */
    public static String parseFindKeyword(String input) throws ChattyException {
        String keyword = input.substring(CommandType.FIND.getKeyword().length()).strip();
        if (keyword.isEmpty()) {
            throw new ChattyException("OOPS!!! Tell me what keyword to find.");
        }
        return keyword;
    }

    /**
     * Creates a todo from its command arguments.
     *
     * @param input normalized todo command.
     * @return parsed todo.
     * @throws ChattyException if the todo description is empty.
     */
    private static Todo parseTodo(String input) throws ChattyException {
        String description = input.substring("todo".length()).strip();
        requireDescription(description, "todo");
        return new Todo(description);
    }

    /**
     * Creates a deadline from its description and ISO date.
     *
     * @param input normalized deadline command.
     * @return parsed deadline.
     * @throws ChattyException if the deadline details or date are invalid.
     */
    private static Deadline parseDeadline(String input) throws ChattyException {
        String details = input.substring("deadline".length()).strip();
        int byIndex = details.indexOf(DEADLINE_BY_DELIMITER);
        if (byIndex < 0) {
            throw new ChattyException("OOPS!!! A deadline needs '" + DEADLINE_BY_DELIMITER + "'. "
                    + "Try: deadline DESCRIPTION " + DEADLINE_BY_DELIMITER + " DATE_OR_TIME");
        }

        requireSingleDelimiter(details, DEADLINE_BY_DELIMITER, "deadline");
        String description = details.substring(0, byIndex).strip();
        String byText = details.substring(byIndex + DEADLINE_BY_DELIMITER.length()).strip();
        requireDescription(description, "deadline");
        requireStorableText(byText, "deadline date");
        if (byText.isEmpty()) {
            throw new ChattyException("OOPS!!! Tell me when the deadline is due after '"
                    + DEADLINE_BY_DELIMITER + "'.");
        }

        try {
            return new Deadline(description, LocalDate.parse(byText));
        } catch (DateTimeParseException exception) {
            throw new ChattyException("OOPS!!! Use YYYY-MM-DD for deadline dates, such as 2019-10-15.");
        }
    }

    /**
     * Creates an event from its description and time range.
     * The first {@code /from} and the first subsequent {@code /to} are treated as delimiters.
     * Delimiter tokens are therefore not supported as literal text in an event description.
     *
     * @param input normalized event command.
     * @return parsed event.
     * @throws ChattyException if the event details are incomplete.
     */
    private static Event parseEvent(String input) throws ChattyException {
        String details = input.substring("event".length()).strip();
        int fromIndex = details.indexOf(EVENT_FROM_DELIMITER);
        if (fromIndex < 0) {
            throw new ChattyException("OOPS!!! An event needs '" + EVENT_FROM_DELIMITER
                    + "' and '" + EVENT_TO_DELIMITER + "'. "
                    + "Try: event DESCRIPTION " + EVENT_FROM_DELIMITER + " START "
                    + EVENT_TO_DELIMITER + " END");
        }

        requireSingleDelimiter(details, EVENT_FROM_DELIMITER, "event");
        int toIndex = details.indexOf(EVENT_TO_DELIMITER,
                fromIndex + EVENT_FROM_DELIMITER.length());
        if (toIndex < 0) {
            throw new ChattyException("OOPS!!! An event with '" + EVENT_FROM_DELIMITER
                    + "' also needs an ending value after '" + EVENT_TO_DELIMITER + "'.");
        }

        requireSingleDelimiter(details, EVENT_TO_DELIMITER, "event");
        String description = details.substring(0, fromIndex).strip();
        String from = details.substring(fromIndex + EVENT_FROM_DELIMITER.length(), toIndex).strip();
        String to = details.substring(toIndex + EVENT_TO_DELIMITER.length()).strip();
        requireDescription(description, "event");
        if (from.isEmpty()) {
            throw new ChattyException("OOPS!!! Tell me when the event starts after '"
                    + EVENT_FROM_DELIMITER + "'.");
        } else if (to.isEmpty()) {
            throw new ChattyException("OOPS!!! Tell me when the event ends after '"
                    + EVENT_TO_DELIMITER + "'.");
        }
        requireStorableText(from, "event start");
        requireStorableText(to, "event end");
        return new Event(description, from, to);
    }

    /**
     * Creates a recurring task from its next date and recurrence interval.
     * The first {@code /on} and first subsequent {@code /every} are treated as delimiters.
     *
     * @param input normalized recurring-task command.
     * @return parsed recurring task.
     * @throws ChattyException if the recurring-task details are missing or invalid.
     */
    private static RecurringTask parseRecurringTask(String input) throws ChattyException {
        String details = input.substring(CommandType.RECURRING.getKeyword().length()).strip();
        int onIndex = details.indexOf(RECURRING_ON_DELIMITER);
        int everyIndex = findRecurringEveryIndex(details, onIndex);
        String description = details.substring(0, onIndex).strip();
        String dateText = details.substring(
                onIndex + RECURRING_ON_DELIMITER.length(), everyIndex).strip();
        String recurrenceText = details.substring(
                everyIndex + RECURRING_EVERY_DELIMITER.length()).strip();
        requireDescription(description, "recurring task");
        requireRecurringValues(dateText, recurrenceText);
        LocalDate nextOccurrenceDate = parseRecurringDate(dateText);
        return createRecurringTask(description, nextOccurrenceDate, recurrenceText);
    }

    /**
     * Validates recurring delimiters and locates the interval delimiter.
     *
     * @param details recurring-task details.
     * @param onIndex location of the date delimiter, or -1 if absent.
     * @return location of the interval delimiter.
     * @throws ChattyException if delimiters are missing, repeated, or out of order.
     */
    private static int findRecurringEveryIndex(String details, int onIndex) throws ChattyException {
        if (onIndex < 0) {
            throw new ChattyException("OOPS!!! A recurring task needs '"
                    + RECURRING_ON_DELIMITER + "' and '" + RECURRING_EVERY_DELIMITER + "'. "
                    + "Try: recurring DESCRIPTION " + RECURRING_ON_DELIMITER
                    + " YYYY-MM-DD " + RECURRING_EVERY_DELIMITER + " NUMBER UNIT");
        }

        requireSingleDelimiter(details, RECURRING_ON_DELIMITER, "recurring task");
        int everyIndex = details.indexOf(RECURRING_EVERY_DELIMITER,
                onIndex + RECURRING_ON_DELIMITER.length());
        if (everyIndex < 0) {
            throw new ChattyException("OOPS!!! A recurring task with '"
                    + RECURRING_ON_DELIMITER + "' also needs an interval after '"
                    + RECURRING_EVERY_DELIMITER + "'.");
        }

        requireSingleDelimiter(details, RECURRING_EVERY_DELIMITER, "recurring task");
        return everyIndex;
    }

    /**
     * Checks that both recurring schedule values are supplied.
     *
     * @param dateText next occurrence date text.
     * @param recurrenceText interval text.
     * @throws ChattyException if either value is empty.
     */
    private static void requireRecurringValues(String dateText, String recurrenceText) throws ChattyException {
        if (dateText.isEmpty()) {
            throw new ChattyException("OOPS!!! Tell me the next occurrence date after '"
                    + RECURRING_ON_DELIMITER + "'.");
        } else if (recurrenceText.isEmpty()) {
            throw new ChattyException("OOPS!!! Tell me the recurrence interval after '"
                    + RECURRING_EVERY_DELIMITER + "'.");
        }

    }

    /**
     * Parses the next occurrence date with a recurring-task-specific error.
     *
     * @param dateText next occurrence in ISO date format.
     * @return parsed calendar date.
     * @throws ChattyException if the date is invalid.
     */
    private static LocalDate parseRecurringDate(String dateText) throws ChattyException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new ChattyException("OOPS!!! Use YYYY-MM-DD for recurring-task dates, "
                    + "such as 2026-09-14.");
        }

    }

    /**
     * Creates a recurring task after validating its interval and unit.
     *
     * @param description validated task description.
     * @param nextOccurrenceDate validated next occurrence date.
     * @param recurrenceText positive interval followed by a supported unit.
     * @return task with the supplied schedule.
     * @throws ChattyException if the interval or unit is invalid.
     */
    private static RecurringTask createRecurringTask(String description, LocalDate nextOccurrenceDate,
            String recurrenceText) throws ChattyException {
        String[] recurrenceParts = recurrenceText.split("\\s+");
        if (recurrenceParts.length != 2) {
            throw invalidRecurrenceInterval();
        }

        int interval;
        try {
            interval = Integer.parseInt(recurrenceParts[0]);
        } catch (NumberFormatException exception) {
            throw invalidRecurrenceInterval();
        }
        if (interval <= 0) {
            throw invalidRecurrenceInterval();
        }

        RecurrenceUnit recurrenceUnit;
        try {
            recurrenceUnit = RecurrenceUnit.fromInput(recurrenceParts[1]);
        } catch (IllegalArgumentException exception) {
            throw invalidRecurrenceInterval();
        }
        return new RecurringTask(description, nextOccurrenceDate, interval, recurrenceUnit);
    }

    /**
     * Returns the standard error for an invalid recurrence interval.
     *
     * @return exception describing the accepted interval syntax.
     */
    private static ChattyException invalidRecurrenceInterval() {
        return new ChattyException("OOPS!!! Use a positive whole number followed by day, week, "
                + "month, or year after '" + RECURRING_EVERY_DELIMITER + "'.");
    }

    /**
     * Ensures a delimiter occurs exactly once in a task command.
     *
     * @param details task-command details containing delimiters.
     * @param delimiter delimiter that must occur once.
     * @param taskType task type used in the error message.
     * @throws ChattyException if the delimiter is repeated.
     */
    private static void requireSingleDelimiter(String details, String delimiter, String taskType)
            throws ChattyException {
        if (details.indexOf(delimiter) != details.lastIndexOf(delimiter)) {
            throw new ChattyException("OOPS!!! Use '" + delimiter + "' only once in a "
                    + taskType + " command.");
        }
    }

    /**
     * Ensures a user-provided value can be represented by the storage format.
     *
     * @param text value to validate.
     * @param fieldName field described in the error message.
     * @throws ChattyException if the value contains a reserved separator.
     */
    private static void requireStorableText(String text, String fieldName) throws ChattyException {
        if (text.contains("|")) {
            throw new ChattyException("OOPS!!! The " + fieldName
                    + " cannot contain the reserved '|' character.");
        }
    }

    /**
     * Throws a specific error when a task description is empty.
     *
     * @param description task description to validate.
     * @param taskType task type used in the error message.
     * @throws ChattyException if the description is empty.
     */
    private static void requireDescription(String description, String taskType) throws ChattyException {
        if (description.isEmpty()) {
            String article = taskType.equals("event") ? "an" : "a";
            throw new ChattyException("OOPS!!! The description of " + article + " "
                    + taskType + " cannot be empty.");
        }
        requireStorableText(description, taskType + " description");
    }
}
