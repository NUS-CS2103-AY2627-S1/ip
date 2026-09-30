package marcus.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import marcus.task.Deadline;
import marcus.task.Event;
import marcus.task.Task;
import marcus.task.Todo;

/**
 * Interprets user commands and creates tasks from valid task-creation commands.
 */
public class Parser {
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");
    /** Identifies the commands understood by Marcus. */
    public enum CommandType {
        /** Exits Marcus. */
        BYE("bye"),
        /** Lists all tasks. */
        LIST("list"),
        /** Finds dated tasks. */
        FIND("find"),
        /** Marks a task as completed. */
        MARK("mark"),
        /** Marks a task as incomplete. */
        UNMARK("unmark"),
        /** Deletes a task. */
        DELETE("delete"),
        /** Adds tags to a task. */
        TAG("tag"),
        /** Removes tags from a task. */
        UNTAG("untag"),
        /** Creates a to-do. */
        TODO("todo"),
        /** Creates a deadline. */
        DEADLINE("deadline"),
        /** Creates an event. */
        EVENT("event"),
        /** Represents unrecognized input. */
        UNKNOWN("");

        private final String keyword;

        /**
         * Creates a command type associated with its command-line keyword.
         *
         * @param keyword text that identifies the command.
         */
        CommandType(String keyword) {
            this.keyword = keyword;
        }
    }

    /** Creates a parser for interpreting Marcus commands. */
    public Parser() {
    }

    /**
     * Classifies a user command while preserving commands without required arguments as invalid.
     *
     * @param command user input.
     * @return the corresponding command type, or {@code UNKNOWN}.
     */
    public CommandType getCommandType(String command) {
        if (command == null) {
            return CommandType.UNKNOWN;
        }
        String normalizedCommand = command.trim();
        for (CommandType commandType : CommandType.values()) {
            if (commandType == CommandType.UNKNOWN) {
                continue;
            }
            if (normalizedCommand.equals(commandType.keyword)
                    || normalizedCommand.matches(commandType.keyword + "\\s+.*")) {
                return commandType;
            }
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Extracts the text after a command's keyword.
     *
     * @param command user input beginning with the command keyword.
     * @param commandType type of the command.
     * @return command arguments, or an empty string when none are present.
     */
    public String getArguments(String command, CommandType commandType) {
        assert commandType != null && commandType != CommandType.UNKNOWN
                : "Arguments require a recognized command type";
        if (command == null) {
            return "";
        }
        String normalizedCommand = command.trim();
        return normalizedCommand.length() == commandType.keyword.length()
                ? "" : normalizedCommand.substring(commandType.keyword.length()).trim();
    }

    /**
     * Creates a task from a supported task-creation command.
     *
     * @param command user command to interpret.
     * @param commandType parsed type of the command.
     * @return the new task, or {@code null} when the command is invalid.
     */
    public Task createTask(String command, CommandType commandType) {
        assert commandType != null : "A command must have a command type";
        if (command == null || commandType == CommandType.UNKNOWN) {
            return null;
        }
        String arguments = getArguments(command, commandType);
        return switch (commandType) {
            case TODO -> createTodo(arguments);
            case DEADLINE -> createDeadline(arguments);
            case EVENT -> createEvent(arguments);
            default -> null;
        };
    }

    /**
     * Creates a to-do from its command arguments.
     *
     * @param arguments task description.
     * @return the new to-do, or {@code null} when the description is invalid.
     */
    private Task createTodo(String arguments) {
        return isValidTaskPart(arguments) ? new Todo(arguments) : null;
    }

    /**
     * Creates a deadline from its command arguments.
     *
     * @param arguments deadline description and date.
     * @return the new deadline, or {@code null} when the arguments are invalid.
     */
    private Task createDeadline(String arguments) {
        if (countOccurrences(arguments, "/by") != 1) {
            return null;
        }
        String[] parts = arguments.split("\\s+/by\\s+", 2);
        if (parts.length != 2 || !isValidTaskPart(parts[0]) || !isValidTaskPart(parts[1])) {
            return null;
        }
        try {
            return new Deadline(parts[0], LocalDate.parse(parts[1]));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Creates an event from its command arguments.
     *
     * @param arguments event description, start time, and end time.
     * @return the new event, or {@code null} when the arguments are invalid.
     */
    private Task createEvent(String arguments) {
        if (countOccurrences(arguments, "/from") != 1 || countOccurrences(arguments, "/to") != 1) {
            return null;
        }
        String[] descriptionAndFrom = arguments.split("\\s+/from\\s+", 2);
        if (descriptionAndFrom.length != 2) {
            return null;
        }
        String[] fromAndTo = descriptionAndFrom[1].split("\\s+/to\\s+", 2);
        if (fromAndTo.length != 2 || !isValidTaskPart(descriptionAndFrom[0])
                || !isValidTaskPart(fromAndTo[0]) || !isValidTaskPart(fromAndTo[1])) {
            return null;
        }
        if (isInvalidEventOrder(fromAndTo[0], fromAndTo[1])) {
            return null;
        }
        return new Event(descriptionAndFrom[0], fromAndTo[0], fromAndTo[1]);
    }

    private static boolean isInvalidEventOrder(String from, String to) {
        try {
            LocalDateTime start = LocalDateTime.parse(from, EVENT_DATE_TIME_FORMAT);
            LocalDateTime end = LocalDateTime.parse(to, EVENT_DATE_TIME_FORMAT);
            return !start.isBefore(end);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static int countOccurrences(String text, String target) {
        int count = 0;
        int position = 0;
        while ((position = text.indexOf(target, position)) >= 0) {
            count++;
            position += target.length();
        }
        return count;
    }

    /**
     * Returns a helpful message for an invalid command.
     *
     * @param command invalid user command.
     * @param commandType parsed type of the command.
     * @return a command-specific error message.
     */
    public String getErrorMessage(String command, CommandType commandType) {
        if (command == null || command.isBlank()) {
            return "Please enter a command.";
        }
        if ((commandType == CommandType.TODO || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT) && command.contains("|")) {
            return "Task details cannot contain the | character.";
        }
        if (commandType == CommandType.TODO) {
            return "Please enter task with todo, eg. todo go for a run";
        }
        if (commandType == CommandType.DEADLINE) {
            return "Please enter a deadline date in yyyy-mm-dd format, eg. deadline return book /by 2019-10-15";
        }
        if (commandType == CommandType.EVENT) {
            return "Please enter task with event, eg. event project meeting /from Mon 2pm /to 4pm";
        }
        return "What do you mean by \"" + command + "\", please enter a valid command";
    }

    /**
     * Checks whether a task field can be displayed and saved safely.
     *
     * @param value task field to validate.
     * @return whether the field is non-blank and does not contain the file delimiter.
     */
    public static boolean isValidTaskPart(String value) {
        return value != null && !value.isBlank() && !value.contains("|");
    }

    /**
     * Checks whether a tag has the required format.
     *
     * @param tag tag to validate.
     * @return whether the tag starts with {@code #} and contains only permitted characters.
     */
    public static boolean isValidTag(String tag) {
        return tag != null && tag.matches("#[A-Za-z0-9_-]+");
    }
}
