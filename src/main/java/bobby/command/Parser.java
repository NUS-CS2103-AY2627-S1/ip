package bobby.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Pattern;

import bobby.exception.BobbyException;
import bobby.task.Deadline;
import bobby.task.Event;
import bobby.task.Task;
import bobby.task.Todo;

/**
 * Converts user input into commands that Bobby can execute.
 */
public final class Parser {
    private static final String ADD_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String DELETE_COMMAND = "delete";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";
    private static final String LIST_COMMAND = "list";
    private static final String MARK_COMMAND = "mark";
    private static final String STATS_COMMAND = "stats";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Prithee, forgive this humble steward, for thy decree exceedeth my understanding. "
                    + "I beseech thee, employ one of the appointed commands.";
    private static final String EVENT_USAGE_MESSAGE =
            "Thy decree must take precisely this form: "
                    + "event DESCRIPTION /from YYYY-MM-DD HHMM /to YYYY-MM-DD HHMM.";
    private static final String INVALID_DESCRIPTION_MESSAGE =
            "A duty's description must contain readable text and may not contain the character '|'.";
    private static final String INVALID_EVENT_RANGE_MESSAGE =
            "An event must commence before it concludeth.";
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern BY_SEPARATOR_PATTERN =
            Pattern.compile("\\s+/by\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern FROM_SEPARATOR_PATTERN =
            Pattern.compile("\\s+/from\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern TO_SEPARATOR_PATTERN =
            Pattern.compile("\\s+/to\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private Parser() {
    }

    /** Identifies the operation represented by a parsed command. */
    public enum CommandType {
        LIST,
        STATS,
        FIND,
        ADD,
        MARK,
        UNMARK,
        DELETE
    }

    /**
     * Stores the data needed to execute one parsed command.
     */
    public static class Command {
        private final CommandType type;
        private final Task task;
        private final int taskIndex;
        private final String keyword;

        /**
         * Creates a command with its optional task or zero-based task index.
         *
         * @param type the operation to perform
         * @param task the task to add, or {@code null} for other operations
         * @param taskIndex the task index, or {@code -1} when not applicable
         * @param keyword the search keyword, or {@code null} when not applicable
         */
        private Command(CommandType type, Task task, int taskIndex, String keyword) {
            assert type != null : "Command type must not be null";
            boolean hasExpectedTask = (type == CommandType.ADD) == (task != null);
            boolean hasExpectedTaskIndex = isTaskIndexCommand(type) ? taskIndex >= 0 : taskIndex == -1;
            boolean hasKeyword = keyword != null && !keyword.isBlank();
            boolean hasExpectedKeyword = (type == CommandType.FIND) == hasKeyword;
            assert hasExpectedTask : "Only an add command must carry a task";
            assert hasExpectedTaskIndex : "Only a task-index command must carry a valid task index";
            assert hasExpectedKeyword : "Only a find command must carry a non-blank keyword";
            this.type = type;
            this.task = task;
            this.taskIndex = taskIndex;
            this.keyword = keyword;
        }

        /**
         * Returns whether a command type operates on an existing task by index.
         *
         * @param type the command type to inspect
         * @return {@code true} for mark, unmark, and delete commands
         */
        private static boolean isTaskIndexCommand(CommandType type) {
            return type == CommandType.MARK
                    || type == CommandType.UNMARK
                    || type == CommandType.DELETE;
        }

        /**
         * Returns the operation to perform.
         *
         * @return the command type
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the task to add.
         *
         * @return the task for an {@link CommandType#ADD} command, otherwise {@code null}
         */
        public Task getTask() {
            return task;
        }

        /**
         * Returns the zero-based task index to operate on.
         *
         * @return the task index for mark, unmark, and delete commands, otherwise {@code -1}
         */
        public int getTaskIndex() {
            return taskIndex;
        }

        /**
         * Returns the keyword used to find tasks.
         *
         * @return the search keyword for a {@link CommandType#FIND} command, otherwise {@code null}
         */
        public String getKeyword() {
            return keyword;
        }
    }

    /**
     * Returns whether the input is Bobby's exit command.
     *
     * @param input the user's raw input
     * @return {@code true} when the input requests exit
     */
    public static boolean isByeCommand(String input) {
        return input != null && input.strip().equalsIgnoreCase("bye");
    }

    /**
     * Parses a user command and validates any referenced task number.
     *
     * @param input the user's raw input
     * @param taskCount the number of currently stored tasks
     * @return the parsed command
     * @throws BobbyException if the command is malformed or has an invalid task number
     */
    public static Command parse(String input, int taskCount) throws BobbyException {
        assert taskCount >= 0 : "Task count must not be negative";
        if (input == null) {
            throw new BobbyException(UNKNOWN_COMMAND_MESSAGE);
        }
        String command = input.strip();
        String commandWord = getCommandWord(command).toLowerCase(Locale.ROOT);

        switch (commandWord) {
            case LIST_COMMAND:
                return parseListCommand(command);
            case STATS_COMMAND:
                return parseStatsCommand(command);
            case FIND_COMMAND:
                return parseFindCommand(command);
            case MARK_COMMAND:
                return parseTaskIndexCommand(CommandType.MARK, command, MARK_COMMAND, taskCount);
            case UNMARK_COMMAND:
                return parseTaskIndexCommand(CommandType.UNMARK, command, UNMARK_COMMAND, taskCount);
            case DELETE_COMMAND:
                return parseTaskIndexCommand(CommandType.DELETE, command, DELETE_COMMAND, taskCount);
            case ADD_COMMAND:
                return parseTodoCommand(command);
            case DEADLINE_COMMAND:
                return parseDeadlineCommand(command);
            case EVENT_COMMAND:
                return parseEventCommand(command);
            default:
                throw new BobbyException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Extracts the first space-delimited word from a command.
     */
    private static String getCommandWord(String command) {
        for (int i = 0; i < command.length(); i++) {
            if (Character.isWhitespace(command.charAt(i))) {
                return command.substring(0, i);
            }
        }
        return command;
    }

    /**
     * Parses a list command that must not contain arguments.
     */
    private static Command parseListCommand(String command) throws BobbyException {
        if (!command.equalsIgnoreCase(LIST_COMMAND)) {
            throw new BobbyException(UNKNOWN_COMMAND_MESSAGE);
        }
        return new Command(CommandType.LIST, null, -1, null);
    }

    /**
     * Parses a statistics command that must not contain arguments.
     */
    private static Command parseStatsCommand(String command) throws BobbyException {
        if (!command.equalsIgnoreCase(STATS_COMMAND)) {
            throw new BobbyException("Thy decree must take precisely this form: stats.");
        }
        return new Command(CommandType.STATS, null, -1, null);
    }

    /**
     * Parses a find command and its required keyword.
     */
    private static Command parseFindCommand(String command) throws BobbyException {
        String keyword = normalizeWhitespace(getCommandArgument(command, FIND_COMMAND));
        if (keyword.isEmpty()) {
            throw new BobbyException(
                    "Pray furnish a word or phrase for which the register may be searched.");
        }
        return new Command(CommandType.FIND, null, -1, keyword);
    }

    /**
     * Parses a mark, unmark, or delete command and its task number.
     */
    private static Command parseTaskIndexCommand(CommandType type, String command, String commandWord,
            int taskCount) throws BobbyException {
        String taskNumber = getCommandArgument(command, commandWord);
        return new Command(type, null, getTaskIndex(taskNumber, taskCount), null);
    }

    /**
     * Parses a to-do command and its required description.
     */
    private static Command parseTodoCommand(String command) throws BobbyException {
        String description = normalizeWhitespace(getCommandArgument(command, ADD_COMMAND));
        if (description.isEmpty()) {
            throw new BobbyException(
                    "Thy decree containeth no duty to inscribe. Pray use: todo DESCRIPTION.");
        }
        validateDescription(description);
        return new Command(CommandType.ADD, new Todo(description), -1, null);
    }

    /**
     * Parses a deadline command and its required description and date-time.
     */
    private static Command parseDeadlineCommand(String command) throws BobbyException {
        String deadlineDetails = getCommandArgument(command, DEADLINE_COMMAND);
        String[] parts = BY_SEPARATOR_PATTERN.split(deadlineDetails, -1);
        boolean hasDescription = parts.length == 2 && !parts[0].isBlank();
        boolean hasDateTime = parts.length == 2 && !parts[1].isBlank();
        if (!hasDescription || !hasDateTime) {
            throw new BobbyException(
                    "Thy decree must take precisely this form: "
                            + "deadline DESCRIPTION /by YYYY-MM-DD HHMM.");
        }
        String description = normalizeWhitespace(parts[0]);
        validateDescription(description);
        return new Command(CommandType.ADD,
                new Deadline(description, parseDateTime(parts[1])), -1, null);
    }

    /**
     * Parses an event command and its required description, start, and end date-times.
     */
    private static Command parseEventCommand(String command) throws BobbyException {
        String eventDetails = getCommandArgument(command, EVENT_COMMAND);
        String[] fromParts = FROM_SEPARATOR_PATTERN.split(eventDetails, -1);
        if (fromParts.length != 2 || TO_SEPARATOR_PATTERN.matcher(fromParts[0]).find()) {
            throw new BobbyException(EVENT_USAGE_MESSAGE);
        }

        String description = normalizeWhitespace(fromParts[0]);
        String[] toParts = TO_SEPARATOR_PATTERN.split(fromParts[1], -1);
        boolean hasDescription = !description.isEmpty();
        boolean hasStartAndEnd = toParts.length == 2
                && !toParts[0].isBlank()
                && !toParts[1].isBlank();
        if (!hasDescription || !hasStartAndEnd) {
            throw new BobbyException(EVENT_USAGE_MESSAGE);
        }
        validateDescription(description);
        LocalDateTime startDateTime = parseDateTime(toParts[0]);
        LocalDateTime endDateTime = parseDateTime(toParts[1]);
        if (!startDateTime.isBefore(endDateTime)) {
            throw new BobbyException(INVALID_EVENT_RANGE_MESSAGE);
        }
        return new Command(CommandType.ADD,
                new Event(description, startDateTime, endDateTime), -1, null);
    }

    /**
     * Returns the trimmed text following a command word.
     */
    private static String getCommandArgument(String command, String commandWord) {
        return command.substring(commandWord.length()).strip();
    }

    /**
     * Collapses user-entered whitespace so equivalent text is stored consistently.
     */
    private static String normalizeWhitespace(String text) {
        return WHITESPACE_PATTERN.matcher(text.strip()).replaceAll(" ");
    }

    /**
     * Rejects descriptions that cannot be represented safely in the save-file format.
     */
    private static void validateDescription(String description) throws BobbyException {
        boolean hasInvalidCharacter = description.indexOf('|') >= 0
                || description.chars().anyMatch(character -> Character.isISOControl(character));
        if (hasInvalidCharacter) {
            throw new BobbyException(INVALID_DESCRIPTION_MESSAGE);
        }
    }

    /**
     * Parses a date and time entered in {@code yyyy-MM-dd HHmm} format.
     */
    private static LocalDateTime parseDateTime(String dateTime) throws BobbyException {
        try {
            return LocalDateTime.parse(normalizeWhitespace(dateTime), INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BobbyException(
                    "The appointed date and hour are not in an acceptable form. "
                            + "Pray employ YYYY-MM-DD HHMM.");
        }
    }

    /**
     * Converts a one-based task number to a valid zero-based task-list index.
     */
    private static int getTaskIndex(String taskNumber, int taskCount) throws BobbyException {
        if (!taskNumber.matches("[1-9]\\d*")) {
            throw new BobbyException(
                    "The number thou hast named correspondeth to no duty presently held within the register.");
        }
        try {
            int index = Integer.parseInt(taskNumber) - 1;
            if (index < 0 || index >= taskCount) {
                throw new BobbyException(
                        "The number thou hast named correspondeth to no duty presently held within the register.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new BobbyException(
                    "The number thou hast named correspondeth to no duty presently held within the register.");
        }
    }
}
