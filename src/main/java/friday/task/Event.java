package friday.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task occurring between a start and end time.
 */
public class Event extends Task {
    private static final String START_PREFIX = "from ";
    private static final String END_PREFIX = "to ";

    private final String start;
    private final String end;

    /**
     * Creates an event from its description, start, and end values.
     *
     * @param event Fields containing the description, start, and end values.
     * @throws IllegalArgumentException If a required field is absent or blank.
     */
    public Event(String[] event) {
        super(requireField(event, 0, "Event", "description"), "[E]");
        String startValue = requireField(event, 1, "Event", "start");
        String endValue = requireField(event, 2, "Event", "end");
        start = startValue.startsWith(START_PREFIX)
                ? startValue.substring(START_PREFIX.length()).trim()
                : startValue;
        end = endValue.startsWith(END_PREFIX)
                ? endValue.substring(END_PREFIX.length()).trim()
                : endValue;
        if (start.isBlank() || end.isBlank()) {
            throw new IllegalArgumentException("Event start and end cannot be empty.");
        }
        validateOrder();
        assert !start.isBlank() && !end.isBlank()
                : "Validated event times must remain nonblank after prefix removal";
    }

    /** Validates structured dates and times while retaining free-text event labels. */
    private void validateOrder() {
        Comparable<?> startTime = parseTime(start);
        Comparable<?> endTime = parseTime(end);
        if (start.equalsIgnoreCase(end)) {
            throw new IllegalArgumentException("Event end must be later than its start.");
        }
        if (startTime != null && endTime != null) {
            if (!startTime.getClass().equals(endTime.getClass())) {
                throw new IllegalArgumentException("Use the same date/time format for event start and end.");
            }
            boolean isOrdered = switch (startTime) {
                case LocalDate date -> date.isBefore((LocalDate) endTime);
                case LocalDateTime dateTime -> dateTime.isBefore((LocalDateTime) endTime);
                case LocalTime time -> time.isBefore((LocalTime) endTime);
                default -> true;
            };
            if (!isOrdered) {
                throw new IllegalArgumentException("Event end must be later than its start.");
            }
        }
    }

    /** Parses supported ISO dates, ISO date-times, and clock times when recognizable. */
    private static Comparable<?> parseTime(String value) {
        if (value.matches("[+-]?\\d{4,}-.*")) {
            return value.contains("T") ? LocalDateTime.parse(value) : LocalDate.parse(value);
        }
        if (value.matches("\\d{1,2}:.*") && !value.toLowerCase(Locale.ENGLISH).endsWith("m")) {
            return LocalTime.parse(value);
        }
        if (value.toLowerCase(Locale.ENGLISH).matches("\\d.*[ap]m")) {
            DateTimeFormatter format = DateTimeFormatter.ofPattern(
                    value.contains(":") ? "h:mma" : "ha", Locale.ENGLISH)
                    .withResolverStyle(ResolverStyle.STRICT);
            return LocalTime.parse(value.toUpperCase(Locale.ENGLISH), format);
        }
        return null;
    }

    /**
     * Returns the display representation of this event.
     *
     * @return Display representation including the start and end values.
     */
    @Override
    public String toString() {
        return super.toString()
                + " (from: " + start + " to: " + end + ")";
    }

    /**
     * Returns the representation stored in the task data file.
     *
     * @return Escaped, comma-separated representation of the event.
     */
    @Override
    public String toFileString() {
        return super.toFileString()
                + "," + escapeFileField(start)
                + "," + escapeFileField(end);
    }

}
