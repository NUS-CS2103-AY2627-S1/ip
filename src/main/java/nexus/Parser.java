package nexus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Converts user commands into tasks. */
public class Parser {
    /** Date format accepted in deadline commands. */
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DEADLINE_DATE_MARKER = " /by ";
    private static final String EVENT_START_MARKER = " /from ";
    private static final String EVENT_END_MARKER = " /to ";

    /** Parses a task-creation command. */
    public Task parseTask(String command) throws NexusException {
        if (command == null || command.isBlank() || !command.equals(command.trim())
                || command.matches(".*\\s{2,}.*")) {
            throw new NexusException("Commands must not have leading, trailing, or repeated spaces.");
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.substring("todo".length()).trim();
            if (description.isEmpty() || containsControlCharacter(description)) {
                throw new NexusException("A todo needs a description.");
            }
            return new Todo(description);
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            String body = command.substring("deadline".length()).trim();
            int marker = body.indexOf(DEADLINE_DATE_MARKER);
            if (marker < 0 || body.substring(0, marker).trim().isEmpty()
                    || body.substring(marker + DEADLINE_DATE_MARKER.length()).trim().isEmpty()
                    || body.indexOf(DEADLINE_DATE_MARKER, marker + DEADLINE_DATE_MARKER.length()) >= 0
                    || containsControlCharacter(body)) {
                throw new NexusException("A deadline needs a description and a /by date.");
            }
            assert marker >= 0 : "Validated deadline command must contain a /by marker";
            try {
                LocalDate date = LocalDate.parse(
                        body.substring(marker + DEADLINE_DATE_MARKER.length()).trim(), INPUT_DATE_FORMAT);
                return new Deadline(body.substring(0, marker).trim(), date);
            } catch (DateTimeParseException exception) {
                throw new NexusException("Please enter the deadline date in yyyy-MM-dd format.");
            }
        }
        if (command.equals("event") || command.startsWith("event ")) {
            String body = command.substring("event".length()).trim();
            int fromMarker = body.indexOf(EVENT_START_MARKER);
            int toMarker = body.indexOf(EVENT_END_MARKER);
            if (fromMarker < 0 || toMarker <= fromMarker || body.substring(0, fromMarker).trim().isEmpty()
                    || body.substring(fromMarker + EVENT_START_MARKER.length(), toMarker).trim().isEmpty()
                    || body.substring(toMarker + EVENT_END_MARKER.length()).trim().isEmpty()
                    || body.indexOf(EVENT_START_MARKER, fromMarker + EVENT_START_MARKER.length()) >= 0
                    || body.indexOf(EVENT_END_MARKER, toMarker + EVENT_END_MARKER.length()) >= 0
                    || containsControlCharacter(body)) {
                throw new NexusException("An event needs a description, /from time, and /to time.");
            }
            assert fromMarker >= 0 && toMarker > fromMarker
                    : "Validated event command must contain ordered time markers";
            String from = body.substring(fromMarker + EVENT_START_MARKER.length(), toMarker).trim();
            String to = body.substring(toMarker + EVENT_END_MARKER.length()).trim();
            validateEventTimes(from, to);
            return new Event(body.substring(0, fromMarker).trim(), from, to);
        }
        throw new NexusException("I'm sorry, but I don't know what that means.");
    }

    /** Returns whether text contains characters that cannot safely be stored or displayed. */
    private boolean containsControlCharacter(String text) {
        return text.chars().anyMatch(Character::isISOControl);
    }

    /** Validates event date/time values when they use a standard ISO representation. */
    private void validateEventTimes(String from, String to) throws NexusException {
        try {
            java.time.LocalDateTime start = java.time.LocalDateTime.parse(from);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(to);
            if (!end.isAfter(start)) {
                throw new NexusException("An event must end after it starts.");
            }
        } catch (java.time.format.DateTimeParseException exception) {
            // Short labels such as 10:00 are valid display values; validate those below.
            try {
                java.time.LocalTime start = java.time.LocalTime.parse(from);
                java.time.LocalTime end = java.time.LocalTime.parse(to);
                if (!end.isAfter(start)) {
                    throw new NexusException("An event must end after it starts.");
                }
            } catch (java.time.format.DateTimeParseException ignored) {
                if (from.matches(".*\\d{4}-\\d{2}-\\d{2}.*") || to.matches(".*\\d{4}-\\d{2}-\\d{2}.*")) {
                    throw new NexusException("Event dates must be valid ISO dates and times.");
                }
            }
        }
    }
}
