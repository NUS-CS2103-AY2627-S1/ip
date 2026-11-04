package soar.parser;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import soar.command.AddCommand;
import soar.command.Command;
import soar.command.DateCommand;
import soar.command.DeleteCommand;
import soar.command.EditCommand;
import soar.command.EditField;
import soar.command.ExitCommand;
import soar.command.FindCommand;
import soar.command.ListCommand;
import soar.command.MarkCommand;
import soar.command.UnmarkCommand;
import soar.exception.EmptyDescriptionException;
import soar.exception.InvalidTaskFormatException;
import soar.exception.InvalidTaskNumberException;
import soar.exception.SoarException;
import soar.exception.UnknownCommandException;
import soar.task.Deadline;
import soar.task.Event;
import soar.task.Task;
import soar.task.ToDo;

/**
 * Interprets user input and converts command arguments into application values.
 */
public class Parser {
    /** Recognizes slash-word tokens that delimit edit fields. */
    private static final Pattern EDIT_FIELD_PATTERN =
            Pattern.compile("(?<!\\S)/[A-Za-z]+(?=\\s|$)");

    /** Prevents creation of a stateless parser utility. */
    private Parser() {
    }

    /**
     * Interprets a complete input line and builds the command it represents.
     *
     * @param input Complete line entered by the user.
     * @return command ready to execute
     * @throws SoarException if the command or one of its arguments is invalid
     */
    public static Command parse(String input) throws SoarException {
        String normalizedInput = input == null ? "" : input.trim();
        CommandType commandType = parseCommand(normalizedInput);
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case FIND -> new FindCommand(parseKeyword(normalizedInput));
            case DATE -> new DateCommand(parseDate(normalizedInput));
            case TODO, DEADLINE, EVENT -> new AddCommand(parseTask(normalizedInput, commandType));
            case MARK -> new MarkCommand(parseTaskNumber(normalizedInput, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(normalizedInput, commandType));
            case DELETE -> new DeleteCommand(parseTaskNumber(normalizedInput, commandType));
            case EDIT -> parseEdit(normalizedInput);
        };
    }

    /**
     * Identifies the command represented by a complete input line.
     *
     * @param input Complete line entered by the user.
     * @return matching command type
     * @throws UnknownCommandException if the input does not start with a supported command
     */
    private static CommandType parseCommand(String input) throws UnknownCommandException {
        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.matches(input))
                .findFirst()
                .orElseThrow(UnknownCommandException::new);
    }

    /**
     * Builds the task described by a todo, deadline, or event command.
     *
     * @param input Complete task-creation command.
     * @param commandType Type of task command being parsed.
     * @return validated task represented by the command
     * @throws SoarException if a required task detail is missing or invalid
     */
    private static Task parseTask(String input, CommandType commandType) throws SoarException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Only task-creation commands should be parsed as tasks";
        return switch (commandType) {
            case TODO -> parseToDo(input);
            case DEADLINE -> parseDeadline(input);
            case EVENT -> parseEvent(input);
            default -> throw new IllegalArgumentException(
                    "Only todo, deadline, and event commands describe new tasks");
        };
    }

    /**
     * Parses the task number in a mark, unmark, or delete command.
     *
     * @param input Complete line entered by the user.
     * @param commandType Mark, unmark, or delete command type.
     * @return one-based task number entered by the user
     * @throws InvalidTaskNumberException if the number is missing or not an integer
     */
    private static int parseTaskNumber(String input, CommandType commandType)
            throws InvalidTaskNumberException {
        assert commandType.getMissingTaskNumberAction() != null
                : "Only numbered commands should be parsed for a task number";
        String taskNumberText = argumentsAfter(input, commandType);
        return parseTaskNumberText(taskNumberText, commandType);
    }

    /** Parses a task-number token using the selected command's error wording. */
    private static int parseTaskNumberText(String taskNumberText, CommandType commandType)
            throws InvalidTaskNumberException {
        String commandWord = commandType.getCommandWord();
        if (taskNumberText.isEmpty()) {
            throw new InvalidTaskNumberException("Add a task number after '" + commandWord
                    + "' so I know which task "
                    + commandType.getMissingTaskNumberAction().getDescription() + "!");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new InvalidTaskNumberException("'" + taskNumberText
                    + "' is not a whole task number. Choose a number from your list to keep flying high!");
        }

        return taskNumber;
    }

    /** Builds an edit command from a task number and ordered field-value pairs. */
    private static EditCommand parseEdit(String input) throws SoarException {
        String details = argumentsAfter(input, CommandType.EDIT);
        if (details.isEmpty()) {
            parseTaskNumberText("", CommandType.EDIT);
        }

        int firstSpace = details.indexOf(' ');
        String taskNumberText = firstSpace < 0 ? details : details.substring(0, firstSpace);
        int taskNumber = parseTaskNumberText(taskNumberText, CommandType.EDIT);
        if (firstSpace < 0 || details.substring(firstSpace).isBlank()) {
            throw new InvalidTaskFormatException(
                    "Add at least one editable field after the task number.");
        }

        String fieldText = details.substring(firstSpace).trim();
        return new EditCommand(taskNumber, parseEditFields(fieldText));
    }

    /** Parses field markers and their values while retaining command order. */
    private static Map<EditField, String> parseEditFields(String fieldText)
            throws InvalidTaskFormatException {
        Matcher matcher = EDIT_FIELD_PATTERN.matcher(fieldText);
        List<MatchResult> matches = matcher.results().toList();
        if (matches.isEmpty() || matches.get(0).start() != 0) {
            throw new InvalidTaskFormatException(
                    "Add at least one editable field after the task number.");
        }

        LinkedHashMap<EditField, String> edits = new LinkedHashMap<>();
        for (int i = 0; i < matches.size(); i++) {
            MatchResult match = matches.get(i);
            String marker = match.group();
            EditField field = EditField.fromMarker(marker).orElseThrow(() ->
                    new InvalidTaskFormatException("I don't recognize the edit field '" + marker
                            + "'. Use /description, /by, /from, or /to."));
            if (edits.containsKey(field)) {
                throw new InvalidTaskFormatException("The edit field '" + marker
                        + "' appears more than once. Provide each field only once.");
            }

            int valueEnd = i + 1 < matches.size() ? matches.get(i + 1).start() : fieldText.length();
            String value = fieldText.substring(match.end(), valueEnd).trim();
            if (value.isEmpty()) {
                throw new InvalidTaskFormatException("The '" + marker
                        + "' value is empty. Add a value after it.");
            }
            edits.put(field, value);
        }
        return edits;
    }

    /**
     * Parses the calendar date supplied to a date query.
     *
     * @param input Complete date command.
     * @return requested calendar date
     * @throws InvalidTaskFormatException if the date is missing or unrecognized
     */
    private static LocalDate parseDate(String input) throws InvalidTaskFormatException {
        String value = argumentsAfter(input, CommandType.DATE);
        if (value.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "Add a date after 'date' so I know which day's flight plan to show!");
        }

        var requestedDate = DateTimeParser.parseCalendarDate(value);
        if (requestedDate.isEmpty()) {
            throw new InvalidTaskFormatException("I couldn't understand the date '" + value
                    + "'. Use one of the supported deadline date or date-time formats!");
        }
        return requestedDate.get();
    }

    /**
     * Parses the text supplied to a task-description search.
     *
     * @param input complete find command
     * @return non-empty keyword or phrase to find
     * @throws InvalidTaskFormatException if no search text was supplied
     */
    private static String parseKeyword(String input) throws InvalidTaskFormatException {
        String keyword = argumentsAfter(input, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "Add a keyword after 'find' so I know which tasks to look for!");
        }
        return keyword;
    }

    /** Builds a todo after checking its description. */
    private static ToDo parseToDo(String input) throws EmptyDescriptionException {
        String description = argumentsAfter(input, CommandType.TODO);
        requireDescription(description, CommandType.TODO.getCommandWord());
        return new ToDo(description);
    }

    /** Builds a deadline after checking its description and {@code /by} value. */
    private static Deadline parseDeadline(String input) throws SoarException {
        String details = argumentsAfter(input, CommandType.DEADLINE);
        int byIndex = details.indexOf("/by");
        if (byIndex < 0) {
            throw new InvalidTaskFormatException(
                    "This deadline has no '/by' date. Add one so it has a clear path through the sky!");
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + "/by".length()).trim();
        requireDescription(description, CommandType.DEADLINE.getCommandWord());
        if (by.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "The deadline's '/by' date is empty. Add a date or time so it can fly on schedule!");
        }
        var dateTime = DateTimeParser.parseDateTime(by);
        if (dateTime.isPresent()) {
            return new Deadline(description, dateTime.get());
        }
        var date = DateTimeParser.parseDate(by);
        if (date.isPresent()) {
            return new Deadline(description, date.get());
        }
        throw new InvalidTaskFormatException("I couldn't understand the deadline '" + by
                + "'. Use yyyy-MM-dd, d/M/yyyy, d/M/yyyy HHmm, yyyy-MM-dd HH:mm, "
                + "d MMM yyyy h:mm a, or an ISO date-time!");
    }

    /** Builds an event after checking its description and time range. */
    private static Event parseEvent(String input) throws SoarException {
        String details = argumentsAfter(input, CommandType.EVENT);
        int fromIndex = details.indexOf("/from");
        int toIndex = fromIndex < 0 ? -1 : details.indexOf("/to", fromIndex + "/from".length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new InvalidTaskFormatException(
                    "This event needs both '/from' and '/to' times to map its flight across the sky!");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = details.substring(toIndex + "/to".length()).trim();
        requireDescription(description, CommandType.EVENT.getCommandWord());
        if (from.isEmpty() || to.isEmpty()) {
            throw new InvalidTaskFormatException(
                    "The event's flight times are incomplete. Fill in both '/from' and '/to' values!");
        }
        if (!Event.hasValidDateOrder(from, to)) {
            throw new InvalidTaskFormatException(
                    "The event ends before it starts. Make its '/to' date or time the same as or after '/from'.");
        }
        return new Event(description, from, to);
    }

    /** Returns the trimmed argument text following a recognized command word. */
    private static String argumentsAfter(String input, CommandType commandType) {
        assert commandType.matches(input) : "Input should match its recognized command type";
        return input.substring(commandType.getCommandWord().length()).trim();
    }

    /** Rejects an empty task description with a task-specific explanation. */
    private static void requireDescription(String description, String taskType)
            throws EmptyDescriptionException {
        if (description.isEmpty()) {
            throw new EmptyDescriptionException(taskType);
        }
    }
}
