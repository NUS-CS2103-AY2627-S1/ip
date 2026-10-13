package baymax.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baymax.exception.BaymaxException;
import baymax.exception.EmptyByException;
import baymax.exception.EmptyDescriptionException;
import baymax.exception.EmptyFromException;
import baymax.exception.EmptyToException;
import baymax.exception.InvalidCommandException;
import baymax.task.TaskDate;

/**
 * Identifies the type of a user command.
 */
public final class Parser {

    /**
     * Supported command types.
     */
    public enum CommandType {
        BYE,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        FIND,
        TODO,
        DEADLINE,
        EVENT
    }

    private Parser() {
        // Utility class.
    }

    /**
     * Identifies the command type.
     *
     * @param command the user's command
     * @return the command type
     */
    public static CommandType getCommandType(String command) {
        command = normalizeCommand(command);
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (command.equals("mark") || command.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (command.equals("unmark") || command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (command.equals("delete")
                || command.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (command.equals("find") || command.startsWith("find ")) {
            return CommandType.FIND;
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (command.equals("event") || command.startsWith("event ")) {
            return CommandType.EVENT;
        }

        throw new InvalidCommandException();
    }

    /** Normalizes spacing and rejects empty commands and embedded control characters. */
    public static String normalizeCommand(String command) {
        if (command == null || command.isBlank()) {
            throw new BaymaxException(" Sorry, please enter a command.");
        }
        if (command.chars().anyMatch(value -> Character.isISOControl(value) && value != '\t')) {
            throw new BaymaxException(" Sorry, please enter one command on a single line.");
        }
        String normalizedCommand = command.replaceAll("\\h+", " ").strip();
        if (normalizedCommand.isEmpty()) {
            throw new BaymaxException(" Sorry, please enter a command.");
        }
        return normalizedCommand;
    }

    /**
     * Parses a task number into a zero-based task index.
     *
     * @param command the complete command
     * @param commandType the command type
     * @return the zero-based task index
     */
    public static int parseTaskIndex(
            String command, CommandType commandType) {

        command = normalizeCommand(command);
        String taskNumberText;

        if (commandType == CommandType.MARK) {
            assert command.equals("mark") || command.startsWith("mark ")
                    : "Mark index parsing should receive mark command text.";
            taskNumberText = command.substring("mark".length()).trim();
        } else if (commandType == CommandType.UNMARK) {
            assert command.equals("unmark") || command.startsWith("unmark ")
                    : "Unmark index parsing should receive unmark command text.";
            taskNumberText = command.substring("unmark".length()).trim();
        } else if (commandType == CommandType.DELETE) {
            assert command.equals("delete") || command.startsWith("delete ")
                    : "Delete index parsing should receive delete command text.";
            taskNumberText = command.equals("delete")
                    ? ""
                    : command.substring("delete ".length()).trim();
        } else {
            throw new InvalidCommandException();
        }

        try {
            if (!taskNumberText.matches("[0-9]+") || Integer.parseInt(taskNumberText) == 0) {
                throw new NumberFormatException();
            }
            return Integer.parseInt(taskNumberText) - 1;
        } catch (NumberFormatException exception) {
            throw new BaymaxException(
                    " Sorry, please provide a valid task number.");
        }
    }

    /**
     * Extracts a todo description.
     *
     * @param command the complete todo command
     * @return the todo description
     */
    public static String parseTodoDescription(String command) {
        assert command.startsWith("todo")
                : "Todo description parsing should receive todo command text.";
        String description =
                command.substring("todo".length()).trim();

        if (description.isEmpty()) {
            throw new EmptyDescriptionException("todo");
        }

        return description;
    }

    /**
     * Extracts the keyword used to search task descriptions.
     *
     * @param command the complete find command
     * @return the keyword to search for
     */
    public static String parseFindKeyword(String command) {
        assert command.startsWith("find")
                : "Find keyword parsing should receive find command text.";
        String keyword =
                command.substring("find".length()).trim();

        if (keyword.isEmpty()) {
            throw new EmptyDescriptionException("find");
        }

        return keyword;
    }

    /**
     * Parsed deadline details.
     *
     * @param description the deadline description
     * @param dueDate the deadline date
     */
    public record DeadlineDetails(
            String description, LocalDate dueDate) {
    }

    /**
     * Parsed event details.
     *
     * @param description the event description
     * @param startDate the event start date
     * @param endDate the event end date
     */
    public record EventDetails(
            String description,
            LocalDate startDate,
            LocalDate endDate) {
    }

    /**
     * Stores deadline fields before the date text is validated and converted.
     *
     * @param description the deadline description
     * @param dueDateText the unparsed deadline date
     */
    private record DeadlineTextDetails(
            String description, String dueDateText) {
    }

    /**
     * Stores event fields before the date text is validated and converted.
     *
     * @param description the event description
     * @param startDateText the unparsed event start date
     * @param endDateText the unparsed event end date
     */
    private record EventTextDetails(
            String description,
            String startDateText,
            String endDateText) {
    }

    /**
     * Parses a deadline command.
     *
     * @param command the complete deadline command
     * @return parsed deadline details
     */
    public static DeadlineDetails parseDeadline(String command) {
        assert command.startsWith("deadline")
                : "Deadline parsing should receive deadline command text.";
        validateMarkers(command, "/by");
        DeadlineTextDetails details = extractDeadlineTextDetails(command);

        if (details.dueDateText().isEmpty()) {
            throw new EmptyByException();
        }

        if (details.description().isEmpty()) {
            throw new EmptyDescriptionException("deadline");
        }

        return new DeadlineDetails(
                details.description(), parseDate(details.dueDateText()));
    }

    /**
     * Parses an event command.
     *
     * @param command the complete event command
     * @return parsed event details
     */
    public static EventDetails parseEvent(String command) {
        assert command.startsWith("event")
                : "Event parsing should receive event command text.";
        validateMarkers(command, "/from", "/to");
        EventTextDetails details = extractEventTextDetails(command);

        if (details.description().isEmpty()) {
            throw new EmptyDescriptionException("event");
        }

        if (details.startDateText().isEmpty()) {
            throw new EmptyFromException();
        }

        if (details.endDateText().isEmpty()) {
            throw new EmptyToException();
        }

        LocalDate startDate = parseDate(details.startDateText());
        LocalDate endDate = parseDate(details.endDateText());
        if (!startDate.isBefore(endDate)) {
            throw new BaymaxException(" Sorry, an event must end after its start date.");
        }
        return new EventDetails(details.description(), startDate, endDate);
    }

    /** Checks that date parameters are known, separated, unique, and in order. */
    private static void validateMarkers(String command, String... markers) {
        Matcher matcher = Pattern.compile("/\\S*").matcher(command);
        int previousIndex = -1;
        while (matcher.find()) {
            int markerIndex = -1;
            for (int i = 0; i < markers.length; i++) {
                if (markers[i].equals(matcher.group())) {
                    markerIndex = i;
                }
            }
            if (markerIndex <= previousIndex || markerIndex < 0
                    || matcher.start() == 0 || !Character.isWhitespace(command.charAt(matcher.start() - 1))) {
                throw new BaymaxException(" Sorry, use each date parameter once, in order: "
                        + String.join(" ", markers) + ".");
            }
            previousIndex = markerIndex;
        }
    }

    /**
     * Separates a deadline command into its raw description and date text.
     *
     * @param command the complete deadline command
     * @return the raw deadline fields, with empty values for a missing marker
     */
    private static DeadlineTextDetails extractDeadlineTextDetails(String command) {
        String deadlineDetails =
                command.substring("deadline".length()).trim();
        int byMarkerIndex = deadlineDetails.indexOf("/by");

        if (byMarkerIndex < 0) {
            return new DeadlineTextDetails("", "");
        }

        String description =
                deadlineDetails.substring(0, byMarkerIndex).trim();
        String dueDateText = deadlineDetails.substring(
                byMarkerIndex + "/by".length()).trim();
        return new DeadlineTextDetails(description, dueDateText);
    }

    /**
     * Separates an event command into its raw description and date text.
     *
     * @param command the complete event command
     * @return the raw event fields, with empty values for missing markers
     */
    private static EventTextDetails extractEventTextDetails(String command) {
        String eventDetails =
                command.substring("event".length()).trim();
        int fromMarkerIndex = eventDetails.indexOf("/from");

        if (fromMarkerIndex < 0) {
            throw new EmptyFromException();
        }

        String description =
                eventDetails.substring(0, fromMarkerIndex).trim();
        int toMarkerIndex = eventDetails.indexOf(
                "/to", fromMarkerIndex + "/from".length());
        if (toMarkerIndex < 0) {
            throw new EmptyToException();
        }

        String startDateText = eventDetails.substring(
                fromMarkerIndex + "/from".length(), toMarkerIndex).trim();
        String endDateText = eventDetails.substring(
                toMarkerIndex + "/to".length()).trim();
        return new EventTextDetails(
                description, startDateText, endDateText);
    }

    /**
     * Parses a date in yyyy-MM-dd format.
     *
     * @param dateText the date text
     * @return the parsed date
     */
    private static LocalDate parseDate(String dateText) {
        assert dateText != null && !dateText.isBlank()
                : "Date parsing should receive non-empty date text.";
        try {
            return TaskDate.validate(LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            throw new BaymaxException(
                    " Sorry, dates must use the format yyyy-MM-dd.");
        }
    }
}
