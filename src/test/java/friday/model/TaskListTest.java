package friday.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the public operations and boundary behavior of {@link TaskList}.
 */
class TaskListTest {
    /**
     * Verifies that the no-argument constructor starts with no tasks.
     */
    @Test
    void defaultConstructor_createsEmptyTaskList() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertEquals(List.of(), tasks.asList());
    }

    /**
     * Verifies that constructing from a list copies its contents.
     */
    @Test
    void listConstructor_copiesSuppliedTasks() {
        Task first = new ToDo("first");
        List<Task> source = new ArrayList<>(List.of(first));
        TaskList tasks = new TaskList(source);

        source.add(new ToDo("outside task"));

        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(0));
    }

    /**
     * Verifies that the varargs constructor preserves argument order.
     */
    @Test
    void varargsConstructor_preservesArgumentOrder() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");

        TaskList tasks = new TaskList(first, second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    /**
     * Verifies that added tasks retain insertion order and update the size.
     */
    @Test
    void add_tasksAppendedInInsertionOrder_andSizeUpdated() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    /**
     * Verifies that a valid index returns the corresponding task.
     */
    @Test
    void get_validIndex_returnsTaskAtThatIndex() {
        Task expected = new ToDo("expected");
        TaskList tasks = new TaskList(List.of(new ToDo("first"), expected));

        assertSame(expected, tasks.get(1));
    }

    /**
     * Verifies that a negative lookup index is rejected.
     */
    @Test
    void get_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
    }

    /**
     * Verifies that looking up the size index is rejected.
     */
    @Test
    void getIndexAtSize_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(tasks.size()));
    }

    /**
     * Verifies removal returns the task and closes the resulting gap.
     */
    @Test
    void remove_validIndex_returnsRemovedTaskAndPreservesOrder() {
        Task first = new ToDo("first");
        Task removed = new ToDo("removed");
        Task last = new ToDo("last");
        TaskList tasks = new TaskList(List.of(first, removed, last));

        Task result = tasks.remove(1);

        assertSame(removed, result);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(last, tasks.get(1));
    }

    /**
     * Verifies that a negative removal index is rejected without mutation.
     */
    @Test
    void remove_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(-1));
        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that removing at the size index is rejected without mutation.
     */
    @Test
    void removeIndexAtSize_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(tasks.size()));
        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that removing from an empty list is rejected.
     */
    @Test
    void remove_emptyList_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(0));
        assertEquals(0, tasks.size());
    }

    /**
     * Verifies that size reflects both additions and removals.
     */
    @Test
    void size_afterAddAndRemove_reportsCurrentNumberOfTasks() {
        TaskList tasks = new TaskList();

        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));
        tasks.remove(0);

        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that the exposed list is read-only but remains a live view.
     */
    @Test
    void asList_returnsReadOnlyView_thatReflectsTaskListChanges() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList(List.of(first));
        List<Task> view = tasks.asList();

        tasks.add(second);

        assertEquals(List.of(first, second), view);
        assertThrows(UnsupportedOperationException.class, () -> view.add(new ToDo("blocked")));
    }

    /**
     * Verifies that find is case-insensitive and preserves task order.
     */
    @Test
    void find_keywordMatchesDescriptionIgnoringCase_andPreservesOrder() {
        Task first = new ToDo("Read a book");
        Task second = new ToDo("Return the book");
        TaskList tasks = new TaskList(List.of(first, new ToDo("Write notes"), second));

        List<Task> matches = tasks.find("BOOK");

        assertEquals(List.of(first, second), matches);
    }

    /**
     * Verifies that find returns no tasks when the keyword is absent.
     */
    @Test
    void find_unknownKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new ToDo("Read a book")));

        assertEquals(List.of(), tasks.find("meeting"));
    }
}
