package tem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void constructor_varargs_storesTasksInOrder() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    public void addAll_varargs_appendsTasks() {
        TaskList tasks = new TaskList();
        tasks.addAll(new Todo("read"), new Todo("write"));
        assertEquals(2, tasks.size());
        assertEquals("read", tasks.get(0).getDescription());
        assertEquals("write", tasks.get(1).getDescription());
    }

    @Test
    public void getTasks_returnsUnmodifiableView() {
        TaskList tasks = new TaskList(new Todo("read"));
        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks().add(new Todo("write")));
    }

    @Test
    public void findMatchingIndices_partialKeyword_returnsMatchingIndices() {
        TaskList tasks = new TaskList(new Todo("read book"), new Todo("write essay"), new Todo("book flight"));
        assertEquals(List.of(0, 2), tasks.findMatchingIndices("book"));
    }

    @Test
    public void containsEquivalent_sameTodoWithDifferentCompletionStatus_returnsTrue() {
        Todo completedTask = new Todo("Read book");
        completedTask.markAsDone();
        TaskList tasks = new TaskList(completedTask);

        assertTrue(tasks.containsEquivalent(new Todo("read book")));
    }

    @Test
    public void containsEquivalent_sameDeadlineDescriptionDifferentDate_returnsFalse() {
        TaskList tasks = new TaskList(new Deadline("submit report", java.time.LocalDate.of(2019, 10, 15)));

        assertFalse(tasks.containsEquivalent(new Deadline("submit report", java.time.LocalDate.of(2019, 10, 16))));
    }

    @Test
    public void sortChronologically_deadlinesFirstByDate_thenOtherTasks() {
        Task laterDeadline = new Deadline("submit report", java.time.LocalDate.of(2019, 12, 2));
        Task earlierDeadline = new Deadline("return book", java.time.LocalDate.of(2019, 10, 15));
        Task todo = new Todo("read book");
        Task event = new Event("meeting", "Mon 2pm", "4pm");
        TaskList tasks = new TaskList(todo, laterDeadline, event, earlierDeadline);

        tasks.sortChronologically();

        assertEquals(4, tasks.size());
        assertEquals(earlierDeadline, tasks.get(0));
        assertEquals(laterDeadline, tasks.get(1));
        assertEquals(todo, tasks.get(2));
        assertEquals(event, tasks.get(3));
    }
}
