package kojispawn.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import kojispawn.exception.KojisPawnException;
import kojispawn.task.Deadline;
import kojispawn.task.Event;
import kojispawn.task.Task;
import kojispawn.task.Todo;

/**
 * Converts task-creation command arguments into task objects.
 */
final class TaskParser {
    private static final String MISSING_EVENT_DESCRIPTION =
            "An event without a description cannot enter the plan. "
                    + "Use: event DESCRIPTION /from START /to END.";
    private static final String MISSING_EVENT_START = "Every event has an origin. Include /from START.";
    private static final String MISSING_EVENT_END =
            "Even calculated events need an endpoint. Include /to END.";
    private static final Pattern ISO_DATE_PREFIX = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}(?:$|\\s)");
    /**
     * Creates a todo from a todo command.
     *
     * @param command Complete todo command.
     * @return Todo described by the command.
     * @throws KojisPawnException If the description is missing or contains reserved storage characters.
     */
    Task parseTodo(String command) throws KojisPawnException {
        validateStorageText(command);
        String description = command.substring("todo".length()).strip();
        if (description.isBlank()) {
            throw new KojisPawnException(
                    "An empty task has no place in the plan. Describe what must be done after todo.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from a deadline command.
     *
     * @param command Complete deadline command.
     * @return Deadline described by the command.
     * @throws KojisPawnException If the description or deadline date is invalid.
     */
    Task parseDeadline(String command) throws KojisPawnException {
        validateStorageText(command);
        String deadlineDetails = command.substring("deadline".length()).strip();
        if (deadlineDetails.isBlank() || deadlineDetails.startsWith("/by")) {
            throw new KojisPawnException(
                    "A deadline without a description is merely noise. Use: deadline DESCRIPTION /by DATE.");
        }

        String byMarker = " /by";
        int byIndex = deadlineDetails.indexOf(byMarker);
        if (byIndex == -1) {
            throw new KojisPawnException(
                    "Even a deadline needs a boundary. Use: deadline DESCRIPTION /by DATE.");
        }

        String description = deadlineDetails.substring(0, byIndex).strip();
        if (description.isBlank()) {
            throw new KojisPawnException(
                    "A deadline without a description is merely noise. Use: deadline DESCRIPTION /by DATE.");
        }
        rejectRepeatedMarker(deadlineDetails, byMarker, byIndex, "Specify /by only once.");

        String dateBy = parseRequiredValue(deadlineDetails.substring(byIndex + byMarker.length()),
                "The plan requires a deadline value after /by.",
                "Even a deadline needs a boundary. Use: deadline DESCRIPTION /by DATE.");
        try {
            return new Deadline(description, LocalDate.parse(dateBy));
        } catch (DateTimeParseException exception) {
            throw new KojisPawnException(
                    "Deadline dates must use yyyy-MM-dd and describe a real calendar date.");
        }
    }

    /**
     * Creates an event from an event command.
     *
     * @param command Complete event command.
     * @return Event described by the command.
     * @throws KojisPawnException If the description, start, or end is invalid.
     */
    Task parseEvent(String command) throws KojisPawnException {
        validateStorageText(command);
        String eventDetails = parseEventDetails(command);
        String fromMarker = " /from";
        String toMarker = " /to";
        int fromIndex = findRequiredMarker(eventDetails, fromMarker, MISSING_EVENT_START);
        int toIndex = findRequiredMarker(eventDetails, toMarker, MISSING_EVENT_END);
        rejectRepeatedMarker(eventDetails, fromMarker, fromIndex, "Specify /from only once.");
        rejectRepeatedMarker(eventDetails, toMarker, toIndex, "Specify /to only once.");
        if (toIndex < fromIndex) {
            throw new KojisPawnException("Causality matters. Place /from START before /to END.");
        }

        String description = eventDetails.substring(0, fromIndex).strip();
        String dateFrom = parseRequiredValue(eventDetails.substring(fromIndex + fromMarker.length(), toIndex),
                "The plan requires a starting value after /from.", MISSING_EVENT_START);
        String dateTo = parseRequiredValue(eventDetails.substring(toIndex + toMarker.length()),
                "The plan requires an ending value after /to.", MISSING_EVENT_END);
        validateEventDateOrder(dateFrom, dateTo);
        return new Event(description, dateFrom, dateTo);
    }

    /**
     * Compares calendar dates when both event values begin with ISO dates.
     */
    private void validateEventDateOrder(String dateFrom, String dateTo) throws KojisPawnException {
        LocalDate startDate = parseEventDatePrefix(dateFrom);
        LocalDate endDate = parseEventDatePrefix(dateTo);
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new KojisPawnException("An event cannot end before it starts. Use /to on or after /from.");
        }
    }

    /**
     * Returns a leading ISO date, or {@code null} for a free-form event value.
     */
    private LocalDate parseEventDatePrefix(String value) throws KojisPawnException {
        if (!ISO_DATE_PREFIX.matcher(value).find()) {
            return null;
        }
        try {
            return LocalDate.parse(value.substring(0, 10));
        } catch (DateTimeParseException exception) {
            throw new KojisPawnException("Event dates must use yyyy-MM-dd and describe a real calendar date.");
        }
    }

    private String parseEventDetails(String command) throws KojisPawnException {
        String details = command.substring("event".length()).strip();
        if (details.isBlank() || details.startsWith("/from") || details.startsWith("/to")) {
            throw new KojisPawnException(MISSING_EVENT_DESCRIPTION);
        }
        return details;
    }

    private int findRequiredMarker(String details, String marker, String errorMessage)
            throws KojisPawnException {
        int index = details.indexOf(marker);
        if (index == -1) {
            throw new KojisPawnException(errorMessage);
        }
        return index;
    }

    /**
     * Rejects another complete marker while allowing similar text such as {@code /fromage} in a value.
     */
    private void rejectRepeatedMarker(String details, String marker, int firstIndex, String errorMessage)
            throws KojisPawnException {
        int nextIndex = details.indexOf(marker, firstIndex + marker.length());
        while (nextIndex != -1) {
            int markerEnd = nextIndex + marker.length();
            if (markerEnd == details.length() || Character.isWhitespace(details.charAt(markerEnd))) {
                throw new KojisPawnException(errorMessage);
            }
            nextIndex = details.indexOf(marker, markerEnd);
        }
    }

    /**
     * Requires a nonempty value separated from its marker by whitespace.
     */
    private String parseRequiredValue(String value, String missingValueMessage, String missingMarkerMessage)
            throws KojisPawnException {
        if (value.isBlank()) {
            throw new KojisPawnException(missingValueMessage);
        }
        // Check before stripping so malformed markers such as /from2pm are rejected.
        if (!Character.isWhitespace(value.charAt(0))) {
            throw new KojisPawnException(missingMarkerMessage);
        }
        return value.strip();
    }

    /**
     * Rejects text that would be mistaken for a field or record boundary in the data file.
     */
    private void validateStorageText(String text) throws KojisPawnException {
        if (text.contains(" | ") || text.contains("\n") || text.contains("\r")) {
            throw new KojisPawnException(
                    "Task text cannot contain the reserved separator ' | ' or line breaks.");
        }
    }
}
