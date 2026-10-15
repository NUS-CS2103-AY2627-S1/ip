package lily.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}, the collection every command in the app ultimately
 * reads from
 * or writes to.
 *
 * <p>
 * Individually, most of its methods are thin one-line delegations to {@link
 * ArrayList}, which would ordinarily make them low-value to test. What makes
 * this class
 * worth covering thoroughly is that it is the single shared piece of state the
 * whole
 * application depends on, and it has two behaviours easy to silently get wrong
 * in a
 * future refactor: the defensive copying in its constructor and {@link
 * TaskList#toList()} (so a caller can never mutate Lily's live task list
 * through a
 * side door), and the boundary logic in {@link TaskList#containsIndex(int)}
 * that every
 * "mark"/"unmark"/"delete" command relies on to safely reject an out-of-range
 * index.
 */
public class TaskListTest {

    @Test
    public void constructor_noArguments_startsEmpty() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_givenList_copiesSuppliedTasks() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("a"));
        TaskList tasks = new TaskList(source);
        assertEquals(1, tasks.size());
    }

    @Test
    public void constructor_givenList_makesDefensiveCopy() {
        // Mutating the source list after construction must not affect the TaskList,
        // otherwise a caller could bypass TaskList entirely and corrupt Lily's task
        // list through a reference it should not have any lasting influence over.
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("a"));
        TaskList tasks = new TaskList(source);

        source.add(new ToDo("b - added after construction"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void add_singleTask_sizeIncreasesAndTaskIsRetrievable() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");
        tasks.add(task);

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    public void add_multipleTasks_appendedInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));

        assertEquals("T | 0 | first", tasks.get(0).toFileString());
        assertEquals("T | 0 | second", tasks.get(1).toFileString());
    }

    @Test
    public void get_indexOutOfRange_assertionErrorThrown() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("only task"));
        assertThrows(AssertionError.class, () -> tasks.get(1));
    }

    @Test
    public void remove_existingIndex_taskRemovedAndReturned() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");
        tasks.add(task);

        Task removed = tasks.remove(0);

        assertEquals(task, removed);
        assertEquals(0, tasks.size());
    }

    @Test
    public void remove_indexOutOfRange_assertionErrorThrown() {
        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.remove(0));
    }

    @Test
    public void mark_existingIndex_taskBecomesDone() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        tasks.mark(0);

        assertTrue(tasks.get(0).toFileString().startsWith("T | 1 |"));
    }

    @Test
    public void unmark_previouslyMarkedTask_taskBecomesNotDone() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.mark(0);

        tasks.unmark(0);

        assertTrue(tasks.get(0).toFileString().startsWith("T | 0 |"));
    }

    @Test
    public void mark_indexOutOfRange_assertionErrorThrown() {
        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.mark(0));
    }

    // ----- containsIndex: boundary logic every mark/unmark/delete command relies
    // on -----

    @Test
    public void containsIndex_negativeIndex_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        assertFalse(tasks.containsIndex(-1));
    }

    @Test
    public void containsIndex_firstValidIndex_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        tasks.add(new ToDo("b"));
        assertTrue(tasks.containsIndex(0));
    }

    @Test
    public void containsIndex_lastValidIndex_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        tasks.add(new ToDo("b"));
        assertTrue(tasks.containsIndex(1));
    }

    @Test
    public void containsIndex_indexEqualToSize_returnsFalse() {
        // The classic off-by-one boundary: size() itself is one past the last valid
        // index, and must be rejected just like anything further out.
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        tasks.add(new ToDo("b"));
        assertFalse(tasks.containsIndex(2));
    }

    @Test
    public void containsIndex_wayOutOfRange_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        assertFalse(tasks.containsIndex(999));
    }

    @Test
    public void containsIndex_emptyList_everyIndexReturnsFalse() {
        TaskList tasks = new TaskList();
        assertFalse(tasks.containsIndex(0));
    }

    // ----- size -----

    @Test
    public void size_afterAddsAndRemoves_reflectsCurrentCount() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        tasks.add(new ToDo("b"));
        tasks.remove(0);
        assertEquals(1, tasks.size());
    }

    // ----- toList -----

    @Test
    public void toList_reflectsCurrentTasksInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));
        tasks.add(new ToDo("b"));

        List<Task> snapshot = tasks.toList();

        assertEquals(2, snapshot.size());
        assertEquals("T | 0 | a", snapshot.get(0).toFileString());
        assertEquals("T | 0 | b", snapshot.get(1).toFileString());
    }

    @Test
    public void toList_returnsIndependentCopy() {
        // Mutating the returned snapshot must not affect the live TaskList, otherwise
        // code that calls toList() purely to pass to Storage.save(...) could
        // accidentally corrupt Lily's actual in-memory task list.
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("a"));

        List<Task> snapshot = tasks.toList();
        snapshot.add(new ToDo("b - added to snapshot only"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void findTasks_keywordMatchesDescription_returnsMatchingTasksOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("write code"));
        tasks.add(new Deadline("return book", java.time.LocalDateTime.of(2019, 12, 2, 18, 0)));

        List<Task> matches = tasks.findTasks("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }
}
