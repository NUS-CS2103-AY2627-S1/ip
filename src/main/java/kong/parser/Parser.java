package kong.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kong.command.Command;
import kong.command.DeadlineCommand;
import kong.command.DeleteCommand;
import kong.command.EventCommand;
import kong.command.ExitCommand;
import kong.command.FindCommand;
import kong.command.ListCommand;
import kong.command.MarkCommand;
import kong.command.OnDateCommand;
import kong.command.TodoCommand;
import kong.command.UnknownCommand;
import kong.command.UnmarkCommand;
import kong.exception.KongException;

/**
 * Makes sense of user command text and turns it into structured values.
 */
public class Parser {
    private static final String ERROR_TODO_FORMAT =
            "Invalid command. A todo command needs to be in the following format: todo <description>";
    private static final String ERROR_DEADLINE_FORMAT =
            "Invalid command. A deadline command needs to be in the following format: "
                    + "deadline <description> /by <date>";
    private static final String ERROR_EVENT_FORMAT =
            "Invalid command. An event command needs to be in the following format: "
                    + "event <description> /from <date> /to <date>";
    private static final String ERROR_FIND_FORMAT =
            "Invalid command. A find command needs to be in the following format: find <keyword>";

    private static final Pattern DEADLINE_PATTERN =
            Pattern.compile("^(.*?)\\s*/by\\s+(.*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EVENT_FROM_TO_PATTERN =
            Pattern.compile("^(.*?)\\s*/from\\s+(.*?)\\s*/to\\s+(.*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EVENT_TO_FROM_PATTERN =
            Pattern.compile("^(.*?)\\s*/to\\s+(.*?)\\s*/from\\s+(.*)$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern BY_FLAG_PATTERN =
            Pattern.compile("(?i)(?:^|(?<=\\s))/by(?:$|(?=\\s))");
    private static final Pattern FROM_FLAG_PATTERN =
            Pattern.compile("(?i)(?:^|(?<=\\s))/from(?:$|(?=\\s))");
    private static final Pattern TO_FLAG_PATTERN =
            Pattern.compile("(?i)(?:^|(?<=\\s))/to(?:$|(?=\\s))");

    private static final Pattern DATE_SYNTAX_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final DateTimeFormatter STRICT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    /** Creates a parser. */
    public Parser() {
    }

    /**
     * Parses a complete input line into an executable command.
     *
     * @param input user-entered command line
     * @return command represented by the input
     * @throws KongException if a recognized command has invalid arguments
     */
    public static Command parse(String input) throws KongException {
        if (input == null || input.trim().isEmpty()) {
            throw new KongException("Please enter a command.");
        }

        String trimmedInput = input.trim();

        String[] parts = trimmedInput.split("\\s+", 2);
        CommandType commandType = CommandType.fromString(parts[0]);
        String arg = parts.length > 1 ? parts[1] : "";

        switch (commandType) {
            case TODO:
                return createTodoCommand(arg);
            case DEADLINE:
                return createDeadlineCommand(arg);
            case EVENT:
                return createEventCommand(arg);
            case LIST:
                if (!arg.isBlank()) {
                    throw new KongException("Invalid command. The list command does not take any arguments.");
                }
                return new ListCommand();
            case FIND:
                return createFindCommand(arg);
            case ON:
                return createOnDateCommand(arg);
            case MARK:
                return new MarkCommand(arg);
            case UNMARK:
                return new UnmarkCommand(arg);
            case DELETE:
                return new DeleteCommand(arg);
            case BYE:
                if (!arg.isBlank()) {
                    throw new KongException("Invalid command. The bye command does not take any arguments.");
                }
                return new ExitCommand();
            case UNKNOWN:
                return new UnknownCommand();
            default:
                assert false : "Every command type must be handled by the parser";
                return new UnknownCommand();
        }
    }

    /**
     * Validates that a task description is not blank and does not contain unsupported characters.
     *
     * @param description raw description to validate
     * @return trimmed description
     * @throws KongException if description contains forbidden characters
     */
    private static String validateDescription(String description) throws KongException {
        String trimmed = description.trim();
        if (trimmed.contains("|")) {
            throw new KongException("Task description cannot contain the '|' character.");
        }
        if (trimmed.contains("\n") || trimmed.contains("\r")) {
            throw new KongException("Task description cannot contain newline characters.");
        }
        return trimmed;
    }

    /** Creates a todo command after validating its description. */
    private static Command createTodoCommand(String arg) throws KongException {
        String trimmed = arg.trim();
        if (trimmed.isEmpty()) {
            throw new KongException(ERROR_TODO_FORMAT);
        }
        String description = validateDescription(trimmed);
        return new TodoCommand(description);
    }

    /** Creates a deadline command from its structured arguments. */
    private static Command createDeadlineCommand(String arg) throws KongException {
        DeadlineDetails deadlineDetails = parseDeadline(arg);
        return new DeadlineCommand(deadlineDetails.getDescription(), deadlineDetails.getBy());
    }

    /** Creates an event command from its structured arguments. */
    private static Command createEventCommand(String arg) throws KongException {
        EventDetails eventDetails = parseEvent(arg);
        return new EventCommand(eventDetails.getDescription(), eventDetails.getFrom(), eventDetails.getTo());
    }

    /** Creates a date-query command after validating its date argument. */
    private static Command createOnDateCommand(String arg) throws KongException {
        String trimmed = arg.trim();
        if (trimmed.isEmpty()) {
            throw new KongException("Invalid command. An on command needs to be in the following format: on <date>");
        }
        return new OnDateCommand(parseDate(trimmed));
    }

    /** Creates a find command after validating its keyword. */
    private static Command createFindCommand(String keyword) throws KongException {
        String trimmed = keyword.trim();
        if (trimmed.isEmpty()) {
            throw new KongException(ERROR_FIND_FORMAT);
        }
        return new FindCommand(trimmed);
    }

    /**
     * Extracts the description and due date from deadline arguments.
     *
     * @param arg text following the deadline command word
     * @return validated deadline details
     * @throws KongException if the arguments do not match the required format
     */
    private static DeadlineDetails parseDeadline(String arg) throws KongException {
        int byCount = countOccurrences(arg, BY_FLAG_PATTERN);
        if (byCount > 1) {
            throw new KongException("Invalid command. The /by parameter cannot be specified multiple times.");
        }
        if (byCount == 0) {
            throw new KongException(ERROR_DEADLINE_FORMAT);
        }

        Matcher matcher = DEADLINE_PATTERN.matcher(arg.trim());
        if (!matcher.matches()) {
            throw new KongException(ERROR_DEADLINE_FORMAT);
        }

        String rawDescription = matcher.group(1).trim();
        String by = matcher.group(2).trim();
        if (rawDescription.isEmpty() || by.isEmpty()) {
            throw new KongException(ERROR_DEADLINE_FORMAT);
        }

        String description = validateDescription(rawDescription);
        return new DeadlineDetails(description, parseDate(by));
    }

    /**
     * Extracts the description and date range from event arguments.
     *
     * @param arg text following the event command word
     * @return validated event details
     * @throws KongException if the arguments do not match the required format
     */
    private static EventDetails parseEvent(String arg) throws KongException {
        int fromCount = countOccurrences(arg, FROM_FLAG_PATTERN);
        if (fromCount > 1) {
            throw new KongException("Invalid command. The /from parameter cannot be specified multiple times.");
        }
        int toCount = countOccurrences(arg, TO_FLAG_PATTERN);
        if (toCount > 1) {
            throw new KongException("Invalid command. The /to parameter cannot be specified multiple times.");
        }
        if (fromCount == 0 || toCount == 0) {
            throw new KongException(ERROR_EVENT_FORMAT);
        }

        String rawDescription;
        String from;
        String to;

        Matcher matcherFromTo = EVENT_FROM_TO_PATTERN.matcher(arg.trim());
        if (matcherFromTo.matches()) {
            rawDescription = matcherFromTo.group(1).trim();
            from = matcherFromTo.group(2).trim();
            to = matcherFromTo.group(3).trim();
        } else {
            Matcher matcherToFrom = EVENT_TO_FROM_PATTERN.matcher(arg.trim());
            if (matcherToFrom.matches()) {
                rawDescription = matcherToFrom.group(1).trim();
                to = matcherToFrom.group(2).trim();
                from = matcherToFrom.group(3).trim();
            } else {
                throw new KongException(ERROR_EVENT_FORMAT);
            }
        }

        if (rawDescription.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new KongException(ERROR_EVENT_FORMAT);
        }

        String description = validateDescription(rawDescription);
        LocalDate fromDate = parseDate(from);
        LocalDate toDate = parseDate(to);
        if (fromDate.isAfter(toDate)) {
            throw new KongException("Invalid command. The event start date cannot be after the end date.");
        }

        return new EventDetails(description, fromDate, toDate);
    }

    /**
     * Counts occurrences of a regular expression pattern in the provided text.
     *
     * @param text text to scan
     * @param pattern regular expression pattern to match
     * @return match count
     */
    private static int countOccurrences(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Parses a date in ISO {@code yyyy-MM-dd} format with strict calendar validation.
     *
     * @param dateText date entered by the user
     * @return parsed date
     * @throws KongException if the text is not a valid ISO date or does not exist on the calendar
     */
    private static LocalDate parseDate(String dateText) throws KongException {
        String trimmed = dateText.trim();
        if (!DATE_SYNTAX_PATTERN.matcher(trimmed).matches()) {
            throw new KongException("Invalid date. Please use the format yyyy-MM-dd, for example 2019-10-15.");
        }
        try {
            return LocalDate.parse(trimmed, STRICT_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new KongException(String.format("Invalid date. The date '%s' does not exist on the calendar.",
                    trimmed));
        }
    }

    /**
     * Parsed description and date for a deadline command.
     */
    private static class DeadlineDetails {
        private final String description;
        private final LocalDate by;

        /** Creates a parsed deadline value. */
        public DeadlineDetails(String description, LocalDate by) {
            this.description = description;
            this.by = by;
        }

        /** @return parsed task description */
        public String getDescription() {
            return description;
        }

        /** @return parsed due date */
        public LocalDate getBy() {
            return by;
        }
    }

    /**
     * Parsed description and dates for an event command.
     */
    private static class EventDetails {
        private final String description;
        private final LocalDate from;
        private final LocalDate to;

        /** Creates a parsed event value. */
        public EventDetails(String description, LocalDate from, LocalDate to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }

        /** @return parsed task description */
        public String getDescription() {
            return description;
        }

        /** @return parsed first date */
        public LocalDate getFrom() {
            return from;
        }

        /** @return parsed last date */
        public LocalDate getTo() {
            return to;
        }
    }
}
