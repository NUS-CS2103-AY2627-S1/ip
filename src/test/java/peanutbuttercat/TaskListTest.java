package peanutbuttercat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list filtering by description. */
class TaskListTest {
    @Test
    void findByDescription_caseInsensitiveSubstring_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read a book"),
                new Todo("Buy groceries"),
                new Deadline("Return book", java.time.LocalDateTime.of(2026, 6, 6, 18, 0))));

        List<Task> matches = tasks.findByDescription("BOOK");

        assertEquals(List.of(tasks.get(0), tasks.get(2)), matches);
    }

    @Test
    void findByDescription_noMatch_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("Read a book")));

        assertEquals(List.of(), tasks.findByDescription("travel"));
    }

    @Test
    void containsSameDetails_sameDetailsIgnoringCase_returnsTrue() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read a book"),
                new Deadline("Return book", java.time.LocalDateTime.of(2026, 6, 6, 18, 0))));

        assertTrue(tasks.containsSameDetails(new Todo("read a book")));
        assertTrue(tasks.containsSameDetails(new Todo("read  a   book")));
        assertTrue(tasks.containsSameDetails(
                new Deadline("return BOOK", java.time.LocalDateTime.of(2026, 6, 6, 18, 0))));
        assertFalse(tasks.containsSameDetails(
                new Deadline("return book", java.time.LocalDateTime.of(2026, 6, 7, 18, 0))));
    }

    @Test
    void countCompletedBetween_mixedCompletionDates_countsInclusiveRangeOnly() {
        Todo firstBoundaryTask = new Todo("first boundary");
        firstBoundaryTask.markAsDone(LocalDate.of(2026, 9, 3));
        Todo lastBoundaryTask = new Todo("last boundary");
        lastBoundaryTask.markAsDone(LocalDate.of(2026, 9, 9));
        Todo oldTask = new Todo("old task");
        oldTask.markAsDone(LocalDate.of(2026, 9, 2));
        Todo incompleteTask = new Todo("incomplete task");
        TaskList tasks = new TaskList(List.of(
                firstBoundaryTask, lastBoundaryTask, oldTask, incompleteTask));

        assertEquals(2, tasks.countCompletedBetween(
                LocalDate.of(2026, 9, 3), LocalDate.of(2026, 9, 9)));
    }

    @Test
    void countCompletedWithUnknownDate_legacyCompletedTask_countsTask() {
        Todo legacyCompletedTask = new Todo("legacy completed task");
        legacyCompletedTask.restoreCompletion(true, null);
        TaskList tasks = new TaskList(List.of(legacyCompletedTask, new Todo("incomplete task")));

        assertEquals(1, tasks.countCompletedWithUnknownDate());
    }
}
