package maggigorengayam.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import maggigorengayam.task.Event;
import maggigorengayam.task.Task;

/**
 * Finds the nearest day, starting from a given date/time, on which a
 * contiguous free slot of a given length exists inside a daily
 * working-hours window. Only {@link Event} tasks are treated as occupying
 * time - a to-do/deadline has no duration, so it never blocks a slot.
 */
public class FreeTimeFinder {
    private static final int MINUTES_PER_DAY = 24 * 60;

    /** A free slot: the date it falls on, and its start/end time within that day. */
    public static class FreeSlot {
        public final LocalDate date;
        public final LocalTime start;
        public final LocalTime end;

        /** Pairs a date with the start/end time of the free slot found on it. */
        public FreeSlot(LocalDate date, LocalTime start, LocalTime end) {
            this.date = date;
            this.start = start;
            this.end = end;
        }
    }

    /** One block of busy time within a single day, expressed as minutes since midnight. */
    private static class BusyInterval {
        private final int startMinutes;
        private final int endMinutes;

        private BusyInterval(int startMinutes, int endMinutes) {
            this.startMinutes = startMinutes;
            this.endMinutes = endMinutes;
        }
    }

    /**
     * Searches forward from {@code searchStartDate} for the nearest day
     * with a free slot of {@code durationMinutes} inside
     * {@code [windowStart, windowEnd]}. On {@code searchStartDate} itself,
     * only time from {@code searchStartTimeOnFirstDay} onward is
     * considered (so a moment already in the past today is never
     * suggested); every later day uses the full {@code windowStart}. Gives
     * up after {@code maxDaysAhead} days.
     *
     * @return the first (earliest) qualifying slot, sized to exactly
     *         {@code durationMinutes}, or empty if none was found within
     *         {@code maxDaysAhead} days.
     */
    public static Optional<FreeSlot> findNearestFreeSlot(
            List<Task> tasks, int durationMinutes, LocalTime windowStart, LocalTime windowEnd,
            LocalDate searchStartDate, LocalTime searchStartTimeOnFirstDay, int maxDaysAhead) {
        List<Event> events = tasks.stream()
                .filter(task -> task instanceof Event)
                .map(task -> (Event) task)
                .collect(Collectors.toList());
        int windowEndMinutes = toMinutes(windowEnd);

        for (int dayOffset = 0; dayOffset < maxDaysAhead; dayOffset++) {
            LocalDate day = searchStartDate.plusDays(dayOffset);
            LocalTime dayStart = dayOffset == 0 ? searchStartTimeOnFirstDay : windowStart;
            int dayStartMinutes = toMinutes(dayStart);
            if (dayStartMinutes >= windowEndMinutes) {
                // No time left in today's window (search moment is at/past window end).
                continue;
            }
            Optional<FreeSlot> slot =
                    findSlotOnDay(events, day, dayStartMinutes, windowEndMinutes, durationMinutes);
            if (slot.isPresent()) {
                return slot;
            }
        }
        return Optional.empty();
    }

    /** Finds the first {@code durationMinutes}-long gap on {@code day} within {@code [dayStart, dayEnd]}. */
    private static Optional<FreeSlot> findSlotOnDay(
            List<Event> events, LocalDate day, int dayStartMinutes, int dayEndMinutes, int durationMinutes) {
        List<BusyInterval> busyIntervals = events.stream()
                .filter(event -> event.occursOn(day))
                .map(event -> busyIntervalOnDay(event, day))
                .map(interval -> clip(interval, dayStartMinutes, dayEndMinutes))
                .filter(interval -> interval.startMinutes < interval.endMinutes)
                .sorted((a, b) -> Integer.compare(a.startMinutes, b.startMinutes))
                .collect(Collectors.toList());

        int cursor = dayStartMinutes;
        for (BusyInterval busy : busyIntervals) {
            if (busy.startMinutes - cursor >= durationMinutes) {
                return Optional.of(toFreeSlot(day, cursor, cursor + durationMinutes));
            }
            cursor = Math.max(cursor, busy.endMinutes);
        }
        if (dayEndMinutes - cursor >= durationMinutes) {
            return Optional.of(toFreeSlot(day, cursor, cursor + durationMinutes));
        }
        return Optional.empty();
    }

    /**
     * The portion of {@code event} that falls on {@code day}, expressed as
     * minutes since midnight. An open-ended side - the event's own time is
     * {@code null}, or {@code day} isn't that side's boundary date (a
     * middle day, or only the start/end date of a multi-day event) - is
     * represented by extending to the day's own boundary (0 or
     * {@link #MINUTES_PER_DAY}); {@link #clip} then cuts that down to the
     * actual search window.
     */
    private static BusyInterval busyIntervalOnDay(Event event, LocalDate day) {
        boolean isFromDay = day.equals(event.getFromDate());
        boolean isToDay = day.equals(event.getToDate());
        int start = isFromDay && event.getFromTime() != null ? toMinutes(event.getFromTime()) : 0;
        int end = isToDay && event.getToTime() != null ? toMinutes(event.getToTime()) : MINUTES_PER_DAY;
        return new BusyInterval(start, end);
    }

    private static BusyInterval clip(BusyInterval interval, int lowMinutes, int highMinutes) {
        int clippedStart = Math.max(interval.startMinutes, lowMinutes);
        int clippedEnd = Math.min(interval.endMinutes, highMinutes);
        return new BusyInterval(clippedStart, clippedEnd);
    }

    private static FreeSlot toFreeSlot(LocalDate day, int startMinutes, int endMinutes) {
        return new FreeSlot(day, toLocalTime(startMinutes), toLocalTime(endMinutes));
    }

    private static int toMinutes(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }

    private static LocalTime toLocalTime(int minutes) {
        return LocalTime.of(minutes / 60, minutes % 60);
    }
}
