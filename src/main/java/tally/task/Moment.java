package tally.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A day, and sometimes an hour within it.
 *
 * <p>An event is written as two of these. Most are a bare day, because most events are
 * remembered by the day they fall on, but a lecture that runs from four to six needs the
 * hour or the two ends say nothing a plain date does not.
 *
 * <p>The time is optional rather than required, so that an event written before this
 * existed still reads, and so that a day-long event is not made to invent an hour.
 */
public record Moment(LocalDate date, Optional<LocalTime> time) {
    /**
     * Pattern for what may be typed: a date, and after it an optional 24-hour time.
     *
     * <p>Spelled out rather than handed to a lenient parser, which would also read forms
     * such as "+999999999-12-31" that nobody means to type and that overflow the date
     * arithmetic done later.
     */
    private static final Pattern FORM =
            Pattern.compile("(\\d{4}-\\d{2}-\\d{2})(?: (\\d{2}:\\d{2}))?");

    /**
     * How the hour is shown: "4:00 pm" rather than the 16:00 that was typed.
     *
     * <p>Level-8 asks that what is read in one form be shown in another, and a clock
     * face is how most people read an hour back. The locale is fixed so that am and pm
     * do not depend on the machine the chatbot runs on.
     */
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);

    /**
     * Creates a moment, keeping the hour no finer than the minute it is written in.
     *
     * <p>Nothing typed or saved carries seconds, and one that reached an hour here
     * would be written to the data file as "16:04:33", which is not a form the reader
     * takes: the task would be reported as damage on the next start.
     */
    public Moment {
        time = time.map(hour -> hour.truncatedTo(ChronoUnit.MINUTES));
    }

    /**
     * Returns the moment some text names, if it names one at all.
     *
     * @param text the text to read, as it was written.
     * @return the day and any hour it names, or empty if it is not a date written as
     *     yyyy-mm-dd, optionally followed by a time written as HH:mm.
     */
    public static Optional<Moment> read(String text) {
        Matcher parts = FORM.matcher(Task.tidySpacing(text));
        if (!parts.matches()) {
            return Optional.empty();
        }
        try {
            LocalDate date = LocalDate.parse(parts.group(1));
            return Optional.of(new Moment(date, Optional.ofNullable(parts.group(2))
                    .map(LocalTime::parse)));
        } catch (DateTimeParseException exception) {
            // The shape was right but the day or the hour was not: "2026-02-30", "25:00".
            return Optional.empty();
        }
    }

    /**
     * Returns this moment as the earliest instant it covers.
     *
     * <p>A start written without an hour begins when its day does.
     *
     * @return the day and hour, midnight standing in for an hour not given.
     */
    public LocalDateTime asStart() {
        return date.atTime(time.orElse(LocalTime.MIN));
    }

    /**
     * Returns this moment as the latest instant it covers.
     *
     * <p>An end written without an hour runs to the close of its day, which is what
     * makes "from the 12th at four to the 14th" an event rather than a contradiction.
     *
     * @return the day and hour, the last instant of the day standing in for an hour
     *     not given.
     */
    public LocalDateTime asEnd() {
        return date.atTime(time.orElse(LocalTime.MAX));
    }

    /**
     * Returns this moment written as the data file keeps it.
     *
     * <p>Both parts are written the way java.time prints them, which is the same form
     * they are read back from, so they survive a round trip.
     *
     * @return for example "2026-09-12" or "2026-09-12 16:00".
     */
    public String toSaveFormat() {
        return time.map(hour -> date + " " + hour).orElseGet(date::toString);
    }

    /**
     * Returns this moment as the user sees it.
     *
     * @return for example "Sep 12 2026" or "Sep 12 2026 4:00 pm".
     */
    @Override
    public String toString() {
        return time.map(hour -> Task.formatDate(date) + " " + DISPLAY_TIME.format(hour)
                        .toLowerCase(Locale.ENGLISH))
                .orElseGet(() -> Task.formatDate(date));
    }
}
