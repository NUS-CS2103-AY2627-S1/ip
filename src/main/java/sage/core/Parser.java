package sage.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sage.command.AddCommand;
import sage.command.Command;
import sage.command.DeleteCommand;
import sage.command.ExitCommand;
import sage.command.FindCommand;
import sage.command.ListCommand;
import sage.command.MarkCommand;
import sage.command.UnmarkCommand;
import sage.exception.SageException;
import sage.task.Deadline;
import sage.task.Event;
import sage.task.Note;
import sage.task.TaskDateTime;
import sage.task.Todo;

/**
 * Converts raw user input into concrete command objects.
 */
public class Parser {
    private static final Pattern FIELD_DELIMITER = Pattern.compile("(?<!\\S)/(by|from|to)(?=\\s|$)");
    private static final String UNKNOWN_COMMAND_MESSAGE = "I'm sorry, but I don't know what that means. "
            + "Try a valid command like todo, deadline, event, note, list, find, mark, unmark, delete, or bye.";

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Parses a full command string into a command instance.
     *
     * @param fullCommand The user-entered command text.
     * @return The corresponding command object.
     * @throws SageException If the command is empty, malformed, or unsupported.
     */
    public static Command parse(String fullCommand) throws SageException {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            throw new SageException(UNKNOWN_COMMAND_MESSAGE);
        }

        if (fullCommand.indexOf('\n') >= 0 || fullCommand.indexOf('\r') >= 0
                || fullCommand.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new SageException("Please enter one command on a single line, without control characters.");
        }
        String[] parts = fullCommand.strip().split("\\s+", 2);
        String commandWord = parts[0];
        String arguments = parts.length == 2 ? parts[1].strip() : "";
        switch (commandWord) {
        case "bye":
            requireNoArguments(arguments, commandWord);
            return new ExitCommand();
        case "list":
            requireNoArguments(arguments, commandWord);
            return new ListCommand();
        case "note":
            return parseNote(arguments);
        case "find":
            return parseFind(arguments);
        case "todo":
            return parseTodo(arguments);
        case "deadline":
            return parseDeadline(arguments);
        case "event":
            return parseEvent(arguments);
        case "mark":
            return new MarkCommand(parseTaskNumber(arguments, commandWord));
        case "unmark":
            return new UnmarkCommand(parseTaskNumber(arguments, commandWord));
        case "delete":
            return new DeleteCommand(parseTaskNumber(arguments, commandWord));
        default:
            throw new SageException(UNKNOWN_COMMAND_MESSAGE);
        }
    }

    /**
     * Rejects extra parameters for commands that take no arguments.
     *
     * @param arguments The text after the command name.
     * @param commandWord The command being validated.
     * @throws SageException If extra text follows the command.
     */
    private static void requireNoArguments(String arguments, String commandWord) throws SageException {
        if (!arguments.isEmpty()) {
            throw new SageException("The " + commandWord + " command takes no extra arguments. Try: " + commandWord);
        }
    }

    /**
     * Creates a search command after validating its keyword.
     *
     * @param keyword The search text.
     * @return The search command.
     * @throws SageException If the keyword is empty.
     */
    private static Command parseFind(String keyword) throws SageException {
        if (keyword.isEmpty()) {
            throw new SageException("The keyword of a find cannot be empty. Try: find <keyword>");
        }
        return new FindCommand(keyword);
    }

    /**
     * Creates a to-do command after validating its description.
     *
     * @param description The task description.
     * @return The command that adds the to-do task.
     * @throws SageException If the description is empty.
     */
    private static Command parseTodo(String description) throws SageException {
        if (description.isEmpty()) {
            throw new SageException("The description of a todo cannot be empty. Try: todo <task>");
        }
        return new AddCommand(new Todo(description));
    }

    /**
     * Creates a note after validating its nonempty text.
     *
     * @param text The information to remember.
     * @return The command that adds the note.
     * @throws SageException If the note is empty.
     */
    private static Command parseNote(String text) throws SageException {
        if (text.isBlank()) {
            throw new SageException("The text of a note cannot be empty. Try: note <text>");
        }
        return new AddCommand(new Note(text));
    }

    /**
     * Parses a deadline description and its due time.
     *
     * @param details The arguments following the deadline command.
     * @return The command that adds the deadline.
     * @throws SageException If a required deadline field is missing.
     */
    private static Command parseDeadline(String details) throws SageException {
        String[] fields = parseFields(details, new String[]{"by"},
                "The deadline format is invalid. Try: deadline <task> /by <time>");
        String description = fields[0];
        if (description.isEmpty()) {
            throw new SageException("The description of a deadline cannot be empty. "
                    + "Try: deadline <task> /by <time>");
        }
        String deadlineTime = fields[1];
        if (deadlineTime.isEmpty()) {
            throw new SageException("The deadline time cannot be empty. Try: deadline <task> /by <time>");
        }
        validateTime(deadlineTime);
        return new AddCommand(new Deadline(description, deadlineTime));
    }

    /**
     * Parses an event description and its start and end times.
     *
     * @param details The arguments following the event command.
     * @return The command that adds the event.
     * @throws SageException If a field is missing or invalid, or the end is not after the start.
     */
    private static Command parseEvent(String details) throws SageException {
        String[] fields = parseFields(details, new String[]{"from", "to"},
                "The event format is invalid. Try: event <task> /from <start> /to <end>");
        String description = fields[0];
        if (description.isEmpty()) {
            throw new SageException("The description of an event cannot be empty. "
                    + "Try: event <task> /from <start> /to <end>");
        }
        String startTime = fields[1];
        String endTime = fields[2];
        if (startTime.isEmpty() || endTime.isEmpty()) {
            throw new SageException("The event timings cannot be empty. "
                    + "Try: event <task> /from <start> /to <end>");
        }
        validateTime(startTime);
        validateTime(endTime);
        Event event = new Event(description, startTime, endTime);
        if (event.getFrom() != null && event.getTo() != null && !event.getFrom().isBefore(event.getTo())) {
            throw new SageException("The event end time must be after the start time.");
        }
        return new AddCommand(event);
    }

    /**
     * Extracts exactly the expected delimiters in order, allowing spaces or tabs around them.
     *
     * @param details The raw command arguments.
     * @param expectedNames The delimiter names in their required order.
     * @param formatMessage The actionable error for malformed fields.
     * @return The description followed by each field value.
     * @throws SageException If delimiters are missing, repeated, unexpected, or out of order.
     */
    private static String[] parseFields(String details, String[] expectedNames, String formatMessage)
            throws SageException {
        Matcher matcher = FIELD_DELIMITER.matcher(details);
        String[] fields = new String[expectedNames.length + 1];
        int previousEnd = 0;
        for (int i = 0; i < expectedNames.length; i++) {
            if (!matcher.find() || !expectedNames[i].equals(matcher.group(1))) {
                throw new SageException(formatMessage);
            }
            fields[i] = details.substring(previousEnd, matcher.start()).strip();
            previousEnd = matcher.end();
        }
        if (matcher.find()) {
            throw new SageException("Each time parameter must appear exactly once. " + formatMessage);
        }
        fields[expectedNames.length] = details.substring(previousEnd).strip();
        return fields;
    }

    /**
     * Converts invalid numeric dates into an actionable command error.
     *
     * @param value The time text entered by the user.
     * @throws SageException If the date-like value is not valid.
     */
    private static void validateTime(String value) throws SageException {
        try {
            TaskDateTime.validate(value);
        } catch (IllegalArgumentException exception) {
            throw new SageException(exception.getMessage(), exception);
        }
    }

    /**
     * Extracts the required task number for mark, unmark, and delete commands.
     *
     * @param indexText The trimmed arguments.
     * @param commandWord The matched command name.
     * @return The one-based task number, with range validation left to execution.
     * @throws SageException If the task number is missing or is not an integer.
     */
    private static int parseTaskNumber(String indexText, String commandWord) throws SageException {
        if (indexText.isEmpty()) {
            throw new SageException("The task number is missing. Try: " + commandWord + " <task number>");
        }
        return parseIndex(indexText);
    }

    /**
     * Parses a task index from user input.
     *
     * @param text The numeric text entered by the user.
     * @return The parsed task number.
     * @throws SageException If the text is not a valid integer.
     */
    private static int parseIndex(String text) throws SageException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new SageException("The task number must be a valid integer. "
                    + "Try: mark <number>, unmark <number>, or delete <number>");
        }
    }
}
