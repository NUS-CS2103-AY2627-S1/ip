package cooper.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import cooper.exception.CooperException;
import cooper.task.Deadline;
import cooper.task.Event;
import cooper.task.ToDo;

/**
 * Converts user input into actions, task numbers, and task objects.
 */
public class Parser {
    /** Recognizes standalone date delimiters without treating date slashes as delimiters. */
    private static final Pattern DATE_DELIMITER = Pattern.compile("(?<!\\S)/(by|from|to)(?=[ \\t]|$)");
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT));
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT));

    /**
     * Parses the command word at the start of the input.
     *
     * @param input Complete command entered by the user.
     * @return Action corresponding to the command word.
     * @throws CooperException If the input is empty or the command is unknown.
     */
    public static Action parseAction(String input) {
        String trimmedInput = input.strip();
        if (trimmedInput.isEmpty()) {
            throw new CooperException("Please enter a command!");
        }

        String commandWord = trimmedInput.split("\\s+", 2)[0];
        try {
            Action action = Action.valueOf(commandWord.toUpperCase(Locale.ROOT));

            // Notes retain their more specific single-line error from NoteParser.
            if (action != Action.NOTE && input.matches("(?s).*\\R.*")) {
                throw new CooperException("I need each command on a single line!");
            }

            if ((action == Action.LIST || action == Action.BYE) && !trimmedInput.equalsIgnoreCase(commandWord)) {
                throw new CooperException("I don't need extra arguments. Use: " + commandWord.toLowerCase(Locale.ROOT));
            }

            return action;
        } catch (IllegalArgumentException e) {
            throw new CooperException("I don't understand this command: " + commandWord);
        }
    }

    /**
     * Parses a positive, one-based task number from a command containing exactly two words.
     *
     * @param input Command containing the task number.
     * @param syntaxErrorMessage Message used when the command does not contain exactly two words.
     * @return Parsed positive task number.
     * @throws CooperException If the syntax or task number is invalid.
     */
    public static int parseTaskNumber(String input, String syntaxErrorMessage) {
        String[] parameters = input.strip().split("[ \\t]+");
        if (parameters.length != 2) {
            throw new CooperException(syntaxErrorMessage);
        }

        int taskNumber = wordToNum(parameters[1]);
        if (taskNumber <= 0) {
            throw new CooperException("The index isn't valid!");
        }

        return taskNumber;
    }

    /**
     * Parses the keyword from a find command.
     *
     * @param input Complete find command.
     * @return Non-empty keyword supplied by the user.
     * @throws CooperException If the keyword is missing.
     */
    public static String parseFindKeyword(String input) {
        String[] parameters = input.trim().split("\\s+", 2);
        if (parameters.length != 2 || parameters[1].isBlank()) {
            throw new CooperException("I need a keyword to find matching tasks!");
        }

        return parameters[1].trim();
    }

    /**
     * Creates a todo task from a todo command.
     *
     * @param input Complete todo command.
     * @return Todo containing the supplied description.
     * @throws CooperException If the description is missing.
     */
    public static ToDo parseTodo(String input) {
        String description = arguments(input);
        if (description.isBlank()) {
            throw new CooperException("I need a task description. Use: todo <description>");
        }

        return new ToDo(description);
    }

    /**
     * Creates a deadline task from a command containing a {@code /by} date.
     *
     * @param input Complete deadline command.
     * @return Deadline containing the supplied description and due date.
     * @throws CooperException If the command or date is invalid.
     */
    public static Deadline parseDeadline(String input) {
        String[] fields = dateFields(input, new String[] {"by"},
                "I need exactly one deadline. Use: deadline <description> /by <date>");
        if (fields[0].isBlank()) {
            throw new CooperException("I can't keep track of tasks with no name!");
        }

        return new Deadline(fields[0], parseDate(fields[1]));
    }

    /**
     * Creates an event task from a command containing {@code /from} and {@code /to} dates.
     *
     * @param input Complete event command.
     * @return Event containing the supplied description and date range.
     * @throws CooperException If the command or either date is invalid.
     */
    public static Event parseEvent(String input) {
        String[] fields = dateFields(input, new String[] {"from", "to"},
                "I need an event title and start date. Use: event <description> /from <date> /to <date>");
        if (fields[0].isBlank()) {
            throw new CooperException("I need a title for this event!");
        }

        LocalDateTime start = parseDate(fields[1]);
        LocalDateTime end = parseDate(fields[2]);
        if (!end.isAfter(start)) {
            throw new CooperException("I need the end time to be later than the start time.");
        }

        return new Event(fields[0], start, end);
    }

    /** Removes only the command word and outer whitespace, preserving description spacing. */
    private static String arguments(String input) {
        String[] parts = input.strip().split("[ \\t]+", 2);
        return parts.length == 2 ? parts[1].strip() : "";
    }

    /** Extracts date-command fields after validating their delimiters and required values. */
    private static String[] dateFields(String input, String[] expected, String usage) {
        String body = arguments(input);
        List<MatchResult> delimiters = DATE_DELIMITER.matcher(body).results().toList();

        validateDateDelimiters(delimiters, expected, usage);

        String[] fields = extractDateFields(body, delimiters);
        validateDateValues(fields, usage);

        return fields;
    }

    /** Requires exactly the expected delimiters in their prescribed order. */
    private static void validateDateDelimiters(List<MatchResult> delimiters, String[] expected, String usage) {
        if (delimiters.size() != expected.length) {
            throw new CooperException(usage);
        }

        for (int i = 0; i < expected.length; i++) {
            if (!delimiters.get(i).group(1).equals(expected[i])) {
                throw new CooperException(usage);
            }
        }
    }

    /** Splits the description and date values at known delimiter positions, trimming only their edges. */
    private static String[] extractDateFields(String body, List<MatchResult> delimiters) {
        String[] fields = new String[delimiters.size() + 1];
        int previous = 0;

        for (int i = 0; i < delimiters.size(); i++) {
            MatchResult delimiter = delimiters.get(i);
            fields[i] = body.substring(previous, delimiter.start()).strip();
            previous = delimiter.end();
        }

        fields[delimiters.size()] = body.substring(previous).strip();
        return fields;
    }

    /** Rejects empty date values; callers provide command-specific errors for an empty description. */
    private static void validateDateValues(String[] fields, String usage) {
        for (int i = 1; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new CooperException(usage);
            }
        }
    }

    /**
     * Parses a supported date or date-time supplied by the user.
     * Dates without a time are represented at the start of the day.
     *
     * @param time Date or date-time in year-first or day-first format.
     * @return Parsed date-time.
     * @throws CooperException If the value does not match a supported format.
     */
    public static LocalDateTime parseDate(String time) {
        String normalizedTime = time.strip().replace('/', '-').replaceAll("[ \\t]+", " ");

        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(normalizedTime, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date-time format.
            }
        }

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(normalizedTime, formatter).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }

        throw new CooperException("Invalid date. Use yyyy-MM-dd or dd-MM-yyyy, and HH:mm optionally.");
    }

    /** Converts a string of decimal digits to an integer, or returns {@code -1} for invalid or overflowing values. */
    private static int wordToNum(String numberString) {
        try {
            return numberString.matches("[0-9]+") ? Integer.parseInt(numberString) : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
