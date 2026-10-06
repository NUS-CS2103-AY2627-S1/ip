package ducky;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses user commands into structured commands for Ducky.
 */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String FIND_COMMAND_PREFIX = FIND_COMMAND + " ";
    private static final String MARK_COMMAND_PREFIX = "mark ";
    private static final String UNMARK_COMMAND_PREFIX = "unmark ";
    private static final String DELETE_COMMAND_PREFIX = "delete ";
    private static final String TODO_COMMAND = "todo";
    private static final String TODO_COMMAND_PREFIX = TODO_COMMAND + " ";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String DEADLINE_COMMAND_PREFIX = DEADLINE_COMMAND + " ";
    private static final String EVENT_COMMAND = "event";
    private static final String EVENT_COMMAND_PREFIX = EVENT_COMMAND + " ";
    private static final String DEADLINE_MARKER = "/by";
    private static final String EVENT_START_MARKER = "/from";
    private static final String EVENT_END_MARKER = "/to";
    private static final String MULTIPLE_WHITESPACE_REGEX = "\\s{2,}";

    private static final Pattern TAG_PATTERN =
            Pattern.compile("(?<!\\S)#([A-Za-z0-9][A-Za-z0-9_-]*)");

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a complete user command.
     *
     * @param command the user's command.
     * @return the parsed command.
     * @throws DuckyException if the command is invalid.
     */
    public ParsedCommand parse(String command) throws DuckyException {
        if (command == null || command.isBlank()) {
            throw new DuckyException("Please enter a command.");
        }
        command = command.trim();
        if (BYE_COMMAND.equals(command)) {
            return ParsedCommand.createByeCommand();
        }
        if (LIST_COMMAND.equals(command)) {
            return ParsedCommand.createListCommand();
        }
        if (FIND_COMMAND.equals(command) || command.startsWith(FIND_COMMAND_PREFIX)) {
            return ParsedCommand.createFindCommand(parseFindKeyword(command));
        }
        if (command.startsWith(MARK_COMMAND_PREFIX)) {
            return ParsedCommand.createMarkCommand(parseTaskNumber(command, MARK_COMMAND_PREFIX));
        }
        if (command.startsWith(UNMARK_COMMAND_PREFIX)) {
            return ParsedCommand.createUnmarkCommand(parseTaskNumber(command, UNMARK_COMMAND_PREFIX));
        }
        if (command.startsWith(DELETE_COMMAND_PREFIX)) {
            return ParsedCommand.createDeleteCommand(parseTaskNumber(command, DELETE_COMMAND_PREFIX));
        }
        if (TODO_COMMAND.equals(command) || command.startsWith(TODO_COMMAND_PREFIX)) {
            return ParsedCommand.createAddCommand(parseTodo(command));
        }
        if (DEADLINE_COMMAND.equals(command) || command.startsWith(DEADLINE_COMMAND_PREFIX)) {
            return ParsedCommand.createAddCommand(parseDeadline(command));
        }
        if (EVENT_COMMAND.equals(command) || command.startsWith(EVENT_COMMAND_PREFIX)) {
            return ParsedCommand.createAddCommand(parseEvent(command));
        }
        throw new DuckyException("I didn't get what you said.");
    }

    /**
     * Parses a todo command.
     *
     * @param command the complete todo command.
     * @return the parsed todo task.
     * @throws DuckyException if the description is empty.
     */
    private Task parseTodo(String command) throws DuckyException {
        String rawDescription = command.substring(TODO_COMMAND.length()).trim();
        List<String> tags = extractTags(rawDescription);
        String description = removeTags(rawDescription);
        if (description.isEmpty()) {
            throw new DuckyException("Please use: todo <name>");
        }
        return new ToDo(description, tags);
    }

    /**
     * Parses the keyword from a find command.
     *
     * @param command the complete find command
     * @return the search keyword
     * @throws DuckyException if the keyword is empty
     */
    private String parseFindKeyword(String command) throws DuckyException {
        String keyword = command.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new DuckyException("Please provide a keyword to find.");
        }
        return keyword;
    }

    /**
     * Parses a deadline command and converts its date to a LocalDate.
     *
     * @param command the complete deadline command.
     * @return the parsed deadline task.
     * @throws DuckyException if the command or date is invalid.
     */
    private Task parseDeadline(String command) throws DuckyException {
        String rawCommandWithoutPrefix = command.substring(DEADLINE_COMMAND.length()).trim();
        List<String> tags = extractTags(rawCommandWithoutPrefix);
        String commandWithoutPrefix = removeTags(rawCommandWithoutPrefix);
        int markerIndex = commandWithoutPrefix.indexOf(DEADLINE_MARKER);
        if (markerIndex == -1) {
            throw new DuckyException("Please use: deadline <name> /by <date>");
        }
        if (commandWithoutPrefix.indexOf(DEADLINE_MARKER, markerIndex + DEADLINE_MARKER.length()) != -1) {
            throw new DuckyException("A deadline can only include one '/by' marker.");
        }

        String description = commandWithoutPrefix.substring(0, markerIndex).trim();
        String dateText = commandWithoutPrefix.substring(markerIndex + DEADLINE_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new DuckyException("Please use: deadline <name> /by <date>");
        }
        if (dateText.isEmpty()) {
            throw new DuckyException("Please use: deadline <name> /by <date>");
        }

        try {
            return new Deadline(description, LocalDate.parse(dateText, DATE_FORMAT), tags);
        } catch (DateTimeParseException e) {
            throw new DuckyException("Please enter the deadline in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses an event command and converts its times to LocalDateTime values.
     *
     * @param command the complete event command.
     * @return the parsed event task.
     * @throws DuckyException if the command or times are invalid.
     */
    private Task parseEvent(String command) throws DuckyException {
        String rawCommandWithoutPrefix = command.substring(EVENT_COMMAND.length()).trim();
        List<String> tags = extractTags(rawCommandWithoutPrefix);
        String commandWithoutPrefix = removeTags(rawCommandWithoutPrefix);
        int fromIndex = commandWithoutPrefix.indexOf(EVENT_START_MARKER);
        int toIndex = commandWithoutPrefix.indexOf(EVENT_END_MARKER);
        if (fromIndex == -1 || toIndex == -1) {
            throw new DuckyException("An event must include both '/from' and '/to'.");
        }
        if (commandWithoutPrefix.indexOf(EVENT_START_MARKER, fromIndex + EVENT_START_MARKER.length()) != -1) {
            throw new DuckyException("An event can only include one '/from' marker.");
        }
        if (commandWithoutPrefix.indexOf(EVENT_END_MARKER, toIndex + EVENT_END_MARKER.length()) != -1) {
            throw new DuckyException("An event can only include one '/to' marker.");
        }
        if (fromIndex > toIndex) {
            throw new DuckyException("'/from' must appear before '/to'.");
        }

        String description = commandWithoutPrefix.substring(0, fromIndex).trim();
        String startText = commandWithoutPrefix.substring(
                fromIndex + EVENT_START_MARKER.length(), toIndex).trim();
        String endText = commandWithoutPrefix.substring(toIndex + EVENT_END_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new DuckyException("An event description cannot be empty.");
        }
        if (startText.isEmpty()) {
            throw new DuckyException("An event must include a start time after '/from'.");
        }
        if (endText.isEmpty()) {
            throw new DuckyException("An event must include an end time after '/to'.");
        }

        try {
            LocalDateTime start = LocalDateTime.parse(startText, DATE_TIME_FORMAT);
            LocalDateTime end = LocalDateTime.parse(endText, DATE_TIME_FORMAT);
            if (!end.isAfter(start)) {
                throw new DuckyException("An event must end after it starts.");
            }
            return new Event(description, start, end, tags);
        } catch (DateTimeParseException e) {
            throw new DuckyException("Please enter event times in yyyy-MM-dd HHmm format.");
        }
    }

    /**
     * Extracts unique tags from task text in their original order.
     *
     * @param text the task text containing optional hashtags.
     * @return the normalized tags without {@code #} prefixes.
     */
    private List<String> extractTags(String text) {
        return TAG_PATTERN.matcher(text)
                .results()
                .map(result -> result.group(1).toLowerCase(Locale.ENGLISH))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Removes tags from task text and normalizes leftover whitespace.
     *
     * @param text the task text containing optional hashtags.
     * @return the task text without tags.
     */
    private String removeTags(String text) {
        return TAG_PATTERN.matcher(text)
                .replaceAll("")
                .replaceAll(MULTIPLE_WHITESPACE_REGEX, " ")
                .trim();
    }

    /**
     * Parses a one-based task number and converts it to a zero-based index.
     *
     * @param command the complete task command.
     * @param commandPrefix the command prefix to remove.
     * @return the zero-based task index.
     * @throws DuckyException if the task number is invalid.
     */
    private int parseTaskNumber(String command, String commandPrefix) throws DuckyException {
        String numberText = command.substring(commandPrefix.length()).trim();
        try {
            int taskNumber = Integer.parseInt(numberText);
            if (taskNumber < 1) {
                throw new DuckyException("That task number does not exist.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new DuckyException("Please enter a valid task number.");
        }
    }

    /**
     * Represents the type of a parsed command.
     */
    public enum CommandType {
        ADD,
        MARK,
        UNMARK,
        DELETE,
        LIST,
        FIND,
        BYE
    }

    /**
     * Represents a parsed command and its optional argument.
     */
    public static class ParsedCommand {
        private static final int NO_TASK_INDEX = -1;

        private final CommandType type;
        private final Task task;
        private final String keyword;
        private final int taskIndex;

        private ParsedCommand(CommandType type, Task task, String keyword, int taskIndex) {
            assert type != null : "A parsed command must have a type";
            assert (type == CommandType.ADD) == (task != null)
                    : "Only add commands may contain a task";
            assert (type == CommandType.FIND) == (keyword != null)
                    : "Only find commands may contain a keyword";
            assert (type == CommandType.MARK
                    || type == CommandType.UNMARK
                    || type == CommandType.DELETE) == (taskIndex >= 0)
                    : "Only task-modifying commands may contain a task index";
            this.type = type;
            this.task = task;
            this.keyword = keyword;
            this.taskIndex = taskIndex;
        }

        private static ParsedCommand createByeCommand() {
            return new ParsedCommand(CommandType.BYE, null, null, NO_TASK_INDEX);
        }

        private static ParsedCommand createListCommand() {
            return new ParsedCommand(CommandType.LIST, null, null, NO_TASK_INDEX);
        }

        private static ParsedCommand createFindCommand(String keyword) {
            return new ParsedCommand(CommandType.FIND, null, keyword, NO_TASK_INDEX);
        }

        private static ParsedCommand createAddCommand(Task task) {
            return new ParsedCommand(CommandType.ADD, task, null, NO_TASK_INDEX);
        }

        private static ParsedCommand createMarkCommand(int taskIndex) {
            return new ParsedCommand(CommandType.MARK, null, null, taskIndex);
        }

        private static ParsedCommand createUnmarkCommand(int taskIndex) {
            return new ParsedCommand(CommandType.UNMARK, null, null, taskIndex);
        }

        private static ParsedCommand createDeleteCommand(int taskIndex) {
            return new ParsedCommand(CommandType.DELETE, null, null, taskIndex);
        }

        /**
         * Returns the command type.
         *
         * @return the command type.
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the task argument.
         *
         * @return the task argument.
         */
        public Task getTask() {
            return task;
        }

        /**
         * Returns the search keyword.
         *
         * @return the search keyword.
         */
        public String getKeyword() {
            return keyword;
        }

        /**
         * Returns the zero-based task index.
         *
         * @return the task index.
         */
        public int getTaskIndex() {
            return taskIndex;
        }
    }
}
