package sel.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void noArgConstructor_startsEmpty() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
    }

    @Test
    public void listConstructor_wrapsGivenList() {
        List<Task> initial = new ArrayList<>();
        initial.add(new ToDo("existing task"));
        TaskList tasks = new TaskList(initial);
        assertEquals(1, tasks.size());
        assertEquals("existing task", tasks.get(0).getDescription());
    }

    @Test
    public void add_increasesSizeAndStoresTaskAtEnd() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        tasks.add(new ToDo("task 2"));
        assertEquals(2, tasks.size());
        assertEquals("task 2", tasks.get(1).getDescription());
    }

    @Test
    public void get_returnsTaskAtGivenIndex() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("task 1");
        tasks.add(task);
        assertSame(task, tasks.get(0));
    }

    @Test
    public void get_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertThrows(AssertionError.class, () -> tasks.get(5));
    }

    @Test
    public void delete_removesTaskAndReturnsIt() {
        TaskList tasks = new TaskList();
        Task task1 = new ToDo("task 1");
        Task task2 = new ToDo("task 2");
        tasks.add(task1);
        tasks.add(task2);

        Task deleted = tasks.delete(0);

        assertSame(task1, deleted);
        assertEquals(1, tasks.size());
        assertSame(task2, tasks.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.delete(0));
    }

    @Test
    public void isValidIndex_withinBounds_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertTrue(tasks.isValidIndex(0));
    }

    @Test
    public void isValidIndex_negative_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertFalse(tasks.isValidIndex(-1));
    }

    @Test
    public void isValidIndex_equalToSize_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        assertFalse(tasks.isValidIndex(1));
    }

    @Test
    public void isValidIndex_emptyList_returnsFalseForZero() {
        TaskList tasks = new TaskList();
        assertFalse(tasks.isValidIndex(0));
    }

    @Test
    public void mark_marksTaskAtGivenIndex() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));
        tasks.add(new ToDo("task 2"));

        tasks.mark(1);

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    public void unmark_unmarksTaskAtGivenIndex() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("task 1");
        task.mark();
        tasks.add(task);

        tasks.unmark(0);

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void asList_reflectsLiveState() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task 1"));

        List<Task> snapshot = tasks.asList();
        assertEquals(1, snapshot.size());

        tasks.add(new ToDo("task 2"));
        assertEquals(2, snapshot.size(), "asList() should expose the live underlying list");
    }

    @Test
    public void findDuplicateOf_matchingTask_returnsTheExistingOne() {
        TaskList tasks = new TaskList();
        ToDo existing = new ToDo("read book");
        tasks.add(existing);

        assertSame(existing, tasks.findDuplicateOf(new ToDo("read book")).orElseThrow());
        assertSame(existing, tasks.findDuplicateOf(new ToDo("Read Book")).orElseThrow());
    }

    @Test
    public void findDuplicateOf_noMatch_returnsEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertTrue(tasks.findDuplicateOf(new ToDo("read magazine")).isEmpty());
    }

    @Test
    public void findDuplicateOf_emptyList_returnsEmpty() {
        assertTrue(new TaskList().findDuplicateOf(new ToDo("read book")).isEmpty());
    }

    // ---------- searching ----------

    @Test
    public void find_returnsTasksWhoseDescriptionContainsTheKeyword() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("wash car"));
        tasks.add(new ToDo("return book"));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void find_ignoresCaseOnBothSides() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read BOOK"));

        assertEquals(1, tasks.find("book").size());
        assertEquals(1, tasks.find("BOOK").size());
        assertEquals(1, tasks.find("rEaD").size());
    }

    @Test
    public void find_matchesPartOfAWord() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertEquals(1, tasks.find("oo").size());
    }

    @Test
    public void find_severalKeywords_requiresAllOfThem() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("read magazine"));

        assertEquals(1, tasks.find("read book").size());
        assertEquals(2, tasks.find("read").size());
        // The keywords need not be adjacent or in order.
        assertEquals(1, tasks.find("book read").size());
        assertEquals(0, tasks.find("read newspaper").size());
    }

    @Test
    public void find_keywordsSeparatedByExtraSpaces_stillMatch() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertEquals(1, tasks.find("  read    book  ").size());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertTrue(tasks.find("bicycle").isEmpty());
    }

    @Test
    public void find_emptyList_returnsEmptyList() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    @Test
    public void find_keepsTheOriginalListOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("book three"));
        tasks.add(new ToDo("book one"));
        tasks.add(new ToDo("book two"));

        List<Task> matches = tasks.find("book");

        assertEquals("book three", matches.get(0).getDescription());
        assertEquals("book one", matches.get(1).getDescription());
        assertEquals("book two", matches.get(2).getDescription());
    }

    @Test
    public void find_blankKeyword_matchesEverything() {
        // Documents current behaviour: every description contains the empty
        // string. Sel rejects a blank keyword before it ever gets here, so
        // this only matters to direct callers.
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("wash car"));

        assertEquals(2, tasks.find("").size());
    }

    // ---------- guarding against bad indices ----------

    @Test
    public void mark_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertThrows(AssertionError.class, () -> tasks.mark(1));
        assertThrows(AssertionError.class, () -> tasks.mark(-1));
    }

    @Test
    public void unmark_invalidIndex_throwsAssertionError() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertThrows(AssertionError.class, () -> tasks.unmark(1));
        assertThrows(AssertionError.class, () -> tasks.unmark(-1));
    }
}
