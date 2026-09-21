package peter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TaskList}.
 *
 * <p>Most methods here delegate straight to {@link ArrayList}, so the tests
 * concentrate on the parts that are genuinely this class's own behavior: the
 * defensive copy in the constructor, the unmodifiable view returned by
 * {@code asList}, index-based insertion used to undo a failed deletion, and
 * the index bounds that protect the rest of the app.
 */
public class TaskListTest {

    // =====================================================================
    // Construction
    // =====================================================================

    @Test
    public void constructor_noArguments_emptyListCreated() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_existingTasks_tasksCopiedInOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");

        TaskList tasks = new TaskList(List.of(first, second));

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    public void constructor_sourceListModifiedLater_taskListUnaffected() {
        // The constructor copies rather than storing the caller's list, so the
        // list Storage hands over cannot later change the app's state behind
        // its back.
        ArrayList<Task> source = new ArrayList<>();
        source.add(new Todo("first"));

        TaskList tasks = new TaskList(source);
        source.add(new Todo("second"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void constructor_emptyList_emptyListCreated() {
        assertEquals(0, new TaskList(List.of()).size());
    }

    @Test
    public void constructor_taskArguments_tasksStoredInOrder() {
        // The var-args constructor also has to keep an empty call working,
        // since that is how the app starts with no saved tasks.
        Todo first = new Todo("first");
        Todo second = new Todo("second");

        TaskList tasks = new TaskList(first, second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    // =====================================================================
    // add, get, size
    // =====================================================================

    @Test
    public void add_task_taskAppendedAtEnd() {
        // Appending matters because the "added" message reports the new count
        // and the user refers to tasks by position.
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
        assertEquals(2, tasks.size());
    }

    // An out-of-range index is a bug in the calling command rather than a user
    // mistake, so TaskList now asserts against it and an AssertionError arrives
    // before the backing list can throw IndexOutOfBoundsException. These tests
    // rely on Gradle running the test JVM with assertions enabled.
    @Test
    public void get_indexOutOfBounds_assertionFailed() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));

        assertThrows(AssertionError.class, () -> tasks.get(1));
        assertThrows(AssertionError.class, () -> tasks.get(-1));
    }

    @Test
    public void get_emptyList_assertionFailed() {
        assertThrows(AssertionError.class, () -> new TaskList().get(0));
    }

    // =====================================================================
    // add(int, Task): used to undo a failed deletion
    // =====================================================================

    @Test
    public void addAtIndex_middleOfList_taskInsertedWithoutLoss() {
        // This is the rollback path: after a failed save, the deleted task
        // must return to its original position, not the end of the list.
        TaskList tasks = new TaskList(
                new Todo("first"), new Todo("second"), new Todo("third"));

        Task removed = tasks.delete(1);
        tasks.add(1, removed);

        assertEquals(3, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
        assertEquals("third", tasks.get(2).getDescription());
    }

    @Test
    public void addAtIndex_endOfList_taskAppended() {
        // Deleting the last task then rolling back inserts at size(), which
        // must be a valid position rather than out of bounds.
        TaskList tasks = new TaskList(new Todo("first"));
        tasks.add(1, new Todo("second"));

        assertEquals(2, tasks.size());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    public void addAtIndex_indexBeyondSize_assertionFailed() {
        assertThrows(AssertionError.class, () ->
                new TaskList().add(1, new Todo("first")));
    }

    // =====================================================================
    // delete(int)
    // =====================================================================

    @Test
    public void delete_validIndex_taskRemovedAndReturned() {
        // The removed task is returned so the UI can name what it deleted.
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        Task removed = tasks.delete(0);

        assertEquals("first", removed.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getDescription());
    }

    @Test
    public void delete_lastRemainingTask_listBecomesEmpty() {
        TaskList tasks = new TaskList(new Todo("only"));
        tasks.delete(0);
        assertEquals(0, tasks.size());
    }

    @Test
    public void delete_indexOutOfBounds_assertionFailed() {
        TaskList tasks = new TaskList(new Todo("only"));
        assertThrows(AssertionError.class, () -> tasks.delete(1));
    }

    // =====================================================================
    // setDone(int, boolean)
    // =====================================================================

    @Test
    public void setDone_markTask_taskBecomesDone() {
        TaskList tasks = new TaskList(new Todo("first"));
        tasks.setDone(0, true);
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void setDone_unmarkTask_taskBecomesNotDone() {
        TaskList tasks = new TaskList(new Todo("first"));
        tasks.setDone(0, true);
        tasks.setDone(0, false);
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void setDone_oneTask_otherTasksUnchanged() {
        // Guards against the status being applied to the wrong index.
        TaskList tasks = new TaskList(
                new Todo("first"), new Todo("second"), new Todo("third"));

        tasks.setDone(1, true);

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
        assertFalse(tasks.get(2).isDone());
    }

    @Test
    public void setDone_indexOutOfBounds_assertionFailed() {
        assertThrows(AssertionError.class, () -> new TaskList().setDone(0, true));
    }

    // =====================================================================
    // findMatchingIndexes()
    // =====================================================================

    @Test
    public void findMatchingIndexes_someTasksMatch_indexesInListOrder() {
        TaskList tasks = new TaskList(
                new Todo("read book"), new Todo("return pen"), new Todo("borrow book"));

        List<Integer> matchingIndexes = tasks.findMatchingIndexes(task -> task.hasKeyword("book"));

        assertEquals(List.of(0, 2), matchingIndexes);
    }

    @Test
    public void findMatchingIndexes_noTasksMatch_emptyListReturned() {
        TaskList tasks = new TaskList(new Todo("read book"));

        assertTrue(tasks.findMatchingIndexes(task -> task.hasKeyword("pen")).isEmpty());
    }

    @Test
    public void findMatchingIndexes_emptyList_emptyListReturned() {
        assertTrue(new TaskList().findMatchingIndexes(task -> true).isEmpty());
    }

    @Test
    public void findMatchingIndexes_allTasksMatch_everyIndexReturned() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        assertEquals(List.of(0, 1), tasks.findMatchingIndexes(task -> true));
    }

    @Test
    public void findMatchingIndexes_attemptToModifyResult_exceptionThrown() {
        // Stream.toList() returns an unmodifiable list, so callers cannot
        // accidentally treat the search result as a scratch list.
        List<Integer> matchingIndexes = new TaskList(new Todo("first"))
                .findMatchingIndexes(task -> true);

        assertThrows(UnsupportedOperationException.class, () -> matchingIndexes.add(1));
    }

    // =====================================================================
    // asList()
    // =====================================================================

    @Test
    public void asList_tasksPresent_sameTasksInOrder() {
        TaskList tasks = new TaskList(new Todo("first"), new Todo("second"));

        List<Task> view = tasks.asList();

        assertEquals(2, view.size());
        assertEquals("first", view.get(0).getDescription());
    }

    @Test
    public void asList_attemptToModify_exceptionThrown() {
        // The view is handed to Storage for saving. Making it unmodifiable
        // keeps the task list the single owner of its contents.
        List<Task> view = new TaskList().asList();
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("first")));
    }

    @Test
    public void asList_emptyList_emptyViewReturned() {
        assertTrue(new TaskList().asList().isEmpty());
    }
}
