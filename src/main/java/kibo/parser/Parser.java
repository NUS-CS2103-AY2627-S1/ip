package kibo.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import kibo.exception.InvalidCommandException;
import kibo.exception.KiboException;
import kibo.task.Deadline;
import kibo.task.Event;
import kibo.task.Task;
import kibo.task.Todo;

/**
 * Interprets and validates commands entered by the user.
 */
public class Parser {
    private static final String TODO_USAGE = "Usage: todo [description]";
    private static final String DEADLINE_USAGE =
            "Usage: deadline [description] /by yyyy-MM-dd";
    private static final String EVENT_USAGE =
            "Usage: event [description] /from [start] /to [end]";
    private static final String FIND_USAGE = "Usage: find [keyword]";
    private static final String SCHEDULE_USAGE = "Usage: schedule yyyy-MM-dd";
    private static final String DATED_EVENT_USAGE =
            "Date-aware event usage: event [description] /from yyyy-MM-dd [time] /to [end]";
    private static final String AVAILABLE_COMMANDS =
            "Available commands: todo, deadline, event, list, find, schedule, mark, unmark, delete, bye";
    private static final Pattern DATE_LIKE_EVENT_PREFIX =
            Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");

    /**
     * Creates a parser for interpreting Kibo commands.
     */
    public Parser() {
    }

    /**
     * Identifies a valid command from the full user input.
     *
     * @param input full user input.
     * @return command type represented by the input.
     * @throws InvalidCommandException if the input is empty or does not begin with a command.
     */
    public CommandType parseCommandType(String input) throws InvalidCommandException {
        if (input.isEmpty()) {
            throw new InvalidCommandException("Please enter a command.\n" + AVAILABLE_COMMANDS);
        }

        CommandType commandType = CommandType.fromInput(input);
        if (commandType == CommandType.UNKNOWN) {
            throw new InvalidCommandException(
                    "Sorry, that is not a valid command.\n" + AVAILABLE_COMMANDS);
        }
        return commandType;
    }

    /**
     * Rejects additional words supplied to a command that has no arguments.
     *
     * @param input full user input.
     * @param command command type.
     * @throws InvalidCommandException if the user supplied extra text.
     */
    public void ensureNoArguments(String input, CommandType command)
            throws InvalidCommandException {
        if (!input.equals(command.getKeyword())) {
            throw new InvalidCommandException("This command does not take any additional text.\n"
                    + "Usage: " + command.getKeyword());
        }
    }

    /**
     * Extracts and validates the task number used by mark, unmark, and delete commands.
     *
     * @param input full user input.
     * @param command command type.
     * @param taskCount number of tasks currently stored.
     * @return zero-based task index.
     * @throws KiboException if the number is missing, invalid, or out of range.
     */
    public int parseTaskIndex(String input, CommandType command, int taskCount)
            throws KiboException {
        assert command == CommandType.MARK
                || command == CommandType.UNMARK
                || command == CommandType.DELETE
                : "Only numbered task commands can have a task index";
        assert taskCount >= 0 : "The number of stored tasks cannot be negative";

        String numberText = input.substring(command.getKeyword().length()).trim();
        int taskNumber;

        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new InvalidCommandException("Please provide a valid task number.\n"
                    + "Usage: " + command.getKeyword() + " [task number]");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new KiboException("Task " + taskNumber + " does not exist in your list.");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts and validates the keyword used to find tasks.
     *
     * @param input full find command.
     * @return non-empty search keyword.
     * @throws InvalidCommandException if the keyword is empty.
     */
    public String parseFindKeyword(String input) throws InvalidCommandException {
        String keyword = input.substring(CommandType.FIND.getKeyword().length()).trim();
        if (keyword.isEmpty()) {
            throw new InvalidCommandException(
                    "The search keyword cannot be empty.\n" + FIND_USAGE);
        }
        return keyword;
    }

    /**
     * Extracts and validates the date used to view a schedule.
     *
     * @param input full schedule command.
     * @return requested schedule date.
     * @throws InvalidCommandException if the date is missing or invalid.
     */
    public LocalDate parseScheduleDate(String input) throws InvalidCommandException {
        String dateText = input.substring(CommandType.SCHEDULE.getKeyword().length()).trim();
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandException(
                    "The schedule date must use yyyy-MM-dd format.\n" + SCHEDULE_USAGE);
        }
    }

    /**
     * Creates a to-do task from validated user input.
     *
     * @param input full todo command.
     * @return parsed to-do task.
     * @throws InvalidCommandException if the description is empty.
     */
    public Task parseTodo(String input) throws InvalidCommandException {
        String description = input.substring(CommandType.TODO.getKeyword().length()).trim();
        if (description.isEmpty()) {
            throw new InvalidCommandException(
                    "The description of a todo cannot be empty.\n" + TODO_USAGE);
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline task from validated user input.
     *
     * @param input full deadline command.
     * @return parsed deadline task.
     * @throws InvalidCommandException if the description, marker, or date is missing or invalid.
     */
    public Task parseDeadline(String input) throws InvalidCommandException {
        String taskDetails = input.substring(CommandType.DEADLINE.getKeyword().length()).trim();
        int byMarkerIndex = taskDetails.indexOf(" /by");
        if (byMarkerIndex < 0) {
            throw new InvalidCommandException(
                    "A deadline needs a description and /by date.\n"
                    + DEADLINE_USAGE);
        }

        String description = taskDetails.substring(0, byMarkerIndex).trim();
        String dateText = taskDetails.substring(byMarkerIndex + 4).trim();
        if (description.isEmpty() || dateText.isEmpty()) {
            throw new InvalidCommandException(
                    "A deadline needs a description and /by date.\n"
                    + DEADLINE_USAGE);
        }

        try {
            return new Deadline(description, LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandException("The deadline date must use yyyy-MM-dd format.\n"
                    + DEADLINE_USAGE);
        }
    }

    /**
     * Creates an event task from validated user input.
     *
     * @param input full event command.
     * @return parsed event task.
     * @throws InvalidCommandException if the description, markers, start, or end is missing.
     */
    public Task parseEvent(String input) throws InvalidCommandException {
        String taskDetails = input.substring(CommandType.EVENT.getKeyword().length()).trim();
        int fromMarkerIndex = taskDetails.indexOf(" /from");
        int toMarkerIndex = taskDetails.indexOf(" /to", fromMarkerIndex + 1);
        if (fromMarkerIndex < 0 || toMarkerIndex < 0 || toMarkerIndex < fromMarkerIndex) {
            throw new InvalidCommandException(
                    "An event needs a description, /from start, and /to end.\n"
                    + EVENT_USAGE);
        }

        String description = taskDetails.substring(0, fromMarkerIndex).trim();
        String from = taskDetails.substring(fromMarkerIndex + 6, toMarkerIndex).trim();
        String to = taskDetails.substring(toMarkerIndex + 4).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new InvalidCommandException(
                    "An event needs a description, /from start, and /to end.\n"
                    + EVENT_USAGE);
        }
        validateDatedEventStart(from);
        return new Event(description, from, to);
    }

    /**
     * Rejects an invalid ISO-like date at the beginning of an event start.
     *
     * @param from event start text.
     * @throws InvalidCommandException if the first token resembles but is not a valid ISO date.
     */
    private static void validateDatedEventStart(String from) throws InvalidCommandException {
        String dateText = from.split("\\s+", 2)[0];
        if (!DATE_LIKE_EVENT_PREFIX.matcher(dateText).matches()) {
            return;
        }

        try {
            LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new InvalidCommandException(
                    "The event start date must use yyyy-MM-dd format.\n" + DATED_EVENT_USAGE);
        }
    }
}
