package aster.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list mutation and date filtering behaviour.
 */
class TaskListTest {

    @Test
    void addGetRemoveAndSize_tasksManagedInInsertionOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList taskList = new TaskList();

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(first, taskList.remove(0));
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    @Test
    void constructorAndGetAll_inputCopiedAndReturnedListIsUnmodifiable() {
        Task first = new Todo("first");
        Task[] initialTasks = {first};
        TaskList taskList = new TaskList(initialTasks);
        initialTasks[0] = new Todo("changed");

        assertEquals(List.of(first), taskList.getAll());
        assertThrows(UnsupportedOperationException.class, () -> taskList.getAll().add(new Todo("second")));
    }

    @Test
    void findTasksOnDate_matchingDeadlineAndEvent_returnedInListOrder() {
        Deadline deadline = new Deadline("submit report", "2026-09-01 17:00");
        Event event = new Event("conference", "2026-08-31", "2026-09-02");
        TaskList taskList = new TaskList(new Todo("unrelated"), deadline, event);

        assertEquals(List.of(deadline, event), taskList.findTasksOnDate(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void findTasksOnDate_eventBoundaryDates_eventReturned() {
        Event event = new Event("conference", "2026-09-01", "2026-09-03");
        TaskList taskList = new TaskList(event);

        assertEquals(List.of(event), taskList.findTasksOnDate(LocalDate.of(2026, 9, 1)));
        assertEquals(List.of(event), taskList.findTasksOnDate(LocalDate.of(2026, 9, 3)));
    }

    @Test
    void findTasksOnDate_unparseableDatesOrNoMatches_emptyListReturned() {
        TaskList taskList = new TaskList(
                new Deadline("freeform deadline", "next Tuesday"),
                new Event("freeform event", "morning", "afternoon"));

        assertEquals(List.of(), taskList.findTasksOnDate(LocalDate.of(2026, 9, 1)));
    }

    @Test
    void find_caseInsensitiveMatch_returnedInListOrder() {
        Task first = new Todo("read book");
        Task second = new Todo("buy bread");
        Task third = new Deadline("return book", "2026-06-06");
        TaskList taskList = new TaskList(first, second, third);

        assertEquals(List.of(first, third), taskList.find("BOOK"));
    }

    @Test
    void find_defaultLocaleDoesNotAffectMatching() {
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr"));
        try {
            Task task = new Todo("INDIGO");
            TaskList taskList = new TaskList(task);

            assertEquals(List.of(task), taskList.find("indigo"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void find_noMatch_emptyListReturned() {
        TaskList taskList = new TaskList(new Todo("read book"));

        assertEquals(List.of(), taskList.find("laptop"));
    }

    @Test
    void containsTaskWithSameDetails_matchingTodoDeadlineAndEvent_trueReturned() {
        TaskList taskList = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", "2026-09-01"),
                new Event("meeting", "2026-09-01 09:00", "2026-09-01 10:00"));

        assertTrue(taskList.containsTaskWithSameDetails(new Todo("read book")));
        assertTrue(taskList.containsTaskWithSameDetails(new Deadline("submit report", "2026-09-01")));
        assertTrue(taskList.containsTaskWithSameDetails(
                new Event("meeting", "2026-09-01 09:00", "2026-09-01 10:00")));
    }

    @Test
    void containsTaskWithSameDetails_differentTypeOrDifferentDetails_falseReturned() {
        TaskList taskList = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", "2026-09-01"),
                new Event("meeting", "2026-09-01 09:00", "2026-09-01 10:00"));

        assertFalse(taskList.containsTaskWithSameDetails(new Deadline("read book", "2026-09-01")));
        assertFalse(taskList.containsTaskWithSameDetails(new Deadline("submit report", "2026-09-02")));
        assertFalse(taskList.containsTaskWithSameDetails(
                new Event("meeting", "2026-09-01 11:00", "2026-09-01 12:00")));
    }
}
