package aravii;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates command syntax and converts user input into typed task values.
 */
public final class Parser {
    static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private static final Pattern DEADLINE_PATTERN = Pattern.compile("^(.+?) /by (.+)$");

    private static final Pattern EVENT_PATTERN = Pattern.compile("^(.+?) /from (.+?) /to (.+)$");

    private Parser() {
    }

    /**
     * Represents a validated command name and its remaining arguments.
     *
     * @param name The supported command name.
     * @param arguments The stripped argument text.
     */
    public record Command(String name, String arguments) {
        /**
         * Returns whether executing this command changes the task list.
         *
         * @return Whether a successful execution needs to be saved.
         */
        public boolean isMutation() {
            return switch (name) {
                case "todo", "deadline", "event", "mark", "unmark", "delete", "sort" -> true;
                default -> false;
            };
        }
    }

    /**
     * Validates a command and separates its name from its arguments.
     *
     * @param input The complete line entered by the user.
     * @return The parsed command.
     */
    public static Command parse(String input) {
        validateText(input);
        String[] parts = input.strip().split(" +", 2);
        String name = parts[0];
        String arguments = parts.length == 2 ? parts[1].strip() : "";
        switch (name) {
            case "help", "list", "sort", "bye":
                if (!arguments.isEmpty()) {
                    throw new IllegalArgumentException(name + " does not take arguments.");
                }
                break;
            case "todo", "deadline", "event", "find", "mark", "unmark", "delete":
                if (arguments.isEmpty()) {
                    throw new IllegalArgumentException(name + " needs a value. Type help for its format.");
                }
                break;
            default:
                throw new IllegalArgumentException("I don't recognize that command. Type help for available commands.");
        }
        return new Command(name, arguments);
    }

    /**
     * Parses the arguments of an add command without modifying the task list.
     *
     * @param command The todo, deadline, or event command.
     * @return A fully validated, incomplete task.
     */
    public static Task parseTask(Command command) {
        return switch (command.name()) {
            case "todo" -> new Todo(command.arguments());
            case "deadline" -> parseDeadline(command.arguments());
            case "event" -> parseEvent(command.arguments());
            default -> throw new IllegalArgumentException("This command does not add a task.");
        };
    }

    private static Deadline parseDeadline(String arguments) {
        Matcher matcher = DEADLINE_PATTERN.matcher(arguments);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Use deadline <description> /by <YYYY-MM-DD>.");
        }
        return new Deadline(matcher.group(1), parseDate(matcher.group(2)));
    }

    private static Event parseEvent(String arguments) {
        Matcher matcher = EVENT_PATTERN.matcher(arguments);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Use event <description> /from <YYYY-MM-DD HH:MM>"
                    + " /to <YYYY-MM-DD HH:MM>.");
        }
        return new Event(matcher.group(1), parseDateTime(matcher.group(2)), parseDateTime(matcher.group(3)));
    }

    /**
     * Rejects control characters that cannot be represented in the TSV save format.
     *
     * @param text The command or description to validate.
     */
    static void validateText(String text) {
        if (text == null || text.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Use a single line without tabs or control characters.");
        }
    }

    /**
     * Parses a real calendar date without correcting impossible dates.
     *
     * @param value The ISO date entered by the user or read from storage.
     * @return The parsed date.
     */
    static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.strip(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Use a valid date in YYYY-MM-DD format.", exception);
        }
    }

    /**
     * Parses a real calendar date and a 24-hour time strictly.
     *
     * @param value The date and time entered by the user or read from storage.
     * @return The parsed date and time.
     */
    static LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value.strip(), DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Use a valid date and time in YYYY-MM-DD HH:MM format.", exception);
        }
    }
}
