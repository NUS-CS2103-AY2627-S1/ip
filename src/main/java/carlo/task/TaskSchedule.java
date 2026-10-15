package carlo.task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Locale;

/**
 * Works out how urgent a task is relative to the current time: which
 * {@link TaskGroup} it belongs in, a short human-friendly description of
 * its timing (such as "due tomorrow" or "2 days late"), and whether Carlo
 * should remind the user about it.
 *
 * <p>Every method takes the current time as a parameter instead of calling
 * {@code LocalDateTime.now()} itself. This keeps the class easy to test,
 * because a test can pass in any fixed time it likes.
 *
 * <p>A deadline or event entered without a time of day (e.g. just
 * {@code 2026-10-15}) is treated as lasting the whole day, so a date-only
 * deadline only becomes overdue once that day has fully passed.
 */
public final class TaskSchedule {
    /** Tasks due within this many days of today go in {@link TaskGroup#SOON}. */
    private static final int SOON_DAYS = 1;
    /** Tasks due within this many days of today go in {@link TaskGroup#THIS_WEEK}. */
    private static final int WEEK_DAYS = 7;

    // Locale.ENGLISH keeps month and day names the same on every computer (e.g. "Sep", not "Sept").
    private static final DateTimeFormatter WEEKDAY_FORMAT = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
    private static final DateTimeFormatter MONTH_DAY_FORMAT = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("h:mma", Locale.ENGLISH);

    private TaskSchedule() {
        // utility class: not meant to be instantiated
    }

    /**
     * Returns the urgency group that a task belongs in at the given time.
     *
     * @param task the task to classify
     * @param now the current date and time
     * @return the task's urgency group
     */
    public static TaskGroup groupOf(Task task, LocalDateTime now) {
        if (task.isDone()) {
            return TaskGroup.DONE;
        }
        if (task instanceof Deadline deadline) {
            if (isOverdue(deadline, now)) {
                return TaskGroup.OVERDUE;
            }
            return groupByDate(deadline.getDueDateTime(), now);
        }
        if (task instanceof Event event) {
            if (hasEnded(event, now)) {
                return TaskGroup.OVERDUE;
            }
            if (isOngoing(event, now)) {
                return TaskGroup.SOON;
            }
            return groupByDate(event.getStartDateTime(), now);
        }
        return TaskGroup.ANYTIME;
    }

    /**
     * Returns a short description of when a task is due or happening,
     * relative to the given time, e.g. "due tomorrow 6:00pm", "3 days late"
     * or "starts Fri". Todos have no date, so they return an empty string.
     *
     * @param task the task to describe
     * @param now the current date and time
     * @return the timing description, or an empty string if there is none
     */
    public static String describeTiming(Task task, LocalDateTime now) {
        if (task instanceof Deadline deadline) {
            return describeDeadline(deadline, now);
        }
        if (task instanceof Event event) {
            return describeEvent(event, now);
        }
        return "";
    }

    /**
     * Returns whether an incomplete task starts or is due within the given
     * window from now. Only tasks with a specific time of day count, since
     * a date-only task has no particular moment to be reminded about.
     *
     * @param task the task to check
     * @param now the current date and time
     * @param window how far ahead to look
     * @return {@code true} if Carlo should remind the user about this task now
     */
    public static boolean isComingUp(Task task, LocalDateTime now, Duration window) {
        if (task.isDone()) {
            return false;
        }
        CarloDateTime moment = null;
        if (task instanceof Deadline deadline) {
            moment = deadline.getDueDateTime();
        } else if (task instanceof Event event) {
            moment = event.getStartDateTime();
        }
        if (moment == null || !moment.isParsed() || !moment.hasTime()) {
            return false;
        }
        LocalDateTime time = moment.getDateTime();
        return !time.isBefore(now) && !time.isAfter(now.plus(window));
    }

    /**
     * Returns a comparator that orders tasks by their key date (a deadline's
     * due time, or an event's start time), soonest first. Tasks without a
     * date, such as todos, keep their original order and come last.
     *
     * @return the comparator
     */
    public static Comparator<Task> bySoonestFirst() {
        return Comparator.comparing(TaskSchedule::keyTime, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    /**
     * Returns whether a deadline's due time has passed.
     *
     * @param deadline the deadline to check
     * @param now the current date and time
     * @return {@code true} if the deadline is incomplete and past due
     */
    public static boolean isOverdue(Deadline deadline, LocalDateTime now) {
        CarloDateTime due = deadline.getDueDateTime();
        return !deadline.isDone() && due.isParsed() && now.isAfter(endOfPeriod(due));
    }

    private static boolean hasEnded(Event event, LocalDateTime now) {
        CarloDateTime end = event.getEndDateTime();
        return end.isParsed() && now.isAfter(endOfPeriod(end));
    }

    private static boolean isOngoing(Event event, LocalDateTime now) {
        CarloDateTime start = event.getStartDateTime();
        return start.isParsed() && !now.isBefore(start.getDateTime()) && !hasEnded(event, now);
    }

    /**
     * Places a task into SOON, THIS_WEEK or LATER based on how many days
     * away its date is.
     */
    private static TaskGroup groupByDate(CarloDateTime dateTime, LocalDateTime now) {
        if (!dateTime.isParsed()) {
            return TaskGroup.ANYTIME;
        }
        long daysAway = ChronoUnit.DAYS.between(now.toLocalDate(), dateTime.getDateTime().toLocalDate());
        if (daysAway <= SOON_DAYS) {
            return TaskGroup.SOON;
        }
        if (daysAway <= WEEK_DAYS) {
            return TaskGroup.THIS_WEEK;
        }
        return TaskGroup.LATER;
    }

    private static String describeDeadline(Deadline deadline, LocalDateTime now) {
        CarloDateTime due = deadline.getDueDateTime();
        if (!due.isParsed()) {
            return "due " + due.toDisplayString();
        }
        if (isOverdue(deadline, now)) {
            long daysLate = ChronoUnit.DAYS.between(due.getDateTime().toLocalDate(), now.toLocalDate());
            if (daysLate == 0) {
                return "overdue";
            }
            return daysLate == 1 ? "1 day late" : daysLate + " days late";
        }
        return "due " + describeWhen(due, now);
    }

    private static String describeEvent(Event event, LocalDateTime now) {
        if (hasEnded(event, now)) {
            return "ended " + describeWhen(event.getEndDateTime(), now);
        }
        if (isOngoing(event, now)) {
            return "on now, until " + describeWhen(event.getEndDateTime(), now);
        }
        return "starts " + describeWhen(event.getStartDateTime(), now);
    }

    /**
     * Describes a date relative to today ("today", "tomorrow", "Fri",
     * "Oct 16"), followed by the time of day if one was given.
     */
    private static String describeWhen(CarloDateTime dateTime, LocalDateTime now) {
        if (!dateTime.isParsed()) {
            return dateTime.toDisplayString();
        }
        LocalDateTime time = dateTime.getDateTime();
        String day = describeDay(time.toLocalDate(), now.toLocalDate());
        if (!dateTime.hasTime()) {
            return day;
        }
        return day + " " + time.format(TIME_FORMAT).toLowerCase();
    }

    private static String describeDay(LocalDate date, LocalDate today) {
        long daysAway = ChronoUnit.DAYS.between(today, date);
        if (daysAway == 0) {
            return "today";
        }
        if (daysAway == 1) {
            return "tomorrow";
        }
        if (daysAway == -1) {
            return "yesterday";
        }
        if (daysAway > 1 && daysAway < WEEK_DAYS) {
            return date.format(WEEKDAY_FORMAT);
        }
        return date.format(MONTH_DAY_FORMAT);
    }

    /**
     * Returns the moment a date/time stops being "current": the given time
     * itself, or the very end of the day for a date with no time of day.
     */
    private static LocalDateTime endOfPeriod(CarloDateTime dateTime) {
        if (dateTime.hasTime()) {
            return dateTime.getDateTime();
        }
        return dateTime.getDateTime().toLocalDate().atTime(LocalTime.MAX);
    }

    private static LocalDateTime keyTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getDueDateTime().getDateTime();
        }
        if (task instanceof Event event) {
            return event.getStartDateTime().getDateTime();
        }
        return null;
    }
}
