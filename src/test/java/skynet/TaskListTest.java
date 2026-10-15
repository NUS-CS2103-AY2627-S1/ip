package skynet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

// write add_task_increasesize by hand to get used to writing tests
// follow structure of @Tests -> Arrange -> Act -> Assert

public class TaskListTest {

    @Test
    void add_task_increasesSize() {
        // Arrange
        TaskList tasks = new TaskList();
        Task task = new Todo("test task");
        // Act
        tasks.add(task);
        // Assert
        assertEquals(1, tasks.size());
    }

    @Test
    void delete_existingTask_returnsDeletedTask() {
        // Arrange
        TaskList tasks = new TaskList();
        Task task = new Todo("test task");
        tasks.add(task);
        // Act
        Task deleted = tasks.delete(0);
        // Assert
        assertEquals(task, deleted);
        assertEquals(0, tasks.size());
    }

    @Test
    void get_existingIndex_returnsCorrectTask() {
        // Arrange
        TaskList tasks = new TaskList();
        Task task = new Todo("test task");
        tasks.add(task);
        // Act
        Task result = tasks.get(0);
        // Assert
        assertEquals(task, result);
    }

    @Test
    void find_matchingKeyword_returnsMatchingTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("go shopping"));

        List<Task> matches = tasks.find("book");

        assertEquals(1, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
    }

    @Test
    void find_isCaseInsensitiveAndReturnsEmptyWhenNothingMatches() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read a Book"),
                new Todo("Go shopping")));

        assertEquals(1, tasks.find("book").size());
        assertEquals(0, tasks.find("holiday").size());
    }

    @Test
    void add_nullTask_isRejected() {
        TaskList tasks = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> tasks.add(null));
    }

    @Test
    void find_nullOrBlankKeyword_isRejected() {
        TaskList tasks = new TaskList(List.of(new Todo("test task")));

        assertThrows(IllegalArgumentException.class, () -> tasks.find(null));
        assertThrows(IllegalArgumentException.class, () -> tasks.find("   "));
    }

    @Test
    void constructor_copiesInputList() {
        List<Task> original = new java.util.ArrayList<>();
        original.add(new Todo("test task"));
        TaskList tasks = new TaskList(original);

        original.clear();

        assertEquals(1, tasks.size());
    }

    @Test
    void constructor_nullListOrNullElement_isRejected() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(null));
        assertThrows(IllegalArgumentException.class, () ->
                new TaskList(java.util.Arrays.asList(new Todo("valid"), null)));
    }

    @Test
    void getAndDelete_invalidIndex_areRejected() {
        TaskList tasks = new TaskList(List.of(new Todo("test task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(1));
    }
}
