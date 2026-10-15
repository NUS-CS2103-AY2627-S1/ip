package bern.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import bern.task.Task;
import bern.task.TaskFactory;

/**
 * Verifies daily task grouping and the minimum number of non-overlapping timetable columns.
 */
public class DayScheduleTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 15);

    @Test
    public void createSchedule_unsortedOverlaps_usesMinimumColumns() throws ParseException {
        Task first = makeEvent("First", "15/9/2026 @ 08:00", "15/9/2026 @ 10:00");
        Task fourth = makeEvent("Fourth", "15/9/2026 @ 11:00", "15/9/2026 @ 13:00");
        Task second = makeEvent("Second", "15/9/2026 @ 09:00", "15/9/2026 @ 11:00");
        Task third = makeEvent("Third", "15/9/2026 @ 10:00", "15/9/2026 @ 12:00");
        List<Task> tasks = List.of(first, fourth, second, third);

        DaySchedule schedule = new DaySchedule(DATE, tasks);

        // Assigning columns in this input order can use three columns although the maximum overlap is two.
        assertEquals(2, schedule.getColumnCount());
        assertEquals(Set.copyOf(tasks), new HashSet<>(schedule.getEntries().stream()
                .map(ScheduleEntry::getTask).toList()));
        assertValidMinimumColumns(schedule);
    }

    @Test
    public void createSchedule_nestedEvents_reusesColumnsAfterShorterEvents() throws ParseException {
        List<Task> tasks = List.of(
                makeEvent("Outer", "15/9/2026 @ 08:00", "15/9/2026 @ 18:00"),
                makeEvent("Inner", "15/9/2026 @ 09:00", "15/9/2026 @ 16:00"),
                makeEvent("Morning", "15/9/2026 @ 10:00", "15/9/2026 @ 11:00"),
                makeEvent("Lunch", "15/9/2026 @ 12:00", "15/9/2026 @ 13:00"),
                makeEvent("Afternoon", "15/9/2026 @ 16:00", "15/9/2026 @ 17:00"),
                makeEvent("Evening", "15/9/2026 @ 18:00", "15/9/2026 @ 19:00"));

        DaySchedule schedule = new DaySchedule(DATE, tasks);

        assertEquals(6, schedule.getEntries().size());
        assertEquals(3, schedule.getColumnCount());
        assertEquals(List.of(0, 1, 2, 2, 1, 0),
                schedule.getEntries().stream().map(ScheduleEntry::getColumn).toList());
        assertValidMinimumColumns(schedule);
    }

    @Test
    public void createSchedule_availableColumns_reusesLeftmostEvenIfItEndsLater() throws ParseException {
        Task left = makeEvent("Left", "15/9/2026 @ 08:00", "15/9/2026 @ 11:00");
        Task right = makeEvent("Right", "15/9/2026 @ 09:00", "15/9/2026 @ 10:00");
        Task next = makeEvent("Next", "15/9/2026 @ 11:00", "15/9/2026 @ 12:00");

        DaySchedule schedule = new DaySchedule(DATE, List.of(next, right, left));

        assertEquals(List.of(left, right, next),
                schedule.getEntries().stream().map(ScheduleEntry::getTask).toList());
        assertEquals(List.of(0, 1, 0),
                schedule.getEntries().stream().map(ScheduleEntry::getColumn).toList());
        assertEquals(2, schedule.getColumnCount());
        assertValidMinimumColumns(schedule);
    }

    @Test
    public void createSchedule_repeatedColumnReleases_reusesLeftmostWithoutCollisions() throws ParseException {
        List<Task> tasks = List.of(
                makeEvent("First", "15/9/2026 @ 08:00", "15/9/2026 @ 11:00"),
                makeEvent("Second", "15/9/2026 @ 08:30", "15/9/2026 @ 10:00"),
                makeEvent("Third", "15/9/2026 @ 09:00", "15/9/2026 @ 09:30"),
                makeEvent("Reuse first", "15/9/2026 @ 11:00", "15/9/2026 @ 12:00"),
                makeEvent("Reuse second", "15/9/2026 @ 11:15", "15/9/2026 @ 12:30"),
                makeEvent("Reuse third", "15/9/2026 @ 11:30", "15/9/2026 @ 11:45"),
                makeEvent("Reuse third again", "15/9/2026 @ 11:45", "15/9/2026 @ 13:00"),
                makeEvent("Reuse first again", "15/9/2026 @ 12:00", "15/9/2026 @ 14:00"),
                makeEvent("Reuse second again", "15/9/2026 @ 12:30", "15/9/2026 @ 13:30"));

        DaySchedule schedule = new DaySchedule(DATE, tasks);

        assertEquals(List.of(0, 1, 2, 0, 1, 2, 2, 0, 1),
                schedule.getEntries().stream().map(ScheduleEntry::getColumn).toList());
        assertEquals(3, schedule.getColumnCount());
        assertValidMinimumColumns(schedule);
    }

    @Test
    public void createSchedule_touchingEvents_reusesOneColumn() throws ParseException {
        List<Task> tasks = List.of(
                makeEvent("Later", "15/9/2026 @ 10:00", "15/9/2026 @ 11:00"),
                makeEvent("Earlier", "15/9/2026 @ 09:00", "15/9/2026 @ 10:00"),
                makeEvent("Last", "15/9/2026 @ 11:00", "15/9/2026 @ 11:01"));

        DaySchedule schedule = new DaySchedule(DATE, tasks);

        assertEquals(3, schedule.getEntries().size());
        assertEquals(1, schedule.getColumnCount());
        assertValidMinimumColumns(schedule);
    }

    @Test
    public void createSchedule_sameStartTimes_keepsEveryOverlappingEvent() throws ParseException {
        List<Task> tasks = List.of(
                makeEvent("Short", "15/9/2026 @ 09:00", "15/9/2026 @ 09:01"),
                makeEvent("Medium", "15/9/2026 @ 09:00", "15/9/2026 @ 10:00"),
                makeEvent("Long", "15/9/2026 @ 09:00", "15/9/2026 @ 11:00"));

        DaySchedule schedule = new DaySchedule(DATE, tasks);

        assertEquals(3, schedule.getEntries().size());
        assertEquals(3, schedule.getColumnCount());
        assertValidMinimumColumns(schedule);
    }

    @Test
    public void createSchedules_crossDayEvent_clipsAtEachDayBoundary() throws ParseException {
        Task task = makeEvent("Overnight", "14/9/2026 @ 23:00", "16/9/2026 @ 01:00");

        List<DaySchedule> schedules = DaySchedule.createSchedules(List.of(task));

        assertEquals(List.of(DATE.minusDays(1), DATE, DATE.plusDays(1)),
                schedules.stream().map(DaySchedule::getDate).toList());
        assertSingleEntry(schedules.get(0), task, 1380, 1440);
        assertSingleEntry(schedules.get(1), task, 0, 1440);
        assertSingleEntry(schedules.get(2), task, 0, 60);
    }

    @Test
    public void createSchedules_eventEndsAtMidnight_excludesEmptyFinalDay() throws ParseException {
        Task task = makeEvent("Late event", "15/9/2026 @ 23:00", "16/9/2026 @ 00:00");

        List<DaySchedule> schedules = DaySchedule.createSchedules(List.of(task));

        assertEquals(1, schedules.size());
        assertEquals(DATE, schedules.getFirst().getDate());
        assertSingleEntry(schedules.getFirst(), task, 1380, 1440);
    }

    @Test
    public void createSchedules_dateOnlyEvent_includesBothBoundaryDates() throws ParseException {
        Task task = makeEvent("Conference", "14/9/2026", "16/9/2026");

        List<DaySchedule> schedules = DaySchedule.createSchedules(List.of(task));

        assertEquals(List.of(DATE.minusDays(1), DATE, DATE.plusDays(1)),
                schedules.stream().map(DaySchedule::getDate).toList());
        for (DaySchedule schedule : schedules) {
            assertEquals(List.of(task), schedule.getOtherTasks());
            assertTrue(schedule.getEntries().isEmpty());
            assertEquals(0, schedule.getColumnCount());
        }
    }

    @Test
    public void createSchedules_lastSupportedDate_includesFinalDayWithoutOverflow() throws ParseException {
        Task task = makeEvent("Final dates", "30/12/999999999", "31/12/999999999");

        List<DaySchedule> schedules = DaySchedule.createSchedules(List.of(task));

        assertEquals(List.of(LocalDate.MAX.minusDays(1), LocalDate.MAX),
                schedules.stream().map(DaySchedule::getDate).toList());
        assertEquals(List.of(task), schedules.getFirst().getOtherTasks());
        assertEquals(List.of(task), schedules.getLast().getOtherTasks());
    }

    @Test
    public void createSchedules_reversedEventDates_preservesEventOnStartDate() throws ParseException {
        Task task = makeEvent("Legacy event", "15/9/2026 @ 10:00", "14/9/2026 @ 09:00");

        List<DaySchedule> schedules = DaySchedule.createSchedules(List.of(task));

        assertEquals(1, schedules.size());
        assertEquals(DATE, schedules.getFirst().getDate());
        assertEquals(List.of(task), schedules.getFirst().getOtherTasks());
        assertTrue(schedules.getFirst().getEntries().isEmpty());
    }

    @Test
    public void createSchedule_nonIntervalTasks_keepsDatedTasksOutsideColumns() throws ParseException {
        Task deadline = makeDeadline("Submit report", "15/9/2026 @ 10:00");
        Task dateOnlyDeadline = makeDeadline("Pay fee", "15/9/2026");
        Task dateOnlyEvent = makeEvent("Holiday", "15/9/2026", "15/9/2026");
        Task zeroDuration = makeEvent("Reminder", "15/9/2026 @ 09:00", "15/9/2026 @ 09:00");
        Task todo = TaskFactory.makeTaskFromData(new String[] {"T", "0", "Buy groceries"});
        Task anotherDay = makeDeadline("Tomorrow", "16/9/2026");
        List<Task> expected = List.of(deadline, dateOnlyDeadline, dateOnlyEvent, zeroDuration);

        DaySchedule schedule = new DaySchedule(DATE, List.of(deadline, dateOnlyDeadline, dateOnlyEvent,
                zeroDuration, todo, anotherDay));

        assertEquals(Set.copyOf(expected), new HashSet<>(schedule.getOtherTasks()));
        assertEquals(expected.size(), schedule.getOtherTasks().size());
        assertTrue(schedule.getEntries().isEmpty());
        assertEquals(0, schedule.getColumnCount());
    }

    @Test
    public void createSchedule_oneMissingTime_usesRespectiveDayBoundary() throws ParseException {
        Task noStartTime = makeEvent("Morning event", "15/9/2026", "15/9/2026 @ 10:00");
        Task noEndTime = makeEvent("Evening event", "15/9/2026 @ 22:00", "15/9/2026");

        DaySchedule schedule = new DaySchedule(DATE, List.of(noEndTime, noStartTime));

        assertEquals(2, schedule.getEntries().size());
        assertSame(noStartTime, schedule.getEntries().get(0).getTask());
        assertEquals(0, schedule.getEntries().get(0).getStartMinute());
        assertEquals(600, schedule.getEntries().get(0).getEndMinute());
        assertSame(noEndTime, schedule.getEntries().get(1).getTask());
        assertEquals(1320, schedule.getEntries().get(1).getStartMinute());
        assertEquals(1440, schedule.getEntries().get(1).getEndMinute());
        assertTrue(schedule.getOtherTasks().isEmpty());
        assertValidMinimumColumns(schedule);
    }

    @Test
    public void createSchedules_unsortedDates_returnsChronologicalNonemptyDays() throws ParseException {
        Task later = makeDeadline("Later", "17/9/2026");
        Task earlier = makeDeadline("Earlier", "15/9/2026");
        Task todo = TaskFactory.makeTaskFromData(new String[] {"T", "0", "Undated task"});

        List<DaySchedule> schedules = DaySchedule.createSchedules(List.of(later, todo, earlier));

        assertEquals(List.of(DATE, DATE.plusDays(2)), schedules.stream().map(DaySchedule::getDate).toList());
        assertEquals(List.of(earlier), schedules.get(0).getOtherTasks());
        assertEquals(List.of(later), schedules.get(1).getOtherTasks());
    }

    @Test
    public void createSchedules_onlyUndatedTasks_hasNoDatedSchedules() throws ParseException {
        Task todo = TaskFactory.makeTaskFromData(new String[] {"T", "0", "Undated task"});

        assertTrue(DaySchedule.createSchedules(List.of(todo)).isEmpty());
    }

    @Test
    public void createSchedule_emptyInput_hasNoEntriesOrColumns() {
        DaySchedule schedule = new DaySchedule(DATE, List.of());

        assertEquals(DATE, schedule.getDate());
        assertTrue(schedule.getEntries().isEmpty());
        assertTrue(schedule.getOtherTasks().isEmpty());
        assertEquals(0, schedule.getColumnCount());
        assertTrue(DaySchedule.createSchedules(List.of()).isEmpty());
    }

    /**
     * Creates an event through saved data, including legacy records with equal start and end times.
     *
     * @param name The event description.
     * @param start The saved start date and optional time.
     * @param end The saved end date and optional time.
     * @return The reconstructed event.
     * @throws ParseException If the task factory rejects the saved record.
     */
    private Task makeEvent(String name, String start, String end) throws ParseException {
        return TaskFactory.makeTaskFromData(new String[] {"E", "0", name, start, end});
    }

    /**
     * Creates a deadline through the public task factory.
     *
     * @param name The deadline description.
     * @param dateTime The saved due date and optional time.
     * @return The reconstructed deadline.
     * @throws ParseException If the task factory rejects the saved record.
     */
    private Task makeDeadline(String name, String dateTime) throws ParseException {
        return TaskFactory.makeTaskFromData(new String[] {"D", "0", name, dateTime});
    }

    /**
     * Verifies a single daily event segment, including its original task identity.
     *
     * @param schedule The daily schedule to inspect.
     * @param task The expected original task object.
     * @param startMinute The expected inclusive start in minutes after midnight.
     * @param endMinute The expected exclusive end in minutes after midnight.
     */
    private void assertSingleEntry(DaySchedule schedule, Task task, int startMinute, int endMinute) {
        assertEquals(1, schedule.getEntries().size());
        ScheduleEntry entry = schedule.getEntries().getFirst();
        assertSame(task, entry.getTask());
        assertEquals(startMinute, entry.getStartMinute());
        assertEquals(endMinute, entry.getEndMinute());
        assertEquals(1, schedule.getColumnCount());
        assertValidMinimumColumns(schedule);
    }

    /**
     * Checks collision freedom and compares the column count against an independent minute-by-minute overlap count.
     *
     * @param schedule The schedule whose placements and column count must be valid.
     */
    private void assertValidMinimumColumns(DaySchedule schedule) {
        List<ScheduleEntry> entries = schedule.getEntries();
        int maximumOverlap = 0;
        for (int minute = 0; minute < 1440; minute++) {
            int overlap = 0;
            for (ScheduleEntry entry : entries) {
                if (entry.getStartMinute() <= minute && minute < entry.getEndMinute()) {
                    overlap++;
                }
            }
            maximumOverlap = Math.max(maximumOverlap, overlap);
        }
        assertEquals(maximumOverlap, schedule.getColumnCount());
        for (int i = 0; i < entries.size(); i++) {
            ScheduleEntry entry = entries.get(i);
            assertTrue(entry.getColumn() >= 0 && entry.getColumn() < schedule.getColumnCount());
            assertTrue(entry.getStartMinute() >= 0 && entry.getEndMinute() <= 1440);
            assertTrue(entry.getStartMinute() < entry.getEndMinute());
            for (int j = i + 1; j < entries.size(); j++) {
                ScheduleEntry other = entries.get(j);
                if (entry.getColumn() == other.getColumn()) {
                    assertTrue(entry.getEndMinute() <= other.getStartMinute()
                            || other.getEndMinute() <= entry.getStartMinute(),
                            "Events sharing a column must not overlap");
                }
            }
        }
    }
}
