package com.fireinyu.themyth.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.fireinyu.themyth.Defaults;
import com.fireinyu.themyth.exceptions.DateFormatException;

/**
 * Represents a local date and time used by The Myth.
 * Uses LocalDateTime internally.
 *
 * @see MythDateTime
 * @see LocalDateTime
 */
public class MythDateTime implements Comparable<MythDateTime> {

    private final LocalDateTime datetime;

    /**
     * Initializes a MythDateTime at a local datetime instant.
     *
     * @param datetime datetime instant of this MythDateTime.
     * @see String
     * @see LocalDateTime
     */
    public MythDateTime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    /**
     * Returns the current local date and time.
     *
     * @return the current date and time.
     */
    public static MythDateTime now() {
        return new MythDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    /**
     * Parses an input String into a MythDateTime.
     * Accepts formats from yyyy through yyyy-MM-dd-HH-mm-ss, defaulting omitted fields.
     *
     * @param datetime the input String.
     * @return the MythDateTime instance corresponding to the input String.
     * @see String
     */
    public static MythDateTime parse(String datetime) {
        String[] parts = datetime.split("-");
        if (parts.length == 0 || parts.length > 6) {
            throw new DateFormatException(datetime);
        }
        try {
            return new MythDateTime(LocalDateTime.of(
                    Integer.parseInt(parts[0]),
                    parts.length > 1 ? Integer.parseInt(parts[1]) : 1,
                    parts.length > 2 ? Integer.parseInt(parts[2]) : 1,
                    parts.length > 3 ? Integer.parseInt(parts[3]) : 0,
                    parts.length > 4 ? Integer.parseInt(parts[4]) : 0,
                    parts.length > 5 ? Integer.parseInt(parts[5]) : 0
            ));
        } catch (NumberFormatException e) {
            throw new DateFormatException(datetime);
        }
    }

    /**
     * Serializes this MythDateTime into a String.
     * Uses the format yyyy-MM-dd-HH-mm-ss.
     *
     * @return the serialized String representing this MythDateTime.
     * @see String
     */
    public String dump() {
        return datetime.format(DateTimeFormatter.ofPattern(Defaults.DATE_INPUTFORMAT));
    }

    /**
     * Returns whether this date-time is strictly between the given start and end values.
     *
     * @param start start MythDateTime.
     * @param end end MythDateTime.
     * @return whether this MythDateTime is between given start and end MythDateTimes.
     */
    public boolean isBetween(MythDateTime start, MythDateTime end) {
        return isBefore(end) && start.isBefore(this);
    }

    /**
     * Returns whether this date-time is strictly before another date-time.
     *
     * @param other other MythDateTime.
     * @return whether this MythDateTime is before the other MythDateTime.
     */
    public boolean isBefore(MythDateTime other) {
        return datetime.isBefore(other.datetime);
    }

    /**
     * Returns a user-friendly String representation of this MythDateTime.
     *
     * @return user-friendly String representation of this MythDateTime.
     */
    @Override
    public String toString() {
        return datetime.format(DateTimeFormatter.ofPattern("LLL dd yyyy hh:mm:ss a"));
    }

    /**
     * Compares this date-time with another date-time.
     *
     * @param other date-time to compare with.
     * @return a negative value, zero, or a positive value if this date-time is earlier than,
     *         equal to, or later than the other date-time.
     */
    @Override
    public int compareTo(MythDateTime other) {
        return datetime.compareTo(other.datetime);
    }
}
