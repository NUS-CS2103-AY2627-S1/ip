package nori.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nori.NoriException;

/**
 * Represents a task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    /** Finds date-like text in an event detail, ignoring digits that are part of a longer run. */
    private static final Pattern DATE_PATTERN =
            Pattern.compile("(?<![0-9])\\d{4}-\\d{1,2}-\\d{1,2}(?![0-9])");

    /** Start details, kept exactly as the user typed them. */
    private final String from;
    /** End details, kept exactly as the user typed them. */
    private final String to;

    /**
     * Creates an incomplete event with the given description, start, and end times.
     *
     * @param description the event description.
     * @param from the start date or time, stored as entered by the user.
     * @param to the end date or time, stored as entered by the user.
     * @throws NoriException if a date in the start or end details is invalid.
     */
    public Event(String description, String from, String to) throws NoriException {
        super(description);
        validateDates(from);
        validateDates(to);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start details as entered by the user.
     *
     * @return the event start details.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end details as entered by the user.
     *
     * @return the event end details.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns whether the event's start or end details explicitly contain the given ISO-8601 date.
     *
     * @param date the date to find in this event's start or end details.
     * @return {@code true} if the event explicitly includes {@code date}.
     */
    public boolean occursOn(LocalDate date) {
        LocalDate eventStart = findDate(from);
        LocalDate eventEnd = findDate(to);
        if (eventStart != null && eventEnd != null) {
            return !date.isBefore(eventStart) && !date.isAfter(eventEnd);
        }
        return date.equals(eventStart) || date.equals(eventEnd);
    }

    /**
     * Returns whether this event overlaps the inclusive date range.
     *
     * An event with dates in both its start and end details is treated as a date interval.
     * An event with a date in only one detail is treated as occurring on that date.
     *
     * @param rangeStart the earliest date in the range.
     * @param rangeEnd the latest date in the range.
     * @return {@code true} if this event occurs in the range.
     */
    public boolean occursInDateRange(LocalDate rangeStart, LocalDate rangeEnd) {
        assert !rangeEnd.isBefore(rangeStart) : "A DateRange orders its two dates when it is built.";

        LocalDate eventStart = findDate(from);
        LocalDate eventEnd = findDate(to);
        if (eventStart != null && eventEnd != null) {
            return !eventStart.isAfter(rangeEnd) && !eventEnd.isBefore(rangeStart);
        }
        if (eventStart != null) {
            return !eventStart.isBefore(rangeStart) && !eventStart.isAfter(rangeEnd);
        }
        if (eventEnd != null) {
            return !eventEnd.isBefore(rangeStart) && !eventEnd.isAfter(rangeEnd);
        }
        return false;
    }

    /**
     * Returns the time of day this event starts at on a given date.
     *
     * An event is timed on a date only when its own start details name that
     * date and give a time. A multi-day event is therefore untimed on the days
     * after the one it begins on, because the hour it began at says nothing
     * about those later days.
     *
     * @param date the date to read a start time for.
     * @return the start time on {@code date}, or empty when the event gives none there.
     */
    public Optional<LocalTime> findStartTimeOn(LocalDate date) {
        if (!date.equals(findDate(from))) {
            return Optional.empty();
        }
        return ClockTimes.findFirstIn(removeDates(from));
    }

    /**
     * Removes every ISO-8601 date from an event detail.
     *
     * @param eventDetail the start or end detail to clear of dates.
     * @return the detail with each date replaced by a space.
     */
    private static String removeDates(String eventDetail) {
        return DATE_PATTERN.matcher(eventDetail).replaceAll(" ");
    }

    /**
     * Validates every date-like value in an event detail.
     *
     * @param eventDetail the start or end detail to validate.
     * @throws NoriException if a date-like value is not a valid ISO-8601 date.
     */
    private static void validateDates(String eventDetail) throws NoriException {
        if (eventDetail == null) {
            throw new NoriException("NOOT?! An event cannot have a missing date or time.");
        }

        Matcher matcher = DATE_PATTERN.matcher(eventDetail);
        while (matcher.find()) {
            String dateText = matcher.group();
            try {
                LocalDate.parse(dateText);
            } catch (DateTimeParseException exception) {
                throw new NoriException("NOOT?! I cannot understand \"" + dateText + "\" as an event date."
                        + " Use a date like \"2019-10-15\".");
            }
        }
    }

    /**
     * Reports a start and an end that a user has written the wrong way round.
     *
     * Only an ordering Nori can be sure of is reported. Start and end details
     * are free-form, so they may name a day in words Nori does not read: "Mon
     * 2pm" to "Tue 2pm" is left alone, because the two times say nothing about
     * which of them falls first. Two details naming one explicit date, or two
     * bare clock times, are not ambiguous and are compared.
     *
     * This is a fault in what a user typed rather than a broken event, so it
     * is reported to the caller instead of refusing construction. An event
     * saved before this check existed therefore still loads from disk, rather
     * than reading as a corrupted file.
     *
     * @param from the event's start details.
     * @param to the event's end details.
     * @return the correction to show the user, or empty when the two are in order.
     */
    public static Optional<String> findOrderingError(String from, String to) {
        if (from == null || to == null) {
            // A detail that is not there has no place in an ordering. The
            // constructor reports the missing detail itself, which is the fault
            // the user actually needs to hear about.
            return Optional.empty();
        }

        LocalDate startDate = findDate(from);
        LocalDate endDate = findDate(to);
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            return Optional.of("NOOT?! An event cannot end before it starts."
                    + " Time only waddles forward.");
        }
        if (!isCertainlyOneDay(from, to)) {
            return Optional.empty();
        }

        Optional<LocalTime> startTime = ClockTimes.findFirstIn(removeDates(from));
        Optional<LocalTime> endTime = ClockTimes.findFirstIn(removeDates(to));
        boolean isOrdered = startTime.isEmpty() || endTime.isEmpty()
                || endTime.get().isAfter(startTime.get());
        if (isOrdered) {
            return Optional.empty();
        }
        return Optional.of("NOOT?! That event ends at or before it starts."
                + " Time only waddles forward.");
    }

    /**
     * Reports a start or end that names a clock time no clock can show, such as {@code 25:00}.
     *
     * Like a reversed ordering, this is a fault in what a user typed, so it is
     * reported to the caller rather than refusing construction. An event saved
     * before this check existed therefore still loads from disk.
     *
     * @param from the event's start details.
     * @param to the event's end details.
     * @return the correction to show the user, or empty when every time is real.
     */
    public static Optional<String> findTimeError(String from, String to) {
        return findImpossibleTime(from)
                .or(() -> findImpossibleTime(to))
                .map(time -> "NOOT?! I cannot understand \"" + time + "\" as an event time."
                        + " Use a time like \"1400\" or \"2:30pm\".");
    }

    /**
     * Returns the first impossible clock time in one event detail.
     *
     * @param eventDetail the start or end detail to inspect.
     * @return the impossible time as written, or empty when there is none or no detail at all.
     */
    private static Optional<String> findImpossibleTime(String eventDetail) {
        if (eventDetail == null) {
            return Optional.empty();
        }
        return ClockTimes.findImpossibleTime(removeDates(eventDetail));
    }

    /**
     * Returns whether the start and end details are certain to fall on one day.
     *
     * @param from the event's start details.
     * @param to the event's end details.
     * @return {@code true} only when no reading of the details puts them on two days.
     */
    private static boolean isCertainlyOneDay(String from, String to) {
        LocalDate startDate = findDate(from);
        LocalDate endDate = findDate(to);
        if (startDate != null && endDate != null) {
            return startDate.equals(endDate);
        }
        if (startDate == null && endDate == null) {
            return ClockTimes.isOnlyClockTime(from) && ClockTimes.isOnlyClockTime(to);
        }

        String undatedDetail = startDate == null ? from : to;
        return ClockTimes.isOnlyClockTime(undatedDetail);
    }

    /**
     * Returns the first ISO-8601 date in an event detail, if any.
     *
     * @param eventDetail the start or end detail to inspect.
     * @return the first valid date, or {@code null} when the detail has no date.
     */
    private static LocalDate findDate(String eventDetail) {
        if (eventDetail == null) {
            return null;
        }

        Matcher matcher = DATE_PATTERN.matcher(eventDetail);
        if (!matcher.find()) {
            return null;
        }
        try {
            return LocalDate.parse(matcher.group());
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Returns whether another task is an event for the same thing over the same span.
     *
     * The start and end details are compared the way descriptions are, so
     * spacing alone never makes two spans differ.
     *
     * @param other the task to compare this one with.
     * @return {@code true} when the descriptions and both spans match.
     */
    @Override
    public boolean isSameTask(Task other) {
        if (!super.isSameTask(other)) {
            return false;
        }

        Event otherEvent = (Event) other;
        return isSameText(otherEvent.from, from) && isSameText(otherEvent.to, to);
    }

    /**
     * Returns this event with its {@code [E]} type icon and its start and end details.
     *
     * @return the task rendering behind an {@code [E]} type icon, followed
     *         by the start and end details exactly as the user typed them,
     *         for example {@code [E][ ] book fair (from: 2pm to: 4pm)}.
     */
    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
