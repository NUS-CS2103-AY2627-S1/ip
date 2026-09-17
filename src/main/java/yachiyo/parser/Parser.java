package yachiyo.parser;

import static yachiyo.exception.ErrorCategory.INVALID_OPERATION;
import static yachiyo.exception.ErrorCategory.WARNING;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import yachiyo.command.AddCommand;
import yachiyo.command.Command;
import yachiyo.command.DeleteCommand;
import yachiyo.command.EditDeadlineDateTimeCommand;
import yachiyo.command.EditDescriptionCommand;
import yachiyo.command.EditEventEndDateTimeCommand;
import yachiyo.command.EditEventStartDateTimeCommand;
import yachiyo.command.ExitCommand;
import yachiyo.command.FindCommand;
import yachiyo.command.FindOnDateCommand;
import yachiyo.command.ListCommand;
import yachiyo.command.MarkCommand;
import yachiyo.command.UnmarkCommand;
import yachiyo.exception.YachiyoException;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.Task;
import yachiyo.task.ToDo;

/**
 * Interprets user commands and converts their arguments into application objects.
 */
public final class Parser {
    private static final String DEADLINE_DELIMITER_PATTERN = createCommandDelimiterPattern("by");
    private static final String EVENT_START_DELIMITER_PATTERN = createCommandDelimiterPattern("from");
    private static final String EVENT_END_DELIMITER_PATTERN = createCommandDelimiterPattern("to");
    private static final DateTimeFormatter DATE_TIME_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Parser() {
    }

    /**
     * Creates a pattern that recognizes a slash-prefixed delimiter only as a separate command token.
     *
     * @param delimiter delimiter text without its leading slash.
     * @return regex for matching the command delimiter.
     */
    private static String createCommandDelimiterPattern(String delimiter) {
        return "(?<!\\S)/" + delimiter + "(?!\\S)";
    }

    /**
     * Interprets a user input line and creates the command that should handle it.
     *
     * @param userInput complete input entered by the user.
     * @return executable command represented by the input.
     * @throws YachiyoException if the command or its arguments are invalid.
     */
    public static Command parse(String userInput) throws YachiyoException {
        String[] parts = userInput.trim().split("\\s+", 2);
        CommandType type = CommandType.parse(parts[0]);
        String arguments = parts.length == 2 ? parts[1].trim() : "";

        return switch (type) {
            case MARK -> new MarkCommand(parseTaskNumber(arguments));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(arguments));
            case LIST -> {
                validateNoArguments(arguments, "list");
                yield new ListCommand();
            }
            case FIND -> new FindCommand(parseKeyword(arguments));
            case TODO -> new AddCommand(parseToDo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
            case ON -> new FindOnDateCommand(parseDate(arguments));
            case EDIT -> parseEdit(arguments);
            case DELETE -> new DeleteCommand(parseTaskNumber(arguments));
            case BYE -> {
                validateNoArguments(arguments, "bye");
                yield new ExitCommand();
            }
        };
    }

    /**
     * Checks that a command which takes no arguments has no trailing details.
     *
     * @param arguments text following the command word.
     * @param commandWord command word shown in the correction message.
     * @throws YachiyoException if unexpected arguments are present.
     */
    private static void validateNoArguments(String arguments, String commandWord)
            throws YachiyoException {
        if (!arguments.isBlank()) {
            throw new YachiyoException(WARNING,
                    String.format("Hmm... the %s command doesn't need anything after it. "
                            + "Did you mean \"%s\"?", commandWord, commandWord)
            );
        }
    }

    /**
     * Creates a command that edits one detail of a numbered task.
     *
     * @param arguments task number, field selector, and replacement value.
     * @return command for the selected task field.
     * @throws YachiyoException if the task number, field, or replacement value is invalid.
     */
    private static Command parseEdit(String arguments) throws YachiyoException {
        if (arguments.isBlank()) {
            throw new YachiyoException(WARNING,
                    "Which task should I edit? Tell me its number!"
            );
        }

        String[] editParts = arguments.split("\\s+", 3);
        int taskNumber = parseTaskNumber(editParts[0]);
        if (editParts.length < 2) {
            throw new YachiyoException(WARNING,
                    "Which detail should I edit? Try /description, /by, /from, or /to "
                            + "followed by its new value."
            );
        }

        String field = editParts[1];
        String value = editParts.length == 3 ? editParts[2].trim() : "";
        return switch (field) {
            case "/description" -> new EditDescriptionCommand(
                    taskNumber, parseEditDescription(value));
            case "/by" -> new EditDeadlineDateTimeCommand(
                    taskNumber, parseEditDateTime(value, "deadline"));
            case "/from" -> new EditEventStartDateTimeCommand(
                    taskNumber, parseEditDateTime(value, "event start"));
            case "/to" -> new EditEventEndDateTimeCommand(
                    taskNumber, parseEditDateTime(value, "event end"));
            default -> throw new YachiyoException(INVALID_OPERATION,
                    "I can't edit that detail. Try /description, /by, /from, or /to."
            );
        };
    }

    /**
     * Returns a non-blank replacement task description.
     *
     * @param description replacement description supplied by the user.
     * @return validated replacement description.
     * @throws YachiyoException if the description is missing or contains a reserved character.
     */
    private static String parseEditDescription(String description) throws YachiyoException {
        if (description.isBlank()) {
            throw new YachiyoException(WARNING, "What should the new description be?");
        }
        validateDescriptionCharacters(description);
        return description;
    }

    /**
     * Checks that a description excludes characters reserved by the storage format.
     *
     * @param description non-blank description supplied by the user.
     * @throws YachiyoException if the description contains a reserved character.
     */
    private static void validateDescriptionCharacters(String description) throws YachiyoException {
        if (!Task.isValidDescription(description)) {
            throw new YachiyoException(WARNING,
                    "Descriptions can't contain the \"|\" character. Could you remove it?"
            );
        }
    }

    /**
     * Parses a replacement date-time supplied to the {@code edit} command.
     *
     * @param dateTimeText replacement date-time text.
     * @param fieldName name used to identify the field in error messages.
     * @return parsed replacement date-time.
     * @throws YachiyoException if the replacement date-time is missing or invalid.
     */
    private static LocalDateTime parseEditDateTime(String dateTimeText, String fieldName)
            throws YachiyoException {
        if (dateTimeText.isBlank()) {
            throw new YachiyoException(WARNING,
                    String.format("What should the new %s be?", fieldName)
            );
        }
        return parseDateTime(dateTimeText, fieldName);
    }

    /**
     * Returns a non-blank keyword supplied to the {@code find} command.
     *
     * @param keyword Keyword supplied by the user.
     * @return Validated keyword.
     * @throws YachiyoException If the keyword is missing.
     */
    private static String parseKeyword(String keyword) throws YachiyoException {
        if (keyword.isBlank()) {
            throw new YachiyoException(WARNING,
                    "What should I search for? Tell me a keyword!"
            );
        }
        return keyword;
    }

    /**
     * Parses a task number supplied to a task-related command.
     *
     * @param arguments text expected to contain a task number.
     * @return parsed one-based task number.
     * @throws YachiyoException if the task number is missing or is not a whole number.
     */
    private static int parseTaskNumber(String arguments) throws YachiyoException {
        if (arguments.isBlank()) {
            throw new YachiyoException(WARNING,
                    "Which task should I use? Tell me its number!"
            );
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new YachiyoException(WARNING,
                    "Hmm... task numbers need to be whole numbers, okay?"
            );
        }
    }

    /**
     * Parses a date supplied to the {@code on} command.
     *
     * @param dateText date supplied in d/M/yyyy format.
     * @return parsed date.
     * @throws YachiyoException if the date is missing or invalid.
     */
    private static LocalDate parseDate(String dateText) throws YachiyoException {
        if (dateText.isBlank()) {
            throw new YachiyoException(WARNING,
                    "Which date should I check? Please enter it as d/M/yyyy."
            );
        }

        try {
            return LocalDate.parse(dateText, DATE_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new YachiyoException(WARNING,
                    "Hmm, please enter the date as d/M/yyyy, for example 2/12/2026."
            );
        }
    }

    /**
     * Creates a to-do task from command arguments.
     *
     * @param description task description supplied by the user.
     * @return parsed to-do task.
     * @throws YachiyoException if the description is missing or contains a reserved character.
     */
    private static ToDo parseToDo(String description) throws YachiyoException {
        if (description.isBlank()) {
            throw new YachiyoException(WARNING,
                    "Hmm, this to-do is missing a description. What shall we call it?"
            );
        }
        validateDescriptionCharacters(description);
        return new ToDo(description);
    }

    /**
     * Creates a deadline task from command arguments.
     *
     * @param taskDetails description and deadline supplied by the user.
     * @return parsed deadline task.
     * @throws YachiyoException if a required field is missing or invalid.
     */
    private static Deadline parseDeadline(String taskDetails) throws YachiyoException {
        String[] deadlineParts = taskDetails.split(DEADLINE_DELIMITER_PATTERN, 2);
        String description = deadlineParts[0].trim();
        if (description.isBlank()) {
            throw new YachiyoException(WARNING,
                    "Hmm, this deadline is missing a description. What shall we call it?"
            );
        }
        validateDescriptionCharacters(description);

        if (deadlineParts.length < 2 || deadlineParts[1].trim().isBlank()) {
            throw new YachiyoException(WARNING,
                    "It seems this task is missing a deadline. When should it be completed?"
            );
        }

        LocalDateTime by = parseDateTime(deadlineParts[1].trim(), "deadline");
        return new Deadline(description, by);
    }

    /**
     * Creates an event task from command arguments.
     *
     * @param taskDetails description, start, and end supplied by the user.
     * @return parsed event task.
     * @throws YachiyoException if a required field is missing or invalid.
     */
    private static Event parseEvent(String taskDetails) throws YachiyoException {
        String[] eventParts = taskDetails.split(EVENT_START_DELIMITER_PATTERN, 2);
        String description = eventParts[0].trim();
        if (description.isBlank()) {
            throw new YachiyoException(WARNING,
                    "Hmm, this event is missing a description. What shall we call it?"
            );
        }
        validateDescriptionCharacters(description);

        if (eventParts.length < 2 || eventParts[1].trim().isBlank()) {
            throw new YachiyoException(WARNING,
                    "This event still needs a start time. When should it begin?"
            );
        }

        EventPeriod eventPeriod = parseEventPeriod(eventParts[1].trim());
        return new Event(description, eventPeriod.start(), eventPeriod.end());
    }

    /**
     * Parses and validates the start and end of an event period.
     *
     * @param eventPeriodText start and end date-times separated by {@code /to}.
     * @return parsed event period.
     * @throws YachiyoException if either date-time is missing or invalid, or the end is not later.
     */
    private static EventPeriod parseEventPeriod(String eventPeriodText) throws YachiyoException {
        String[] durationParts = eventPeriodText.split(EVENT_END_DELIMITER_PATTERN, 2);
        String fromText = durationParts[0].trim();
        if (fromText.isBlank()) {
            throw new YachiyoException(WARNING,
                    "This event still needs a start time. When should it begin?"
            );
        }

        if (durationParts.length < 2 || durationParts[1].trim().isBlank()) {
            throw new YachiyoException(WARNING,
                    "And when should this event come to an end?"
            );
        }

        LocalDateTime from = parseDateTime(fromText, "event start");
        LocalDateTime to = parseDateTime(durationParts[1].trim(), "event end");
        if (!to.isAfter(from)) {
            throw new YachiyoException(INVALID_OPERATION,
                    "Hmm, the event should end after it starts.");
        }

        return new EventPeriod(from, to);
    }

    /**
     * Parses a date-time field using the format accepted in user commands.
     *
     * @param dateTimeText date-time text supplied by the user.
     * @param fieldName name used to identify the field in error messages.
     * @return parsed date-time.
     * @throws YachiyoException if the supplied date-time is invalid.
     */
    private static LocalDateTime parseDateTime(String dateTimeText, String fieldName)
            throws YachiyoException {
        try {
            return LocalDateTime.parse(dateTimeText, DATE_TIME_INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new YachiyoException(WARNING,
                    String.format("Hmm, please enter the %s as d/M/yyyy HHmm, "
                            + "for example 2/12/2019 1800.", fieldName)
            );
        }
    }

    /**
     * Holds the validated start and end date-times of an event.
     *
     * @param start date and time when the event starts.
     * @param end date and time when the event ends.
     */
    private record EventPeriod(LocalDateTime start, LocalDateTime end) {
    }
}
