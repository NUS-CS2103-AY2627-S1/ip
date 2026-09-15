package larp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations performed by {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void find_matchingKeyword_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Event("project meeting", "2pm", "3pm"),
                new Deadline("return book", LocalDate.of(2026, 9, 1))));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return book", matchingTasks.get(1).getDescription());
    }

    @Test
    public void find_keywordWithDifferentCase_returnsMatchingTasks() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read Book"),
                new Todo("submit report")));

        TaskList matchingTasks = tasks.find("BOOK");

        assertEquals(1, matchingTasks.size());
        assertEquals("Read Book", matchingTasks.get(0).getDescription());
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyTaskList() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Event("project meeting", "2pm", "3pm")));

        TaskList matchingTasks = tasks.find("exercise");

        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    public void get_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.get(1));
    }

    @Test
    public void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void markAndUnmark_validTask_updatesCompletionState() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        Task markedTask = tasks.markAsDone(0);
        assertTrue(markedTask.isDone());

        Task unmarkedTask = tasks.markAsNotDone(0);
        assertFalse(unmarkedTask.isDone());
    }

    @Test
    public void delete_validTask_removesAndReturnsTask() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("submit report")));

        Task deletedTask = tasks.delete(0);

        assertEquals("read book", deletedTask.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("submit report", tasks.get(0).getDescription());
    }

    @Test
    public void getTasks_returnedListCannotModifyTaskList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        List<Task> returnedTasks = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class,
                () -> returnedTasks.add(new Todo("write report")));
        assertEquals(1, tasks.size());
    }
}
