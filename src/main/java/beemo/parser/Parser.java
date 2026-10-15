package beemo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import beemo.BeemoException;
import beemo.command.AddCommand;
import beemo.command.Command;
import beemo.command.DeleteCommand;
import beemo.command.ExitCommand;
import beemo.command.FindCommand;
import beemo.command.HelpCommand;
import beemo.command.ListCommand;
import beemo.command.MarkCommand;
import beemo.command.UnmarkCommand;
import beemo.task.Deadline;
import beemo.task.Event;
import beemo.task.Task;
import beemo.task.Todo;

/**
 * Interprets user commands and converts their arguments into application objects.
 */
public class Parser {
    private static final String TODO_KEYWORD = "todo";
    private static final String DEADLINE_KEYWORD = "deadline";
    private static final String EVENT_KEYWORD = "event";
    private static final String BY_DELIMITER = "/by ";
    private static final String FROM_DELIMITER = "/from ";
    private static final String TO_DELIMITER = "/to ";
    private static final List<DateTimeFormatter> EVENT_TIME_FORMATTERS = List.of(
            createTimeFormatter("h:mma"),
            createTimeFormatter("ha"),
            createTimeFormatter("h:mm a"),
            createTimeFormatter("h a"),
            DateTimeFormatter.ofPattern("H:mm"));

    /**
     * Prevents construction of this utility class.
     */
    private Parser() {
    }

    /**
     * Converts a line of user input into an executable command.
     *
     * @param command Full command entered by the user.
     * @return Executable command represented by the input.
     * @throws BeemoException If the command is unrecognized or malformed.
     */
    public static Command parse(String command) throws BeemoException {
        if (command == null || command.isBlank()) {
            throw new BeemoException(
                    "OOPS... I don't know what that means ╥‸╥ "
                            + "Try 'help' to see the available commands.");
        }

        String normalizedCommand = command.strip().replaceAll("\\s+", " ");
        CommandType commandType = CommandType.from(normalizedCommand);
        switch (commandType) {
            case BYE:
                return new ExitCommand();
            case HELP:
                return new HelpCommand();
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(parseTaskNumber(normalizedCommand, commandType.getKeyword()));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(normalizedCommand, commandType.getKeyword()));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(normalizedCommand, commandType.getKeyword()));
            case FIND:
                return new FindCommand(parseKeyword(normalizedCommand, commandType.getKeyword()));
            case TODO:
                // Fallthrough
            case DEADLINE:
                // Fallthrough
            case EVENT:
                return new AddCommand(parseTask(normalizedCommand));
            case UNKNOWN:
                // Fallthrough
            default:
                throw new BeemoException(
                        "OOPS... I don't know what that means ╥‸╥ "
                                + "Try 'help' to see the available commands.");
        }
    }

    /**
     * Extracts and validates the keyword supplied to a find command.
     *
     * @param command Full command entered by the user.
     * @param commandKeyword Keyword that identifies the command.
     * @return Search keyword supplied by the user.
     * @throws BeemoException If the search keyword is missing.
     */
    private static String parseKeyword(String command, String commandKeyword)
            throws BeemoException {
        String keyword = command.substring(commandKeyword.length()).trim();
        if (keyword.isEmpty()) {
            throw new BeemoException("OOPS... Please provide a keyword after 'find'.");
        }
        return keyword;
    }

    /**
     * Extracts and validates the task number supplied to a task command.
     *
     * @param command Full command entered by the user.
     * @param keyword Keyword that precedes the task number.
     * @return Parsed task number.
     * @throws BeemoException If the task number is missing or is not an integer.
     */
    private static int parseTaskNumber(String command, String keyword) throws BeemoException {
        String numberText = command.substring(keyword.length()).trim();
        if (numberText.isEmpty()) {
            throw new BeemoException(
                    "OOPS... Please provide a task number after '" + keyword + "'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new BeemoException(
                    "OOPS... '" + numberText + "' is not a valid task number. ╥‸╥ "
                            + "Please enter a number shown in your task list.");
        }
        return taskNumber;
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param command Full task command entered by the user.
     * @return Task represented by the command.
     * @throws BeemoException If the command contains invalid or missing task details.
     */
    private static Task parseTask(String command) throws BeemoException {
        assert command.startsWith("todo")
                || command.startsWith("deadline")
                || command.startsWith("event") : "Command must describe a task";
        if (command.equals("todo") || command.startsWith("todo ")) {
            return parseTodo(command);
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return parseDeadline(command);
        }
        return parseEvent(command);
    }

    /**
     * Creates a todo from its command.
     *
     * @param command Full todo command entered by the user.
     * @return Todo represented by the command.
     * @throws BeemoException If the description is empty.
     */
    private static Task parseTodo(String command) throws BeemoException {
        String description = command.substring(TODO_KEYWORD.length()).trim();
        if (description.isEmpty()) {
            throw new BeemoException(
                    "OOPS... The description of a todo cannot be empty. ╥‸╥ "
                            + "Add a description after 'todo'.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from its command and parses its date.
     *
     * @param command Full deadline command entered by the user.
     * @return Deadline represented by the command.
     * @throws BeemoException If the description or date is missing, or the date is invalid.
     */
    private static Task parseDeadline(String command) throws BeemoException {
        String details = command.substring(DEADLINE_KEYWORD.length()).trim();
        int byIndex = details.indexOf(BY_DELIMITER);
        if (byIndex < 0) {
            throw new BeemoException(
                    "OOPS... A deadline needs a '/by' date or time. ╥‸╥ "
                            + "Use: deadline DESCRIPTION /by yyyy-MM-dd.");
        }
        String description = details.substring(0, byIndex).trim();
        String byText = details.substring(byIndex + BY_DELIMITER.length()).trim();
        if (description.isEmpty()) {
            throw new BeemoException(
                    "OOPS... The description of a deadline cannot be empty. ╥‸╥ "
                            + "Add a description before '/by'.");
        }
        if (byText.isEmpty()) {
            throw new BeemoException(
                    "OOPS... The '/by' date or time cannot be empty. ╥‸╥ "
                            + "Add a date after '/by' using yyyy-MM-dd.");
        }
        try {
            return new Deadline(description, LocalDate.parse(byText));
        } catch (DateTimeParseException e) {
            throw new BeemoException(
                    "OOPS... Deadline dates must use the yyyy-MM-dd format. ╥‸╥ "
                            + "For example, use 2026-09-30.");
        }
    }

    /**
     * Creates an event from its command.
     *
     * @param command Full event command entered by the user.
     * @return Event represented by the command.
     * @throws BeemoException If the description, start time, or end time is missing.
     */
    private static Task parseEvent(String command) throws BeemoException {
        String details = command.substring(EVENT_KEYWORD.length()).trim();
        int fromIndex = details.indexOf(FROM_DELIMITER);
        int toIndex = fromIndex < 0
                ? -1
                : details.indexOf(TO_DELIMITER, fromIndex + FROM_DELIMITER.length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new BeemoException(
                    "OOPS... An event needs both '/from' and '/to' times. ╥‸╥ "
                            + "Use: event DESCRIPTION /from START /to END.");
        }
        boolean hasDuplicateFrom = details.indexOf(
                FROM_DELIMITER, fromIndex + FROM_DELIMITER.length()) >= 0;
        boolean hasDuplicateTo = details.indexOf(
                TO_DELIMITER, toIndex + TO_DELIMITER.length()) >= 0;
        if (hasDuplicateFrom || hasDuplicateTo) {
            throw new BeemoException(
                    "OOPS... An event must contain exactly one '/from' and one '/to'. ╥‸╥ "
                            + "Remove the duplicate time delimiter.");
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + FROM_DELIMITER.length(), toIndex).trim();
        String to = details.substring(toIndex + TO_DELIMITER.length()).trim();
        if (description.isEmpty()) {
            throw new BeemoException(
                    "OOPS... The description of an event cannot be empty. ╥‸╥ "
                            + "Add a description before '/from'.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new BeemoException(
                    "OOPS... Event start and end times cannot be empty. ╥‸╥ "
                            + "Add values after both '/from' and '/to'.");
        }
        validateEventOrder(from, to);
        return new Event(description, from, to);
    }

    /**
     * Validates the format and order of an event's start and end values.
     *
     * @param startText Event start supplied by the user.
     * @param endText Event end supplied by the user.
     * @throws BeemoException If the values use an invalid or mismatched format, or the range is invalid.
     */
    private static void validateEventOrder(String startText, String endText)
            throws BeemoException {
        try {
            LocalDate startDate = LocalDate.parse(startText);
            LocalDate endDate = LocalDate.parse(endText);
            if (endDate.isBefore(startDate)) {
                throw new BeemoException(
                        "OOPS... An event cannot end before it starts. ╥‸╥ "
                                + "Enter an '/to' date later than or equal to the '/from' date.");
            }
            return;
        } catch (DateTimeParseException e) {
            // Values that are not ISO dates may still be comparable date-times or clock times.
        }

        Optional<LocalDateTime> startDateTime = parseEventDateTime(startText);
        Optional<LocalDateTime> endDateTime = parseEventDateTime(endText);
        if (startDateTime.isPresent() && endDateTime.isPresent()) {
            if (!endDateTime.get().isAfter(startDateTime.get())) {
                throw new BeemoException(
                        "OOPS... An event must end after it starts. ╥‸╥ "
                                + "Enter an '/to' date and time later than '/from'.");
            }
            return;
        }

        Optional<LocalTime> startTime = parseEventTime(startText);
        Optional<LocalTime> endTime = parseEventTime(endText);
        if (startTime.isPresent() && endTime.isPresent()) {
            if (endTime.get().equals(startTime.get())) {
                throw new BeemoException(
                        "OOPS... An event must end after it starts. ╥‸╥ "
                                + "Enter an '/to' time later than the '/from' time.");
            }
            return;
        }

        throw new BeemoException(
                "OOPS... Event dates and times are invalid. ╥‸╥ "
                        + "Use yyyy-MM-dd, a clock time such as 2pm, or yyyy-MM-dd 2pm.");
    }

    /**
     * Parses an ISO date followed by a supported clock time.
     *
     * @param dateTimeText Possible event date and time.
     * @return Parsed date-time, or an empty result when the text is not recognized.
     */
    private static Optional<LocalDateTime> parseEventDateTime(String dateTimeText) {
        int separatorIndex = dateTimeText.indexOf(' ');
        if (separatorIndex < 0) {
            return Optional.empty();
        }

        try {
            LocalDate date = LocalDate.parse(dateTimeText.substring(0, separatorIndex));
            String timeText = dateTimeText.substring(separatorIndex + 1);
            return parseEventTime(timeText).map(date::atTime);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    /**
     * Parses a supported clock time.
     *
     * @param timeText Possible clock time.
     * @return Parsed time, or an empty result when the text is not a recognized clock time.
     */
    private static Optional<LocalTime> parseEventTime(String timeText) {
        for (DateTimeFormatter formatter : EVENT_TIME_FORMATTERS) {
            try {
                return Optional.of(LocalTime.parse(timeText, formatter));
            } catch (DateTimeParseException e) {
                // Try the next supported clock-time format.
            }
        }
        return Optional.empty();
    }

    /**
     * Creates a case-insensitive formatter for a clock-time pattern.
     *
     * @param pattern Date-time pattern to use.
     * @return Case-insensitive formatter using the English locale.
     */
    private static DateTimeFormatter createTimeFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern(pattern)
                .toFormatter(Locale.ENGLISH);
    }
}
