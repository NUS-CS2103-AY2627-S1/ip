package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lebron.exception.LebronException;

/**
 * Tests for {@link TaskList}: the add/delete/mark/unmark/get operations and,
 * in particular, the 1-based range checking that must throw
 * {@link LebronException} rather than let an invalid index reach the
 * underlying list.
 */
public class TaskListTest {
    private TaskList tasks;
    private Task first;
    private Task second;
    private Task third;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        first = new Todo("read book");
        second = new Todo("return book");
        third = new Todo("buy bread");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);
    }

    @Test
    public void add_increasesSize() {
        assertEquals(3, tasks.size());
        tasks.add(new Todo("extra"));
        assertEquals(4, tasks.size());
    }

    @Test
    public void newTaskList_isEmpty() {
        assertEquals(0, new TaskList().size());
    }

    // ---- get -------------------------------------------------------------

    @Test
    public void get_validOneBasedIndex_returnsThatTask() throws LebronException {
        assertSame(first, tasks.get(1));
        assertSame(third, tasks.get(3));
    }

    @Test
    public void get_zeroIndex_throws() {
        assertThrows(LebronException.class, () -> tasks.get(0));
    }

    @Test
    public void get_negativeIndex_throws() {
        assertThrows(LebronException.class, () -> tasks.get(-1));
    }

    @Test
    public void get_indexPastEnd_throws() {
        assertThrows(LebronException.class, () -> tasks.get(4));
    }

    @Test
    public void get_onEmptyList_throws() {
        assertThrows(LebronException.class, () -> new TaskList().get(1));
    }

    // ---- mark / unmark --------------------------------------------------

    @Test
    public void mark_validIndex_marksAndReturnsTask() throws LebronException {
        Task returned = tasks.mark(2);
        assertSame(second, returned);
        assertEquals("X", second.getStatusIcon());
    }

    @Test
    public void unmark_previouslyMarked_clearsDoneStatus() throws LebronException {
        tasks.mark(2);
        Task returned = tasks.unmark(2);
        assertSame(second, returned);
        assertEquals(" ", second.getStatusIcon());
    }

    @Test
    public void mark_outOfRange_throwsAndLeavesListUnchanged() {
        assertThrows(LebronException.class, () -> tasks.mark(99));
        assertEquals(" ", first.getStatusIcon());
        assertEquals(3, tasks.size());
    }

    // ---- delete -------------------------------------------------------

    @Test
    public void delete_middleIndex_removesReturnsAndRenumbers() throws LebronException {
        Task removed = tasks.delete(2);
        assertSame(second, removed);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(1));
        assertSame(third, tasks.get(2));
    }

    @Test
    public void delete_outOfRange_throws() {
        assertThrows(LebronException.class, () -> tasks.delete(0));
        assertThrows(LebronException.class, () -> tasks.delete(4));
    }

    // ---- asList -----------------------------------------------------

    @Test
    public void asList_reflectsContentsInOrder() {
        assertEquals(3, tasks.asList().size());
        assertSame(first, tasks.asList().get(0));
        assertSame(third, tasks.asList().get(2));
    }

    @Test
    public void asList_isUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.asList().add(new Todo("nope")));
    }

    @Test
    public void asList_staysInSyncAfterDelete() throws LebronException {
        tasks.delete(1);
        assertEquals(2, tasks.asList().size());
        assertTrue(tasks.asList().contains(second));
    }

    // ---- find ---------------------------------------------------------

    @Test
    public void find_keywordInSomeDescriptions_returnsThoseInOrder() {
        // setUp() adds: "read book", "return book", "buy bread"
        List<Task> matches = tasks.find("book");
        assertEquals(2, matches.size());
        assertSame(first, matches.get(0));
        assertSame(second, matches.get(1));
    }

    @Test
    public void find_noDescriptionContainsKeyword_returnsEmpty() {
        assertTrue(tasks.find("plane").isEmpty());
    }

    @Test
    public void find_isCaseSensitive() {
        assertTrue(tasks.find("Book").isEmpty());
    }

    @Test
    public void find_onEmptyList_returnsEmpty() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    // ---- hasDuplicateDetails (A-MoreErrorHandling) ------------------------

    @Test
    public void hasDuplicateDetails_matchingTodo_returnsTrue() {
        // setUp() already added a Todo("read book").
        assertTrue(tasks.hasDuplicateDetails(new Todo("read book")));
    }

    @Test
    public void hasDuplicateDetails_differentDescription_returnsFalse() {
        assertFalse(tasks.hasDuplicateDetails(new Todo("read newspaper")));
    }

    @Test
    public void hasDuplicateDetails_sameDescriptionDifferentType_returnsFalse() {
        // A deadline and a todo aren't "the same task" just for sharing text.
        assertFalse(tasks.hasDuplicateDetails(new Deadline("read book", DateTime.parse("2025-01-01"))));
    }

    @Test
    public void hasDuplicateDetails_ignoresDoneStatus() throws LebronException {
        tasks.mark(1); // marks "read book" done
        assertTrue(tasks.hasDuplicateDetails(new Todo("read book")));
    }

    @Test
    public void hasDuplicateDetails_matchingDeadline_returnsTrue() {
        tasks.add(new Deadline("pay rent", DateTime.parse("2025-01-01")));
        assertTrue(tasks.hasDuplicateDetails(new Deadline("pay rent", DateTime.parse("2025-01-01"))));
    }

    @Test
    public void hasDuplicateDetails_sameDescriptionDifferentDate_returnsFalse() {
        tasks.add(new Deadline("pay rent", DateTime.parse("2025-01-01")));
        assertFalse(tasks.hasDuplicateDetails(new Deadline("pay rent", DateTime.parse("2025-02-01"))));
    }
}
