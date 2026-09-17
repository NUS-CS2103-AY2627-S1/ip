package wobble.parser;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import wobble.exceptions.WobbleException;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.Todo;

/** Interprets user command text and creates the corresponding task data. */
public class Parser {
    private static final int DEFAULT_REMINDER_DAYS = 7;
    private static final int MAX_REMINDER_DAYS = 36500;
    private static final String DATE_TIME_ERROR_HINT =
            "Check that the date exists and the time is between 00:00 and 23:59. Type help for formats.";

    /** Creates a parser for Wobble commands. */
    public Parser() {
    }

    /** Converts an add command into the appropriate task subtype. */
    public Task parseTask(String command) throws WobbleException {
        String normalizedCommand = normalizeCommand(command);
        if (normalizedCommand.isEmpty()) {
            throw new WobbleException("a task command cannot be empty.");
        }
        String lowerCaseCommand = normalizedCommand.toLowerCase(Locale.ROOT);
        if (lowerCaseCommand.equals("todo") || lowerCaseCommand.startsWith("todo ")) {
            String description = normalizedCommand.length() > 4
                    ? normalizedCommand.substring(4).trim() : "";
            validateDescription(description, "todo");
            return new Todo(description);
        }
        if (lowerCaseCommand.equals("deadline") || lowerCaseCommand.startsWith("deadline ")) {
            int byParameterCount = countToken(lowerCaseCommand, "/by");
            if (byParameterCount == 0) {
                throw new WobbleException("a deadline needs a description and a /by date.");
            }
            if (byParameterCount > 1) {
                throw new WobbleException("a deadline accepts only one /by date.");
            }
            int separator = lowerCaseCommand.indexOf(" /by ");
            if (separator < 0) {
                throw new WobbleException("a deadline needs both a description and a /by date.");
            }
            String description = separator > 9 ? normalizedCommand.substring(9, separator).trim() : "";
            String by = normalizedCommand.substring(separator + 5).trim();
            validateDescription(description, "deadline");
            if (by.isEmpty()) {
                throw new WobbleException("a deadline needs both a description and a /by date.");
            }
            try {
                LocalDateTime deadline = DateTimeParser.parseDeadline(by);
                return new Deadline(description, deadline);
            } catch (DateTimeParseException exception) {
                throw new WobbleException("the deadline date or time is invalid. " + DATE_TIME_ERROR_HINT,
                        false);
            }
        }
        if (lowerCaseCommand.equals("event") || lowerCaseCommand.startsWith("event ")) {
            int fromParameterCount = countToken(lowerCaseCommand, "/from");
            int toParameterCount = countToken(lowerCaseCommand, "/to");
            if (fromParameterCount == 0 || toParameterCount == 0) {
                throw new WobbleException("an event needs a description, a /from time, and a /to time.");
            }
            if (fromParameterCount > 1 || toParameterCount > 1) {
                throw new WobbleException("an event accepts only one /from time and one /to time.");
            }
            int fromSeparator = lowerCaseCommand.indexOf(" /from ");
            int toSeparator = lowerCaseCommand.indexOf(" /to ");
            if (fromSeparator < 0 || toSeparator < 0 || fromSeparator + 7 > toSeparator) {
                throw new WobbleException("an event needs a description, a /from time, and a /to time.");
            }
            String description = fromSeparator > 6
                    ? normalizedCommand.substring(6, fromSeparator).trim() : "";
            String from = normalizedCommand.substring(fromSeparator + 7, toSeparator).trim();
            String to = normalizedCommand.substring(toSeparator + 5).trim();
            validateDescription(description, "event");
            if (from.isEmpty() || to.isEmpty()) {
                throw new WobbleException("an event needs a description, a /from time, and a /to time.");
            }
            try {
                LocalDateTime start = DateTimeParser.parse(from);
                LocalDateTime end = DateTimeParser.parse(to);
                if (!start.isBefore(end)) {
                    throw new WobbleException("an event must end after it starts.", false);
                }
                return new Event(description, start, end);
            } catch (DateTimeParseException exception) {
                throw new WobbleException("the event date or time is invalid. " + DATE_TIME_ERROR_HINT,
                        false);
            }
        }
        throw new WobbleException("I do not recognize that command.");
    }

    /** Parses a date used by the due-on command. */
    public LocalDate parseDueDate(String command) throws WobbleException {
        String normalizedCommand = normalizeCommand(command);
        String lowerCaseCommand = normalizedCommand.toLowerCase(Locale.ROOT);
        if (!lowerCaseCommand.equals("due on") && !lowerCaseCommand.startsWith("due on ")) {
            throw new WobbleException("the due-on command must use the format: due on <date>.");
        }
        String dateText = normalizedCommand.length() > 7 ? normalizedCommand.substring(7).trim() : "";
        if (dateText.isEmpty()) {
            throw new WobbleException("a due date is required.");
        }
        try {
            return DateTimeParser.parse(dateText).toLocalDate();
        } catch (DateTimeParseException exception) {
            throw new WobbleException("the date must use yyyy-MM-dd, yyyy.MM.dd, or yyyy/MM/dd.", false);
        }
    }

    /** Parses the optional day range used by the reminders command. */
    public int parseReminderDays(String command) throws WobbleException {
        String normalizedCommand = normalizeCommand(command);
        String lowerCaseCommand = normalizedCommand.toLowerCase(Locale.ROOT);
        if (!lowerCaseCommand.equals("reminders") && !lowerCaseCommand.startsWith("reminders ")) {
            throw new WobbleException("the reminders command must use the format: reminders [days].");
        }
        String[] parts = normalizedCommand.split(" ");
        if (parts.length == 1) {
            return DEFAULT_REMINDER_DAYS;
        }
        if (parts.length != 2) {
            throw new WobbleException("the reminders command accepts an optional number of days.");
        }
        if (!parts[1].matches("\\d+")) {
            throw new WobbleException("the reminder range must be a whole number of days.");
        }
        try {
            int days = Integer.parseInt(parts[1]);
            if (days < 0) {
                throw new WobbleException("the reminder range cannot be negative.");
            }
            if (days > MAX_REMINDER_DAYS) {
                throw new WobbleException("the reminder range cannot exceed "
                        + MAX_REMINDER_DAYS + " days.");
            }
            try {
                LocalDateTime.now().plusDays(days);
            } catch (DateTimeException exception) {
                throw new WobbleException("the reminder range is too large; choose a smaller number.");
            }
            return days;
        } catch (NumberFormatException exception) {
            throw new WobbleException("the reminder range is too large; choose a smaller number.");
        }
    }

    /** Normalizes user input so harmless whitespace differences do not cause command failures. */
    public static String normalizeCommand(String command) {
        return command == null ? "" : command.trim().replaceAll("\\s+", " ");
    }

    /** Parses a whole number used to select a task. */
    public static int parseTaskNumber(String value) throws WobbleException {
        if (value == null || !value.matches("\\d+")) {
            throw new WobbleException("task numbers must be whole numbers.");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new WobbleException("that task number is too large.");
        }
    }

    /** Validates a task description before it is stored. */
    private static void validateDescription(String description, String taskType) throws WobbleException {
        if (description.isEmpty()) {
            throw new WobbleException("a " + taskType + " description cannot be empty.");
        }
        if (description.chars().anyMatch(Character::isISOControl)) {
            throw new WobbleException("a task description cannot contain control characters.");
        }
    }

    /** Counts exact parameter tokens so repeated command parameters can be rejected clearly. */
    private static int countToken(String command, String token) {
        int count = 0;
        for (String part : command.split(" ")) {
            if (part.equals(token)) {
                count++;
            }
        }
        return count;
    }
}
