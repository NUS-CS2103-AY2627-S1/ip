package amadeus.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import amadeus.AmadeusException;

/**
 * A date written by the user, with an optional time of day.
 * <p>
 * Deadlines and events store one of these instead of a plain String, so the app
 * actually understands that "2/12/2019 1800" means the 2nd of December 2019 at
 * 6pm. That is what makes it possible to compare a task against a date, as the
 * "on" command does.
 * <p>
 * The time is optional because "deadline submit report /by 2019-10-15" is a
 * perfectly reasonable thing to type. A missing time is stored as null, and
 * every method below checks for that. (A tidier but more advanced alternative
 * is {@code Optional<LocalTime>}, which makes "there may be no time here"
 * visible in the type itself.)
 */
public class TaskDateTime {

    /**
     * Input formats accepted when the user includes a time of day.
     * Tried before the date-only formats below, so "2019-10-15 1800" is read as
     * a date and a time rather than failing.
     */
    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        strictFormatter("uuuu-MM-dd HHmm"),
        strictFormatter("uuuu-MM-dd HH:mm"),
        strictFormatter("d/M/uuuu HHmm"),
        strictFormatter("d/M/uuuu HH:mm"),
    };

    /** Input formats accepted when the user gives a date only. */
    private static final DateTimeFormatter[] DATE_FORMATS = {
        strictFormatter("uuuu-MM-dd"),
        strictFormatter("d/M/uuuu"),
    };

    /** Shown to the user in a message a person would ask for, e.g. "2019-10-15". */
    private static final String EXAMPLE_FORMATS =
            "2019-10-15, 2019-10-15 1800, 2/12/2019, or 2/12/2019 1800";

    /**
     * Formats used when printing.
     * Locale.ENGLISH is given explicitly so that the month always prints as
     * "Oct" regardless of the language the computer is set to.
     */
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    /** Format used for the time of day inside the save file, e.g. "1800". */
    private static final DateTimeFormatter STORAGE_TIME =
            DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH);

    private final LocalDate date;

    /** The time of day, or null when the user gave a date without one. */
    private final LocalTime time;

    /** Private so that every instance has to come from {@link #parse(String)}. */
    private TaskDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Builds a formatter that rejects impossible dates such as 31/2/2019.
     * <p>
     * The patterns use "uuuu" rather than "yyyy" for the year: STRICT resolving
     * treats "yyyy" as a year within an era and then complains that no era was
     * given, while "uuuu" is the plain year everyone means. Without STRICT, Java
     * would quietly turn 31 February into 28 February.
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
                .withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Reads a date, and optionally a time, from what the user typed.
     *
     * @param input text such as "2019-10-15" or "2/12/2019 1800"
     * @return the date and time it describes
     * @throws AmadeusException if the text does not match any accepted format
     */
    public static TaskDateTime parse(String input) throws AmadeusException {
        // Extra spaces between the date and the time would not match the pattern,
        // so any run of whitespace is reduced to a single space first.
        String trimmed = input.trim().replaceAll("\\s+", " ");

        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                // Parsed as one LocalDateTime and then split, rather than parsed
                // twice, so the date and the time cannot disagree.
                LocalDateTime parsed = LocalDateTime.parse(trimmed, formatter);
                return new TaskDateTime(parsed.toLocalDate(), parsed.toLocalTime());
            } catch (DateTimeParseException e) {
                // Not this format; fall through and try the next one.
            }
        }
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(trimmed, formatter), null);
            } catch (DateTimeParseException e) {
                // Not this format either.
            }
        }
        throw new AmadeusException("A hundred apologies, I cannot read '" + input + "' as a date, sir.",
                "Please use one of: " + EXAMPLE_FORMATS);
    }

    /** Returns the calendar day, with the time of day dropped. */
    public LocalDate getDate() {
        return date;
    }

    /** Returns true if this falls on the given day, whatever the time of day is. */
    public boolean isOn(LocalDate other) {
        return date.equals(other);
    }

    /** Returns a date on its own formatted the way the chatbot prints dates. */
    public static String format(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Returns the form written to the save file, e.g. "2019-12-02 1800".
     * <p>
     * Deliberately one of the formats {@link #parse(String)} accepts, so that
     * saving and loading are exact opposites of each other.
     */
    public String toStorageString() {
        String storedDate = date.toString(); // LocalDate.toString() is already uuuu-MM-dd
        return time == null ? storedDate : storedDate + " " + time.format(STORAGE_TIME);
    }

    /**
     * Returns the form shown to the user, e.g. "Oct 15 2019" or
     * "Dec 02 2019, 6:00PM".
     */
    @Override
    public String toString() {
        String shownDate = date.format(DISPLAY_DATE);
        return time == null ? shownDate : shownDate + ", " + time.format(DISPLAY_TIME);
    }
}
