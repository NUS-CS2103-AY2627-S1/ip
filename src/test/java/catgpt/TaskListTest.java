package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations in {@link TaskList}.
 */
class TaskListTest {
    @Test
    void addNullTaskViolatesInternalInvariant() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void addTaskStoresTaskAtEnd() throws CatGPTException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void deleteTaskRemovesAndReturnsSelectedTask() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("write notes");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task deletedTask = tasks.delete(0);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    void restoreTaskInsertsItAtOriginalPosition() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("write notes");
        TaskList tasks = new TaskList(List.of(secondTask));

        tasks.restore(0, firstTask);

        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    void markTaskUpdatesDisplayedStatus() throws CatGPTException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.get(0).markAsDone();

        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    void findMatchingKeywordReturnsOnlyMatchingTasks() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("write notes");
        Task secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstMatch, matchingTasks.get(0));
        assertSame(secondMatch, matchingTasks.get(1));
    }

    @Test
    void findKeywordIgnoresLetterCase() {
        Task uppercaseDescription = new Todo("Review Project Notes");
        TaskList tasks = new TaskList(List.of(uppercaseDescription));

        TaskList matchingTasks = tasks.find("project");

        assertEquals(1, matchingTasks.size());
        assertSame(uppercaseDescription, matchingTasks.get(0));
    }

    @Test
    void addTaskToFullListThrowsException() {
        List<Task> fullTaskList = new ArrayList<>();
        for (int i = 0; i < TaskList.MAX_TASKS; i++) {
            fullTaskList.add(new Todo("task " + i));
        }
        TaskList tasks = new TaskList(fullTaskList);

        CatGPTException exception = assertThrows(
                CatGPTException.class, () -> tasks.add(new Todo("one too many")));

        assertEquals("Your task list is full.", exception.getMessage());
        assertEquals(TaskList.MAX_TASKS, tasks.size());
    }

    @Test
    void snapshotCannotBeModified() throws CatGPTException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> snapshot = tasks.snapshot();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write notes")));
    }

    @Test
    void sortByDescriptionOrdersTasksAlphabeticallyIgnoringCase() {
        Task firstAlphaMatch = new Todo("Write report");
        Task secondAlphaMatch = new Todo("write slides");
        Task earliestTask = new Todo("attend meeting");
        TaskList tasks = new TaskList(List.of(firstAlphaMatch, secondAlphaMatch, earliestTask));

        tasks.sortByDescription();

        assertSame(earliestTask, tasks.get(0));
        assertSame(firstAlphaMatch, tasks.get(1));
        assertSame(secondAlphaMatch, tasks.get(2));
    }

    @Test
    void sortByDescriptionEqualDescriptionsPreserveExistingOrder() {
        Task firstTask = new Todo("Read book");
        Task secondTask = new Deadline("read book", java.time.LocalDate.of(2026, 9, 30));
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        tasks.sortByDescription();

        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }
}
