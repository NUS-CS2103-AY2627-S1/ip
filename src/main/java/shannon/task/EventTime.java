package shannon.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import shannon.exception.InvalidDateException;

/**
 * One end of an event: a date, and the time of day if the user gave one, e.g.
 * {@code 2026-08-09 14:00}, or just {@code 2026-08-09}.
 * <p>
 * The time is optional so that an event lasting whole days, such as a camp, does not have to
 * make up a time nobody asked for. An end given without a time covers that whole day, so an
 * event {@code /from 2026-08-09 /to 2026-08-09} lasts all of the 9th rather than being over the
 * moment it begins.
 * <p>
 * A {@code record}, like {@code shannon.Response}, because it is nothing but its two values. The
 * {@code equals} that Java generates for it is what lets two events be compared for duplicates.
 *
 * @param date the day; never null.
 * @param time the time of day, or {@code null} if only a date was given.
 */
public record EventTime(LocalDate date, LocalTime time) {

    /** Checks the one value that must always be present. */
    public EventTime {
        assert date != null : "An event time must have a date";
    }

    /**
     * Turns the text after {@code /from} or {@code /to} into an event time.
     * <p>
     * Used by both the command parser and the save-file loader, so the two accept exactly the
     * same text.
     *
     * @param text a date in {@code yyyy-mm-dd} form, optionally followed by a space and a time in
     *             24-hour {@code H:mm} form.
     * @return the date and time it names.
     * @throws InvalidDateException if the text is not in that form, or names a date or time that
     *                              does not exist.
     */
    public static EventTime parse(String text) throws InvalidDateException {
        String[] parts = text.trim().split("\\s+");
        if (parts.length > 2 || !DateUtil.hasDateShape(parts[0])) {
            // Checked before the date itself is read, so that text such as "tomorrow 2pm" is
            // answered with both accepted forms rather than a complaint about the date alone.
            throw InvalidDateException.ofUnreadableDateTime(text.trim());
        }
        LocalDate date = DateUtil.parseDate(parts[0]);
        LocalTime time = parts.length == 2 ? DateUtil.parseTime(parts[1]) : null;
        return new EventTime(date, time);
    }

    /**
     * Returns the first moment this covers, which is when an event starting here begins.
     *
     * @return the given time, or the start of the day if no time was given.
     */
    public LocalDateTime toStartDateTime() {
        return time == null ? date.atStartOfDay() : date.atTime(time);
    }

    /**
     * Returns the moment this is over, which is when an event ending here ends.
     *
     * @return the given time, or the start of the next day if no time was given, so that the
     *         whole day is included.
     */
    public LocalDateTime toEndDateTime() {
        return time == null ? date.plusDays(1).atStartOfDay() : date.atTime(time);
    }

    /** Renders in the form shown to the user, e.g. {@code Aug 09 2026 14:00}. */
    @Override
    public String toString() {
        String shownDate = date.format(DateUtil.DATE_DISPLAY_FORMAT);
        return time == null ? shownDate : shownDate + " " + time.format(DateUtil.TIME_FORMAT);
    }

    /**
     * Returns this in the form it is saved in, e.g. {@code 2026-08-09 14:00}, which
     * {@link #parse(String)} reads back.
     *
     * @return the text to write to the save file.
     */
    public String toFileFormat() {
        String savedDate = date.format(DateUtil.DATE_INPUT_FORMAT);
        return time == null ? savedDate : savedDate + " " + time.format(DateUtil.TIME_FORMAT);
    }
}
