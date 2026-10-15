package charlie.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import charlie.command.AddCommand;
import charlie.command.Command;
import charlie.command.CommandType;
import charlie.command.DeleteCommand;
import charlie.command.ExitCommand;
import charlie.command.FindCommand;
import charlie.command.ListCommand;
import charlie.command.MarkCommand;
import charlie.command.OnCommand;
import charlie.command.UnmarkCommand;
import charlie.command.UpdateCommand;
import charlie.command.UpdateField;
import charlie.exception.CharlieException;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.Todo;

/**
 * Interprets raw user input and validates command arguments.
 */
public final class Parser {
    /** Matches a deadline delimiter used as a separate command token. */
    private static final Pattern BY_DELIMITER = Pattern.compile("(?<!\\S)/by(?!\\S)");

    /** Matches an event-start delimiter used as a separate command token. */
    private static final Pattern FROM_DELIMITER = Pattern.compile("(?<!\\S)/from(?!\\S)");

    /** Matches an event-end delimiter used as a separate command token. */
    private static final Pattern TO_DELIMITER = Pattern.compile("(?<!\\S)/to(?!\\S)");

    /**
     * Prevents instantiation of this utility class.
     */
    private Parser() {
    }

    /**
     * Converts user input into a command that is ready to execute.
     *
     * @param input Complete user input.
     * @return Command represented by the input.
     * @throws CharlieException If the command or its arguments are invalid.
     */
    public static Command parse(String input) {
        CommandType commandType = parseCommand(input);
        return switch (commandType) {
            case BYE -> parseExitCommand(input);
            case LIST -> parseListCommand(input);
            case ON -> new OnCommand(parseDate(input));
            case FIND -> new FindCommand(parseFindKeyword(input));
            case MARK -> new MarkCommand(parseTaskIndex(input));
            case UNMARK -> new UnmarkCommand(parseTaskIndex(input));
            case DELETE -> new DeleteCommand(parseTaskIndex(input));
            case TODO, DEADLINE, EVENT -> new AddCommand(parseTask(input, commandType));
            case UPDATE -> parseUpdateCommand(input);
        };
    }

    /**
     * Converts the first word of a user input line into a command.
     *
     * @param input Complete user input.
     * @return The recognized command.
     * @throws CharlieException If the input is empty or starts with an unknown command.
     */
    public static CommandType parseCommand(String input) {
        if (input == null || input.isBlank()) {
            throw new CharlieException("Um... please give me a command first.");
        }

        String[] parts = input.trim().split("\\s+");
        return CommandType.parseKeyword(parts[0]);
    }

    /**
     * Parses the single date argument of an {@code on} command.
     *
     * @param input Complete user input.
     * @return The requested date.
     * @throws CharlieException If the argument count or date is invalid.
     */
    public static LocalDate parseDate(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            throw new CharlieException(
                    "Please provide exactly one date in yyyy-MM-dd format.");
        }

        try {
            return LocalDate.parse(parts[1]);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Date must be a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses the one-based task number supplied to a task command.
     *
     * @param input Complete user input.
     * @return Zero-based task index.
     * @throws CharlieException If the input does not contain exactly one numeric task number.
     */
    public static int parseTaskIndex(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 2) {
            throw new CharlieException("Please provide exactly one task number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new CharlieException("Please enter a valid task number.");
        }

        return taskNumber - 1;
    }

    /**
     * Parses all search text supplied after a {@code find} command.
     *
     * @param input Complete user input.
     * @return Keyword or phrase to search for.
     * @throws CharlieException If the input does not contain a keyword.
     */
    public static String parseFindKeyword(String input) {
        String[] commandAndKeywordParts = input.trim().split("\\s+", 2);

        if (commandAndKeywordParts.length < 2 || commandAndKeywordParts[1].isBlank()) {
            throw new CharlieException("Please provide a keyword to find.");
        }

        return commandAndKeywordParts[1].trim();
    }

    /**
     * Converts a task command into the corresponding task type.
     *
     * @param input Complete user input containing the task details.
     * @param commandType Type of task to create.
     * @return Task containing the parsed details.
     * @throws CharlieException If the command does not have the expected format.
     */
    public static Task parseTask(String input, CommandType commandType) {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Only task-creation command types can be parsed as tasks";

        String[] commandAndArgumentParts = input.trim().split("\\s+", 2);
        if (commandAndArgumentParts.length < 2) {
            throw new CharlieException("The task description cannot be empty.");
        }

        String arguments = commandAndArgumentParts[1].trim();
        if (commandType == CommandType.TODO) {
            return new Todo(parseDescription(arguments), false);
        } else if (commandType == CommandType.DEADLINE) {
            return parseDeadline(arguments);
        } else {
            return parseEvent(arguments);
        }
    }

    /**
     * Parses the description and date fields of a deadline.
     *
     * @param arguments Deadline description and date arguments.
     * @return Deadline containing the parsed arguments.
     */
    private static Task parseDeadline(String arguments) {
        Matcher byMatcher = BY_DELIMITER.matcher(arguments);
        if (!byMatcher.find()) {
            throw new CharlieException("A deadline must include /by followed by a date.");
        }
        int byPosition = byMatcher.start();
        int deadlineStartPosition = byMatcher.end();
        if (byMatcher.find()) {
            throw new CharlieException("A deadline must include /by exactly once.");
        }

        String description = parseDescription(arguments.substring(0, byPosition));

        String deadlineText = arguments.substring(deadlineStartPosition).trim();
        if (deadlineText.isBlank()) {
            throw new CharlieException("Deadline cannot be empty.");
        }

        try {
            return new Deadline(description, false, LocalDate.parse(deadlineText));
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Deadline must be a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses the description, start, and end fields of an event.
     *
     * @param arguments Event description, start, and end arguments.
     * @return Event containing the parsed arguments.
     */
    private static Task parseEvent(String arguments) {
        Matcher fromMatcher = FROM_DELIMITER.matcher(arguments);
        Matcher toMatcher = TO_DELIMITER.matcher(arguments);
        if (!fromMatcher.find() || !toMatcher.find()) {
            throw new CharlieException("Need to include /from or /to fields.");
        }

        int fromPosition = fromMatcher.start();
        int fromValuePosition = fromMatcher.end();
        int toPosition = toMatcher.start();
        int toValuePosition = toMatcher.end();
        if (fromMatcher.find() || toMatcher.find()) {
            throw new CharlieException("An event must include /from and /to exactly once.");
        } else if (fromPosition > toPosition) {
            throw new CharlieException("Invalid argument format: /from should appear before /to");
        }

        String description = parseDescription(arguments.substring(0, fromPosition));

        String fromText = arguments.substring(fromValuePosition, toPosition).trim();
        String toText = arguments.substring(toValuePosition).trim();
        if (fromText.isBlank() || toText.isBlank()) {
            throw new CharlieException("from/to fields cannot be empty.");
        }

        return parseEventDateTimes(description, fromText, toText);
    }

    /**
     * Converts validated event fields into an event with a chronological date-time range.
     *
     * @param description Description of the event.
     * @param fromText Start date-time text.
     * @param toText End date-time text.
     * @return Event containing the parsed fields.
     */
    private static Event parseEventDateTimes(String description, String fromText, String toText) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd HHmm")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(fromText, dateTimeFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(toText, dateTimeFormatter);
            if (!startDateTime.isBefore(endDateTime)) {
                throw new CharlieException("Event end must be after its start.");
            }

            return new Event(description, false, startDateTime, endDateTime);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Event dates must use the yyyy-MM-dd HHmm format.");
        }
    }

    /**
     * Parses the task index, field, and replacement value of an update command.
     *
     * @param input Complete update command entered by the user.
     * @return Update command containing the parsed arguments.
     * @throws CharlieException If an argument is missing or invalid.
     */
    private static UpdateCommand parseUpdateCommand(String input) {
        String[] commandAndArgumentParts = input.trim().split("\\s+", 4);

        if (commandAndArgumentParts.length < 4) {
            throw new CharlieException(
                    "Usage: update TASK_NUMBER FIELD NEW_VALUE.");
        }

        int taskIndex = parseUpdateIndex(commandAndArgumentParts[1]);
        UpdateField updateField =
                parseUpdateField(commandAndArgumentParts[2]);
        String newValue = parseUpdateValue(commandAndArgumentParts[3], updateField);

        updateField.validateNewValue(newValue);

        return new UpdateCommand(taskIndex, updateField, newValue);
    }

    /**
     * Converts a one-based update task number into a zero-based index.
     *
     * @param input Task number entered by the user.
     * @return Zero-based task index.
     * @throws CharlieException If the task number is not an integer.
     */
    private static int parseUpdateIndex(String input) {
        try {
            int oneBasedIndex = Integer.parseInt(input);
            return oneBasedIndex - 1;
        } catch (NumberFormatException e) {
            throw new CharlieException(
                    "Please enter a valid task number.");
        }
    }

    /**
     * Converts an update-field keyword into its corresponding field.
     *
     * @param input Update-field keyword entered by the user.
     * @return Matching update field.
     * @throws CharlieException If the keyword does not represent a supported field.
     */
    private static UpdateField parseUpdateField(String input) {
        return UpdateField.parseKeyword(input);
    }

    /**
     * Removes surrounding whitespace from an update replacement value.
     *
     * @param input Replacement value entered by the user.
     * @return Trimmed replacement value.
     */
    private static String parseUpdateValue(String input, UpdateField updateField) {
        if (updateField == UpdateField.DESCRIPTION) {
            return parseDescription(input);
        }
        return input.trim();
    }

    /**
     * Validates and normalizes a task description.
     *
     * @param input Description entered by the user.
     * @return Description with surrounding and repeated whitespace removed.
     * @throws CharlieException If the description is empty or contains the save-file delimiter.
     */
    private static String parseDescription(String input) {
        String description = input.trim().replaceAll("\\s+", " ");
        if (description.isEmpty()) {
            throw new CharlieException("Description cannot be empty.");
        }
        if (description.contains("|")) {
            throw new CharlieException("A task description cannot contain |.");
        }
        return description;
    }

    /**
     * Parses a {@code bye} command that has no arguments.
     *
     * @param input Complete user input.
     * @return Exit command represented by the input.
     */
    private static ExitCommand parseExitCommand(String input) {
        validateNoArguments(input, "bye");
        return new ExitCommand();
    }

    /**
     * Parses a {@code list} command that has no arguments.
     *
     * @param input Complete user input.
     * @return List command represented by the input.
     */
    private static ListCommand parseListCommand(String input) {
        validateNoArguments(input, "list");
        return new ListCommand();
    }

    /**
     * Verifies that a command which takes no arguments contains only its keyword.
     *
     * @param input Complete user input.
     * @param commandKeyword Command keyword used in the error message.
     * @throws CharlieException If unexpected arguments follow the keyword.
     */
    private static void validateNoArguments(String input, String commandKeyword) {
        if (input.trim().split("\\s+").length != 1) {
            throw new CharlieException(
                    "The " + commandKeyword + " command does not accept arguments.");
        }
    }
}
