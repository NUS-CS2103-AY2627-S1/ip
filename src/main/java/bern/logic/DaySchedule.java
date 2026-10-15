package bern.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import bern.task.Deadline;
import bern.task.Event;
import bern.task.Task;

/**
 * Groups one day's tasks and assigns overlapping event intervals to the minimum number of columns.
 */
public final class DaySchedule {
    private static final int MINUTES_PER_DAY = 24 * 60;
    private static final int SECONDS_PER_MINUTE = 60;

    private final LocalDate date;
    private final List<ScheduleEntry> entries;

    /**
     * Preserves deadlines and events without a usable timed duration outside timed columns.
     */
    private final List<Task> otherTasks;
    private final int columnCount;

    /**
     * Creates a timetable for the given date, clipping events that span several days.
     * Deadlines and events without a usable timed duration remain in a separate list; undated todos are omitted.
     *
     * @param date The calendar day to display.
     * @param tasks The tasks to consider, including tasks outside this day.
     */
    public DaySchedule(LocalDate date, List<Task> tasks) {
        this.date = date;
        List<ScheduleEntry> intervals = new ArrayList<>();
        List<Task> untimedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline && deadline.getReferenceDateTime().getDate().equals(date)) {
                untimedTasks.add(task);
            } else if (task instanceof Event event) {
                addEvent(event, intervals, untimedTasks);
            }
        }

        entries = List.copyOf(assignColumns(intervals));
        otherTasks = List.copyOf(untimedTasks);
        columnCount = entries.stream().mapToInt(ScheduleEntry::getColumn).max().orElse(-1) + 1;
    }

    /**
     * Creates chronologically ordered timetables for each day occupied by a dated task.
     * Undated todos are left for the response to display separately.
     *
     * @param tasks The tasks to group, preserving repeated task instances.
     * @return An unmodifiable list of schedules for all occupied dates, in chronological order.
     */
    public static List<DaySchedule> createSchedules(List<Task> tasks) {
        Map<LocalDate, List<Task>> tasksByDate = new TreeMap<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline) {
                LocalDate dueDate = deadline.getReferenceDateTime().getDate();
                addTaskToDate(tasksByDate, dueDate, task);
            } else if (task instanceof Event event) {
                addEventToDates(tasksByDate, event);
            }
        }
        return tasksByDate.entrySet().stream()
                .map(entry -> new DaySchedule(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Returns the calendar date represented by this schedule.
     *
     * @return The scheduled date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns timed event segments in start-time order, breaking ties by end time.
     *
     * @return An unmodifiable list of segments with their assigned columns.
     */
    public List<ScheduleEntry> getEntries() {
        return entries;
    }

    /**
     * Returns this day's deadlines and events without a usable timed duration.
     *
     * @return An unmodifiable list preserving input order and the original task objects.
     */
    public List<Task> getOtherTasks() {
        return otherTasks;
    }

    /**
     * Returns the minimum number of columns required for the timed event segments.
     *
     * @return The maximum number of overlapping segments, or zero when there are no timed segments.
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * Adds an event to each occupied date without stepping beyond its final date.
     *
     * @param tasksByDate The date groups to update.
     * @param event The event to add to each date it occupies.
     */
    private static void addEventToDates(Map<LocalDate, List<Task>> tasksByDate, Event event) {
        LocalDate firstDate = event.getStartDateTime().getDate();
        LocalDate lastDate = event.getLastOccupiedDate();
        for (LocalDate occupiedDate = firstDate; occupiedDate.isBefore(lastDate);
                occupiedDate = occupiedDate.plusDays(1)) {
            addTaskToDate(tasksByDate, occupiedDate, event);
        }
        // Add the inclusive final date separately so LocalDate.MAX never needs to be incremented.
        addTaskToDate(tasksByDate, lastDate, event);
    }

    /**
     * Appends a task to its date's group, preserving input order and repeated task instances.
     *
     * @param tasksByDate The date groups to update.
     * @param date The date whose group receives the task.
     * @param task The task to append.
     */
    private static void addTaskToDate(Map<LocalDate, List<Task>> tasksByDate, LocalDate date, Task task) {
        tasksByDate.computeIfAbsent(date, key -> new ArrayList<>()).add(task);
    }

    /**
     * Adds this day's event segment at minute precision.
     * Events whose boundaries truncate to the same minute remain outside the timed grid.
     *
     * @param event The event to consider; events outside this day are ignored.
     * @param intervals The list receiving a timed segment when one exists.
     * @param untimedTasks The list receiving all-day events and events without a usable timed duration.
     */
    private void addEvent(Event event, List<ScheduleEntry> intervals, List<Task> untimedTasks) {
        if (!event.occursOn(date)) {
            return;
        }
        if (event.isAllDay() || !event.hasPositiveDuration()) {
            untimedTasks.add(event);
            return;
        }

        int startMinute = date.equals(event.getStartDateTime().getDate()) ? getStartMinute(event) : 0;
        int endMinute = date.equals(event.getEndDateTime().getDate())
                ? getEndMinute(event) : MINUTES_PER_DAY;
        if (endMinute <= startMinute) {
            untimedTasks.add(event);
            return;
        }
        intervals.add(new ScheduleEntry(event, startMinute, endMinute, 0));
    }

    /**
     * Reuses the leftmost available column for each next interval in start-time order.
     * Tracks active intervals by end time and available columns by their index.
     * A new column is needed only when every existing column overlaps the next interval;
     * this makes the column count equal to the maximum number of simultaneous events.
     *
     * @param intervals The event segments, sorted in place by start time and then end time.
     * @return New segments with assigned columns, in the sorted order.
     */
    private static List<ScheduleEntry> assignColumns(List<ScheduleEntry> intervals) {
        intervals.sort(Comparator.comparingInt(ScheduleEntry::getStartMinute)
                .thenComparingInt(ScheduleEntry::getEndMinute));
        PriorityQueue<ScheduleEntry> activeEntries = new PriorityQueue<>(
                Comparator.comparingInt(ScheduleEntry::getEndMinute).thenComparingInt(ScheduleEntry::getColumn));
        PriorityQueue<Integer> availableColumns = new PriorityQueue<>();
        List<ScheduleEntry> placements = new ArrayList<>();
        int nextColumn = 0;
        for (ScheduleEntry interval : intervals) {
            // Release every finished interval before choosing the leftmost free column.
            while (!activeEntries.isEmpty() && activeEntries.peek().getEndMinute() <= interval.getStartMinute()) {
                availableColumns.add(activeEntries.remove().getColumn());
            }
            int column;
            if (availableColumns.isEmpty()) {
                column = nextColumn++;
            } else {
                column = availableColumns.remove();
            }
            ScheduleEntry placement = new ScheduleEntry(interval.getTask(), interval.getStartMinute(),
                    interval.getEndMinute(), column);
            placements.add(placement);
            activeEntries.add(placement);
        }
        return placements;
    }

    /**
     * Returns the start minute, using midnight when no start time was supplied.
     *
     * @param event The event whose original start time is used.
     * @return Whole minutes after midnight, truncating any seconds.
     */
    private static int getStartMinute(Event event) {
        LocalTime time = event.getStartDateTime().getTime();
        return time == null ? 0 : time.toSecondOfDay() / SECONDS_PER_MINUTE;
    }

    /**
     * Returns the end minute, using the end of the day when no end time was supplied.
     *
     * @param event The event whose original end time is used.
     * @return Whole minutes after midnight, truncating seconds, or 1440 when no end time was supplied.
     */
    private static int getEndMinute(Event event) {
        LocalTime time = event.getEndDateTime().getTime();
        return time == null ? MINUTES_PER_DAY : time.toSecondOfDay() / SECONDS_PER_MINUTE;
    }
}
