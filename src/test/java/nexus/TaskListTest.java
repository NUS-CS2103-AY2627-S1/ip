package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task storage, lookup, status changes, and deletion in {@link TaskList}. */
class TaskListTest {
    @Test
    void hasIndex_emptyList_returnsFalse() {
        TaskList taskList = new TaskList();

        assertFalse(taskList.hasIndex(0));
        assertFalse(taskList.hasIndex(-1));
    }

    @Test
    void addAndGet_validTask_returnsTaskAtExpectedIndex() {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");

        taskList.add(firstTask);
        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertTrue(taskList.hasIndex(0));
        assertTrue(taskList.hasIndex(1));
        assertEquals(firstTask, taskList.get(0));
        assertEquals(secondTask, taskList.get(1));
    }

    @Test
    void constructorAndAdd_nullValues_throwException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(null));

        TaskList taskList = new TaskList();
        assertThrows(IllegalArgumentException.class, () -> taskList.add(null));
    }

    @Test
    void add_duplicateTask_throwsException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("same task"));
        assertThrows(IllegalArgumentException.class, () -> taskList.add(new Todo("same task")));
    }

    @Test
    void get_invalidIndex_throwsException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("task"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(1));
    }

    @Test
    void markAndUnmark_validIndex_updatesTaskStatus() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("task"));

        taskList.mark(0);
        assertEquals("X", taskList.get(0).getStatusIcon());

        taskList.unmark(0);
        assertEquals(" ", taskList.get(0).getStatusIcon());
    }

    @Test
    void delete_validIndex_removesAndReturnsTask() {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        taskList.add(firstTask);
        taskList.add(secondTask);

        Task deletedTask = taskList.delete(0);

        assertEquals(firstTask, deletedTask);
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.get(0));
    }

    @Test
    void delete_invalidIndex_throwsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));
    }

    @Test
    void find_matchingKeyword_returnsMatchingTasks() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy groceries"),
                new Todo("return book")));

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return book", matchingTasks.get(1).getDescription());
    }

    @Test
    void findKeyword_differentLetterCase_returnsMatchingTasks() {
        TaskList taskList = new TaskList(List.of(new Todo("Read Book")));

        assertEquals(1, taskList.find("book").size());
    }

    @Test
    void find_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertTrue(taskList.find("movie").isEmpty());
    }

    @Test
    void find_emptyKeyword_returnsAllTasks() {
        TaskList taskList = new TaskList(List.of(new Todo("read book"), new Todo("buy milk")));

        assertEquals(2, taskList.find("").size());
    }

    @Test
    void visibleTasks_snoozedTaskIsHiddenUntilDate() {
        TaskList taskList = new TaskList(List.of(new Todo("snoozed"), new Todo("visible")));
        taskList.snooze(0, LocalDate.of(2026, 9, 15));

        assertEquals(1, taskList.visibleTasks(LocalDate.of(2026, 9, 14)).size());
        assertEquals(2, taskList.visibleTasks(LocalDate.of(2026, 9, 15)).size());
    }
}
