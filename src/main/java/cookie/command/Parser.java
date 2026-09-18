package cookie.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cookie.task.DateTimeValue;
import cookie.task.SortCriterion;
import cookie.task.SortDirection;

/** Interprets the command structure of user input for Cookie. */
public class Parser {
    /** Matches the deadline parameter when it appears as a separate token. */
    private static final Pattern DEADLINE_PARAMETER = Pattern.compile("(?<!\\S)/by(?!\\S)");

    /** Matches the event start parameter when it appears as a separate token. */
    private static final Pattern EVENT_START_PARAMETER = Pattern.compile("(?<!\\S)/from(?!\\S)");

    /** Matches the event end parameter when it appears as a separate token. */
    private static final Pattern EVENT_END_PARAMETER = Pattern.compile("(?<!\\S)/to(?!\\S)");

    /** Describes the accepted arguments for the sort command. */
    private static final String SORT_USAGE = "sort <description|date> [asc|desc]";

    /** Parses date and time values that use the ISO date format. */
    private static final DateTimeFormatter ISO_DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Parses date and time values that use slash-separated dates. */
    private static final DateTimeFormatter SLASH_DATE_TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Parses date values that use the ISO date format. */
    private static final DateTimeFormatter ISO_DATE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Parses date values that use slash-separated dates. */
    private static final DateTimeFormatter SLASH_DATE_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Parses time-only values. */
    private static final DateTimeFormatter TIME_INPUT_FORMAT =
            DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);

    /** Creates a parser for Cookie commands. */
    public Parser() {
    }

    /**
     * Parses raw user input into a command and its arguments.
     *
     * @param input The raw user input to parse.
     * @return The parsed command and its arguments.
     * @throws CookieException If the input is blank or uses an unknown command.
     */
    public ParsedCommand parse(String input) throws CookieException {
        if (input == null) {
            throw new CookieException("I couldn't understand an empty command.");
        }

        String normalizedInput = input.trim().replaceAll("\\s+", " ");
        if (normalizedInput.isBlank()) {
            throw new CookieException("I couldn't understand an empty command.");
        }

        String[] parts = normalizedInput.split("\\s+");
        String action = parts[0];
        String description = normalizedInput.substring(action.length()).trim();
        List<String> arguments = List.of(parts).subList(1, parts.length);
        return new ParsedCommand(Command.fromString(action), action, arguments, description);
    }

    /**
     * Ensures that a command has no arguments after its command word.
     *
     * @param command The parsed command to validate.
     * @throws CookieException If the command has one or more arguments.
     */
    public void requireNoArguments(ParsedCommand command) throws CookieException {
        if (command.argumentCount() > 0) {
            throw new CookieException(
                    "The " + command.action() + " command does not take any arguments.");
        }
    }

    /**
     * Ensures that a command has exactly one argument and reports its usage otherwise.
     *
     * @param command The parsed command to validate.
     * @param usage The usage message to display when validation fails.
     * @throws CookieException If the command does not have exactly one argument.
     */
    public void requireSingleArgument(ParsedCommand command, String usage) throws CookieException {
        if (command.argumentCount() != 1) {
            throw new CookieException("Usage: " + usage + ".");
        }
    }

    /**
     * Returns a task description, rejecting commands with no description.
     *
     * @param command The parsed command whose description is required.
     * @return The non-blank task description.
     * @throws CookieException If the command has no description.
     */
    public String requireDescription(ParsedCommand command) throws CookieException {
        if (command.description().isBlank()) {
            throw new CookieException(
                    "A " + command.action() + " task needs a description.");
        }
        return command.description();
    }

    /**
     * Rejects a value that would make the task file format ambiguous.
     *
     * @param value The task value to validate.
     * @return The validated value.
     * @throws CookieException If the value contains the task-file delimiter.
     */
    public String requireFileSafe(String value) throws CookieException {
        if (value.contains("|")) {
            throw new CookieException("Task details cannot contain '|'.");
        }
        if (value.chars().anyMatch(Character::isISOControl)) {
            throw new CookieException("Task details cannot contain control characters.");
        }
        return value;
    }

    /**
     * Parses a user-provided date, time, or date-time using supported formats.
     *
     * @param value The date, time, or date-time text to parse.
     * @return The parsed date, time, or date-time value.
     * @throws CookieException If the value does not use a supported format.
     */
    public DateTimeValue parseDateTime(String value) throws CookieException {
        String normalizedValue = value.trim().replaceAll("\\s+", " ");
        DateTimeFormatter[] formats = {
            ISO_DATE_TIME_INPUT_FORMAT,
            SLASH_DATE_TIME_INPUT_FORMAT
        };

        for (DateTimeFormatter format : formats) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(normalizedValue, format);
                return new DateTimeValue(dateTime.toLocalDate(), dateTime.toLocalTime());
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }

        try {
            return new DateTimeValue(parseDate(normalizedValue), null);
        } catch (CookieException exception) {
            // Try the supported time-only format.
        }

        try {
            return new DateTimeValue(null, LocalTime.parse(normalizedValue, TIME_INPUT_FORMAT));
        } catch (DateTimeParseException exception) {
            throw new CookieException(
                    "A date, time, or date and time must use yyyy-MM-dd, d/M/yyyy, HHmm, "
                            + "yyyy-MM-dd HHmm, or d/M/yyyy HHmm.");
        }
    }

    /**
     * Parses a user-provided date using one of the supported date formats.
     *
     * @param value The date text to parse.
     * @return The parsed date.
     * @throws CookieException If the value does not use a supported date format.
     */
    public LocalDate parseDate(String value) throws CookieException {
        String normalizedValue = value.trim().replaceAll("\\s+", " ");
        DateTimeFormatter[] formats = {
            ISO_DATE_INPUT_FORMAT,
            SLASH_DATE_INPUT_FORMAT
        };

        for (DateTimeFormatter format : formats) {
            try {
                return LocalDate.parse(normalizedValue, format);
            } catch (DateTimeParseException exception) {
                // Try the next supported format.
            }
        }
        throw new CookieException("A date must use yyyy-MM-dd or d/M/yyyy.");
    }

    /**
     * Parses the criterion and optional direction supplied for a sorted task view.
     *
     * @param command The parsed sort command.
     * @return The validated sort criterion and direction.
     * @throws CookieException If the sort arguments do not use the supported syntax.
     */
    public ParsedSort parseSort(ParsedCommand command) throws CookieException {
        if (command.argumentCount() < 1 || command.argumentCount() > 2) {
            throw createSortUsageException();
        }

        SortCriterion criterion = switch (command.argument(0).toLowerCase(Locale.ROOT)) {
            case "description" -> SortCriterion.DESCRIPTION;
            case "date" -> SortCriterion.DATE;
            default -> throw createSortUsageException();
        };

        SortDirection direction = SortDirection.ASCENDING;
        if (command.argumentCount() == 2) {
            direction = switch (command.argument(1).toLowerCase(Locale.ROOT)) {
                case "asc" -> SortDirection.ASCENDING;
                case "desc" -> SortDirection.DESCENDING;
                default -> throw createSortUsageException();
            };
        }
        return new ParsedSort(criterion, direction);
    }

    /** Creates the standard usage error for malformed sort commands. */
    private CookieException createSortUsageException() {
        return new CookieException("Usage: " + SORT_USAGE + ".");
    }

    /**
     * Parses the description and date or time supplied for a deadline.
     *
     * @param description The raw description and deadline value to parse.
     * @return The parsed deadline.
     * @throws CookieException If the description or deadline value is malformed.
     */
    public ParsedDeadline parseDeadline(String description) throws CookieException {
        if (countMatches(DEADLINE_PARAMETER, description) > 1) {
            throw new CookieException("A deadline must contain exactly one /by parameter.");
        }

        String[] deadlineParts = description.split("\\s+/by\\s+", 2);
        if (deadlineParts.length < 2 || deadlineParts[0].isBlank() || deadlineParts[1].isBlank()) {
            throw new CookieException(
                    "A deadline needs a description and a date and time after /by.");
        }

        String taskDescription = requireFileSafe(deadlineParts[0]);
        String deadlineValue = requireFileSafe(deadlineParts[1]);
        DateTimeValue deadlineDateTime;
        try {
            deadlineDateTime = parseDateTime(deadlineValue);
        } catch (CookieException exception) {
            throw new CookieException(
                    "A deadline date, time, or date and time must use yyyy-MM-dd, d/M/yyyy, "
                            + "HHmm, yyyy-MM-dd HHmm, or d/M/yyyy HHmm.");
        }
        return new ParsedDeadline(taskDescription, deadlineDateTime);
    }

    /**
     * Parses the description and time values supplied for an event.
     *
     * @param description The raw description and event values to parse.
     * @return The parsed event.
     * @throws CookieException If the description or event values are malformed.
     */
    public ParsedEvent parseEvent(String description) throws CookieException {
        if (countMatches(EVENT_START_PARAMETER, description) > 1
                || countMatches(EVENT_END_PARAMETER, description) > 1) {
            throw new CookieException(
                    "An event must contain exactly one /from and one /to parameter.");
        }

        String[] eventParts = description.split("\\s+/from\\s+", 2);
        if (eventParts.length < 2 || eventParts[0].isBlank()) {
            throw new CookieException(
                    "An event needs a description, a start time after /from, "
                            + "and an end time after /to.");
        }

        String[] timeParts = eventParts[1].split("\\s+/to\\s+", 2);
        if (timeParts.length < 2 || timeParts[0].isBlank() || timeParts[1].isBlank()) {
            throw new CookieException(
                    "An event needs a description, a start time after /from, "
                            + "and an end time after /to.");
        }

        String eventDescription = requireFileSafe(eventParts[0]);
        String startValue = requireFileSafe(timeParts[0]);
        String endValue = requireFileSafe(timeParts[1]);
        DateTimeValue start;
        DateTimeValue end;
        try {
            start = parseDateTime(startValue);
            end = parseDateTime(endValue);
        } catch (CookieException exception) {
            throw new CookieException(
                    "An event's start and end values must use yyyy-MM-dd, d/M/yyyy, HHmm, "
                            + "yyyy-MM-dd HHmm, or d/M/yyyy HHmm.");
        }
        validateEventPeriod(start, end);
        return new ParsedEvent(eventDescription, start, end);
    }

    /** Ensures that an event has comparable values and a strictly later end. */
    private void validateEventPeriod(DateTimeValue start, DateTimeValue end)
            throws CookieException {
        if (!start.hasSameComponentsAs(end)) {
            throw new CookieException(
                    "An event's start and end must both be dates, both be times, "
                            + "or both be dates and times.");
        }
        if (!start.isBefore(end)) {
            throw new CookieException("An event's end must be later than its start.");
        }
    }

    /** Counts occurrences of a command parameter represented by the supplied pattern. */
    private int countMatches(Pattern pattern, String value) {
        int matchCount = 0;
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            matchCount++;
        }
        return matchCount;
    }

    /**
     * Converts a one-based task number into a zero-based list index.
     *
     * @param command The parsed command containing the task number.
     * @param taskCount The number of tasks currently in the list.
     * @return The corresponding zero-based list index.
     * @throws CookieException If the task number is missing, invalid, or out of range.
     */
    public int parseTaskIndex(ParsedCommand command, int taskCount) throws CookieException {
        assert taskCount >= 0 : "A task count must not be negative";
        if (command.argumentCount() != 1) {
            throw new CookieException("Usage: " + command.action() + " <task number>.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(command.argument(0));
        } catch (NumberFormatException exception) {
            throw new CookieException("The task number must be a positive whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new CookieException("There is no task numbered " + taskNumber + ".");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "A validated task number must map to an existing zero-based index";
        return taskIndex;
    }

    /**
     * Holds the command, original action word, arguments, and description from user input.
     *
     * @param command The command resolved from the action word.
     * @param action The action word as entered by the user.
     * @param arguments The whitespace-separated arguments following the action word.
     * @param description The text following the action word, preserving its internal spaces.
     */
    public record ParsedCommand(Command command, String action, List<String> arguments,
                                String description) {
        /** Creates a parsed command with an immutable copy of its arguments. */
        public ParsedCommand {
            arguments = List.copyOf(arguments);
        }

        /**
         * Returns the number of whitespace-separated arguments.
         *
         * @return The number of arguments.
         */
        public int argumentCount() {
            return arguments.size();
        }

        /**
         * Returns the argument at the specified zero-based argument position.
         *
         * @param index The zero-based argument position.
         * @return The argument at the specified position.
         */
        public String argument(int index) {
            return arguments.get(index);
        }
    }

    /**
     * Holds the validated values parsed from a deadline command.
     *
     * @param description The validated task description.
     * @param dateTime The parsed deadline date or time.
     */
    public record ParsedDeadline(String description, DateTimeValue dateTime) {
    }

    /**
     * Holds the validated values parsed from an event command.
     *
     * @param description The validated event description.
     * @param start The parsed event start date or time.
     * @param end The parsed event end date or time.
     */
    public record ParsedEvent(String description, DateTimeValue start, DateTimeValue end) {
    }

    /**
     * Holds the validated settings for a temporary sorted task view.
     *
     * @param criterion The task property to compare.
     * @param direction The direction in which comparable values should appear.
     */
    public record ParsedSort(SortCriterion criterion, SortDirection direction) {
    }
}
