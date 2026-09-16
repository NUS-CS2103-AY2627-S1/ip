package brucli.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void containing_matchingSubstring_returnsMatchingTasks() {
        TaskList tasks = createTaskList();

        List<TaskList.IndexedTask> matches = tasks.containing("book");

        assertEquals(2, matches.size());
        assertEquals(1, matches.get(0).number());
        assertEquals(3, matches.get(1).number());
    }

    @Test
    public void containing_keywordWithDifferentCase_returnsMatchingTasks() {
        TaskList tasks = createTaskList();

        List<TaskList.IndexedTask> matches = tasks.containing("BOOK FLIGHT");

        assertEquals(1, matches.size());
        assertEquals(1, matches.getFirst().number());
    }

    @Test
    public void containing_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = createTaskList();

        List<TaskList.IndexedTask> matches = tasks.containing("exercise");

        assertEquals(List.of(), matches);
    }

    @Test
    public void containing_blankKeyword_exceptionThrown() {
        TaskList tasks = createTaskList();

        assertThrows(IllegalArgumentException.class, () -> tasks.containing("  "));
    }

    @Test
    public void containing_nullKeyword_exceptionThrown() {
        TaskList tasks = createTaskList();

        assertThrows(IllegalArgumentException.class, () -> tasks.containing(null));
    }

    @Test
    public void sortedBy_description_returnsAlphabeticalViewWithOriginalNumbers() {
        TaskList tasks = new TaskList(List.of(
                new Todo(0, "zebra task"),
                new Todo(1, "Apple task"),
                new Todo(2, "middle task")
        ));

        List<TaskList.IndexedTask> sortedTasks = tasks.sortedBy(SortField.DESCRIPTION);

        assertEquals(List.of(2, 3, 1), taskNumbers(sortedTasks));
    }

    @Test
    public void sortedBy_date_returnsChronologicalViewWithUndatedTasksLast() {
        TaskList tasks = new TaskList(List.of(
                new Todo(0, "undated task"),
                new Deadline(1, "later deadline", LocalDateTime.of(2026, 9, 13, 18, 0)),
                new Event(
                        2,
                        "earlier event",
                        LocalDateTime.of(2026, 9, 13, 9, 0),
                        LocalDateTime.of(2026, 9, 13, 10, 0)
                )
        ));

        List<TaskList.IndexedTask> sortedTasks = tasks.sortedBy(SortField.DATE);

        assertEquals(List.of(3, 2, 1), taskNumbers(sortedTasks));
    }

    @Test
    public void sortedBy_status_returnsIncompleteTasksBeforeCompletedTasks() {
        TaskList tasks = createTaskList();
        tasks.markDone(0);

        List<TaskList.IndexedTask> sortedTasks = tasks.sortedBy(SortField.STATUS);

        assertEquals(List.of(2, 3, 1), taskNumbers(sortedTasks));
    }

    @Test
    public void sortedBy_emptyTaskList_returnsEmptyList() {
        TaskList tasks = new TaskList();

        List<TaskList.IndexedTask> sortedTasks = tasks.sortedBy(SortField.DESCRIPTION);

        assertEquals(List.of(), sortedTasks);
    }

    private TaskList createTaskList() {
        return new TaskList(List.of(
                new Todo(0, "book flight"),
                new Todo(1, "read notes"),
                new Todo(2, "return library book")
        ));
    }

    private List<Integer> taskNumbers(List<TaskList.IndexedTask> indexedTasks) {
        return indexedTasks.stream()
                .map(TaskList.IndexedTask::number)
                .toList();
    }
}
