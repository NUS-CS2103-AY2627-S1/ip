package jaku.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/** Describes the interval between consecutive occurrences of a recurring task. */
public enum Recurrence {
    /** Repeats every calendar day. */
    DAILY("daily", 1),
    /** Repeats every seven calendar days. */
    WEEKLY("weekly", 7);

    /** Text accepted from and displayed to the user. */
    private final String label;

    /** Number of calendar days between occurrences. */
    private final int days;

    /**
     * Creates a recurrence interval.
     *
     * @param label user-facing interval name
     * @param days number of days between occurrences
     */
    Recurrence(String label, int days) {
        this.label = label;
        this.days = days;
    }

    /**
     * Parses a recurrence interval entered by a user.
     *
     * @param text interval text
     * @return matching recurrence interval
     * @throws IllegalArgumentException if the interval is unsupported
     */
    public static Recurrence fromLabel(String text) {
        String normalizedText = text.toLowerCase(Locale.ROOT);
        for (Recurrence recurrence : values()) {
            if (recurrence.label.equals(normalizedText)) {
                return recurrence;
            }
        }
        throw new IllegalArgumentException("Unsupported recurrence: " + text);
    }

    /**
     * Returns the lower-case interval text shown to users.
     *
     * @return user-facing recurrence label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Advances a calendar date by this interval.
     *
     * @param date date to advance
     * @return date one recurrence interval later
     */
    public LocalDate advance(LocalDate date) {
        return date.plusDays(days);
    }

    /**
     * Reverses a calendar date by this interval.
     *
     * @param date date to reverse
     * @return date one recurrence interval earlier
     */
    public LocalDate reverse(LocalDate date) {
        return date.minusDays(days);
    }

    /**
     * Advances a date-time by this interval.
     *
     * @param dateTime date-time to advance
     * @return date-time one recurrence interval later
     */
    public LocalDateTime advance(LocalDateTime dateTime) {
        return dateTime.plusDays(days);
    }

    /**
     * Reverses a date-time by this interval.
     *
     * @param dateTime date-time to reverse
     * @return date-time one recurrence interval earlier
     */
    public LocalDateTime reverse(LocalDateTime dateTime) {
        return dateTime.minusDays(days);
    }
}
