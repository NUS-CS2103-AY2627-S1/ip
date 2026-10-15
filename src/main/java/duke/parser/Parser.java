package duke.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.ToDo;

/**
 * Makes sense of user commands typed into the chatbot.
 */
public class Parser {
    private static final String EXIT_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String DELETE_COMMAND = "delete";
    private static final String DELETE_COMMAND_PREFIX = "delete ";
    private static final String MARK_COMMAND = "mark";
    private static final String MARK_COMMAND_PREFIX = "mark ";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String UNMARK_COMMAND_PREFIX = "unmark ";
    private static final String FIND_COMMAND = "find";
    private static final String FIND_COMMAND_PREFIX = "find ";
    private static final String SCHEDULE_COMMAND = "schedule";
    private static final String SCHEDULE_COMMAND_PREFIX = "schedule ";
    private static final String TODO_COMMAND = "todo";
    private static final String TODO_COMMAND_PREFIX = "todo ";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String DEADLINE_COMMAND_PREFIX = "deadline ";
    private static final String EVENT_COMMAND = "event";
    private static final String EVENT_COMMAND_PREFIX = "event ";
    private static final String BY_SEPARATOR = "/by";
    private static final String FROM_SEPARATOR = "/from";
    private static final String TO_SEPARATOR = "/to";

    /**
     * Commands that the chatbot understands.
     */
    public enum CommandType {
        EXIT,
        LIST,
        DELETE,
        MARK,
        UNMARK,
        FIND,
        SCHEDULE,
        TODO,
        DEADLINE,
        EVENT
    }

    /**
     * Returns the type of command typed by the user.
     *
     * @param command Full command entered by the user.
     * @return Command type represented by the command word.
     * @throws DukeException If the command word is unknown.
     */
    public static CommandType parseCommandType(String command) throws DukeException {
        if (command.isBlank()) {
            throw new DukeException("Please type a command. For example: todo read book");
        }
        if (command.equals(EXIT_COMMAND)) {
            return CommandType.EXIT;
        } else if (command.equals(LIST_COMMAND)) {
            return CommandType.LIST;
        } else if (command.equals(DELETE_COMMAND) || command.startsWith(DELETE_COMMAND_PREFIX)) {
            return CommandType.DELETE;
        } else if (command.equals(MARK_COMMAND) || command.startsWith(MARK_COMMAND_PREFIX)) {
            return CommandType.MARK;
        } else if (command.equals(UNMARK_COMMAND) || command.startsWith(UNMARK_COMMAND_PREFIX)) {
            return CommandType.UNMARK;
        } else if (command.equals(FIND_COMMAND) || command.startsWith(FIND_COMMAND_PREFIX)) {
            return CommandType.FIND;
        } else if (command.equals(SCHEDULE_COMMAND) || command.startsWith(SCHEDULE_COMMAND_PREFIX)) {
            return CommandType.SCHEDULE;
        } else if (command.equals(TODO_COMMAND) || command.startsWith(TODO_COMMAND_PREFIX)) {
            return CommandType.TODO;
        } else if (command.equals(DEADLINE_COMMAND) || command.startsWith(DEADLINE_COMMAND_PREFIX)) {
            return CommandType.DEADLINE;
        } else if (command.equals(EVENT_COMMAND) || command.startsWith(EVENT_COMMAND_PREFIX)) {
            return CommandType.EVENT;
        } else {
            throw new DukeException("Sorry, I don't understand that command. Try todo, deadline, event, list, "
                    + "mark, unmark, delete, find, schedule, or bye.");
        }
    }

    /**
     * Returns the keyword from a find command.
     *
     * @param command Full find command entered by the user.
     * @return Keyword to search for in task descriptions.
     * @throws DukeException If the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws DukeException {
        String keyword = getCommandDetails(command, FIND_COMMAND, FIND_COMMAND_PREFIX);
        if (keyword.isEmpty()) {
            throw new DukeException("A find command needs a keyword. For example: find book");
        }

        return keyword;
    }

    /**
     * Returns the date requested by a schedule command.
     *
     * @param command Full schedule command entered by the user.
     * @return Date whose schedule should be displayed.
     * @throws DukeException If the date is missing or invalid.
     */
    public static LocalDate parseScheduleDate(String command) throws DukeException {
        String dateText = getCommandDetails(command, SCHEDULE_COMMAND, SCHEDULE_COMMAND_PREFIX);
        if (dateText.isEmpty()) {
            throw new DukeException("A schedule command needs a date. For example: schedule 2019-10-15");
        }

        return parseDate(dateText, "schedule 2019-10-15");
    }

    /**
     * Creates a todo task from the user command.
     *
     * @param command Full todo command entered by the user.
     * @return Todo task described by the command.
     * @throws DukeException If the todo description is missing.
     */
    public static ToDo parseToDo(String command) throws DukeException {
        String description = getCommandDetails(command, TODO_COMMAND, TODO_COMMAND_PREFIX);
        if (description.isEmpty()) {
            throw new DukeException("A todo needs a description. For example: todo read book");
        }

        return new ToDo(description);
    }

    /**
     * Creates a deadline task from the user command.
     *
     * @param command Full deadline command entered by the user.
     * @return Deadline task described by the command.
     * @throws DukeException If required fields are missing or the date is invalid.
     */
    public static Deadline parseDeadline(String command) throws DukeException {
        String details = getCommandDetails(command, DEADLINE_COMMAND, DEADLINE_COMMAND_PREFIX);
        int byIndex = details.indexOf(BY_SEPARATOR);

        if (byIndex < 0) {
            throw new DukeException("A deadline needs /by. For example: deadline submit report /by 2019-10-15");
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new DukeException("A deadline needs a description before /by.");
        }
        if (by.isEmpty()) {
            throw new DukeException("A deadline needs a date after /by.");
        }

        return new Deadline(description, parseDate(by, "deadline submit report /by 2019-10-15"));
    }

    /**
     * Creates an event task from the user command.
     *
     * @param command Full event command entered by the user.
     * @return Event task described by the command.
     * @throws DukeException If required fields are missing or a date is invalid.
     */
    public static Event parseEvent(String command) throws DukeException {
        String details = getCommandDetails(command, EVENT_COMMAND, EVENT_COMMAND_PREFIX);
        int fromIndex = details.indexOf(FROM_SEPARATOR);
        int toIndex = details.indexOf(TO_SEPARATOR, fromIndex + FROM_SEPARATOR.length());

        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new DukeException("An event needs /from and /to. For example: event meeting "
                    + "/from 2019-10-15 /to 2019-10-16");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = details.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new DukeException("An event needs a description before /from.");
        }
        if (from.isEmpty()) {
            throw new DukeException("An event needs a start date after /from.");
        }
        if (to.isEmpty()) {
            throw new DukeException("An event needs an end date after /to.");
        }

        LocalDate fromDate = parseDate(from, "event meeting /from 2019-10-15 /to 2019-10-16");
        LocalDate toDate = parseDate(to, "event meeting /from 2019-10-15 /to 2019-10-16");
        if (toDate.isBefore(fromDate)) {
            throw new DukeException("An event's end date cannot be before its start date. Check /from and /to.");
        }
        return new Event(description, fromDate, toDate);
    }

    /**
     * Returns the zero-based task index from commands such as "mark 2".
     *
     * @param command Full mark, unmark, or delete command entered by the user.
     * @param commandType Type of command being parsed.
     * @param taskCount Number of tasks currently in the task list.
     * @return Zero-based task index.
     * @throws DukeException If the task number is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String command, CommandType commandType, int taskCount) throws DukeException {
        String commandWord = getCommandWord(commandType);
        String commandPrefix = commandWord + " ";
        String taskNumberText = getCommandDetails(command, commandWord, commandPrefix);
        if (taskNumberText.isEmpty()) {
            throw new DukeException("Please include a task number. For example: " + commandWord + " 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new DukeException("Task numbers must be whole numbers. For example: " + commandWord + " 2");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new DukeException("Task " + taskNumber
                    + " does not exist. Use list to see the available task numbers.");
        }

        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount : "Parsed task index should be within the task list";
        return taskIndex;
    }

    /**
     * Returns the details typed after a command word.
     */
    private static String getCommandDetails(String command, String commandWord, String commandPrefix) {
        assert command.equals(commandWord) || command.startsWith(commandPrefix)
                : "Command should match its expected word or prefix";
        if (command.equals(commandWord)) {
            return "";
        }

        return command.substring(commandPrefix.length()).trim();
    }

    /**
     * Returns the user-facing command word for commands that need a task number.
     */
    private static String getCommandWord(CommandType commandType) {
        assert commandType == CommandType.DELETE
                || commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                : "Command type should be one that requires a task number";
        if (commandType == CommandType.DELETE) {
            return DELETE_COMMAND;
        } else if (commandType == CommandType.MARK) {
            return MARK_COMMAND;
        } else {
            return UNMARK_COMMAND;
        }
    }

    /**
     * Parses a date typed by the user in yyyy-MM-dd format.
     */
    private static LocalDate parseDate(String dateText, String exampleCommand) throws DukeException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new DukeException("Dates must be in yyyy-MM-dd format. For example: " + exampleCommand);
        }
    }
}
