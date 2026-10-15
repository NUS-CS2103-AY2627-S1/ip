package duchess.tasks.factories;

import java.time.LocalDate;

import duchess.tasks.Event;
import duchess.tasks.exceptions.TaskCreationException;

/** Creates {@link Event} tasks from user commands. */
class EventFactory {
    private static final String TASK_TYPE = "event";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final String CORRECT_FORMAT =
            "event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd";
    private static final String EXAMPLE_COMMAND =
            "event attend meeting /from 2026-12-10 /to 2026-12-11";

    private EventFactory() {
        // Utility class.
    }

    /**
     * Creates an event task from a command.
     *
     * @throws TaskCreationException if required information, valid dates, or a valid
     *                       date range is missing
     */
    static Event create(String command) throws TaskCreationException {
        EventDetails details = parseDetails(command);
        validateDetails(details);

        LocalDate from = TaskFactory.parseDate(details.getFromString());
        LocalDate to = TaskFactory.parseDate(details.getToString());
        validateDateRange(details, from, to);

        return new Event(details.getDescription(), from, to);
    }

    private static EventDetails parseDetails(String command) throws TaskCreationException {
        int fromIndex = TaskFactory.findMarker(command, FROM_MARKER);
        if (fromIndex == -1) {
            EventFactory.declareMissingField(FROM_MARKER);
        }

        int toIndex = TaskFactory.findMarker(command, TO_MARKER);
        if (toIndex == -1) {
            EventFactory.declareMissingField(TO_MARKER);
        }

        return toIndex < fromIndex
                ? parseReversedMarkerOrder(command, fromIndex, toIndex)
                : parseNormalMarkerOrder(command, fromIndex, toIndex);
    }

    private static EventDetails parseNormalMarkerOrder(
            String command, int fromIndex, int toIndex) {
        return new EventDetails(
                TaskFactory.extractCommandSection(command, TASK_TYPE.length(), fromIndex),
                TaskFactory.extractCommandSection(
                        command, fromIndex + FROM_MARKER.length(), toIndex),
                TaskFactory.extractCommandSection(
                        command, toIndex + TO_MARKER.length(), command.length()));
    }

    private static EventDetails parseReversedMarkerOrder(
            String command, int fromIndex, int toIndex) {
        return new EventDetails(
                TaskFactory.extractCommandSection(command, TASK_TYPE.length(), toIndex),
                TaskFactory.extractCommandSection(
                        command, fromIndex + FROM_MARKER.length(), command.length()),
                TaskFactory.extractCommandSection(
                        command, toIndex + TO_MARKER.length(), fromIndex));
    }

    private static void validateDetails(EventDetails details) throws TaskCreationException {
        if (details.getDescription().isEmpty()) {
            throw TaskCreationException.declareEmptyDescription(TASK_TYPE);
        }
        if (details.getFromString().isEmpty()) {
            EventFactory.declareMissingField(FROM_MARKER);
        }
        if (details.getToString().isEmpty()) {
            EventFactory.declareMissingField(TO_MARKER);
        }
    }

    private static void declareMissingField(String fieldName)
            throws TaskCreationException {
        throw TaskCreationException.declareMissingField(
                TASK_TYPE, fieldName, CORRECT_FORMAT, EXAMPLE_COMMAND);
    }

    private static void validateDateRange(EventDetails details,
            LocalDate from, LocalDate to) throws TaskCreationException {
        if (!from.isBefore(to)) {
            throw TaskCreationException.declareInvalidDateRange(
                    details.getFromString(), details.getToString());
        }
    }

    /** Holds the textual fields extracted from an event command. */
    private static class EventDetails {
        private final String description;
        private final String fromString;
        private final String toString;

        EventDetails(String description, String fromString, String toString) {
            this.description = description;
            this.fromString = fromString;
            this.toString = toString;
        }

        String getDescription() {
            return description;
        }

        String getFromString() {
            return fromString;
        }

        String getToString() {
            return toString;
        }
    }
}
