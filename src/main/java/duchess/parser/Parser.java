package duchess.parser;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duchess.DuchessException;
import duchess.task.Deadline;
import duchess.task.Event;
import duchess.task.Task;
import duchess.task.Todo;

/** Converts user-entered text into validated task data and indexes. */
public final class Parser {
    private static final String EMPTY_COMMAND_ERROR = "OOPS!!! A command cannot be empty. "
            + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.";

    /** Prevents creation of a stateless utility parser. */
    private Parser() {
    }

    /**
     * Creates the appropriate task subtype from a user command.
     *
     * <p>Commands without an explicit type are rejected because all supported
     * task commands have an explicit {@code todo}, {@code deadline}, or
     * {@code event} prefix.</p>
     *
     * @param command the complete task command
     * @return a task object whose runtime type matches the command
     * @throws DuchessException if the command is empty, malformed, or unknown
     */
    public static Task parseTask(String command) throws DuchessException {
        if (command == null || command.isBlank()) {
            throw new DuchessException(EMPTY_COMMAND_ERROR);
        }

        String normalizedCommand = normalizeTaskAlias(command.strip());
        String[] commandParts = normalizedCommand.split("\\s+", 2);
        String taskType = commandParts[0].toLowerCase(Locale.ROOT);
        String taskDetails = commandParts.length == 2 ? commandParts[1].strip() : "";
        validateTextCharacters(normalizedCommand);

        if (taskType.equals("todo")) {
            String description = taskDetails;
            if (description.isEmpty()) {
                throw new DuchessException("OOPS!!! The description of a todo cannot be empty.");
            }
            return new Todo(description);
        }
        if (taskType.equals("deadline")) {
            String[] details = splitTaskDetails(taskDetails, "/by", "deadline");
            validateTaskDetails("deadline", details, "/by");
            try {
                return new Deadline(details[0], details[1]);
            } catch (DateTimeParseException exception) {
                throw new DuchessException("OOPS!!! The deadline date is invalid. "
                        + "Use yyyy-MM-dd, for example: 2019-12-02.");
            }
        }
        if (taskType.equals("event")) {
            if (hasMarker(taskDetails, "/from") || hasMarker(taskDetails, "/to")) {
                return parseEventRange(taskDetails);
            }
            String[] details = splitTaskDetails(taskDetails, "/at", "event");
            validateTaskDetails("event", details, "/at");
            return new Event(details[0], details[1]);
        }

        throw new DuchessException("OOPS!!! I'm sorry, but I don't know what that means :-(\n"
                + "Try todo, deadline, event, list, find, mark, unmark, delete, or bye.");
    }

    /** Parses an event's required description and ISO start/end dates. */
    private static Event parseEventRange(String eventDetails) throws DuchessException {
        ArrayList<MatchResult> fromMarkers = findMarkers(eventDetails, "/from");
        ArrayList<MatchResult> toMarkers = findMarkers(eventDetails, "/to");
        if (fromMarkers.size() != 1 || toMarkers.size() != 1
                || hasMarker(eventDetails, "/at")) {
            throw new DuchessException("OOPS!!! An event must contain exactly one /from and one /to parameter.");
        }

        MatchResult fromMarker = fromMarkers.get(0);
        MatchResult toMarker = toMarkers.get(0);
        if (fromMarker.start() >= toMarker.start()) {
            throw new DuchessException("OOPS!!! Use event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.");
        }

        String description = eventDetails.substring(0, fromMarker.start()).strip();
        String from = eventDetails.substring(fromMarker.end(), toMarker.start()).strip();
        String to = eventDetails.substring(toMarker.end()).strip();
        if (description.isBlank() || from.isBlank() || to.isBlank()) {
            throw new DuchessException("OOPS!!! Use event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.");
        }
        try {
            return new Event(description, from, to);
        } catch (DateTimeParseException exception) {
            throw new DuchessException("OOPS!!! The event dates are invalid. Use yyyy-MM-dd.");
        } catch (IllegalArgumentException exception) {
            throw new DuchessException("OOPS!!! The event end date must be later than its start date.");
        }
    }

    /** Converts squad-room task aliases into their original command names. */
    private static String normalizeTaskAlias(String command) {
        String[] commandParts = command.split("\\s+", 2);
        String commandWord = commandParts[0].toLowerCase(Locale.ROOT);
        String canonicalCommand = switch (commandWord) {
            case "case" -> "todo";
            case "timer" -> "deadline";
            case "briefing" -> "event";
            default -> commandParts[0];
        };
        return commandParts.length == 1 ? canonicalCommand : canonicalCommand + " " + commandParts[1];
    }

    /**
     * Converts the one-based task number in a command into a zero-based list index.
     *
     * @param command the complete command
     * @param prefix the command prefix, such as {@code "mark "}
     * @return the zero-based index, or {@code -1} for malformed input
     */
    public static int parseTaskIndex(String command, String prefix) {
        assert command != null && prefix != null : "Task-index parsing requires command and prefix text";
        String[] commandParts = command.strip().split("\\s+");
        if (commandParts.length != 2 || !commandParts[0].equalsIgnoreCase(prefix.strip())
                || !commandParts[1].matches("[1-9]\\d*")) {
            return -1;
        }
        try {
            long oneBasedIndex = Long.parseLong(commandParts[1]);
            return oneBasedIndex <= Integer.MAX_VALUE ? (int) oneBasedIndex - 1 : -1;
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /**
     * Splits a typed task command into its description and detail value.
     *
     * @param taskDetails the text after the task type
     * @param marker the detail marker, such as {@code /by} or {@code /at}
     * @return a two-element array containing description and detail
     */
    private static String[] splitTaskDetails(String taskDetails, String marker, String taskType)
            throws DuchessException {
        ArrayList<MatchResult> markers = findMarkers(taskDetails, marker);
        if (markers.isEmpty()) {
            return new String[]{taskDetails.trim(), ""};
        }
        if (markers.size() > 1) {
            String article = taskType.equals("event") ? "An" : "A";
            throw new DuchessException("OOPS!!! " + article + " " + taskType + " must contain exactly one "
                    + marker + " parameter.");
        }

        MatchResult markerMatch = markers.get(0);
        String description = taskDetails.substring(0, markerMatch.start()).strip();
        String detail = taskDetails.substring(markerMatch.end()).strip();
        return new String[]{description, detail};
    }

    /** Returns whether task details contain a whitespace-delimited parameter marker. */
    private static boolean hasMarker(String taskDetails, String marker) {
        return markerPattern(marker).matcher(taskDetails).find();
    }

    /** Returns all occurrences of a whitespace-delimited parameter marker. */
    private static ArrayList<MatchResult> findMarkers(String taskDetails, String marker) {
        ArrayList<MatchResult> markers = new ArrayList<>();
        Matcher matcher = markerPattern(marker).matcher(taskDetails);
        while (matcher.find()) {
            markers.add(matcher.toMatchResult());
        }
        return markers;
    }

    /** Returns a case-insensitive pattern that recognizes one complete parameter marker. */
    private static Pattern markerPattern(String marker) {
        return Pattern.compile("(?i)(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
    }

    /** Rejects control characters that cannot be entered safely as one command. */
    private static void validateTextCharacters(String command) throws DuchessException {
        if (command.chars().anyMatch(character -> Character.isISOControl(character)
                && character != '\t')) {
            throw new DuchessException("OOPS!!! Commands cannot contain control characters.");
        }
    }

    /**
     * Checks that a deadline or event has both required pieces of information.
     *
     * @param taskType the task type being validated
     * @param details the parsed description and detail
     * @param marker the required detail marker
     * @throws DuchessException if the description or detail is missing
     */
    private static void validateTaskDetails(String taskType, String[] details, String marker)
            throws DuchessException {
        assert details != null && details.length == 2
                : "Task details must contain exactly a description and a detail value";
        if (details[0].isEmpty()) {
            String article = taskType.equals("event") ? "an" : "a";
            throw new DuchessException("OOPS!!! The description of " + article + " " + taskType
                    + " cannot be empty.");
        }
        if (details[1].isEmpty()) {
            String article = taskType.equals("event") ? "An" : "A";
            throw new DuchessException("OOPS!!! " + article + " " + taskType
                    + " must include a non-empty "
                    + marker + " value. Example: " + taskType + " task description "
                    + marker + " time.");
        }
    }
}
