package laby;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import laby.command.Command;
import laby.command.CommandType;
import laby.contact.Contact;
import laby.task.Deadline;
import laby.task.Event;
import laby.task.Task;
import laby.task.Todo;

/** Converts user-entered command text into structured commands. */
public class Parser {
    public static final String FIELD_SEPARATOR = "|";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private static final String DESCRIPTION_FIELD = "description";
    private static final String FILE_FORMAT_ERROR = "invalid file format.";

    /**
     * Parses a complete input line or throws an error for invalid syntax.
     *
     * @param input User-entered command line.
     * @return Parsed command.
     * @throws LabyException If the input does not follow a supported command format.
     */
    public static Command parseInput(String input) throws LabyException {
        if (!isValidInput(input)) {
            throw new LabyException("invalid input.");
        }
        String[] parts = input.trim().split("\\s+", 2);
        CommandType commandType = CommandType.from(parts[0]);

        return switch (commandType) {
            case BYE, LIST -> parseByeOrList(parts);
            case MARK, UNMARK, DELETE_TASK, DELETE_CONTACT -> parseModifyItem(parts);
            case CONTACT -> parseContact(parts);
            case TODO, DEADLINE, EVENT -> parseTask(parts);
            case FIND -> parseFind(parts);
            default -> throw new LabyException("please input the correct commands.");
        };
    }

    /**
     * Validates a command that must not have additional arguments.
     *
     * @param parts Command words to validate.
     * @return Parsed command.
     * @throws LabyException If additional arguments are present.
     */
    private static Command parseByeOrList(String[] parts) throws LabyException {
        if (parts.length != 1) {
            throw new LabyException("please enter a valid command.");
        }

        return new Command(CommandType.from(parts[0]), 0, null, null, null, null, null);
    }

    /**
     * Parses a command that targets an item by its one-based user index.
     *
     * @param parts Command words containing the index.
     * @return Parsed item-modification command.
     * @throws LabyException If the index is missing or invalid.
     */
    private static Command parseModifyItem(String[] parts) throws LabyException {
        String invalidIndexErrorMsg = "please enter a valid index.";

        if (parts.length != 2) {
            throw new LabyException(invalidIndexErrorMsg);
        }

        try {
            String input = parts[1].trim();
            int id = Integer.parseInt(input) - 1;
            if (id < 0) {
                throw new LabyException("please enter a valid index.");
            }
            return new Command(CommandType.from(parts[0]), id, null, null, null, null, null);
        } catch (NumberFormatException e) {
            throw new LabyException(invalidIndexErrorMsg);
        }
    }

    /**
     * Parses an add-contact command containing a name, phone number, and email address.
     *
     * @param parts Command words containing the contact details.
     * @return Parsed add-contact command.
     * @throws LabyException If a marker or contact field is missing.
     */
    private static Command parseContact(String[] parts) throws LabyException {
        if (parts.length != 2) {
            throw new LabyException(getErrorMessage("name"));
        }

        String input = parts[1];
        String phoneIndicator = "/p";
        int phoneIndex = getIndexOfField(input, phoneIndicator, 0, "phone number");

        String emailIndicator = "/e";
        int emailIndex = getIndexOfField(input, emailIndicator,
                phoneIndex + phoneIndicator.length(), "email");

        String name = parseField(input, 0, phoneIndex, "name");
        String phone = parseField(input, phoneIndex + phoneIndicator.length(), emailIndex,
                "phone number");
        String email = parseField(input, emailIndex + emailIndicator.length(), input.length(),
                "email");

        return new Command(CommandType.CONTACT, 0, name, phone, email, null, null);
    }

    /**
     * Parses a task-creation command and dispatches it to the task-specific parser.
     *
     * @param parts Command words containing the task details.
     * @return Parsed task-creation command.
     * @throws LabyException If the task details are missing or malformed.
     */
    private static Command parseTask(String[] parts) throws LabyException {
        if (parts.length != 2) {
            throw new LabyException(getErrorMessage(DESCRIPTION_FIELD));
        }

        CommandType commandType = CommandType.from(parts[0]);
        return switch (commandType) {
            case TODO -> parseTodo(parts);
            case DEADLINE -> parseDeadline(parts);
            case EVENT -> parseEvent(parts);
            default -> throw new LabyException("please input the correct commands.");
        };
    }

    /**
     * Parses a todo command and validates its description.
     *
     * @param parts Command words containing the task description.
     * @return Parsed todo command.
     * @throws LabyException If the task description is missing.
     */
    private static Command parseTodo(String[] parts) throws LabyException {
        String input = parts[1];
        String description = parseField(input, 0, input.length(), DESCRIPTION_FIELD);

        return new Command(CommandType.TODO, 0, description, null, null, null, null);
    }

    /**
     * Parses a deadline command containing a description and deadline time.
     *
     * @param parts Command words containing the task description and deadline.
     * @return Parsed deadline command.
     * @throws LabyException If the description, marker, or deadline is invalid.
     */
    private static Command parseDeadline(String[] parts) throws LabyException {
        String input = parts[1];
        String deadlineIndicator = "/by";
        int deadlineIndex = getIndexOfField(input, deadlineIndicator, 0, "deadline");
        int deadlineBeginIndex = deadlineIndex + deadlineIndicator.length();

        String description = parseField(input, 0, deadlineIndex, DESCRIPTION_FIELD);

        LocalDateTime deadline = parseDateTimeField(input, deadlineBeginIndex, input.length(), "deadline");

        return new Command(CommandType.DEADLINE, 0, description, null, null, deadline, null);
    }

    /**
     * Parses an event command containing a description, start time, and end time.
     *
     * @param parts Command words containing the task description and event times.
     * @return Parsed event command.
     * @throws LabyException If the description, markers, or event times are invalid.
     */
    private static Command parseEvent(String[] parts) throws LabyException {
        String input = parts[1];
        String startIndicator = "/from";
        int startIndex = getIndexOfField(input, startIndicator, 0, "starting time");
        int startBeginIndex = startIndex + startIndicator.length();

        String endIndicator = "/to";
        int endIndex = getIndexOfField(input, endIndicator, startBeginIndex, "ending time");
        int endBeginIndex = endIndex + endIndicator.length();

        String description = parseField(input, 0, startIndex, DESCRIPTION_FIELD);

        LocalDateTime startTime = parseDateTimeField(input, startBeginIndex, endIndex, "starting time");
        LocalDateTime endTime = parseDateTimeField(input, endBeginIndex, input.length(), "ending time");

        validateEventTime(startTime, endTime);

        return new Command(CommandType.EVENT, 0, description, null, null, startTime, endTime);
    }

    /**
     * Parses a find command and validates its search text.
     *
     * @param parts Command words containing the search text.
     * @return Parsed find command.
     * @throws LabyException If the search text is missing.
     */
    private static Command parseFind(String[] parts) throws LabyException {
        if (parts.length != 2 || parts[1].trim().isEmpty()) {
            throw new LabyException(getErrorMessage("search input"));
        }

        String description = parts[1].trim();
        return new Command(CommandType.FIND, 0, description, null, null, null, null);
    }

    /**
     * Parses a date-time using the application's standard date-time format.
     *
     * @param input Date-time text to parse.
     * @return Parsed date-time value.
     * @throws LabyException If the input is empty or has an invalid format.
     */
    private static LocalDateTime parseDateTime(String input) throws LabyException {
        if (input.isEmpty()) {
            throw new LabyException(getErrorMessage("time"));
        }
        try {
            return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new LabyException("time format must be yyyy-MM-dd HH:mm.");
        }
    }

    /**
     * Extracts and parses a non-empty date-time field from a command string.
     *
     * @param input Input containing the date-time field.
     * @param startIndex Inclusive start index of the field.
     * @param endIndex Exclusive end index of the field.
     * @param fieldType Name used in the missing-field error message.
     * @return Parsed date and time.
     * @throws LabyException If the field is empty, malformed, or its indexes are invalid.
     */
    private static LocalDateTime parseDateTimeField(String input, int startIndex,
                                                     int endIndex, String fieldType)
            throws LabyException {
        String timeText = parseField(input, startIndex, endIndex, fieldType);
        return parseDateTime(timeText);
    }

    /**
     * Extracts a non-empty field from a command string.
     *
     * @param input Input containing the field.
     * @param startIndex Inclusive start index of the field.
     * @param endIndex Exclusive end index of the field.
     * @param fieldType Name used in the missing-field error message.
     * @return Trimmed field value.
     * @throws LabyException If the field is empty or its indexes are invalid.
     */
    private static String parseField(String input, int startIndex, int endIndex,
                                     String fieldType) throws LabyException {
        try {
            String field = input.substring(startIndex, endIndex).trim();
            if (field.isEmpty()) {
                throw new LabyException(getErrorMessage(fieldType));
            }
            return field;
        } catch (IndexOutOfBoundsException e) {
            throw new LabyException(getErrorMessage(fieldType));
        }
    }

    /**
     * Finds a required field marker in a command input.
     *
     * @param input Input containing the marker.
     * @param target Marker to find.
     * @param startIndex Index from which to start searching.
     * @param field Field name used in the missing-field error message.
     * @return Index of the marker.
     * @throws LabyException If the marker cannot be found.
     */
    private static int getIndexOfField(String input, String target, int startIndex, String field)
            throws LabyException {
        int index = input.indexOf(target, startIndex);
        if (index < 0) {
            throw new LabyException(getErrorMessage(field));
        }

        return index;
    }

    /**
     * Creates the standard missing-field error message.
     *
     * @param missingField Name of the missing field.
     * @return Formatted missing-field error message.
     */
    private static String getErrorMessage(String missingField) {
        return missingField + " cannot be empty.";
    }

    /**
     * Parses one serialized task record from the application's data file.
     *
     * @param input Serialized task record.
     * @return Task represented by the record.
     * @throws LabyException If the record does not follow the storage format.
     */
    public static Task parseTaskFromFile(String input) throws LabyException {
        try {
            String[] parts = input.trim().split(Pattern.quote(FIELD_SEPARATOR));
            if (!parts[1].equals("0") && !parts[1].equals("1")) {
                throw new LabyException(FILE_FORMAT_ERROR);
            }

            final String todoSymbol = "T";
            final String deadlineSymbol = "D";
            final String eventSymbol = "E";
            final String doneSymbol = "1";

            boolean isTaskDone = parts[1].equals(doneSymbol);

            return switch (parts[0]) {
                case todoSymbol -> requireTaskFields(parts, 3, new Todo(parts[2], isTaskDone));
                case deadlineSymbol -> requireTaskFields(parts, 4, new Deadline(parts[2],
                        LocalDateTime.parse(parts[3], DATE_TIME_FORMATTER), isTaskDone));
                case eventSymbol -> requireTaskFields(parts, 5, new Event(parts[2],
                            LocalDateTime.parse(parts[3], DATE_TIME_FORMATTER),
                            LocalDateTime.parse(parts[4], DATE_TIME_FORMATTER), isTaskDone));
                default -> throw new LabyException(FILE_FORMAT_ERROR);
            };
        } catch (IndexOutOfBoundsException | DateTimeParseException e) {
            throw new LabyException(FILE_FORMAT_ERROR);
        }
    }

    /**
     * Validates the exact field count of a serialized task record.
     *
     * @param parts Split task record fields.
     * @param expectedFieldCount Required number of fields.
     * @param task Parsed task to return when the count is valid.
     * @return The supplied task.
     * @throws LabyException If the record contains extra or missing fields.
     */
    private static Task requireTaskFields(String[] parts, int expectedFieldCount, Task task)
            throws LabyException {
        if (parts.length != expectedFieldCount) {
            throw new LabyException(FILE_FORMAT_ERROR);
        }
        return task;
    }

    /**
     * Validates that an event ends strictly after it starts.
     *
     * @param startTime Event starting date and time.
     * @param endTime Event ending date and time.
     * @throws LabyException If the ending time is not after the starting time.
     */
    private static void validateEventTime(LocalDateTime startTime, LocalDateTime endTime)
            throws LabyException {
        if (!startTime.isBefore(endTime)) {
            throw new LabyException("starting time must be before ending time.");
        }
    }

    /**
     * Parses one serialized contact record from the application's data file.
     *
     * @param input Serialized contact record.
     * @return Contact represented by the record.
     * @throws LabyException If the record does not follow the contact storage format.
     */
    public static Contact parseContactFromFile(String input) throws LabyException {
        try {
            String[] parts = input.trim().split(Pattern.quote(FIELD_SEPARATOR), -1);
            if (parts.length != 4 || !parts[0].equals("C")) {
                throw new LabyException(FILE_FORMAT_ERROR);
            }

            return new Contact(parts[1], parts[2], parts[3]);
        } catch (IndexOutOfBoundsException e) {
            throw new LabyException(FILE_FORMAT_ERROR);
        }
    }

    /**
     * Checks whether a input is valid.
     *
     * @param field Field value to validate.
     * @return Whether the field is non-empty and contains no record delimiters.
     */
    public static boolean isValidInput(String field) {
        return field != null && !field.trim().isEmpty()
                && !field.contains(FIELD_SEPARATOR);
    }
}
