package rocky.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import rocky.task.Deadline;
import rocky.task.Event;
import rocky.task.Task;
import rocky.task.Todo;

/**
 * Converts user input into commands that Rocky can execute.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Creates a parser for Rocky commands.
     */
    public Parser() {
    }

    /**
     * The command categories understood by Rocky.
     */
    public enum CommandType {
        /**
         * Exits the application.
         */
        BYE,
        /**
         * Displays all tasks.
         */
        LIST,
        /**
         * Sorts tasks by their dates.
         */
        SORT,
        /**
         * Marks a task as complete.
         */
        MARK,
        /**
         * Marks a task as incomplete.
         */
        UNMARK,
        /**
         * Deletes a task.
         */
        DELETE,
        /**
         * Adds a task.
         */
        ADD,
        /**
         * Reports an invalid command.
         */
        ERROR,
        /**
         * Reports a command with no task description.
         */
        MISSING_DESCRIPTION,

        /**
         * Finds tasks based on their descriptions.
         */
        FIND
    }

    /**
     * A parsed command and the data needed to execute it.
     */
    public static class Command {
        private final CommandType type;
        private final String argument;
        private final Task task;
        private final String message;
        private final boolean showWithDivider;

        /** Creates a parsed command with all of its optional data.
         *
         * @param type the command category
         * @param argument the command argument, if present
         * @param task the task to add, if present
         * @param message the user-facing message, if present
         * @param showWithDivider whether the message should be surrounded by dividers
         */
        private Command(CommandType type, String argument, Task task,
                        String message, boolean showWithDivider) {
            assert type != null : "Parsed commands must have a command type";
            assert type != CommandType.ADD || task != null
                    : "ADD commands must contain a task";
            assert (type != CommandType.ERROR && type != CommandType.MISSING_DESCRIPTION)
                    || message != null : "Error commands must contain a message";
            this.type = type;
            this.argument = argument;
            this.task = task;
            this.message = message;
            this.showWithDivider = showWithDivider;
        }

        /** Returns the category of this parsed command.
         *
         * @return the command category.
         */
        public CommandType getType() {
            return type;
        }

        /** Returns the argument supplied with this command.
         *
         * @return the command argument, or {@code null} when there is none.
         */
        public String getArgument() {
            return argument;
        }

        /** Returns the task created by this command.
         *
         * @return the created task, or {@code null} when this command does not add a task.
         */
        public Task getTask() {
            return task;
        }

        /** Returns the user-facing message associated with this command.
         *
         * @return the command message, or {@code null} when there is none.
         */
        public String getMessage() {
            return message;
        }

        /** Returns whether the message should be printed between UI dividers.
         *
         * @return {@code true} when dividers should surround the message.
         */
        public boolean showWithDivider() {
            return showWithDivider;
        }
    }

    /** Parses one line of user input.
     *
     * @param userInput the command entered by the user.
     * @return the parsed command and any associated data.
     */
    public Command parse(String userInput) {
        String input = userInput.trim();
        if (input.equals("bye")) {
            return command(CommandType.BYE);
        }
        if (input.equals("list")) {
            return command(CommandType.LIST);
        }
        if (input.equals("sort")) {
            return command(CommandType.SORT);
        }
        if (input.equals("find")) {
            return error("Use: find KEYWORD", false);
        }
        if (input.startsWith("find ")) {
            String keyword = input.substring("find".length()).trim();
            return keyword.isEmpty() ? error("Use: find KEYWORD", false)
                    : command(CommandType.FIND, keyword);
        }
        if (input.equals("mark") || input.startsWith("mark ")) {
            return command(CommandType.MARK, input.substring("mark".length()).trim());
        }
        if (input.equals("unmark") || input.startsWith("unmark ")) {
            return command(CommandType.UNMARK, input.substring("unmark".length()).trim());
        }
        if (input.equals("delete") || input.startsWith("delete ")) {
            return command(CommandType.DELETE, input.substring("delete".length()).trim());
        }
        if (input.equals("todo") || input.startsWith("todo ")) {
            String description = input.substring("todo".length()).trim();
            return description.isEmpty() ? missingDescription() : add(new Todo(description));
        }
        if (input.equals("deadline") || input.startsWith("deadline ")) {
            return parseDeadline(input);
        }
        if (input.equals("event") || input.startsWith("event ")) {
            return parseEvent(input);
        }
        return error("Rocky don't understand what this is... Try something else", true);
    }

    /** Parses a deadline command containing a due date or date-time.
     *
     * @param input the complete deadline command.
     * @return the parsed command.
     */
    private Command parseDeadline(String input) {
        int byMarkerIndex = input.indexOf(" /by ");
        if (byMarkerIndex < 0) {
            return error("Use: deadline DESCRIPTION /by DATE_OR_TIME", false);
        }
        String description = input.substring("deadline".length(), byMarkerIndex).trim();
        if (description.isEmpty()) {
            return missingDescription();
        }
        ParsedDateTime dueDateTime = parseDateTime(
                input.substring(byMarkerIndex + " /by ".length()).trim());
        if (dueDateTime == null) {
            return dateError();
        }
        Task task = dueDateTime.hasTime ? new Deadline(description, dueDateTime.value)
                : new Deadline(description, dueDateTime.value.toLocalDate());
        return add(task);
    }

    /** Parses an event command containing start and end dates or date-times.
     *
     * @param input the complete event command.
     * @return the parsed command.
     */
    private Command parseEvent(String input) {
        int fromMarkerIndex = input.indexOf(" /from ");
        int toMarkerIndex = input.indexOf(" /to ");
        if (fromMarkerIndex < 0 || toMarkerIndex < 0 || toMarkerIndex < fromMarkerIndex) {
            return error("Use: event DESCRIPTION /from START /to END", false);
        }
        String description = input.substring("event".length(), fromMarkerIndex).trim();
        if (description.isEmpty()) {
            return missingDescription();
        }
        ParsedDateTime startDateTime = parseDateTime(
                input.substring(fromMarkerIndex + " /from ".length(), toMarkerIndex).trim());
        ParsedDateTime endDateTime = parseDateTime(
                input.substring(toMarkerIndex + " /to ".length()).trim());
        if (startDateTime == null || endDateTime == null) {
            return dateError();
        }
        if (startDateTime.hasTime != endDateTime.hasTime) {
            return error("Use the same date or date-time format for both event dates.", false);
        }
        if (endDateTime.value.isBefore(startDateTime.value)) {
            return error("The event end date/time cannot be earlier than its start date/time.", false);
        }
        Task task = startDateTime.hasTime ? new Event(description, startDateTime.value, endDateTime.value)
                : new Event(description, startDateTime.value.toLocalDate(), endDateTime.value.toLocalDate());
        return add(task);
    }

    /** Parses a supported date or date-time string.
     *
     * @param text the date or date-time text.
     * @return the parsed value, or {@code null} when the input is invalid.
     */
    private ParsedDateTime parseDateTime(String text) {
        try {
            if (text.contains("T")) {
                return new ParsedDateTime(LocalDateTime.parse(text), true);
            }
            if (text.contains(" ")) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm");
                return new ParsedDateTime(LocalDateTime.parse(text, format), true);
            }
            return new ParsedDateTime(LocalDate.parse(text, INPUT_DATE_FORMAT).atStartOfDay(), false);
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /** Creates a command without an argument, task, or message.
     *
     * @param type the command category
     * @return the created command
     */
    private Command command(CommandType type) {
        return new Command(type, null, null, null, false);
    }

    /** Creates a command with a textual argument.
     *
     * @param type the command category
     * @param argument the command argument
     * @return the created command
     */
    private Command command(CommandType type, String argument) {
        return new Command(type, argument, null, null, false);
    }

    /** Creates a command that adds the supplied task.
     *
     * @param task the task to add
     * @return the created command
     */
    private Command add(Task task) {
        return new Command(CommandType.ADD, null, task, null, false);
    }

    /**
     * Creates the standard missing-description response.
     */
    private Command missingDescription() {
        return new Command(CommandType.MISSING_DESCRIPTION, null, null,
                "Curious? Rocky don't see description for the task...", true);
    }

    /**
     * Creates the standard invalid-date response.
     */
    private Command dateError() {
        return error("Use yyyy-MM-dd or yyyy-MM-dd HHmm, for example: 2019-10-15 or 2019-12-02 1800", false);
    }

    /** Creates an error command with the requested display behavior.
     *
     * @param message the user-facing error message
     * @param showWithDivider whether the UI should surround the message with dividers
     * @return the created error command
     */
    private Command error(String message, boolean showWithDivider) {
        return new Command(CommandType.ERROR, null, null, message, showWithDivider);
    }

    /** Holds a parsed date-time and whether its input included a time. */
    private static class ParsedDateTime {
        private final LocalDateTime value;
        private final boolean hasTime;

        /** Creates a parsed date-time and records whether the input included a time.
         *
         * @param value the parsed date-time value
         * @param hasTime whether the original input included a time
         */
        ParsedDateTime(LocalDateTime value, boolean hasTime) {
            assert value != null : "Parsed date-time values must not be null";
            this.value = value;
            this.hasTime = hasTime;
        }
    }
}
