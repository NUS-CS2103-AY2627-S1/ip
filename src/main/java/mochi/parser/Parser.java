package mochi.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import mochi.DateTimeUtil;
import mochi.MochiException;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.Todo;

/**
 * Converts raw user input into commands that Mochi can execute.
 */
public final class Parser {
    /**
     * Identifies the action represented by a parsed command.
     */
    public enum CommandType {
        /** Adds a new task. */
        ADD,
        /** Deletes an existing task. */
        DELETE,
        /** Marks an existing task as complete. */
        MARK,
        /** Marks an existing task as incomplete. */
        UNMARK,
        /** Displays all tasks. */
        LIST,
        /** Finds tasks by a keyword in their descriptions. */
        FIND,
        /** Exits Mochi. */
        BYE
    }

    /**
     * Contains the action and argument produced by parsing one user command.
     */
    public static final class Command {
        private final CommandType type;
        private final Task task;
        private final int taskNumber;
        private final String keyword;

        private Command(CommandType type, Task task, int taskNumber, String keyword) {
            this.type = type;
            this.task = task;
            this.taskNumber = taskNumber;
            this.keyword = keyword;
        }

        /**
         * Returns the action represented by this command.
         *
         * @return The command type.
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the task created by an add command.
         *
         * @return The task to add, or {@code null} when the command does not add a task.
         */
        public Task getTask() {
            return task;
        }

        /**
         * Returns the one-based task number used by a task operation.
         *
         * @return The task number, or {@code 0} when the command does not target a task.
         */
        public int getTaskNumber() {
            return taskNumber;
        }

        /**
         * Returns the keyword supplied to a find command.
         *
         * @return Keyword to search for, or {@code null} for other commands.
         */
        public String getKeyword() {
            return keyword;
        }
    }

    private Parser() {
    }

    /**
     * Parses one line of user input.
     *
     * @param input Raw input from the user.
     * @return A command containing the requested action and argument.
     * @throws MochiException If the command or its arguments are invalid.
     */
    public static Command parse(String input) throws MochiException {
        String command = input.trim();
        String[] words = command.split("\\s+", 2);
        String commandWord = expandAlias(words[0]);

        if (commandWord.equals("bye") && words.length == 1) {
            return new Command(CommandType.BYE, null, 0, null);
        }
        if (commandWord.equals("list") && words.length == 1) {
            return new Command(CommandType.LIST, null, 0, null);
        }

        if (words.length < 2) {
            throw missingArgumentException(commandWord);
        }

        String arguments = words[1].trim();
        return parseCommandWithArguments(commandWord, arguments);
    }

    private static Command parseCommandWithArguments(String commandWord, String arguments)
            throws MochiException {
        switch (commandWord) {
            case "mark":
                return new Command(CommandType.MARK, null, parseTaskNumber(arguments), null);
            case "unmark":
                return new Command(CommandType.UNMARK, null, parseTaskNumber(arguments), null);
            case "delete":
                return new Command(CommandType.DELETE, null, parseTaskNumber(arguments), null);
            case "todo":
                return new Command(CommandType.ADD, new Todo(arguments), 0, null);
            case "deadline":
                return new Command(CommandType.ADD, parseDeadline(arguments), 0, null);
            case "event":
                return new Command(CommandType.ADD, parseEvent(arguments), 0, null);
            case "find":
                return new Command(CommandType.FIND, null, 0, arguments);
            default:
                throw unknownCommandException();
        }
    }

    private static String expandAlias(String commandWord) {
        switch (commandWord) {
            case "t":
                return "todo";
            case "d":
                return "deadline";
            case "e":
                return "event";
            case "l":
                return "list";
            case "f":
                return "find";
            default:
                return commandWord;
        }
    }

    private static int parseTaskNumber(String argument) throws MochiException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new MochiException("The task number must be a whole number.");
        }
    }

    private static Deadline parseDeadline(String details) throws MochiException {
        if (!details.contains(" /by ")) {
            throw new MochiException("Use this format: deadline DESCRIPTION /by DATE.");
        }
        String[] parts = details.split(" /by ", 2);
        assert parts.length == 2 : "Deadline delimiter check must produce two parts";

        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new MochiException("A deadline needs both a description and a date.");
        }

        try {
            return new Deadline(description, DateTimeUtil.parse(by));
        } catch (DateTimeParseException e) {
            throw invalidDateTimeException();
        }
    }

    private static Event parseEvent(String details) throws MochiException {
        if (!details.contains(" /from ")) {
            throw new MochiException("Use this format: event DESCRIPTION /from START /to END.");
        }
        String[] fromParts = details.split(" /from ", 2);
        assert fromParts.length == 2 : "Event start delimiter check must produce two parts";

        if (!fromParts[1].contains(" /to ")) {
            throw new MochiException("Use this format: event DESCRIPTION /from START /to END.");
        }

        String[] toParts = fromParts[1].split(" /to ", 2);
        assert toParts.length == 2 : "Event end delimiter check must produce two parts";

        String description = fromParts[0].trim();
        String from = toParts[0].trim();
        String to = toParts[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new MochiException("An event needs a description, start, and end.");
        }

        try {
            LocalDateTime start = DateTimeUtil.parse(from);
            LocalDateTime end = DateTimeUtil.parse(to);
            return new Event(description, start, end);
        } catch (DateTimeParseException e) {
            throw invalidDateTimeException();
        } catch (IllegalArgumentException e) {
            throw new MochiException("An event must end after it starts.");
        }
    }

    private static MochiException missingArgumentException(String commandWord) {
        switch (commandWord) {
            case "todo":
            case "deadline":
            case "event":
                return new MochiException(
                        "The description of a " + commandWord + " cannot be empty.");
            case "mark":
            case "unmark":
            case "delete":
                return new MochiException(
                        "Please specify a task number to " + commandWord + ".");
            case "find":
                return new MochiException("Please specify a keyword to find.");
            default:
                return unknownCommandException();
        }
    }

    private static MochiException invalidDateTimeException() {
        return new MochiException(
                "Use a valid date and time in the format "
                        + DateTimeUtil.INPUT_FORMAT_DESCRIPTION + ".");
    }

    private static MochiException unknownCommandException() {
        return new MochiException("I'm sorry, but I don't know what that means :-(");
    }
}
