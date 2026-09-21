package melodie.command;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

import melodie.MelodieException;
import melodie.task.Deadline;
import melodie.task.Event;
import melodie.task.Task;
import melodie.task.Todo;

/**
 * Interprets user commands and converts their arguments into task data.
 */
public class Parser {
    private static final String DATE_TIME_FORMATS =
            "d/M/yyyy HHmm, today HHmm, tomorrow HHmm, or <weekday> HHmm";
    private static final String DATE_TIME_ERROR_MESSAGE =
            "Please enter a valid date and time :(\n"
                    + "    Formats: " + DATE_TIME_FORMATS + "\n"
                    + "    Examples: 2/12/2019 1800, tomorrow 0900, Mon 1400";
    private static final String UPDATE_FORMAT_ERROR_MESSAGE =
            "Please enter a valid update command :(\n"
                    + "    Format: update <task number> "
                    + "</description|/by|/from|/to> <new value>";
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter INPUT_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    private final Clock clock;

    /**
     * Creates a parser that resolves natural dates using the system date.
     */
    public Parser() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a parser that resolves natural dates using the specified clock.
     *
     * @param clock Clock used to determine the current date.
     */
    Parser(Clock clock) {
        assert clock != null : "Parser clock must not be null";
        this.clock = clock;
    }

    /**
     * Parses raw user input into a command and its arguments.
     *
     * @param input Raw command entered by the user.
     * @return Parsed command and arguments.
     * @throws MelodieException If the command word is not recognised.
     */
    public ParsedCommand parse(String input) throws MelodieException {
        String[] parts = input.trim().split(" ", 2);
        Command command = Command.from(parts[0]);
        String arguments = parts.length > 1 ? parts[1] : "";
        return new ParsedCommand(command, arguments);
    }

    /**
     * Converts the arguments of a task-creation command into the matching task type.
     *
     * @param parsedCommand Task-creation command and its arguments.
     * @return Task created from the command arguments.
     * @throws MelodieException If the command does not create a task or its arguments are invalid.
     */
    public Task parseTask(ParsedCommand parsedCommand) throws MelodieException {
        try {
            switch (parsedCommand.getCommand()) {
                case TODO:
                    return this.parseTodo(parsedCommand.getArguments());
                case DEADLINE:
                    return this.parseDeadline(parsedCommand.getArguments());
                case EVENT:
                    return this.parseEvent(parsedCommand.getArguments());
                default:
                    throw new MelodieException("This command does not create a task :(");
            }
        } catch (DateTimeParseException e) {
            throw new MelodieException(DATE_TIME_ERROR_MESSAGE);
        }
    }

    /**
     * Converts a user-provided task number into a zero-based list index.
     *
     * @param arguments Task number entered by the user.
     * @return Zero-based index of the specified task.
     * @throws MelodieException If the task number is not a valid integer.
     */
    public int parseTaskIndex(String arguments) throws MelodieException {
        try {
            return Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            throw new MelodieException("Please enter a valid task number :(");
        }
    }

    /**
     * Parses and validates the keyword of a find command.
     *
     * @param arguments Arguments supplied with the find command.
     * @return Search keyword without surrounding whitespace.
     * @throws MelodieException If the search keyword is blank.
     */
    public String parseFindKeyword(String arguments) throws MelodieException {
        String keyword = arguments.trim();
        if (keyword.isBlank()) {
            throw new MelodieException("Please enter a keyword to search for :(\n"
                    + "    Format: find <keyword>");
        }
        return keyword;
    }

    /**
     * Parses the task number, field, and replacement value of an update command.
     *
     * @param arguments Arguments supplied with the update command.
     * @return Parsed update data.
     * @throws MelodieException If any required update argument is missing or invalid.
     */
    public ParsedUpdate parseUpdate(String arguments) throws MelodieException {
        String[] indexAndDetails = arguments.trim().split("\\s+", 2);
        if (indexAndDetails.length != 2) {
            throw new MelodieException(UPDATE_FORMAT_ERROR_MESSAGE);
        }

        int taskIndex = this.parseTaskIndex(indexAndDetails[0]);
        String[] fieldAndValue = indexAndDetails[1].trim().split("\\s+", 2);
        if (fieldAndValue.length != 2 || fieldAndValue[1].isBlank()) {
            throw new MelodieException(UPDATE_FORMAT_ERROR_MESSAGE);
        }

        try {
            ParsedUpdate.Field field = ParsedUpdate.Field.from(fieldAndValue[0]);
            return new ParsedUpdate(taskIndex, field, fieldAndValue[1].trim());
        } catch (IllegalArgumentException e) {
            throw new MelodieException(UPDATE_FORMAT_ERROR_MESSAGE);
        }
    }

    /**
     * Creates an updated copy of a task while preserving its unmodified fields.
     *
     * @param task Existing task.
     * @param update Parsed update data.
     * @return Updated task with the same completion status as the existing task.
     * @throws MelodieException If the selected field is invalid for the task or its value is invalid.
     */
    public Task parseUpdatedTask(Task task, ParsedUpdate update) throws MelodieException {
        assert task != null : "Task to update must not be null";
        assert update != null : "Parsed update must not be null";

        try {
            Task updatedTask = switch (update.getField()) {
                case DESCRIPTION -> this.updateDescription(task, update.getValue());
                case DUE_DATE_TIME -> this.updateDeadlineDateTime(task, update.getValue());
                case START_DATE_TIME -> this.updateEventStartDateTime(task, update.getValue());
                case END_DATE_TIME -> this.updateEventEndDateTime(task, update.getValue());
            };
            return preserveCompletionStatus(task, updatedTask);
        } catch (DateTimeParseException e) {
            throw new MelodieException(DATE_TIME_ERROR_MESSAGE);
        }
    }

    /**
     * Creates a todo task from its command arguments.
     *
     * @param taskDescription Description of the todo task.
     * @return Todo task with the specified description.
     * @throws MelodieException If the description is blank.
     */
    private Todo parseTodo(String taskDescription) throws MelodieException {
        if (taskDescription.isBlank()) {
            throw new MelodieException("You can't leave the description of a todo empty :(");
        }
        return new Todo(taskDescription);
    }

    /**
     * Creates a deadline task from its description and due date arguments.
     *
     * @param taskDescription Deadline description and due date arguments.
     * @return Deadline task created from the arguments.
     * @throws MelodieException If the description or due date is missing.
     */
    private Deadline parseDeadline(String taskDescription) throws MelodieException {
        String[] deadlineParts = taskDescription.split("/by ", 2);
        if (deadlineParts.length != 2
                || deadlineParts[0].isBlank()
                || deadlineParts[1].isBlank()) {
            throw new MelodieException("Please enter a valid task description, due date, and time :(\n"
                    + "    Format: deadline <task description> /by <date/time>\n"
                    + "    Date/time: " + DATE_TIME_FORMATS + "\n"
                    + "    Example: deadline return book /by 2/12/2019 1800");
        }

        String description = deadlineParts[0].trim();
        String dueDateTimeString = deadlineParts[1].trim();
        LocalDateTime dueDateTime = this.parseDateTime(dueDateTimeString);
        return new Deadline(description, dueDateTime);
    }

    /**
     * Creates an event task from its description, start date, and end date arguments.
     *
     * @param taskDescription Event description, start date, and end date arguments.
     * @return Event task created from the arguments.
     * @throws MelodieException If an argument is missing or the event ends before it starts.
     */
    private Event parseEvent(String taskDescription) throws MelodieException {
        String[] fromParts = taskDescription.split("/from ", 2);
        if (fromParts.length != 2
                || fromParts[0].isBlank()) {
            throw new MelodieException(
                    "Please enter a valid task description, start date and time, and end date and time :(\n"
                    + "    Format: event <description> /from <date/time> /to <date/time>\n"
                    + "    Date/time: " + DATE_TIME_FORMATS + "\n"
                    + "    Example: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }

        String[] toParts = fromParts[1].split("/to ", 2);
        if (toParts.length != 2
                || toParts[0].isBlank()
                || toParts[1].isBlank()) {
            throw new MelodieException(
                    "Please enter a valid task description, start date and time, and end date and time :(\n"
                    + "    Format: event <description> /from <date/time> /to <date/time>\n"
                    + "    Date/time: " + DATE_TIME_FORMATS + "\n"
                    + "    Example: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }

        String description = fromParts[0].trim();
        String startDateTimeString = toParts[0].trim();
        String endDateTimeString = toParts[1].trim();
        LocalDateTime startDateTime = this.parseDateTime(startDateTimeString);
        LocalDateTime endDateTime = this.parseDateTime(endDateTimeString);
        return createEvent(description, startDateTime, endDateTime);
    }

    /**
     * Creates a copy of a task with a replacement description.
     *
     * @param task Existing task.
     * @param description Replacement description.
     * @return Updated task of the same type.
     */
    private Task updateDescription(Task task, String description) {
        if (task instanceof Deadline deadline) {
            return new Deadline(description, deadline.getDueDateTime());
        }
        if (task instanceof Event event) {
            return new Event(
                    description,
                    event.getStartDateTime(),
                    event.getEndDateTime());
        }
        if (task instanceof Todo) {
            return new Todo(description);
        }
        return new Task(description);
    }

    /**
     * Creates a deadline with a replacement due date and time.
     *
     * @param task Existing task, which must be a deadline.
     * @param dateTimeText Replacement date and time.
     * @return Updated deadline.
     * @throws MelodieException If the task is not a deadline.
     */
    private Task updateDeadlineDateTime(Task task, String dateTimeText)
            throws MelodieException {
        if (!(task instanceof Deadline deadline)) {
            throw createInvalidUpdateFieldException(ParsedUpdate.Field.DUE_DATE_TIME);
        }
        return new Deadline(
                deadline.getDescription(),
                this.parseDateTime(dateTimeText));
    }

    /**
     * Creates an event with a replacement start date and time.
     *
     * @param task Existing task, which must be an event.
     * @param dateTimeText Replacement start date and time.
     * @return Updated event.
     * @throws MelodieException If the task is not an event or the resulting range is invalid.
     */
    private Task updateEventStartDateTime(Task task, String dateTimeText)
            throws MelodieException {
        if (!(task instanceof Event event)) {
            throw createInvalidUpdateFieldException(ParsedUpdate.Field.START_DATE_TIME);
        }
        return createEvent(
                event.getDescription(),
                this.parseDateTime(dateTimeText),
                event.getEndDateTime());
    }

    /**
     * Creates an event with a replacement end date and time.
     *
     * @param task Existing task, which must be an event.
     * @param dateTimeText Replacement end date and time.
     * @return Updated event.
     * @throws MelodieException If the task is not an event or the resulting range is invalid.
     */
    private Task updateEventEndDateTime(Task task, String dateTimeText)
            throws MelodieException {
        if (!(task instanceof Event event)) {
            throw createInvalidUpdateFieldException(ParsedUpdate.Field.END_DATE_TIME);
        }
        return createEvent(
                event.getDescription(),
                event.getStartDateTime(),
                this.parseDateTime(dateTimeText));
    }

    /**
     * Creates an event after validating its date range.
     *
     * @param description Event description.
     * @param startDateTime Event start date and time.
     * @param endDateTime Event end date and time.
     * @return Event containing the supplied details.
     * @throws MelodieException If the event ends before it starts.
     */
    private static Event createEvent(
            String description,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) throws MelodieException {
        if (endDateTime.isBefore(startDateTime)) {
            throw new MelodieException("The event cannot end before it starts :(");
        }
        return new Event(description, startDateTime, endDateTime);
    }

    /**
     * Creates an error for a field that does not belong to the selected task type.
     *
     * @param field Inapplicable update field.
     * @return User-facing update error.
     */
    private static MelodieException createInvalidUpdateFieldException(
            ParsedUpdate.Field field) {
        return new MelodieException(
                "The " + field.getCommandToken()
                        + " field cannot be updated for this task type :(");
    }

    /**
     * Copies the completion status from the original task to its updated copy.
     *
     * @param originalTask Existing task.
     * @param updatedTask Updated copy of the task.
     * @return Updated task with the original completion status.
     */
    private static Task preserveCompletionStatus(Task originalTask, Task updatedTask) {
        if (originalTask.isCompleted()) {
            updatedTask.mark();
        }
        return updatedTask;
    }

    /**
     * Parses either the original numeric date-time format or a supported natural date.
     *
     * @param dateTimeText Date and time supplied in a task command.
     * @return Parsed date and time.
     * @throws DateTimeParseException If the date or time is invalid.
     */
    private LocalDateTime parseDateTime(String dateTimeText) throws DateTimeParseException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return this.parseNaturalDateTime(dateTimeText);
        }
    }

    /**
     * Resolves a relative date word and combines it with a 24-hour time.
     *
     * @param dateTimeText Natural date and time supplied in a task command.
     * @return Resolved date and time.
     * @throws DateTimeParseException If the natural date or time is invalid.
     */
    private LocalDateTime parseNaturalDateTime(String dateTimeText)
            throws DateTimeParseException {
        String[] parts = dateTimeText.trim().split("\\s+");
        if (parts.length != 2) {
            throw createDateTimeParseException(dateTimeText);
        }

        LocalDate date = this.parseNaturalDate(parts[0], dateTimeText);
        LocalTime time = LocalTime.parse(parts[1], INPUT_TIME_FORMATTER);
        return LocalDateTime.of(date, time);
    }

    /**
     * Resolves today, tomorrow, or the next occurrence of an English weekday.
     *
     * @param dateText Natural date portion of the input.
     * @param completeInput Complete input used when reporting parsing failures.
     * @return Resolved calendar date.
     * @throws DateTimeParseException If the date word is not supported.
     */
    private LocalDate parseNaturalDate(String dateText, String completeInput)
            throws DateTimeParseException {
        LocalDate today = LocalDate.now(this.clock);
        return switch (dateText.toLowerCase(Locale.ENGLISH)) {
            case "today" -> today;
            case "tomorrow" -> today.plusDays(1);
            case "mon", "monday" -> getNextWeekday(today, DayOfWeek.MONDAY);
            case "tue", "tues", "tuesday" -> getNextWeekday(today, DayOfWeek.TUESDAY);
            case "wed", "wednesday" -> getNextWeekday(today, DayOfWeek.WEDNESDAY);
            case "thu", "thur", "thurs", "thursday" ->
                getNextWeekday(today, DayOfWeek.THURSDAY);
            case "fri", "friday" -> getNextWeekday(today, DayOfWeek.FRIDAY);
            case "sat", "saturday" -> getNextWeekday(today, DayOfWeek.SATURDAY);
            case "sun", "sunday" -> getNextWeekday(today, DayOfWeek.SUNDAY);
            default -> throw createDateTimeParseException(completeInput);
        };
    }

    /**
     * Returns the first occurrence of a weekday strictly after the supplied date.
     *
     * @param today Date from which to search.
     * @param dayOfWeek Weekday to find.
     * @return Next matching date.
     */
    private static LocalDate getNextWeekday(LocalDate today, DayOfWeek dayOfWeek) {
        return today.with(TemporalAdjusters.next(dayOfWeek));
    }

    /**
     * Creates the common parsing exception used for unsupported natural dates.
     *
     * @param input Invalid input.
     * @return Date-time parsing exception for the input.
     */
    private static DateTimeParseException createDateTimeParseException(String input) {
        return new DateTimeParseException("Unsupported date and time format", input, 0);
    }
}
