package zhangwei.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import zhangwei.ZhangWeiException;

/**
 * Tests {@link TaskList}, the class that owns the 1-based task numbers the
 * user types. Off-by-one mistakes and missing bounds checks would show up as
 * the wrong task being deleted, so those are the cases these tests focus on.
 */
public class TaskListTest {

    /** Returns a list holding the given number of todos named "task 1", "task 2", ... */
    private TaskList listOf(int count) {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= count; i++) {
            tasks.add(new Todo("task " + i));
        }
        return tasks;
    }

    @Test
    public void constructor_noArguments_emptyListCreated() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_givenTasks_tasksCopiedIn() {
        List<Task> loaded = List.of(new Todo("a"), new Todo("b"));
        assertEquals(2, new TaskList(loaded).size());
    }

    @Test
    public void constructor_sourceListChangedLater_taskListUnaffected() {
        // The constructor copies, so the caller's list is not shared with us.
        ArrayList<Task> source = new ArrayList<>();
        source.add(new Todo("a"));
        TaskList tasks = new TaskList(source);
        source.add(new Todo("b"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_taskToEmptyList_sizeBecomesOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_severalTasks_addedAtTheEndInOrder() {
        TaskList tasks = listOf(3);
        assertEquals("task 1", tasks.get(1).getDescription());
        assertEquals("task 3", tasks.get(3).getDescription());
    }

    @Test
    public void get_firstTaskNumber_firstTaskReturned() {
        // Task 1 for the user is index 0 inside the list.
        assertEquals("task 1", listOf(3).get(1).getDescription());
    }

    @Test
    public void get_lastTaskNumber_lastTaskReturned() {
        assertEquals("task 3", listOf(3).get(3).getDescription());
    }

    @Test
    public void delete_validTaskNumber_removedTaskReturned() {
        TaskList tasks = listOf(3);
        Task expected = tasks.get(2);
        assertSame(expected, tasks.delete(2));
    }

    @Test
    public void delete_validTaskNumber_sizeDecreases() {
        TaskList tasks = listOf(3);
        tasks.delete(2);
        assertEquals(2, tasks.size());
    }

    @Test
    public void delete_middleTask_laterTasksShiftDown() {
        TaskList tasks = listOf(3);
        tasks.delete(2);
        assertEquals("task 1", tasks.get(1).getDescription());
        assertEquals("task 3", tasks.get(2).getDescription());
    }

    @Test
    public void delete_onlyTask_listBecomesEmpty() {
        TaskList tasks = listOf(1);
        tasks.delete(1);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void size_emptyList_zeroReturned() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void isEmpty_emptyList_trueReturned() {
        assertTrue(new TaskList().isEmpty());
    }

    @Test
    public void isEmpty_listWithTask_falseReturned() {
        assertFalse(listOf(1).isEmpty());
    }

    @Test
    public void asList_listWithTasks_tasksReturnedInOrder() {
        List<Task> view = listOf(2).asList();
        assertEquals(2, view.size());
        assertEquals("task 1", view.get(0).getDescription());
        assertEquals("task 2", view.get(1).getDescription());
    }

    @Test
    public void asList_attemptToModify_exceptionThrown() {
        // The view is read-only so that every change still goes through TaskList.
        List<Task> view = listOf(1).asList();
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("x")));
    }

    @Test
    public void find_keywordInSeveralDescriptions_matchingTasksReturnedInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Todo("return book"));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_keywordWithDifferentCase_matchingTaskReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        List<Task> matches = tasks.find("book");

        assertEquals(1, matches.size());
    }

    @Test
    public void find_keywordOnlyInDeadlineDate_noTaskReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return assignment", LocalDate.of(2019, 12, 2)));

        List<Task> matches = tasks.find("Dec");

        assertTrue(matches.isEmpty());
    }

    @Test
    public void find_noMatchingDescription_emptyListReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> matches = tasks.find("groceries");

        assertTrue(matches.isEmpty());
    }

    @Test
    public void find_returnedListModified_exceptionThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> matches = tasks.find("book");

        assertThrows(UnsupportedOperationException.class,
                () -> matches.add(new Todo("another book")));
    }

    @Test
    public void requireTask_emptyList_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> new TaskList().requireTask(1));
        assertTrue(e.getMessage().contains("no tasks yet"));
    }

    @Test
    public void requireTask_zero_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> listOf(3).requireTask(0));
    }

    @Test
    public void requireTask_negativeNumber_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> listOf(3).requireTask(-1));
    }

    @Test
    public void requireTask_numberAboveSize_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> listOf(3).requireTask(4));
        assertTrue(e.getMessage().contains("You have 3 tasks"));
    }

    @Test
    public void requireTask_numberAboveSizeOfOne_singularTaskInMessage() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> listOf(1).requireTask(2));
        assertTrue(e.getMessage().contains("You have 1 task."));
    }

    @Test
    public void requireTask_firstTaskNumber_noExceptionThrown() {
        assertDoesNotThrow(() -> listOf(3).requireTask(1));
    }

    @Test
    public void requireTask_lastTaskNumber_noExceptionThrown() {
        assertDoesNotThrow(() -> listOf(3).requireTask(3));
    }
}
