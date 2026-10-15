package niulai.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import niulai.NiuLaiException;
import niulai.command.AddCommand;
import niulai.command.Command;
import niulai.command.DeleteCommand;
import niulai.command.ExitCommand;
import niulai.command.FindCommand;
import niulai.command.ListCommand;
import niulai.command.MarkCommand;
import niulai.command.UndoCommand;
import niulai.command.UnmarkCommand;
import niulai.model.Deadline;
import niulai.model.Event;
import niulai.model.Task;
import niulai.model.Todo;

/**
 * Parses and validates commands entered by the user.
 */
public class Parser {
    /** Parses the date format accepted by the find command. */
    private static final DateTimeFormatter INPUT_DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE;

    /** Recognizes the date-shaped arguments reserved for the date-search variant of find. */
    private static final Pattern DATE_ARGUMENT_PATTERN =
            Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    /** Matches horizontal whitespace accepted between command fields. */
    private static final Pattern HORIZONTAL_WHITESPACE_PATTERN =
            Pattern.compile("[\\p{Zs}\\t]+");

    /** Matches a positive whole-number shape before range checks are applied. */
    private static final Pattern TASK_NUMBER_PATTERN = Pattern.compile("^[0-9]+$");

    /**
     * Parses complete user input into an executable command.
     *
     * @param input the complete user input
     * @param taskCount the number of tasks currently available
     * @return the executable command represented by the input
     * @throws NiuLaiException if the input is unknown or malformed
     */
    public Command parseCommand(String input, int taskCount) throws NiuLaiException {
        String normalizedInput = normalizeInput(input);
        if (Command.Type.BYE.matchesExactly(normalizedInput)) {
            return new ExitCommand();
        }

        if (Command.Type.LIST.matchesExactly(normalizedInput)) {
            return new ListCommand();
        }

        if (Command.Type.UNDO.matchesExactly(normalizedInput)) {
            return new UndoCommand();
        }

        if (Command.Type.FIND.matches(normalizedInput)) {
            return parseFindCommand(normalizedInput);
        }

        if (Command.Type.MARK.matches(normalizedInput)
                || Command.Type.UNMARK.matches(normalizedInput)
                || Command.Type.DELETE.matches(normalizedInput)) {
            return parseTaskIndexCommand(normalizedInput, taskCount);
        }

        if (Command.Type.TODO.matches(normalizedInput)
                || Command.Type.DEADLINE.matches(normalizedInput)
                || Command.Type.EVENT.matches(normalizedInput)) {
            return new AddCommand(parseTaskCreation(normalizedInput));
        }

        for (Command.Type type : new Command.Type[]{
            Command.Type.BYE, Command.Type.LIST, Command.Type.UNDO
        }) {
            if (type.matches(normalizedInput)) {
                throw new NiuLaiException(
                        "NOOO!!! '" + type.getKeyword() + "' does not take any arguments."
                );
            }
        }

        throw new NiuLaiException(
                "NOOO!!! I don't recognize that command. Try 'list' to view your tasks."
        );
    }

    /** Parses a find command into a date search or a keyword search. */
    private Command parseFindCommand(String input) throws NiuLaiException {
        String argument = getArgument(input, Command.Type.FIND);
        if (argument.isEmpty()) {
            throw invalidFindArgumentError();
        }
        if (DATE_ARGUMENT_PATTERN.matcher(argument).matches()) {
            return new FindCommand(parseDateArgument(input, Command.Type.FIND));
        }
        return new FindCommand(argument);
    }

    /** Parses a mark, unmark, or delete command. */
    private Command parseTaskIndexCommand(String input, int taskCount) throws NiuLaiException {
        if (Command.Type.MARK.matches(input)) {
            return new MarkCommand(parseTaskIndex(input, Command.Type.MARK, taskCount));
        }

        if (Command.Type.UNMARK.matches(input)) {
            return new UnmarkCommand(parseTaskIndex(input, Command.Type.UNMARK, taskCount));
        }

        return new DeleteCommand(parseTaskIndex(input, Command.Type.DELETE, taskCount));
    }

    /**
     * Parses the date argument of a command.
     *
     * @param input the complete user input
     * @param command the command whose date argument should be parsed
     * @return the parsed date
     * @throws NiuLaiException if the date is missing or invalid
     */
    public LocalDate parseDateArgument(String input, Command.Type command)
            throws NiuLaiException {
        String argument = getArgument(normalizeInput(input), command);

        if (argument.isEmpty()) {
            throw invalidDateError();
        }

        try {
            return LocalDate.parse(argument, INPUT_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw invalidDateError();
        }
    }

    /**
     * Parses a one-based task number into a zero-based task-list index.
     *
     * @param input the complete user input
     * @param command the command containing the task number
     * @param taskCount the number of tasks currently available
     * @return the zero-based task index
     * @throws NiuLaiException if the task number is missing, invalid, or out of range
     */
    public int parseTaskIndex(String input, Command.Type command, int taskCount)
            throws NiuLaiException {
        String argument = getArgument(normalizeInput(input), command);

        if (argument.isEmpty()) {
            throw new NiuLaiException(
                    "NOOO!!! '" + command.getKeyword() + "' needs a task number, such as '"
                            + command.getKeyword() + " 1'."
            );
        }

        if (!TASK_NUMBER_PATTERN.matcher(argument).matches()) {
            throw invalidTaskNumberError(command);
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw invalidTaskNumberError(command);
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new NiuLaiException(
                    "NOOO!!! Task " + taskNumber
                            + " does not exist. Use 'list' to see your tasks."
            );
        }

        return taskNumber - 1;
    }

    /**
     * Parses a todo, deadline, or event creation command.
     *
     * @param input the complete user input
     * @return the newly constructed task
     * @throws NiuLaiException if the command's fields are missing or malformed
     */
    public Task parseTaskCreation(String input) throws NiuLaiException {
        String normalizedInput = normalizeInput(input);
        if (Command.Type.TODO.matches(normalizedInput)) {
            return parseTodo(normalizedInput);
        }

        if (Command.Type.DEADLINE.matches(normalizedInput)) {
            return parseDeadline(normalizedInput);
        }

        if (Command.Type.EVENT.matches(normalizedInput)) {
            return parseEvent(normalizedInput);
        }

        throw new NiuLaiException(
                "NOOO!!! I don't recognize that command. Try 'list' to view your tasks."
        );
    }

    /** Parses a todo creation command. */
    private Task parseTodo(String input) throws NiuLaiException {
        String description = getArgument(input, Command.Type.TODO);
        if (description.isEmpty()) {
            throw new NiuLaiException(
                    "NOOO!!! A todo needs a description. Try: todo <description>."
            );
        }
        if (containsAnyMarker(description)) {
            throw new NiuLaiException(
                    "NOOO!!! A todo does not use /by, /from, or /to parameters."
            );
        }
        return new Todo(description);
    }

    /** Parses a deadline creation command. */
    private Task parseDeadline(String input) throws NiuLaiException {
        String details = getArgument(input, Command.Type.DEADLINE);
        Matcher byMatcher = markerPattern("/by").matcher(details);
        if (!byMatcher.find() || byMatcher.find()
                || markerPattern("/from").matcher(details).find()
                || markerPattern("/to").matcher(details).find()) {
            throw new NiuLaiException(
                    "NOOO!!! A deadline must look like: deadline <description> /by <date or time>."
            );
        }

        byMatcher.reset();
        byMatcher.find();
        String description = details.substring(0, byMatcher.start()).strip();
        String by = details.substring(byMatcher.end()).strip();
        if (description.isEmpty() || by.isEmpty()) {
            throw new NiuLaiException(
                    "NOOO!!! A deadline needs both a description and a /by date or time."
            );
        }

        try {
            return new Deadline(description, by);
        } catch (IllegalArgumentException e) {
            throw new NiuLaiException("NOOO!!! " + e.getMessage());
        }
    }

    /** Parses an event creation command. */
    private Task parseEvent(String input) throws NiuLaiException {
        String details = getArgument(input, Command.Type.EVENT);
        Matcher fromMatcher = markerPattern("/from").matcher(details);
        Matcher toMatcher = markerPattern("/to").matcher(details);
        if (!fromMatcher.find() || fromMatcher.find()
                || !toMatcher.find() || toMatcher.find()
                || markerPattern("/by").matcher(details).find()) {
            throw new NiuLaiException(
                    "NOOO!!! An event must look like: event <description> /from <start> /to <end>."
            );
        }

        fromMatcher.reset();
        toMatcher.reset();
        fromMatcher.find();
        toMatcher.find();
        if (fromMatcher.start() >= toMatcher.start()) {
            throw new NiuLaiException(
                    "NOOO!!! An event must look like: event <description> /from <start> /to <end>."
            );
        }

        String description = details.substring(0, fromMatcher.start()).strip();
        String from = details.substring(fromMatcher.end(), toMatcher.start()).strip();
        String to = details.substring(toMatcher.end()).strip();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new NiuLaiException(
                    "NOOO!!! An event needs a description, a /from time, and a /to time."
            );
        }

        try {
            return new Event(description, from, to);
        } catch (IllegalArgumentException e) {
            throw new NiuLaiException("NOOO!!! " + e.getMessage());
        }
    }

    /** Returns the trimmed text after a command name. */
    private String getArgument(String input, Command.Type command) {
        return input.substring(command.getKeyword().length()).strip();
    }

    /** Creates the standard invalid-date error. */
    private NiuLaiException invalidDateError() {
        return new NiuLaiException(
                "NOOO!!! 'find' needs a date in yyyy-mm-dd format, such as 'find 2026-08-25'."
        );
    }

    /** Creates the standard error for a find command without a search term. */
    private NiuLaiException invalidFindArgumentError() {
        return new NiuLaiException(
                "NOOO!!! 'find' needs a keyword, such as 'find book', or a date in yyyy-mm-dd format."
        );
    }

    /** Returns a standard task-number syntax error for the supplied command. */
    private NiuLaiException invalidTaskNumberError(Command.Type command) {
        return new NiuLaiException(
                "NOOO!!! Task numbers must be positive whole numbers, such as '"
                        + command.getKeyword() + " 1'."
        );
    }

    /** Normalizes user input and rejects control characters unsafe for storage or display. */
    private String normalizeInput(String input) throws NiuLaiException {
        if (input == null) {
            throw new NullPointerException("input");
        }

        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (Character.isISOControl(character) && character != '\t') {
                throw new NiuLaiException(
                        "NOOO!!! Commands cannot contain control characters."
                );
            }
        }
        return HORIZONTAL_WHITESPACE_PATTERN.matcher(input.strip()).replaceAll(" ");
    }

    /** Returns a pattern matching one standalone command parameter marker. */
    private Pattern markerPattern(String marker) {
        return Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
    }

    /** Returns whether text contains any standalone task parameter marker. */
    private boolean containsAnyMarker(String text) {
        return markerPattern("/by").matcher(text).find()
                || markerPattern("/from").matcher(text).find()
                || markerPattern("/to").matcher(text).find();
    }
}
