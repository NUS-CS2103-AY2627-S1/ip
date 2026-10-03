package ramly.model;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import ramly.exception.TaskValidationException;
import ramly.validation.DateTimeParser;
import ramly.validation.InputValidator;

/** Represents a task occurring between a start and end time. */
public class Event extends Task {

    protected String from;
    protected String to;


    /** Creates an event with the specified description and time bounds. */
    public Event(String description, String from, String to) {
        this(description, from, to, false);
    }

    /** Creates an event and applies either current-input or legacy-storage validation. */
    private Event(String description, String from, String to, boolean allowLegacyValues) {
        super(description, TaskType.EVENT);
        this.from = InputValidator.normalizeStoredText(from, "event start", 40);
        this.to = InputValidator.normalizeStoredText(to, "event end", 40);

        if (allowLegacyValues) {
            validateStoredRangeWhenComparable();
        } else {
            LocalDateTime start = DateTimeParser.parseEventBoundary(this.from);
            LocalDateTime end = DateTimeParser.parseEventBoundary(this.to);
            validateRange(start, end);
        }
    }

    /** Creates an event from storage, allowing legacy free-text time bounds. */
    public static Event fromStorage(String description, String from, String to) {
        return new Event(description, from, to, true);
    }

    /** Rejects invalid ranges when both legacy storage values can be compared. */
    private void validateStoredRangeWhenComparable() {
        Optional<LocalDateTime> start = DateTimeParser.tryParseEventBoundary(from);
        Optional<LocalDateTime> end = DateTimeParser.tryParseEventBoundary(to);
        if (start.isPresent() && end.isPresent()) {
            validateRange(start.get(), end.get());
        }
    }

    /** Requires an event to end strictly after it starts. */
    private void validateRange(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new TaskValidationException("The event start must be earlier than its end.");
        }
    }

    /** Returns whether another event has the same description and time bounds. */
    @Override
    public boolean hasSameIdentity(Task other) {
        if (!(other instanceof Event)) {
            return false;
        }
        Event otherEvent = (Event) other;
        return super.hasSameIdentity(other)
                && normalizedTime(from).equals(normalizedTime(otherEvent.from))
                && normalizedTime(to).equals(normalizedTime(otherEvent.to));
    }

    /** Normalizes a time boundary for duplicate detection. */
    private String normalizedTime(String value) {
        Optional<LocalDateTime> parsed = DateTimeParser.tryParseEventBoundary(value);
        return parsed.map(LocalDateTime::toString)
                .orElseGet(() -> value.toLowerCase(Locale.ROOT));
    }

    /** Returns the serialized event representation. */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + from + " | " + to;
    }

    /** Returns the user-facing event representation. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from + " to: " + this.to + ")";
    }
}
