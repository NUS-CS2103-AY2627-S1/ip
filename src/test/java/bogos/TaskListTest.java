package bogos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for finding tasks in a task list.
 */
public class TaskListTest {
    /**
     * Verifies that finding tasks matches description keywords without regard to case.
     */
    @Test
    public void findTasks_matchingKeyword_returnsMatchesInOriginalOrder() {
        Todo firstMatchingTask = new Todo("read book");
        Deadline secondMatchingTask = new Deadline("return BOOK", LocalDate.of(2026, 9, 6));
        TaskList tasks = new TaskList(List.of(firstMatchingTask, new Todo("buy milk"), secondMatchingTask));

        List<Task> matchingTasks = tasks.findTasks("Book");

        assertEquals(List.of(firstMatchingTask, secondMatchingTask), matchingTasks);
    }

    /**
     * Verifies that finding tasks returns no tasks when no description matches.
     */
    @Test
    public void findTasks_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        List<Task> matchingTasks = tasks.findTasks("milk");

        assertEquals(List.of(), matchingTasks);
    }

    /**
     * Verifies that finding tasks continues to search descriptions rather than tags.
     */
    @Test
    public void findTasks_matchingOnlyTag_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("watch movie", List.of("fun"))));

        List<Task> matchingTasks = tasks.findTasks("fun");

        assertEquals(List.of(), matchingTasks);
    }

    /**
     * Verifies that a task with the same details is not added twice, even when tag order differs.
     */
    @Test
    public void addTask_duplicateTaskWithDifferentTagOrder_exceptionThrown() throws BogosException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Deadline("submit report", LocalDate.of(2026, 9, 15),
                List.of("school", "urgent")));

        BogosException exception = assertThrows(BogosException.class,
                () -> tasks.addTask(new Deadline("submit report", LocalDate.of(2026, 9, 15),
                        List.of("urgent", "school"))));

        assertEquals("Bummer, buplicate bullet. :[", exception.getMessage());
        assertEquals(1, tasks.size());
    }
}
