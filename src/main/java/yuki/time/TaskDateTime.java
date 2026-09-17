package yuki.time;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Holds either a parsed date, a parsed date-time, or the user's original text.
 */
public class TaskDateTime {
    private final LocalDate date;
    private final LocalDateTime dateTime;
    private final String text;

    private TaskDateTime(LocalDate date, LocalDateTime dateTime, String text) {
        assert (date != null ? 1 : 0)
                + (dateTime != null ? 1 : 0)
                + (text != null ? 1 : 0) == 1
                : "TaskDateTime must contain exactly one representation";

        this.date = date;
        this.dateTime = dateTime;
        this.text = text;
    }

    /**
     * Creates a value containing both a date and a time.
     *
     * @param dateTime Date and time to store.
     * @return A value containing the supplied date and time.
     * @throws NullPointerException If {@code dateTime} is {@code null}.
     */
    public static TaskDateTime of(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime must not be null");
        return new TaskDateTime(null, dateTime, null);
    }

    /**
     * Creates a value containing a date without a time.
     *
     * @param date Date to store.
     * @return A value containing the supplied date.
     * @throws NullPointerException If {@code date} is {@code null}.
     */
    public static TaskDateTime of(LocalDate date) {
        Objects.requireNonNull(date, "date must not be null");
        return new TaskDateTime(date, null, null);
    }

    /**
     * Creates a value containing text that does not match a supported date format.
     *
     * @param text Text to store.
     * @return A value containing the supplied text.
     * @throws NullPointerException If {@code text} is {@code null}.
     */
    public static TaskDateTime of(String text) {
        Objects.requireNonNull(text, "text must not be null");
        if (text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
        if (text.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("text must not contain control characters");
        }
        return new TaskDateTime(null, null, text);
    }

    /** Returns whether this value contains both a date and a time. */
    public boolean hasDateTime() {
        return dateTime != null;
    }

    /** Returns whether this value contains a date without a time. */
    public boolean hasDateOnly() {
        return date != null;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getText() {
        return text;
    }

    /**
     * Returns this value as a date-time suitable for reminder comparisons.
     *
     * <p>A date without a time is interpreted as 23:59 on that date. Text values
     * cannot be converted and return an empty result.</p>
     *
     * @return The comparable date-time, or an empty result for text values.
     */
    public Optional<LocalDateTime> toReminderDateTime() {
        if (hasDateTime()) {
            return Optional.of(dateTime);
        }
        if (hasDateOnly()) {
            return Optional.of(date.atTime(23, 59));
        }
        return Optional.empty();
    }

    /** Compares values of the same parsed type; text or mixed values are not ordered here. */
    public boolean isBefore(TaskDateTime other) {
        Objects.requireNonNull(other, "other must not be null");
        if (hasDateTime() && other.hasDateTime()) {
            return dateTime.isBefore(other.dateTime);
        }
        return hasDateOnly() && other.hasDateOnly() && date.isBefore(other.date);
    }

    /** Returns whether this value and another value use the same comparable date representation. */
    public boolean hasComparableType(TaskDateTime other) {
        Objects.requireNonNull(other, "other must not be null");
        return hasDateTime() && other.hasDateTime()
                || hasDateOnly() && other.hasDateOnly();
    }

    /** Returns whether this value stores exactly the same date, date-time, or text as another value. */
    public boolean hasSameValueAs(TaskDateTime other) {
        return other != null
                && Objects.equals(date, other.date)
                && Objects.equals(dateTime, other.dateTime)
                && Objects.equals(text, other.text);
    }
}
