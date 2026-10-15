package carlo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import carlo.exception.CarloException;

/**
 * Tests for {@link TaskSchedule}. Every test uses the same fixed "now"
 * (Friday, 9 Oct 2026, 10:00am) so the results never depend on when the
 * tests are run.
 */
class TaskScheduleTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 9, 10, 0);

    @Test
    void groupOf_todo_isAnytime() {
        assertEquals(TaskGroup.ANYTIME, TaskSchedule.groupOf(new Todo("read"), NOW));
    }

    @Test
    void groupOf_doneTask_isDone() throws CarloException {
        Deadline deadline = new Deadline("essay", "2026-09-01");
        deadline.markAsDone();
        assertEquals(TaskGroup.DONE, TaskSchedule.groupOf(deadline, NOW));
    }

    @Test
    void groupOf_deadlineInThePast_isOverdue() throws CarloException {
        assertEquals(TaskGroup.OVERDUE, TaskSchedule.groupOf(new Deadline("essay", "2026-10-07"), NOW));
    }

    @Test
    void groupOf_dateOnlyDeadlineToday_isNotYetOverdue() throws CarloException {
        // A deadline with no time of day lasts until the end of that day.
        assertEquals(TaskGroup.SOON, TaskSchedule.groupOf(new Deadline("essay", "2026-10-09"), NOW));
    }

    @Test
    void groupOf_timedDeadlineEarlierToday_isOverdue() throws CarloException {
        assertEquals(TaskGroup.OVERDUE, TaskSchedule.groupOf(new Deadline("essay", "2026-10-09 0900"), NOW));
    }

    @Test
    void groupOf_deadlinesByDistance_areGroupedCorrectly() throws CarloException {
        assertEquals(TaskGroup.SOON, TaskSchedule.groupOf(new Deadline("a", "2026-10-10"), NOW));
        assertEquals(TaskGroup.THIS_WEEK, TaskSchedule.groupOf(new Deadline("b", "2026-10-16"), NOW));
        assertEquals(TaskGroup.LATER, TaskSchedule.groupOf(new Deadline("c", "2026-10-17"), NOW));
    }

    @Test
    void groupOf_ongoingEvent_isSoon() throws CarloException {
        assertEquals(TaskGroup.SOON, TaskSchedule.groupOf(new Event("retreat", "2026-10-08", "2026-10-11"), NOW));
    }

    @Test
    void groupOf_endedEvent_isOverdue() throws CarloException {
        assertEquals(TaskGroup.OVERDUE, TaskSchedule.groupOf(new Event("hackathon", "2026-09-19", "2026-09-20"), NOW));
    }

    @Test
    void describeTiming_todo_isEmpty() {
        assertEquals("", TaskSchedule.describeTiming(new Todo("read"), NOW));
    }

    @Test
    void describeTiming_overdueDeadline_showsDaysLate() throws CarloException {
        assertEquals("1 day late", TaskSchedule.describeTiming(new Deadline("a", "2026-10-08"), NOW));
        assertEquals("19 days late", TaskSchedule.describeTiming(new Deadline("b", "2026-09-20"), NOW));
    }

    @Test
    void describeTiming_upcomingDeadline_showsRelativeDay() throws CarloException {
        assertEquals("due today", TaskSchedule.describeTiming(new Deadline("a", "2026-10-09"), NOW));
        assertEquals("due tomorrow 6:00pm", TaskSchedule.describeTiming(new Deadline("b", "2026-10-10 1800"), NOW));
        assertEquals("due Tue", TaskSchedule.describeTiming(new Deadline("c", "2026-10-13"), NOW));
        assertEquals("due Oct 30", TaskSchedule.describeTiming(new Deadline("d", "2026-10-30"), NOW));
    }

    @Test
    void describeTiming_events_describeStartOrEnd() throws CarloException {
        assertEquals("starts tomorrow 9:00am",
                TaskSchedule.describeTiming(new Event("a", "2026-10-10 0900", "2026-10-10 1700"), NOW));
        assertEquals("on now, until Sun",
                TaskSchedule.describeTiming(new Event("b", "2026-10-08", "2026-10-11"), NOW));
        assertEquals("ended Sep 20",
                TaskSchedule.describeTiming(new Event("c", "2026-09-19", "2026-09-20"), NOW));
    }

    @Test
    void isComingUp_timedTaskWithinWindow_isTrue() throws CarloException {
        Event event = new Event("meeting", "2026-10-09 1020", "2026-10-09 1100");
        assertTrue(TaskSchedule.isComingUp(event, NOW, Duration.ofMinutes(30)));
    }

    @Test
    void isComingUp_outsideWindowOrDateOnly_isFalse() throws CarloException {
        Event later = new Event("meeting", "2026-10-09 1200", "2026-10-09 1300");
        Deadline dateOnly = new Deadline("essay", "2026-10-09");
        assertFalse(TaskSchedule.isComingUp(later, NOW, Duration.ofMinutes(30)));
        assertFalse(TaskSchedule.isComingUp(dateOnly, NOW, Duration.ofMinutes(30)));
    }

    @Test
    void bySoonestFirst_sortsByDateWithTodosLast() throws CarloException {
        Task todo = new Todo("read");
        Task later = new Deadline("later", "2026-10-20");
        Task sooner = new Event("sooner", "2026-10-10", "2026-10-11");
        List<Task> tasks = new ArrayList<>(List.of(todo, later, sooner));

        tasks.sort(TaskSchedule.bySoonestFirst());

        assertEquals(List.of(sooner, later, todo), tasks);
    }
}
