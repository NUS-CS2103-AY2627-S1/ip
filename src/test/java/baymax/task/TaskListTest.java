package baymax.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import baymax.exception.BaymaxException;

/**
 * Tests task-list behavior that affects core task management features.
 */
public class TaskListTest {
    @Test
    public void add_sameDetailsWithDifferentStatus_rejectsDuplicate() {
        TaskList tasks = new TaskList();
        Deadline original = new Deadline("work", LocalDate.of(2024, 1, 1));
        original.markAsDone();
        tasks.add(original);
        assertThrows(BaymaxException.class, () -> tasks.add(new Deadline("work", LocalDate.of(2024, 1, 1))));
        tasks.add(new Deadline("work", LocalDate.of(2024, 1, 2)));
        tasks.add(new Todo("work"));
        tasks.add(new Event("work", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2)));
        assertThrows(BaymaxException.class, () -> tasks.add(
                new Event("work", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2))));
        assertEquals(4, tasks.size());
    }


    @Test
    public void find_keywordInTaskDescriptions_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        tasks.add(new Event(
                "team meeting",
                LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4)));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] read book", matchingTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)",
                matchingTasks.get(1).toString());
    }

    @Test
    public void find_keywordWithDifferentCapitalization_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        TaskList matchingTasks = tasks.find("book");

        assertEquals(1, matchingTasks.size());
        assertEquals("[T][ ] Read Book", matchingTasks.get(0).toString());
    }

    @Test
    public void find_partialKeyword_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList matchingTasks = tasks.find("boo");

        assertEquals(1, matchingTasks.size());
        assertEquals("[T][ ] read book", matchingTasks.get(0).toString());
    }

    @Test
    public void find_multiplePartialKeywords_returnsTasksContainingAllKeywords() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        TaskList matchingTasks = tasks.find("ret boo");

        assertEquals(1, matchingTasks.size());
        assertEquals("[T][ ] return book", matchingTasks.get(0).toString());
    }

    @Test
    public void find_nonMatchingPartialKeyword_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        TaskList matchingTasks = tasks.find("movie");

        assertEquals(0, matchingTasks.size());
    }
}
