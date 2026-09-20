package goop.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import goop.command.AddCommand;
import goop.command.Command;
import goop.command.DeleteCommand;
import goop.command.ExitCommand;
import goop.command.FindCommand;
import goop.command.ListCommand;
import goop.command.MarkCommand;
import goop.command.PriorityCommand;
import goop.command.UnmarkCommand;
import goop.exception.GoopException;
import goop.task.Deadline;
import goop.task.Event;
import goop.task.Priority;
import goop.task.Task;
import goop.task.Todo;

/**
 * Interprets and validates commands entered by the user.
 */
public class Parser {
    /** Date-time formats accepted after the {@code /by} delimiter. */
    private static final List<DateTimeFormatter> DEADLINE_INPUT_FORMATS = List.of(
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT));

    /**
     * Creates a parser for Goop commands.
     */
    public Parser() {
    }

    /**
     * Converts complete user input into a typed, validated command.
     *
     * @param input Complete user input.
     * @return Command represented by the input.
     * @throws GoopException If the command or its arguments are invalid.
     */
    public Command parse(String input) throws GoopException {
        validateCommand(input);
        input = input.replaceAll("(?U)\\s+", " ").strip();

        if (isCommand(input, "bye")) {
            requireNoArguments(input, "bye");
            return new ExitCommand();
        }
        if (isCommand(input, "list")) {
            requireNoArguments(input, "list");
            return new ListCommand();
        }
        if (isCommand(input, "find")) {
            return new FindCommand(parseKeyword(input));
        }
        if (isCommand(input, "delete")) {
            return new DeleteCommand(parseTaskNumber(input, "delete"));
        }
        if (isCommand(input, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(input, "unmark"));
        }
        if (isCommand(input, "mark")) {
            return new MarkCommand(parseTaskNumber(input, "mark"));
        }
        if (isCommand(input, "priority")) {
            return parsePriorityCommand(input);
        }
        return new AddCommand(parseTask(input));
    }

    /**
     * Ensures that the user entered a command rather than a blank line.
     */
    private void validateCommand(String input) throws GoopException {
        if (input == null || input.replaceAll("(?U)\\s+", "").isEmpty()) {
            throw new GoopException(
                    "Please enter a command. For example: todo read book.");
        }
        if (input.codePoints().anyMatch(character ->
                Character.isISOControl(character) && character != '\t'
                        || character == 0x2028 || character == 0x2029)) {
            throw new GoopException("Commands must be on one line without control characters.");
        }
    }

    /**
     * Rejects extra arguments on commands that do not take parameters.
     */
    private void requireNoArguments(String input, String commandWord) throws GoopException {
        if (!input.equals(commandWord)) {
            throw new GoopException("The " + commandWord + " command takes no arguments. Use: "
                    + commandWord + ".");
        }
    }

    /**
     * Checks whether an input contains the given command word, optionally followed
     * by arguments.
     *
     * @param input Complete user input.
     * @param commandWord Command word to match.
     * @return True when the input contains the command.
     */
    private boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Parses the positive task number supplied to {@code mark}, {@code unmark}, or
     * {@code delete}. Validation against the live task list belongs to the command.
     *
     * @param input Complete user input.
     * @param commandWord {@code mark}, {@code unmark}, or {@code delete}.
     * @return One-based task number.
     * @throws GoopException If the task number is missing, malformed, or too large.
     */
    private int parseTaskNumber(String input, String commandWord)
            throws GoopException {
        assert isCommand(input, commandWord) : "Task-number parsing requires the matching command";
        String argument = input.substring(commandWord.length()).trim();
        return parsePositiveTaskNumber(argument, commandWord);
    }

    /**
     * Validates a task-number argument independently of the surrounding command.
     */
    private int parsePositiveTaskNumber(String argument, String commandWord) throws GoopException {
        String example = commandWord.equals("priority") ? "priority 1 high" : commandWord + " 1";
        if (argument.isEmpty()) {
            throw new GoopException("The " + commandWord
                    + " command needs one task number. Use: " + commandWord + " <number>.");
        }
        if (!argument.matches("[1-9][0-9]*")) {
            throw new GoopException("The " + commandWord
                    + " command accepts one positive whole number. Use: "
                    + example + ".");
        }

        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException error) {
            throw new GoopException(
                    "That task number is too large. Run list and choose a displayed number.");
        }
    }

    /**
     * Parses a priority assignment after checking its task number and level.
     */
    private PriorityCommand parsePriorityCommand(String input) throws GoopException {
        String[] arguments = input.substring("priority".length()).trim().split("\\s+");
        if (arguments.length != 2) {
            throw new GoopException("Use: priority <number> <high|medium|low|none> "
                    + "(1=high, 2=medium, 3=low).");
        }
        int taskNumber = parsePositiveTaskNumber(arguments[0], "priority");
        Priority priority = parsePriority(arguments[1]);
        return new PriorityCommand(taskNumber, priority);
    }

    /**
     * Converts a priority label or numeric alias to its task value.
     */
    private Priority parsePriority(String text) throws GoopException {
        switch (text.toLowerCase(Locale.ROOT)) {
            case "high", "1":
                return Priority.HIGH;
            case "medium", "2":
                return Priority.MEDIUM;
            case "low", "3":
                return Priority.LOW;
            case "none":
                return Priority.NONE;
            default:
                throw new GoopException("Priority must be high, medium, low, or none "
                        + "(1=high, 2=medium, 3=low).");
        }
    }

    /**
     * Creates a task from a valid add command.
     *
     * @param input Complete user input.
     * @return Task described by the input.
     * @throws GoopException If the command is unknown or required task details are
     *         invalid.
     */
    private Task parseTask(String input) throws GoopException {
        if (isCommand(input, "todo")) {
            return parseTodo(input.substring("todo".length()).trim());
        }

        if (isCommand(input, "deadline")) {
            return parseDeadline(input.substring("deadline".length()).trim());
        }

        if (isCommand(input, "event")) {
            return parseEvent(input.substring("event".length()).trim());
        }

        throw new GoopException("I don't recognise that command. Use todo, deadline, "
                + "event, list, find, mark, unmark, delete, priority, or bye.");
    }

    /**
     * Creates a to-do after validating its description.
     */
    private Todo parseTodo(String description) throws GoopException {
        if (description.isEmpty()) {
            throw new GoopException("A todo needs a description. Use: todo <description>.");
        }
        return new Todo(description);
    }

    /**
     * Extracts the non-empty keyword supplied to {@code find}.
     *
     * @param input Complete user input.
     * @return Keyword used to search task descriptions.
     * @throws GoopException If no keyword was supplied.
     */
    private String parseKeyword(String input) throws GoopException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new GoopException(
                    "The find command needs a keyword. Use: find <keyword>.");
        }
        return keyword;
    }

    /**
     * Creates a deadline after validating its description and parsing its date
     * and time.
     */
    private Deadline parseDeadline(String taskDetails) throws GoopException {
        requireSingleDelimiter(taskDetails, "/by");
        int byPosition = findDelimiter(taskDetails, "/by");
        if (byPosition < 0) {
            throw new GoopException("A deadline needs '/by' between its description "
                    + "and date. Use: deadline <description> /by <date or time>.");
        }

        String description = taskDetails.substring(0, byPosition).trim();
        String byText = taskDetails.substring(byPosition + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new GoopException("A deadline needs a description before '/by'. "
                    + "Use: deadline <description> /by <date or time>.");
        }
        if (byText.isEmpty()) {
            throw new GoopException("A deadline needs a date or time after '/by'. "
                    + "Use: deadline <description> /by <date or time>.");
        }
        return new Deadline(description, parseDeadlineDateTime(byText));
    }

    /**
     * Parses a deadline using either the example day-first format or an ISO-style
     * year-first format.
     */
    private LocalDateTime parseDeadlineDateTime(String text) throws GoopException {
        for (DateTimeFormatter format : DEADLINE_INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException error) {
                // Try the next supported format.
            }
        }
        throw new GoopException("The deadline date and time must use d/M/yyyy HHmm "
                + "or yyyy-MM-dd HHmm. For example: deadline return book "
                + "/by 2/12/2019 1800.");
    }

    /**
     * Creates an event after validating its description, start, and end text.
     */
    private Event parseEvent(String taskDetails) throws GoopException {
        requireSingleDelimiter(taskDetails, "/from");
        requireSingleDelimiter(taskDetails, "/to");
        int fromPosition = findDelimiter(taskDetails, "/from");
        if (fromPosition < 0) {
            throw new GoopException("An event needs '/from' before its start time. "
                    + "Use: event <description> /from <start> /to <end>.");
        }

        int toPosition = findDelimiter(taskDetails, "/to");
        if (toPosition >= 0 && toPosition < fromPosition) {
            throw new GoopException("An event needs '/from' before '/to'. "
                    + "Use: event <description> /from <start> /to <end>.");
        }

        String description = taskDetails.substring(0, fromPosition).trim();
        String eventTimes = taskDetails.substring(fromPosition + "/from".length()).trim();
        if (description.isEmpty()) {
            throw new GoopException("An event needs a description before '/from'. "
                    + "Use: event <description> /from <start> /to <end>.");
        }

        return parseEventSchedule(description, eventTimes);
    }

    /**
     * Creates an event from its validated description and start/end arguments.
     */
    private Event parseEventSchedule(String description, String eventTimes) throws GoopException {
        int toPosition = findDelimiter(eventTimes, "/to");
        if (toPosition < 0) {
            throw new GoopException("An event needs '/to' before its end time. "
                    + "Use: event <description> /from <start> /to <end>.");
        }

        String from = eventTimes.substring(0, toPosition).trim();
        String to = eventTimes.substring(toPosition + "/to".length()).trim();
        if (from.isEmpty()) {
            throw new GoopException("An event needs a start time after '/from'. "
                    + "Use: event <description> /from <start> /to <end>.");
        }
        if (to.isEmpty()) {
            throw new GoopException("An event needs an end time after '/to'. "
                    + "Use: event <description> /from <start> /to <end>.");
        }
        try {
            return new Event(description, from, to);
        } catch (IllegalArgumentException error) {
            throw new GoopException(error.getMessage());
        }
    }

    /**
     * Rejects repeated parameter tokens instead of treating them as task text.
     */
    private void requireSingleDelimiter(String text, String delimiter) throws GoopException {
        int position = findDelimiter(text, delimiter);
        if (position >= 0 && findDelimiter(text.substring(position + delimiter.length()), delimiter) >= 0) {
            throw new GoopException("Use '" + delimiter + "' only once per command.");
        }
    }

    /**
     * Finds a delimiter only when it appears as a separate whitespace-delimited
     * token.
     */
    private int findDelimiter(String text, String delimiter) {
        int position = text.indexOf(delimiter);
        while (position >= 0) {
            int afterDelimiter = position + delimiter.length();
            boolean hasValidStart = position == 0
                    || Character.isWhitespace(text.charAt(position - 1));
            boolean hasValidEnd = afterDelimiter == text.length()
                    || Character.isWhitespace(text.charAt(afterDelimiter));
            if (hasValidStart && hasValidEnd) {
                return position;
            }
            position = text.indexOf(delimiter, position + 1);
        }
        return -1;
    }
}
