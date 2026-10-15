package sine.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sine.exception.SineException;

/**
 * Tests task-list mutation, indexing, and collection encapsulation.
 */
public class TaskListTest {
    /** Tests that the default constructor creates an empty collection. */
    @Test
    public void constructor_noInitialTasks_createsEmptyList() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertTrue(tasks.getTasks().isEmpty());
    }

    /** Tests that added tasks retain their order and identity. */
    @Test
    public void addAndGet_multipleTasks_preservesOrderAndIdentity() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    /** Tests that deletion returns the task and closes the index gap. */
    @Test
    public void delete_existingTask_returnsTaskAndClosesIndexGap() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task removedTask = tasks.delete(0);

        assertSame(first, removedTask);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    /** Tests that later source-list changes do not affect the task list. */
    @Test
    public void constructor_sourceListChanges_doesNotChangeTaskList() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("original"));
        TaskList tasks = new TaskList(source);

        source.add(new Todo("later"));

        assertEquals(1, tasks.size());
    }

    /** Tests that {@code getTasks} returns an unmodifiable snapshot. */
    @Test
    public void getTasks_taskListChanges_returnsUnmodifiableSnapshot() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("original"));
        List<Task> snapshot = tasks.getTasks();

        tasks.add(new Todo("later"));

        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.add(new Todo("not allowed")));
    }

    @Test
    public void find_keywordMatchesDescriptions_returnsMatchesInOriginalOrder() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("write essay");
        Deadline secondMatch = new Deadline("return book",
                LocalDate.of(2026, 8, 30));
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    public void find_keywordDoesNotMatchDescription_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        List<Task> matches = tasks.find("Book");

        assertTrue(matches.isEmpty());
    }

    /** Tests that search results remain an unmodifiable snapshot after later additions. */
    @Test
    public void find_taskListChanges_returnsUnmodifiableSnapshot() {
        Todo original = new Todo("read book");
        TaskList tasks = new TaskList(List.of(original));

        List<Task> matches = tasks.find("book");
        tasks.add(new Todo("return book"));

        assertEquals(List.of(original), matches);
        assertThrows(UnsupportedOperationException.class,
                () -> matches.add(new Todo("another book")));
    }

    /** Tests restoration by position and identity rather than matching descriptions. */
    @Test
    public void undo_deletedDuplicate_restoresExactTaskAndPosition() throws SineException {
        Todo first = new Todo("same | text");
        Todo second = new Todo("same | text");
        second.markAsDone();
        TaskList tasks = new TaskList(List.of(first, second));
        tasks.delete(0);
        tasks.setDone(0, false);

        assertEquals("I marked this task as done:\n  [T][X] same | text", tasks.undo());
        tasks.undo();

        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertTrue(second.isDone());
        assertThrows(SineException.class, tasks::undo);
    }
}
