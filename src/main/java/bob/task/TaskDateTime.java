package bob.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bob.BobException;

/**
 * Represents a point in time attached to a task: the day it falls on, and the
 * time of day when the user gave one.
 *
 * <p>{@link Deadline} and {@link Event} used to keep whatever the user typed as
 * plain text, so {@code 2026-12-02}, {@code Sunday} and {@code no idea :-p} were
 * all equally acceptable and all equally meaningless to the chatbot. Holding a
 * real {@link LocalDate} instead means the date has been understood rather than
 * merely copied: it can be shown back in a friendlier form than it was typed in,
 * and two of them can be compared to answer questions such as which task is due
 * first — neither of which can be done with a string.
 *
 * <p>That comparing is what {@link bob.command.CommandWord#ON CommandWord.ON},
 * {@link bob.command.CommandWord#BEFORE CommandWord.BEFORE},
 * {@link bob.command.CommandWord#AFTER CommandWord.AFTER} and
 * {@link bob.command.CommandWord#NEXT CommandWord.NEXT} are built on. This class
 * implements {@link Comparable} so that a list of dates can be sorted, and offers
 * {@link #isOn}, {@link #isBefore} and {@link #isAfter} so that the code asking
 * the questions can stay in terms of days rather than reaching inside for the
 * {@link LocalDate} and comparing it itself.
 *
 * <p>The price is that text which is not a date can no longer be accepted, so
 * {@code no idea :-p} is now refused with an explanation instead of being stored.
 * Keeping both — a date when the text parses as one, free text when it does not —
 * is possible, but it means every part of the program that reads a date has to
 * cope with two kinds of answer, which is a lot of complication to buy back a
 * habit the requirements are asking to drop.
 *
 * <p>This class is immutable: both fields are {@code final} and neither
 * {@link LocalDate} nor {@link LocalTime} can be changed after it is made. A
 * {@code TaskDateTime} can therefore be passed around freely, with no risk of one
 * holder of it seeing another holder's change.
 */
public class TaskDateTime implements Comparable<TaskDateTime> {

    /** A day on its own, written the way the user types it. */
    public static final String EXAMPLE_DATE = "2026-12-02";

    /** A day and a time of day, written the way the user types them. */
    public static final String EXAMPLE_DATE_TIME = "2026-12-02 1800";

    /**
     * How the time of day is typed, and how it is written to the save file:
     * four digits on a 24-hour clock, so {@code 1800} is six in the evening.
     *
     * <p>The strict resolver is what refuses {@code 2400}. The default one reads it,
     * without a word, as midnight at the start of the same day — a day earlier than
     * the user meant. Strictly, only {@code 0000} to {@code 2359} are times.
     */
    private static final DateTimeFormatter INPUT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("HHmm").withResolverStyle(ResolverStyle.STRICT);

    /**
     * A day written in the form {@link #parse} reads, {@code yyyy-mm-dd}, with the
     * year, the month and the day captured in that order.
     *
     * <p>Used only to explain text that could not be read: text of this form that is
     * still not a day names a month or a day that does not exist.
     */
    private static final Pattern DAY_FORM = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");

    /**
     * A time of day written in the form {@link #parse} reads, four digits, with the
     * hour and the minute captured in that order.
     *
     * <p>Used, like {@link #DAY_FORM}, only to explain text that could not be read.
     */
    private static final Pattern TIME_FORM = Pattern.compile("(\\d{2})(\\d{2})");

    /**
     * How the day is shown back to the user, for example {@code Dec 02 2026}.
     *
     * <p>The language is stated here rather than left to the computer the chatbot
     * runs on, so that the month is always named in English instead of changing
     * with that computer's regional settings.
     *
     * <p>The year is {@code uuuu}, the year as it is counted straight through zero
     * and on into the negative, rather than {@code yyyy}, the year within an era.
     * The two agree from year 1 onwards, which is every date anyone is likely to
     * type, but {@code yyyy} shows the year {@code 0000} as {@code 0001} and every
     * year before it one out as well, since it counts 1 BC as year 1 of its era and
     * says nothing about which era it means.
     */
    private static final DateTimeFormatter OUTPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /**
     * How the time of day is shown back to the user, for example {@code 18:00}.
     *
     * <p>Shown on a 24-hour clock, as it is typed. A 12-hour {@code 6:00 pm} would
     * read more naturally, but it would also mean choosing how to spell "pm" for
     * every language the chatbot might be run in.
     */
    private static final DateTimeFormatter OUTPUT_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** The day this date falls on. */
    private final LocalDate date;

    /**
     * The time of day, or {@code null} when the user gave a day and no time.
     *
     * <p>A missing time is left as {@code null} rather than filled in with
     * midnight, because a task due "on the 2nd" and a task due "at midnight on
     * the 2nd" are different things, and only the second one should be shown
     * with a time. {@link java.util.Optional} would say the same thing without
     * {@code null}, but it is meant for return values rather than for fields, so
     * the simpler field is used and the meaning is written down here instead.
     */
    private final LocalTime time;

    /**
     * Creates a date from parts that have already been checked.
     *
     * <p>Private, so that {@link #parse} is the only way in: every
     * {@code TaskDateTime} that exists has therefore come from text this class was
     * able to understand.
     *
     * @param date the day this falls on.
     * @param time the time of day, or {@code null} if there is none.
     */
    private TaskDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    /**
     * Returns the date written in {@code text}: a day as {@code yyyy-mm-dd},
     * optionally followed by a time of day as four digits on a 24-hour clock —
     * for example {@value #EXAMPLE_DATE} or {@value #EXAMPLE_DATE_TIME}.
     *
     * <p>One way of writing a date is accepted rather than several. Accepting
     * {@code 2/12/2026} as well would mean deciding whether that is the 2nd of
     * December or the 12th of February, and either answer is wrong for half the
     * world; asking for the year first leaves nothing to guess.
     *
     * <p>Text written in that form can still name a day or a time that does not
     * exist, such as {@code 2026-02-30} or {@code 2400}. That is refused with a
     * message saying what is wrong with it, since asking again for the form would
     * point the user at the one part they had right.
     *
     * @param text what the user typed after {@code /by}, {@code /from} or {@code /to}.
     * @return the date that text names.
     * @throws BobException if the text is not a day, or a day and a time, in that
     *                      form, or if it names a day or a time that does not exist.
     */
    public static TaskDateTime parse(String text) throws BobException {
        String[] parts = text.trim().split("\\s+");
        if (parts.length > 2) {
            throw createUnreadableDateError(text);
        }
        // split always returns at least one part, and the check above has ruled out
        // more than two, so the two cases read below are the only ones left.
        assert parts.length == 1 || parts.length == 2 : "Unexpected " + parts.length + " parts";
        LocalDate date = readDay(parts[0]).orElseThrow(() -> createUnreadableDateError(text));
        if (parts.length == 1) {
            return new TaskDateTime(date, null);
        }
        requireExistingTime(parts[1]);
        try {
            return new TaskDateTime(date, LocalTime.parse(parts[1], INPUT_TIME_FORMAT));
        } catch (DateTimeParseException e) {
            // Four digits naming a time that exists have been read above, so what is
            // left is text of some other shape, such as 6pm or 18:00.
            throw createUnreadableDateError(text);
        }
    }

    /**
     * Returns the day {@code dayText} names, or nothing at all when it is not written
     * as {@code yyyy-mm-dd} — which is for the caller to report, since only the caller
     * knows whether it was asking for a day or for a date with a time.
     *
     * <p>Only that one form is read, so the year is four digits and runs from
     * {@code 0001} to {@code 9999}. {@link LocalDate} by itself would also accept a
     * year outside those, written with a sign, as {@code +10000-01-01} or
     * {@code -0001-06-01}. Nobody keeps tasks in those years, the chatbot tells every
     * user to write a date as {@code yyyy-mm-dd}, and accepting a form it never
     * mentions made {@code +10000-01-01} a date while {@code 10000-01-01} was not.
     *
     * @param dayText the word the user typed where a day was expected.
     * @return the day that text names, or nothing if it is not written as a day.
     * @throws BobException if it is written as a day but names a year, a month or a
     *                      day that does not exist.
     */
    private static Optional<LocalDate> readDay(String dayText) throws BobException {
        if (!DAY_FORM.matcher(dayText).matches()) {
            return Optional.empty();
        }
        requireExistingDay(dayText);
        // Every part has been checked above, so there is nothing left for LocalDate
        // to refuse.
        return Optional.of(LocalDate.parse(dayText));
    }

    /**
     * Returns the day written in {@code text} as {@code yyyy-mm-dd}, for example
     * {@value #EXAMPLE_DATE}.
     *
     * <p>This is the whole-day counterpart to {@link #parse}, used by the commands
     * that ask about a day rather than about a moment in it. A time of day is
     * refused here rather than quietly ignored: a user who typed
     * {@code on 2026-12-02 1800} is asking something this chatbot does not answer,
     * and silently treating it as the whole of the 2nd would hide that.
     *
     * @param text what the user typed after the command word.
     * @return the day that text names.
     * @throws BobException if the text is not a day in that form, or names a day that
     *                      does not exist, such as {@code 2026-02-30}.
     */
    public static LocalDate parseDay(String text) throws BobException {
        String dayText = text.trim();
        return readDay(dayText).orElseThrow(() ->
                BobException.withExample("I don't understand \"" + text + "\" as a day."
                        + "\nWrite the day as yyyy-mm-dd, with no time after it.", EXAMPLE_DATE));
    }

    /**
     * Returns a day written the friendly way dates are shown back to the user,
     * for example {@code Dec 02 2026}.
     *
     * <p>Static, and taking a plain {@link LocalDate}, so that a day the user asked
     * about can be echoed in the same form as the dates on the tasks listed under
     * it, without having to be wrapped in a {@code TaskDateTime} first.
     *
     * @param day the day to write out.
     */
    public static String formatDay(LocalDate day) {
        return day.format(OUTPUT_DATE_FORMAT);
    }

    /**
     * Checks that {@code dayText}, if it is written as {@code yyyy-mm-dd}, names a
     * day that exists.
     *
     * <p>Text in any other form passes unchecked, because what is wrong with it is
     * its form, and the caller reports that. The parts are checked from the largest
     * down: how many days a month has means nothing for a month that does not exist,
     * and a month means nothing in a year that does not exist either. {@link YearMonth}
     * knows how long each month is, leap years included.
     *
     * <p>A part written as zero is reported as the thing that does not exist, rather
     * than by the length of whatever contains it. The counting starts at one, so a
     * zero is out of range at either end for the same reason, and saying that
     * December has 31 days would leave the user looking for a 31-day month to put
     * their day 0 in.
     *
     * @param dayText the word the user typed where a day was expected.
     * @throws BobException if it is written as a day but names a year, a month or a day
     *                      that does not exist.
     */
    private static void requireExistingDay(String dayText) throws BobException {
        Matcher matcher = DAY_FORM.matcher(dayText);
        if (!matcher.matches()) {
            return;
        }
        int year = Integer.parseInt(matcher.group(1));
        if (year < 1) {
            // Only 0000 can reach this, since the form allows no sign.
            throw new BobException(dayText + " isn't a real day: there is no year 0.");
        }
        int month = Integer.parseInt(matcher.group(2));
        if (month < 1 || month > 12) {
            throw new BobException(dayText + " isn't a real day: there is no month " + month + ".");
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        int day = Integer.parseInt(matcher.group(3));
        if (day < 1) {
            // Only 00 can reach this, since the form allows no sign.
            throw new BobException(dayText + " isn't a real day: there is no day 0.");
        }
        if (day > yearMonth.lengthOfMonth()) {
            // The month is named in English for the reason given at OUTPUT_DATE_FORMAT.
            String monthName = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            throw new BobException(dayText + " isn't a real day: " + monthName + " " + yearMonth.getYear()
                    + " has " + yearMonth.lengthOfMonth() + " days.");
        }
    }

    /**
     * Checks that {@code timeText}, if it is written as four digits, names a time of
     * day that exists.
     *
     * <p>As with {@link #requireExistingDay}, text in any other form passes unchecked.
     * Both the hour and the minute are named in the message, since {@code 1260} is
     * below {@code 2359} and a range of times alone would not explain it.
     *
     * @param timeText the word the user typed where a time of day was expected.
     * @throws BobException if it is written as a time but its hour or minute does
     *                      not exist.
     */
    private static void requireExistingTime(String timeText) throws BobException {
        Matcher matcher = TIME_FORM.matcher(timeText);
        if (!matcher.matches()) {
            return;
        }
        int hour = Integer.parseInt(matcher.group(1));
        int minute = Integer.parseInt(matcher.group(2));
        if (hour > 23 || minute > 59) {
            throw new BobException(timeText
                    + " isn't a real time: hours go up to 23 and minutes up to 59.");
        }
    }

    /**
     * Returns the error to report for text that is not a date this chatbot can read.
     *
     * <p>The text is quoted back so that a user who mistyped one character can see
     * which of several dates on the line was not understood.
     */
    private static BobException createUnreadableDateError(String text) {
        return BobException.withExample("I don't understand \"" + text + "\" as a date."
                + "\nWrite the day as yyyy-mm-dd, and add a 24-hour time if the hour matters.",
                EXAMPLE_DATE + " or " + EXAMPLE_DATE_TIME);
    }

    /**
     * Returns this date as the single field {@link bob.storage.Storage Storage} writes to the save file.
     *
     * <p>It is saved in the same form the user types it, rather than in the
     * friendlier form {@link #toString()} prints. The saved text is read back by
     * {@link #parse}, so writing it this way means one format has to be read, and
     * a user editing the file by hand writes dates there exactly as they would
     * type them at the chatbot.
     */
    public String toSaveField() {
        if (time == null) {
            return date.toString();
        }
        return date + " " + time.format(INPUT_TIME_FORMAT);
    }

    /** Returns whether this date falls on {@code day}, whatever time of day it carries. */
    public boolean isOn(LocalDate day) {
        return date.equals(day);
    }

    /** Returns whether this date falls on a day earlier than {@code day}. */
    public boolean isBefore(LocalDate day) {
        return date.isBefore(day);
    }

    /** Returns whether this date falls on a day later than {@code day}. */
    public boolean isAfter(LocalDate day) {
        return date.isAfter(day);
    }

    /**
     * Returns whether the user gave a time of day as well as the day.
     *
     * <p>{@link #compareTo} cannot answer this, because it counts a missing time as
     * the start of the day, so {@code 2026-12-02} and {@code 2026-12-02 0000} compare
     * as equal although only the second was given a time.
     */
    public boolean hasTime() {
        return time != null;
    }

    /**
     * Orders dates from earliest to latest, so that sorting a list of them puts
     * the most urgent first.
     *
     * <p>Two dates on the same day are separated by their time, and a date given
     * without a time counts as the start of its day. That is only an ordering
     * rule, not a claim that the task is due at midnight: it puts a task due "on
     * the 2nd" before one due at a particular hour of the 2nd, which is the
     * cautious way round for anyone reading the list to decide what to do next.
     *
     * @param other the date to compare this one with.
     * @return a negative number if this date is earlier, zero if the two are at
     *         the same point in time, a positive number if this date is later.
     */
    @Override
    public int compareTo(TaskDateTime other) {
        int dayComparison = date.compareTo(other.date);
        if (dayComparison != 0) {
            return dayComparison;
        }
        return getTimeOrStartOfDay().compareTo(other.getTimeOrStartOfDay());
    }

    /** Returns the time of day, or the start of the day when the user gave no time. */
    private LocalTime getTimeOrStartOfDay() {
        return (time == null) ? LocalTime.MIN : time;
    }

    /**
     * Returns the moment at which this date begins: the time of day the user gave, or
     * the start of the day when they gave none.
     *
     * <p>Used with {@link #asPeriodEnd} to measure a stretch of time, such as the one an
     * {@link Event} runs for. A day with no time means the whole of that day, so which
     * moment of it is meant depends on which end of the stretch the date is: "from the
     * 2nd to the 2nd" is a day long, not nothing at all.
     */
    public LocalDateTime asPeriodStart() {
        return date.atTime(getTimeOrStartOfDay());
    }

    /**
     * Returns the moment at which this date ends: the time of day the user gave, or the
     * last moment of the day when they gave none.
     *
     * <p>The counterpart of {@link #asPeriodStart}, where the reason for the two is
     * given.
     */
    public LocalDateTime asPeriodEnd() {
        return date.atTime((time == null) ? LocalTime.MAX : time);
    }

    /**
     * Returns whether {@code other} is a date on the same day, with the same time of
     * day or with no time just as this one has none.
     *
     * <p>A day given without a time is not equal to the same day at {@code 0000},
     * although {@link #compareTo} puts the two level. Ordering has to place a missing
     * time somewhere, but a task due "on the 2nd" and one due "at midnight on the 2nd"
     * were written differently and are shown differently, so they are not the same
     * date. The price is that the ordering is not consistent with {@code equals}: a
     * sorted set of dates would keep only one of the two.
     *
     * @param other the object to compare this date with.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return other instanceof TaskDateTime otherDate
                && date.equals(otherDate.date)
                && Objects.equals(time, otherDate.time);
    }

    /**
     * Returns a hash code built from the day and the time of day, the two things
     * {@link #equals} compares, so that equal dates always share a hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(date, time);
    }

    /** Returns for example {@code Dec 02 2026}, or {@code Dec 02 2026 18:00} with a time. */
    @Override
    public String toString() {
        String day = date.format(OUTPUT_DATE_FORMAT);
        if (time == null) {
            return day;
        }
        return day + " " + time.format(OUTPUT_TIME_FORMAT);
    }
}
