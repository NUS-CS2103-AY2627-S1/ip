package goat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import goat.GoatException;

/**
 * Tests the task list's own rules: the 1-based numbering the user sees, the
 * range check that goes with it, and the promise that the wrapped list cannot
 * be reached from outside.
 *
 * These are the rules that used to be copied into each command handler, so
 * they are exactly the ones worth pinning down.
 */
public class TaskListTest {

    /** Builds a list of todos described "a", "b", "c", ... for readability. */
    private static TaskList listOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new Todo(description));
        }
        return tasks;
    }

    @Test
    public void newTaskList_noArguments_isEmpty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void newTaskList_givenTasks_holdsThem() throws GoatException {
        ArrayList<Task> given = new ArrayList<>();
        given.add(new Todo("read book"));
        TaskList tasks = new TaskList(given);

        assertEquals(1, tasks.size());
        assertFalse(tasks.isEmpty());
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void newTaskList_callerChangesTheirListAfterwards_taskListUnaffected() {
        // The constructor copies, so whoever supplied the list cannot go on
        // changing the contents behind the TaskList's back.
        ArrayList<Task> given = new ArrayList<>();
        given.add(new Todo("read book"));
        TaskList tasks = new TaskList(given);

        given.add(new Todo("sneaked in"));
        given.clear();

        assertEquals(1, tasks.size());
    }

    @Test
    public void add_severalTasks_keepsInsertionOrder() throws GoatException {
        TaskList tasks = listOf("a", "b", "c");

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] a", tasks.get(1).toString());
        assertEquals("[T][ ] b", tasks.get(2).toString());
        assertEquals("[T][ ] c", tasks.get(3).toString());
    }

    @Test
    public void get_firstTask_countsFromOne() throws GoatException {
        Todo first = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(first);

        // The user's "1" is the list's index 0, and get() is the only place
        // that knows it.
        assertSame(first, tasks.get(1));
    }

    @Test
    public void get_zero_exceptionThrown() {
        TaskList tasks = listOf("a");
        assertThrows(GoatException.class, () -> tasks.get(0));
    }

    @Test
    public void get_negative_exceptionThrown() {
        TaskList tasks = listOf("a");
        assertThrows(GoatException.class, () -> tasks.get(-1));
    }

    @Test
    public void get_oneBeyondTheEnd_exceptionThrown() {
        TaskList tasks = listOf("a", "b");
        assertThrows(GoatException.class, () -> tasks.get(3));
    }

    @Test
    public void get_onEmptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(GoatException.class, () -> tasks.get(1));
    }

    @Test
    public void get_outOfRange_messageQuotesTheNumber() {
        TaskList tasks = listOf("a");
        GoatException e = assertThrows(GoatException.class, () -> tasks.get(9));
        assertTrue(e.getMessage().contains("There is no task '9'"), e.getMessage());
    }

    @Test
    public void get_doesNotRemove_sizeUnchanged() throws GoatException {
        TaskList tasks = listOf("a", "b");
        tasks.get(1);
        assertEquals(2, tasks.size());
    }

    @Test
    public void delete_middleTask_returnsItAndClosesTheGap() throws GoatException {
        TaskList tasks = listOf("a", "b", "c");

        Task removed = tasks.delete(2);

        assertEquals("[T][ ] b", removed.toString());
        assertEquals(2, tasks.size());
        // What followed shifts down, so the numbers stay contiguous.
        assertEquals("[T][ ] a", tasks.get(1).toString());
        assertEquals("[T][ ] c", tasks.get(2).toString());
    }

    @Test
    public void delete_lastRemainingTask_listBecomesEmpty() throws GoatException {
        TaskList tasks = listOf("a");
        tasks.delete(1);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void delete_outOfRange_exceptionThrownAndNothingRemoved() {
        TaskList tasks = listOf("a", "b");
        assertThrows(GoatException.class, () -> tasks.delete(3));
        assertEquals(2, tasks.size());
    }

    @Test
    public void delete_zero_exceptionThrown() {
        TaskList tasks = listOf("a");
        assertThrows(GoatException.class, () -> tasks.delete(0));
    }

    @Test
    public void delete_onEmptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(GoatException.class, () -> tasks.delete(1));
    }

    @Test
    public void get_afterDelete_numberNowPastTheEndIsRejected() throws GoatException {
        TaskList tasks = listOf("a", "b");
        tasks.delete(2);
        // 2 was valid a moment ago; the range check has to follow the size.
        assertThrows(GoatException.class, () -> tasks.get(2));
    }

    @Test
    public void find_keywordInSomeDescriptions_returnsOnlyThose() {
        TaskList tasks = listOf("read book", "return book", "join sports club");

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
    }

    @Test
    public void find_keywordInNoDescription_returnsEmptyList() {
        assertTrue(listOf("a", "b").find("pineapple").isEmpty());
    }

    @Test
    public void find_onEmptyList_returnsEmptyList() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    @Test
    public void find_differentCase_matches() {
        assertEquals(1, listOf("Read Book").find("read book").size());
        assertEquals(1, listOf("read book").find("READ BOOK").size());
    }

    @Test
    public void find_substringOfAWord_matches() {
        assertEquals(1, listOf("join sports club").find("port").size());
    }

    @Test
    public void find_emptyKeyword_matchesEverything() {
        // Parser rejects an empty keyword before it reaches here, so this only
        // pins down what the list itself does with one.
        assertEquals(2, listOf("a", "b").find("").size());
    }

    @Test
    public void find_always_leavesTheSearchedListAlone() throws GoatException {
        TaskList tasks = listOf("read book", "return book");

        TaskList matches = tasks.find("book");
        matches.delete(1);

        // The results are a separate list, so deleting from it changes nothing.
        assertEquals(2, tasks.size());
    }

    @Test
    public void find_returnsTheLiveTasks_notCopies() throws GoatException {
        TaskList tasks = listOf("read book");

        tasks.find("book").get(1).markAsDone();

        assertEquals("[T][X] read book", tasks.get(1).toString());
    }

    @Test
    public void get_returnsTheLiveTask_changesAreVisibleInTheList() throws GoatException {
        TaskList tasks = listOf("a");

        tasks.get(1).markAsDone();

        // get() hands back the task itself, not a copy, which is what lets
        // MarkCommand work.
        assertEquals("[T][X] a", tasks.get(1).toString());
    }
}
