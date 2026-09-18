package sozius.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import sozius.exception.SoziusException;

/**
 * DueDate class represents a date with an optional time for DeadlineTask and EventTask
 */
public class DueDate {
    private static final DateTimeFormatter USER_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HHmm").withResolverStyle(ResolverStyle.STRICT);

    private final LocalDate date;
    private final LocalTime time;

    /**
     * Creates a DueDate.
     * @param date Date
     * @param time Time
     */
    public DueDate(LocalDate date, LocalTime time) {
        assert date != null;

        this.date = date;
        this.time = time;
    }

    /**
     * Creates a DueDate from a string.
     * A DueDate has a date in yyyy-MM-dd format
     * and an optional time in HHmm format.
     * @param args the string
     * @return the DueDate
     */
    public static DueDate parse(String args) throws SoziusException {
        String trimmed = args == null ? "" : args.trim();
        if (trimmed.isEmpty()) {
            throw new SoziusException(
                    "Missing date. Use the format yyyy-MM-dd (optionally followed by a time in HHmm)");
        }
        String[] splitArgs = trimmed.split("\\s+");
        if (splitArgs.length > 2) {
            throw new SoziusException("Invalid date: \"" + trimmed + "\" has too many parts. "
                    + "Use the format yyyy-MM-dd HHmm, e.g., 2024-12-31 1800");
        }
        LocalDate date = parseDate(splitArgs[0]);
        LocalTime time = splitArgs.length == 1
                ? null
                : parseTime(splitArgs[1]);
        return new DueDate(date, time);
    }

    private static LocalDate parseDate(String s) throws SoziusException {
        if (!s.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new SoziusException("Invalid date: \"" + s + "\". Use the format yyyy-MM-dd, e.g., 2024-12-31");
        }
        try {
            return LocalDate.parse(s, FILE_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SoziusException("Invalid date: \"" + s + "\" does not exist in the calendar "
                    + "(check the month and day, e.g., Feb 30 is not a real date)");
        }
    }

    private static LocalTime parseTime(String s) throws SoziusException {
        if (!s.matches("\\d{4}")) {
            throw new SoziusException("Invalid time: \"" + s + "\". Use the 24-hour format HHmm, e.g., 1800");
        }
        try {
            return LocalTime.parse(s, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new SoziusException("Invalid time: \"" + s + "\". Hours must be 00-23 and minutes 00-59");
        }
    }

    /**
     * Returns date of a DueDate.
     * @return the date
     */
    public LocalDate getDate() {
        return date;
    }
    /**
     * Returns time of a DueDate, or null if the DueDate doesn't have one.
     * @return the time or null.
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns string representation of the DueDate.
     * For displaying to a user.
     * Date represented as MMM d yyyy
     * Time represented as HHmm
     * @return the string
     */
    public String toUserString() {
        return time == null
                ? date.format(USER_DATE_FORMAT)
                : date.format(USER_DATE_FORMAT) + " " + time.format(TIME_FORMAT);
    }

    /**
     * Returns string representation of the DueDate.
     * For writing to file.
     * Date represented as yyyy-mm-dd
     * Time represented as HHmm
     * @return the string
     */
    public String toFileString() {
        return time == null
                ? date.format(FILE_DATE_FORMAT)
                : date.format(FILE_DATE_FORMAT) + " " + time.format(TIME_FORMAT);
    }
}
