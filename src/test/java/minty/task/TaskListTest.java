package minty.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list mutations and date filtering.
 */
public class TaskListTest {

    @Test
    public void delete_validIndex_removesAndReturnsSelectedTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(new ArrayList<>(java.util.List.of(first, second)));

        Task deleted = tasks.delete(0);

        assertSame(first, deleted);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.iterator().next());
    }

    @Test
    public void delete_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.delete(0));
    }

    @Test
    public void markAndUnmark_validIndex_updatesAndReturnsSelectedTask() {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertSame(task, tasks.mark(0));
        assertEquals("X", task.getStatusIcon());
        assertSame(task, tasks.unmark(0));
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void findOn_mixedTasks_returnsOnlyMatchingDatedTasksInListOrder() {
        LocalDate requestedDate = LocalDate.of(2026, 9, 10);
        Todo todo = new Todo("undated");
        Deadline deadline = new Deadline("submit", requestedDate);
        Event event = new Event("conference", requestedDate.minusDays(1),
                requestedDate.plusDays(1));
        Deadline otherDeadline = new Deadline("later", requestedDate.plusDays(1));
        TaskList tasks = new TaskList(new ArrayList<>(
                java.util.List.of(todo, deadline, event, otherDeadline)));

        ArrayList<Task> matches = tasks.findOn(requestedDate);

        assertEquals(java.util.List.of(deadline, event), matches);
    }

    @Test
    public void findOn_noMatchingTasks_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("undated"));
        tasks.add(new Deadline("submit", LocalDate.of(2026, 9, 11)));

        assertEquals(java.util.List.of(), tasks.findOn(LocalDate.of(2026, 9, 10)));
    }

    @Test
    public void find_mixedTasks_returnsDescriptionsContainingKeywordInListOrder() {
        Todo matchingTodo = new Todo("read book");
        Deadline matchingDeadline = new Deadline("return book",
                LocalDate.of(2026, 9, 10));
        Event otherEvent = new Event("conference", LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 11));
        TaskList tasks = new TaskList(new ArrayList<>(
                java.util.List.of(matchingTodo, matchingDeadline, otherEvent)));

        assertEquals(java.util.List.of(matchingTodo, matchingDeadline), tasks.find("book"));
    }

    @Test
    public void find_keywordOnlyInTaskMetadata_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 10)));

        assertEquals(java.util.List.of(), tasks.find("2026"));
    }
}
