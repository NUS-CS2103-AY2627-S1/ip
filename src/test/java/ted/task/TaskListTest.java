package ted.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import ted.TedException;

/**
 * Tests {@link TaskList}, which owns the tasks and the rule that a task number
 * must point at a real one.
 * <p>
 * The bounds checking is the part worth testing: it is what stands between a
 * mistyped task number and a crash.
 */
public class TaskListTest {
    /**
     * Verifies that a new task list is empty and has size zero.
     */
    @Test
    public void size_newList_isEmpty() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
        assertTrue(tasks.isEmpty());
    }

    /**
     * Verifies that adding tasks preserves their insertion order and updates the list size.
     */
    @Test
    public void add_severalTasks_keepsInsertionOrder() throws TedException {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertFalse(tasks.isEmpty());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    /**
     * Verifies that a valid index retrieves the corresponding task.
     */
    @Test
    public void get_indexWithinList_returnsTask() throws TedException {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        assertEquals("only task", tasks.get(0).toString().substring("[T][ ] ".length()));
    }

    /**
     * Verifies that an index past the end is rejected with task numbers the user sees.
     */
    @Test
    public void get_indexPastEnd_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        TedException e = assertThrows(TedException.class, () -> tasks.get(1));
        // The message speaks in the 1-based numbers the user sees.
        assertTrue(e.getMessage().contains("There's no task 2"));
        assertTrue(e.getMessage().contains("the only number that works is 1"));
    }

    /**
     * Verifies that an index past the end of a longer list names the range of
     * numbers that do work.
     */
    @Test
    public void get_indexPastEndOfSeveral_rangeGiven() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second"), new Todo("third")));
        TedException e = assertThrows(TedException.class, () -> tasks.get(8));
        assertTrue(e.getMessage().contains("There's no task 9"));
        assertTrue(e.getMessage().contains("pick a number from 1 to 3"));
    }

    /**
     * Verifies that retrieving a task at a negative index is rejected.
     */
    @Test
    public void get_negativeIndex_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        assertThrows(TedException.class, () -> tasks.get(-1));
    }

    /**
     * Verifies that retrieving from an empty list reports that the list is empty.
     */
    @Test
    public void get_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        TedException e = assertThrows(TedException.class, () -> tasks.get(0));
        assertTrue(e.getMessage().contains("empty"));
    }

    /**
     * Verifies that removing a task returns it and shifts the remaining tasks into place.
     */
    @Test
    public void remove_indexWithinList_removesAndReturnsTask() throws TedException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        assertSame(first, tasks.remove(0));
        assertEquals(1, tasks.size());
        // The gap left behind must close, so the survivor becomes task 1.
        assertSame(second, tasks.get(0));
    }

    /**
     * Verifies that an index past the end is rejected without removing any tasks.
     */
    @Test
    public void remove_indexPastEnd_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        assertThrows(TedException.class, () -> tasks.remove(1));
        // A rejected removal must leave the list untouched.
        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that removing a task from an empty list is rejected.
     */
    @Test
    public void remove_emptyList_exceptionThrown() {
        assertThrows(TedException.class, () -> new TaskList().remove(0));
    }

    /**
     * Verifies that later changes to the source list do not affect the task list.
     */
    @Test
    public void constructor_givenList_copiesIt() throws TedException {
        ArrayList<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new Todo("first"));
        TaskList tasks = new TaskList(sourceTasks);

        // Later changes to the caller's list must not leak into the task list.
        sourceTasks.add(new Todo("second"));
        assertEquals(1, tasks.size());
    }

    /**
     * Verifies that searching returns matching tasks in order without changing the original list.
     */
    @Test
    public void find_keywordInSomeDescriptions_returnsOnlyThose() throws TedException {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"), new Todo("buy milk"), new Todo("return book")));

        TaskList matches = tasks.find("book");
        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[T][ ] return book", matches.get(1).toString());
        // Searching must not disturb the list being searched.
        assertEquals(3, tasks.size());
    }

    /**
     * Verifies that a keyword absent from every description produces no matches.
     */
    @Test
    public void find_keywordInNoDescription_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertTrue(tasks.find("bicycle").isEmpty());
    }

    /**
     * Verifies that description searches ignore differences in letter case.
     */
    @Test
    public void find_differingCase_stillMatches() {
        // A user looking for "book" should not have to remember the case used
        // when the task was added.
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));
        assertEquals(1, tasks.find("book").size());
        assertEquals(1, tasks.find("BOOK").size());
    }

    /**
     * Verifies that description searches match a keyword within a longer word.
     */
    @Test
    public void find_partOfAWord_matches() {
        TaskList tasks = new TaskList(List.of(new Todo("bookshop visit")));
        assertEquals(1, tasks.find("book").size());
    }

    /**
     * Verifies that searching an empty task list produces no matches.
     */
    @Test
    public void find_emptyList_returnsEmptyList() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    /**
     * Verifies that callers cannot add tasks to the list returned by asList.
     */
    @Test
    public void asList_returnedList_cannotBeModified() {
        TaskList tasks = new TaskList(List.of(new Todo("only task")));
        List<Task> tasksView = tasks.asList();
        assertThrows(UnsupportedOperationException.class, () -> tasksView.add(new Todo("sneaky")));
    }

    /**
     * Verifies that tag searches return exact matches and exclude similar tag names.
     */
    @Test
    public void findByTag_similarTags_returnsOnlyExactMatches() throws TedException {
        Todo fun = new Todo("read book");
        fun.addTags(List.of(new Tag("fun")));
        Todo funny = new Todo("watch film");
        funny.addTags(List.of(new Tag("funny")));
        TaskList tasks = new TaskList(List.of(fun, funny, new Todo("buy milk")));

        TaskList matches = tasks.findByTag(new Tag("fun"));
        // #funny starts with "fun" but is a different tag, so it must not match.
        assertEquals(1, matches.size());
        assertSame(fun, matches.get(0));
    }

    /**
     * Verifies that description searches do not match text found only in a tag.
     */
    @Test
    public void find_keywordOnlyInATag_noMatch() {
        // find searches descriptions, and a tag is not part of the description.
        Todo todo = new Todo("read book");
        todo.addTags(List.of(new Tag("fun")));
        assertTrue(new TaskList(List.of(todo)).find("fun").isEmpty());
    }

    /**
     * Verifies that the list reports a duplicate only when it holds one.
     */
    @Test
    public void hasDuplicateOf_matchingAndNewTasks_reportedCorrectly() {
        TaskList tasks = new TaskList(List.of(new Todo("borrow book"), new Todo("read book")));
        assertTrue(tasks.hasDuplicateOf(new Todo("Read book")));
        assertFalse(tasks.hasDuplicateOf(new Todo("return book")));
        assertFalse(new TaskList().hasDuplicateOf(new Todo("read book")));
    }

    /**
     * Verifies that searches still ignore case on a computer set to Turkish,
     * whose rules lower a capital I to a dotless i.
     */
    @Test
    public void find_turkishLocale_stillIgnoresCase() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            TaskList tasks = new TaskList(List.of(new Todo("FINISH work")));
            assertEquals(1, tasks.find("finish").size());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    /**
     * Verifies that each task gets its own number, even when two tasks look
     * the same.
     */
    @Test
    public void getNumberOf_lookalikeTasks_eachHasItsOwnNumber() {
        Todo first = new Todo("water plants");
        Todo second = new Todo("water plants");
        TaskList tasks = new TaskList(List.of(new Todo("buy milk"), first, second));
        assertEquals(2, tasks.getNumberOf(first));
        assertEquals(3, tasks.getNumberOf(second));
    }
}
