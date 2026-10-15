package bot67.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;

import bot67.exception.Bot67Exception;

/** Interprets commands and validates input before tasks are changed. */
public class Parser {
    private static final String DEADLINE_USAGE = "Use: deadline <description> /by <date or time>.";
    private static final String EVENT_USAGE = "Use: event <description> /from <start> /to <end>.";

    /** Normalizes spacing and rejects characters that would corrupt a saved task record. */
    public String normalize(String command) throws Bot67Exception {
        if (command == null || command.isBlank()) {
            throw new Bot67Exception("Enter a command. Try: todo read a book");
        }
        if (command.indexOf('|') >= 0 || command.chars().anyMatch(c -> Character.isISOControl(c) && c != '\t')) {
            throw new Bot67Exception("Commands cannot contain | or control characters other than tabs.");
        }
        return command.strip().replaceAll("(?U)\\s+", " ");
    }

    /** Converts the exit command into its command object. Other commands are not handled yet. */
    public Command parse(String fullCommand, String personalityArt) throws Bot67Exception {
        if (fullCommand.equals("bye")) {
            return new ExitCommand(personalityArt);
        }
        return null;
    }

    /** Parses a positive task number; the caller checks it against the current list size. */
    public int parseTaskNumber(String value) throws Bot67Exception {
        try {
            int taskNumber = Integer.parseInt(value.trim());
            if (taskNumber < 1) {
                throw new Bot67Exception("Task number must be a positive whole number.");
            }
            return taskNumber;
        } catch (NumberFormatException e) {
            throw new Bot67Exception("Task number must be a whole number.");
        }
    }

    /** Validates a todo description. */
    public void requireText(String text) throws Bot67Exception {
        if (text.trim().isEmpty()) {
            throw new Bot67Exception("A todo description cannot be empty.");
        }
    }

    /** Requires exactly one deadline parameter and validates its value. */
    public void requireValidDeadline(String command) throws Bot67Exception {
        requireMarkers(command, List.of("/by"), DEADLINE_USAGE);
        int marker = command.indexOf(" /by ");
        if (marker < 9 || command.substring(9, marker).isBlank()
                || command.substring(marker + 5).isBlank()) {
            throw new Bot67Exception(DEADLINE_USAGE);
        }
        validateDateTime(command.substring(marker + 5), "deadline");
    }

    /** Requires an ordered pair of event parameters and checks comparable ISO endpoints. */
    public void requireValidEvent(String command) throws Bot67Exception {
        requireMarkers(command, List.of("/from", "/to"), EVENT_USAGE);
        int from = command.indexOf(" /from ");
        int to = command.indexOf(" /to ");
        if (from < 6 || command.substring(6, from).isBlank() || to < from + 7
                || command.substring(from + 7, to).isBlank() || command.substring(to + 5).isBlank()) {
            throw new Bot67Exception(EVENT_USAGE);
        }
        LocalDateTime start = validateDateTime(command.substring(from + 7, to), "event");
        LocalDateTime end = validateDateTime(command.substring(to + 5), "event");
        if (start != null && end != null && !start.isBefore(end)) {
            throw new Bot67Exception("Event start must be before its end.");
        }
    }

    /** Rejects missing, repeated, unknown, and reordered slash parameters. */
    private void requireMarkers(String command, List<String> expected, String usage) throws Bot67Exception {
        List<String> markers = Pattern.compile("(?<= )/\\S+").matcher(command)
                .results().map(result -> result.group()).toList();
        if (!markers.equals(expected)) {
            throw new Bot67Exception(usage);
        }
    }

    /** Rejects invalid ISO-looking input while preserving free-form dates such as Sunday. */
    private LocalDateTime validateDateTime(String value, String commandType) throws Bot67Exception {
        try {
            return DateTimeParser.parse(value.trim());
        } catch (DateTimeParseException e) {
            if (value.trim().matches("\\d{4}[-/].*")) {
                throw new Bot67Exception("Invalid " + commandType
                        + " date/time. Use yyyy-MM-dd or yyyy-MM-ddTHH:mm.");
            }
            return null;
        }
    }
}
