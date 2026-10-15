package bern.datetime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Stores an immutable date with an optional time, without a time zone.
 * Values are ordered chronologically, with a missing time sorting before any specified time on the same date.
 * This natural ordering is inconsistent with {@link Object#equals(Object)} because equality uses object identity.
 */
public class DateTime implements Comparable<DateTime> {
    /**
     * Formats dates for display and storage.
     */
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    /**
     * Formats times for display and storage.
     */
    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh.mma");

    /**
     * Separates the date and time parts in stored date-time values.
     */
    static final String SEPARATOR = " @ ";

    private final LocalDate localDate;

    /**
     * Stores the optional time, or {@code null} when only a calendar date was specified.
     */
    private final LocalTime localTime;

    /**
     * Creates a date with an optional time component.
     *
     * @param localDate The non-null date to store.
     * @param localTime The time to store, or {@code null} for a date without a specified time.
     */
    DateTime(LocalDate localDate, LocalTime localTime) {
        this.localDate = localDate;
        this.localTime = localTime;
    }

    /**
     * Returns the current date and time using the system's default time zone.
     *
     * @return A DateTime containing the current date and time.
     */
    public static DateTime now() {
        return new DateTime(LocalDate.now(), LocalTime.now());
    }

    /**
     * Returns the stored calendar date.
     *
     * @return The calendar date.
     */
    public LocalDate getDate() {
        return localDate;
    }

    /**
     * Returns the specified time, or {@code null} when only a date was given.
     *
     * @return The optional time component.
     */
    public LocalTime getTime() {
        return localTime;
    }

    /**
     * Returns the formatted date and its time, when specified.
     * Uses {@code dd MMMM yyyy} for the date and appends {@code " @ "} plus {@code hh.mma} when a time is present.
     * Month names and the AM/PM marker use the formatters' default locale; seconds and nanoseconds are omitted.
     *
     * @return The formatted date, optionally followed by the formatted time when one is present.
     */
    @Override
    public String toString() {
        if (localTime == null) {
            return localDate.format(DATE_FORMATTER);
        }

        return String.format("%s%s%s", localDate.format(DATE_FORMATTER), SEPARATOR,
                localTime.format(TIME_FORMATTER));
    }

    /**
     * Compares this value with another date and optional time in chronological order.
     * On the same date, an omitted time sorts before any specified time, including midnight.
     * Two values with the same date and no specified times compare as equal.
     *
     * @param other The non-null date and optional time to compare with.
     * @return A negative value, zero, or a positive value when this value is earlier, equal, or later.
     * @throws NullPointerException If {@code other} is {@code null}.
     */
    @Override
    public int compareTo(DateTime other) {
        int dateOrder = compareDate(other);
        if (dateOrder != 0) {
            return dateOrder;
        }

        if (localTime == null && other.localTime == null) {
            return 0;
        } else if (localTime == null) {
            return -1;
        } else if (other.localTime == null) {
            return 1;
        }

        return localTime.compareTo(other.localTime);
    }

    /**
     * Compares the calendar dates without considering either time component.
     *
     * @param other The non-null date-time to compare with.
     * @return A negative value, zero, or a positive value when this date is earlier, equal, or later.
     * @throws NullPointerException If {@code other} is {@code null}.
     */
    public int compareDate(DateTime other) {
        return localDate.compareTo(other.localDate);
    }
}
