package gongrilla.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gongrilla.command.AddCommand;
import gongrilla.command.Command;
import gongrilla.command.DeleteCommand;
import gongrilla.command.ExitCommand;
import gongrilla.command.FindCommand;
import gongrilla.command.HelpCommand;
import gongrilla.command.ListCommand;
import gongrilla.command.MarkCommand;
import gongrilla.command.UnmarkCommand;
import gongrilla.exception.GongrillaException;
import gongrilla.task.Deadline;
import gongrilla.task.Event;
import gongrilla.task.Todo;

/**
 * Converts raw user input into executable command objects.
 */
public class Parser {
    private static final List<DateTimeFormatter> INPUT_DATE_TIME_FORMATS = List.of(
            strictFormatter("d/M/uuuu HHmm"),
            strictFormatter("uuuu-MM-dd HHmm"));
    private static final List<DateTimeFormatter> INPUT_DATE_FORMATS = List.of(
            strictFormatter("d/M/uuuu"),
            DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT));

    /** Prevents construction because Parser does not require object state. */
    private Parser() {
        // Utility class.
    }

    /**
     * Parses one complete line of user input.
     *
     * @param command raw command entered by the user.
     * @return command representing the requested action.
     * @throws GongrillaException if the command is missing or malformed.
     */
    public static Command parse(String command) throws GongrillaException {
        command = command == null ? "" : command;
        if (command.chars().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new GongrillaException("One command at a time. No line breaks or control characters.");
        }
        command = command.replaceAll("[\\t\\p{Zs}]", " ").strip();
        if (command.equalsIgnoreCase("bye")) {
            return new ExitCommand();
        } else if (command.equalsIgnoreCase("help")) {
            return new HelpCommand();
        } else if (command.equalsIgnoreCase("list")) {
            return new ListCommand();
        } else if (matchesCommand(command, "find")) {
            return parseFind(command);
        } else if (matchesCommand(command, "deadline")) {
            return parseDeadline(command);
        } else if (matchesCommand(command, "todo")) {
            return parseTodo(command);
        } else if (matchesCommand(command, "event")) {
            return parseEvent(command);
        } else if (matchesCommand(command, "delete")) {
            int index = parseTaskIndex(command, "delete");

            return new DeleteCommand(index);
        } else if (matchesCommand(command, "mark")) {
            int index = parseTaskIndex(command, "mark");
            return new MarkCommand(index);
        } else if (matchesCommand(command, "unmark")) {
            int index = parseTaskIndex(command, "unmark");
            return new UnmarkCommand(index);
        } else {
            throw new GongrillaException(
                    "Hmm. Gongrilla no know that :-(");
        }
    }

    /**
     * Matches a keyword alone or followed by a space, ignoring letter case.
     * Keeps command prefixes such as "todoish" from being treated as commands.
     */
    private static boolean matchesCommand(String command, String keyword) {
        String prefix = keyword + " ";
        return command.equalsIgnoreCase(keyword)
                || command.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /** Parses and validates the find command after dispatch recognizes its keyword. */
    private static Command parseFind(String command) throws GongrillaException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isBlank()) {
            throw new GongrillaException("What find? Gongrilla need keyword.");
        }
        return new FindCommand(keyword);
    }

    /** Parses and validates the deadline command after dispatch recognizes its keyword. */
    private static Command parseDeadline(String command) throws GongrillaException {
        String details = command.substring("deadline".length()).trim();
        String[] parts = splitParameters(details, List.of("by"));
        if (parts[0].isBlank()) {
            throw new GongrillaException("No task. What Gongrilla supposed to do?");
        }
        if (parts[1].isBlank()) {
            throw new GongrillaException("When task due? Gongrilla need date or date-time.");
        }
        Deadline deadline = new Deadline(parts[0], parseDateTime(parts[1]));

        return new AddCommand(deadline);
    }

    /** Parses and validates the todo command after dispatch recognizes its keyword. */
    private static Command parseTodo(String command) throws GongrillaException {
        String name = command.substring("todo".length()).trim();
        if (name.isBlank()) {
            throw new GongrillaException("Empty task. What Gongrilla do? Give something.");
        }
        Todo todo = new Todo(name);

        return new AddCommand(todo);
    }

    /** Parses and validates the event command after dispatch recognizes its keyword. */
    private static Command parseEvent(String command) throws GongrillaException {
        String details = command.substring("event".length()).trim();
        String[] parts = splitParameters(details, List.of("from", "to"));
        if (parts[0].isBlank()) {
            throw new GongrillaException("Task missing. No task, no banana.");
        }
        if (parts[1].isBlank() || parts[2].isBlank()) {
            throw new GongrillaException(
                    "When event start? When event end? Gongrilla need know.");
        }
        Event event = new Event(parts[0], parseDateTime(parts[1]), parseDateTime(parts[2]));

        return new AddCommand(event);
    }

    /** Splits named parameters and rejects missing, repeated, unknown, or out-of-order markers. */
    private static String[] splitParameters(String details, List<String> expected) throws GongrillaException {
        Matcher matcher = Pattern.compile("(?i)(?<!\\S)/([a-z]+)(?=\\s|$)").matcher(details);
        List<String> values = new ArrayList<>();
        List<String> seen = new ArrayList<>();
        int start = 0;
        while (matcher.find()) {
            String parameter = matcher.group(1).toLowerCase(Locale.ROOT);
            if (seen.contains(parameter)) {
                throw new GongrillaException("Too many /" + parameter + ". Gongrilla need it only once.");
            }
            if (!expected.contains(parameter)) {
                throw new GongrillaException("Gongrilla no expect /" + parameter + " here.");
            }
            if (!expected.get(seen.size()).equals(parameter)) {
                throw new GongrillaException("Use /" + String.join(" then /", expected) + " in that order.");
            }
            values.add(details.substring(start, matcher.start()).trim());
            seen.add(parameter);
            start = matcher.end();
        }
        if (seen.size() != expected.size()) {
            throw new GongrillaException("Missing /" + expected.get(seen.size()) + ". Type help for the format.");
        }
        values.add(details.substring(start).trim());
        return values.toArray(String[]::new);
    }

    /**
     * Converts a user-facing task number into a zero-based index.
     *
     * @param command complete command containing the task number.
     * @param keyword command keyword that precedes the number.
     * @return zero-based task index.
     * @throws GongrillaException if the number is missing, malformed, signed, zero, or too large.
     */
    private static int parseTaskIndex(String command, String keyword)
            throws GongrillaException {
        // Ensure the command starts with the expected keyword before trimming it off.
        assert command.regionMatches(true, 0, keyword, 0, keyword.length())
                : "Expected command to start with '" + keyword + "', but got: " + command;
        String value = command.substring(keyword.length()).trim();
        if (value.isEmpty()) {
            throw new GongrillaException("No number. Gongrilla pick air?");
        }
        if (value.startsWith("+")) {
            throw new GongrillaException(
                    "Don't put +. Number already positive. Human make simple thing hard.");
        }
        if (value.startsWith("-")) {
            throw new GongrillaException("Negative task? What next, negative banana?");
        }
        if (!value.matches("[0-9]+")) {
            throw new GongrillaException("Dis not number. Even banana know number.");
        }
        try {
            int taskNumber = Integer.parseInt(value);
            if (taskNumber == 0) {
                throw new GongrillaException("Task 0? Human counting start at 1. ");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new GongrillaException("Number too big. Gongrilla run out of fingers.");
        }
    }


    /**
     * Parses either a date or a date and time. A date without a time starts at midnight.
     *
     * @param value date text entered by the user.
     * @return parsed date and time.
     * @throws DateTimeParseException if none of the supported formats match.
     */
    private static LocalDateTime parseDateTime(String value) {
        value = value.replaceAll("\\s+", " ");
        for (DateTimeFormatter formatter : INPUT_DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        for (DateTimeFormatter formatter : INPUT_DATE_FORMATS) {
            try {
                return LocalDate.parse(value, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new DateTimeParseException("Unsupported date-time format", value, 0);
    }

    /**
     * Creates a locale-stable, strict formatter for a user-input pattern.
     *
     * @param pattern date-time pattern accepted from user input.
     * @return strict English-language formatter.
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }
}
