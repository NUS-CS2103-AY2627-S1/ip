package panda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import panda.exception.InvalidDateException;
import panda.exception.InvalidEventPeriodException;
import panda.exception.InvalidTaskNumberException;
import panda.exception.InvalidUpdateException;

/**
 * Tests task-list operations that depend on one-based numbering and dates.
 */
class TaskListTest {

    @Test
    void getTasksOn_mixedTasks_returnsMatchingDatedTasksWithOriginalNumbers()
            throws InvalidDateException {
        Todo todo = new Todo("read book");
        Deadline matchingDeadline = new Deadline("submit report", "2026-09-11 18:00");
        Event spanningEvent = new Event("orientation", "2026-09-10 09:00",
                "2026-09-12 17:00");
        Deadline otherDeadline = new Deadline("return book", "2026-09-13 10:00");
        TaskList tasks = new TaskList(List.of(
                todo, matchingDeadline, spanningEvent, otherDeadline));

        List<TaskList.NumberedTask> result = tasks.getTasksOn(LocalDate.of(2026, 9, 11));

        assertEquals(2, result.size());
        assertEquals(2, result.get(0).number());
        assertSame(matchingDeadline, result.get(0).task());
        assertEquals(3, result.get(1).number());
        assertSame(spanningEvent, result.get(1).task());
    }

    @Test
    void getTasksOn_eventBoundaryDates_includesBothEndpoints()
            throws InvalidDateException {
        Event event = new Event("camp", "2026-09-10 09:00", "2026-09-12 17:00");
        TaskList tasks = new TaskList(List.of(event));

        assertEquals(1, tasks.getTasksOn(LocalDate.of(2026, 9, 10)).size());
        assertEquals(1, tasks.getTasksOn(LocalDate.of(2026, 9, 12)).size());
        assertEquals(0, tasks.getTasksOn(LocalDate.of(2026, 9, 9)).size());
        assertEquals(0, tasks.getTasksOn(LocalDate.of(2026, 9, 13)).size());
    }

    @Test
    void getTasksMatching_mixedCaseSubstring_returnsMatchesWithOriginalNumbers()
            throws InvalidDateException {
        Todo first = new Todo("Read Book");
        Todo nonMatch = new Todo("buy groceries");
        Deadline deadline = new Deadline("return book", "2026-09-13 10:00");
        Event event = new Event("book club meeting", "2026-09-14 14:00",
                "2026-09-14 16:00");
        TaskList tasks = new TaskList(List.of(first, nonMatch, deadline, event));

        List<TaskList.NumberedTask> result = tasks.getTasksMatching("BOOK");

        assertEquals(3, result.size());
        assertEquals(1, result.get(0).number());
        assertSame(first, result.get(0).task());
        assertEquals(3, result.get(1).number());
        assertSame(deadline, result.get(1).task());
        assertEquals(4, result.get(2).number());
        assertSame(event, result.get(2).task());
        assertEquals(0, tasks.getTasksMatching("homework").size());
    }

    @Test
    void delete_validTaskNumber_removesAndReturnsTheSelectedTask()
            throws InvalidTaskNumberException {
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");
        TaskList tasks = new TaskList(List.of(first, second));

        Task deleted = tasks.delete(1);

        assertSame(first, deleted);
        assertEquals(List.of(second), tasks.getTaskSnapshot());
    }

    @Test
    void delete_outOfRangeTaskNumbers_throwsInvalidTaskNumberException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(0));
        assertThrows(InvalidTaskNumberException.class, () -> tasks.delete(2));
    }

    @Test
    void rename_completedDatedTask_preservesIdentityStatusTimingAndPosition()
            throws InvalidDateException, InvalidTaskNumberException {
        Todo first = new Todo("read book");
        Deadline deadline = new Deadline("submit report", "2026-09-13 10:00");
        deadline.mark();
        TaskList tasks = new TaskList(List.of(first, deadline));

        Task renamedTask = tasks.rename(2, "submit final report");

        assertSame(deadline, renamedTask);
        assertEquals("submit final report", renamedTask.getName());
        assertEquals("submit final report (by: Sep 13 2026 10:00)",
                renamedTask.getDisplayText());
        assertTrue(renamedTask.isDone());
        assertEquals(2, tasks.getTasks().get(1).number());
    }

    @Test
    void rescheduleDeadline_validDate_preservesNameStatusAndPosition()
            throws InvalidDateException, InvalidTaskNumberException, InvalidUpdateException {
        Deadline deadline = new Deadline("submit report", "2026-09-13 10:00");
        deadline.mark();
        TaskList tasks = new TaskList(List.of(deadline));

        Task updatedTask = tasks.rescheduleDeadline(1, "2026-09-15 18:00");

        assertSame(deadline, updatedTask);
        assertEquals("submit report (by: Sep 15 2026 18:00)",
                updatedTask.getDisplayText());
        assertTrue(updatedTask.isDone());
        assertEquals(1, tasks.getTasks().get(0).number());
    }

    @Test
    void rescheduleEvent_validInterval_preservesNameStatusAndPosition()
            throws InvalidDateException, InvalidTaskNumberException, InvalidUpdateException {
        Event event = new Event("project meeting", "2026-09-16 13:00",
                "2026-09-16 15:00");
        TaskList tasks = new TaskList(List.of(event));

        Task updatedTask = tasks.rescheduleEvent(
                1, "2026-09-17 14:00", "2026-09-17 17:00");

        assertSame(event, updatedTask);
        assertEquals("project meeting (from: Sep 17 2026 14:00 to: Sep 17 2026 17:00)",
                updatedTask.getDisplayText());
        assertEquals(1, tasks.getTasks().get(0).number());
    }

    @Test
    void reschedule_wrongTaskType_throwsInvalidUpdateException()
            throws InvalidDateException {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", "2026-09-13 10:00")));

        assertThrows(InvalidUpdateException.class, () ->
                tasks.rescheduleDeadline(1, "2026-09-15 18:00"));
        assertThrows(InvalidUpdateException.class, () ->
                tasks.rescheduleEvent(
                        2, "2026-09-17 14:00", "2026-09-17 17:00"));
    }

    @Test
    void rescheduleEvent_invalidEnd_keepsOriginalInterval()
            throws InvalidDateException {
        Event event = new Event("project meeting", "2026-09-16 13:00",
                "2026-09-16 15:00");

        assertThrows(InvalidDateException.class, () ->
                event.reschedule("2026-09-17 14:00", "not a date"));
        assertEquals("project meeting (from: Sep 16 2026 13:00 to: Sep 16 2026 15:00)",
                event.getDisplayText());
    }

    @Test
    void rescheduleEvent_startAfterEnd_keepsOriginalInterval()
            throws InvalidDateException {
        Event event = new Event("project meeting", "2026-09-16 13:00",
                "2026-09-16 15:00");

        assertThrows(InvalidEventPeriodException.class, () ->
                event.reschedule("2026-09-17 18:00", "2026-09-17 17:00"));
        assertEquals("project meeting (from: Sep 16 2026 13:00 to: Sep 16 2026 15:00)",
                event.getDisplayText());
    }
}
