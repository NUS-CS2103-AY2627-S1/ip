package mochi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import mochi.MochiException;

/**
 * Tests operations on the task collection.
 */
public class TaskListTest {
    @Test
    public void add_validTask_appendsTask() throws MochiException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(1));
    }

    @Test
    public void delete_validTaskNumber_removesAndReturnsTask() throws MochiException {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("write report");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = tasks.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(List.of(secondTask), tasks.getTasks());
    }

    @Test
    public void markAndUnmark_validTaskNumber_updatesAndReturnsTask() throws MochiException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));

        Task markedTask = tasks.mark(1);
        assertSame(todo, markedTask);
        assertEquals("X", todo.getStatusIcon());

        Task unmarkedTask = tasks.unmark(1);
        assertSame(todo, unmarkedTask);
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void taskOperations_taskNumberOutOfBounds_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(MochiException.class, () -> tasks.get(0));
        assertThrows(MochiException.class, () -> tasks.get(2));
        assertThrows(MochiException.class, () -> tasks.delete(-1));
        assertThrows(MochiException.class, () -> tasks.mark(2));
        assertThrows(MochiException.class, () -> tasks.unmark(2));
    }

    @Test
    public void getTasks_returnedListCannotMutateTaskList() {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(List.of(todo));
        List<Task> taskSnapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class,
                () -> taskSnapshot.add(new Todo("write report")));
        assertEquals(List.of(todo), tasks.getTasks());
    }

    @Test
    public void constructor_sourceListChanged_taskListIsUnchanged() {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new Todo("read book"));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.clear();

        assertFalse(tasks.getTasks().isEmpty());
    }

    @Test
    public void find_keywordInDescriptions_returnsMatchesInOriginalOrder() {
        Todo readBook = new Todo("read book");
        Deadline returnBook = new Deadline(
                "return book", LocalDateTime.of(2026, 6, 6, 18, 0));
        Event meeting = new Event(
                "project meeting",
                LocalDateTime.of(2026, 8, 6, 14, 0),
                LocalDateTime.of(2026, 8, 6, 16, 0));
        TaskList tasks = new TaskList(List.of(readBook, returnBook, meeting));

        assertEquals(List.of(readBook, returnBook), tasks.find("book"));
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("meeting").isEmpty());
    }

    @Test
    public void find_keywordOnlyInFormattedDate_returnsEmptyList() {
        Deadline deadline = new Deadline(
                "return book", LocalDateTime.of(2026, 6, 6, 18, 0));
        TaskList tasks = new TaskList(List.of(deadline));

        assertTrue(tasks.find("Jun").isEmpty());
    }
}
