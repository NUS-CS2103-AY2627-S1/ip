package piplupbot.task;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests {@link TaskList}, which owns the tasks and the one rule that goes with
 * them: the user names a task by the number {@code list} showed, counting from
 * 1, while the {@code ArrayList} underneath counts from 0.
 *
 * <p>That conversion is the reason this class is worth testing carefully. An
 * off-by-one here does not crash: it quietly marks or deletes the task next to
 * the one the user meant, which is the kind of bug a person notices only after
 * losing something. The cases below therefore lean on the boundaries -- the
 * first task, the last task, and the numbers just outside both ends -- because
 * that is exactly where a {@code -1} in the wrong place still looks right for
 * every number in the middle.</p>
 *
 * <p>The other group of cases covers the copies this class makes of the list it
 * is given and the list it hands out. Those copies are invisible in normal use
 * and cost nothing to remove, so without a test that notices, they are easy to
 * "tidy away" -- taking with them the guarantee that the only ways to change
 * the list are {@link TaskList#add} and {@link TaskList#remove}.</p>
 */
public class TaskListTest {

    /**
     * Returns a list holding the given descriptions as todos, so each test can
     * say what it is about rather than how to set itself up.
     *
     * <p>The todos go to the constructor rather than through
     * {@link TaskList#add}, so setting up a list cannot fail, and the many cases
     * that only read from one need not declare the exception {@code add} may
     * throw. The cases about adding call {@code add} themselves.</p>
     *
     * @param descriptions What each task is, in the order they are stored.
     * @return A list holding one todo per description.
     */
    private static TaskList createTodoList(String... descriptions) {
        ArrayList<Task> todos = new ArrayList<>();
        for (String description : descriptions) {
            todos.add(new Todo(description));
        }
        return new TaskList(todos);
    }

    /**
     * Returns each stored task's description, in the order they are stored, so a
     * case about ordering can say what it expects without spelling out every
     * rendered row.
     *
     * @param tasks The list to read.
     * @return The descriptions, in the order the user sees them.
     */
    private static List<String> getDescriptions(TaskList tasks) {
        return tasks.asList().stream().map(Task::getDescription).toList();
    }

    // ---------- Counting and adding ----------

    @Test
    public void size_newList_isZero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void add_severalTasks_sizeCountsThemAll() throws PiplupBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.add(new Todo("c"));
        assertEquals(3, tasks.size());
    }

    // ---------- Refusing a task the list already holds ----------

    /**
     * The guard the duplicate check exists for: the same task typed twice is
     * refused, and the list keeps only the first copy.
     */
    @Test
    public void add_sameTaskTwice_exceptionThrownAndListUnchanged() throws PiplupBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", "2019-10-15 1800"));

        assertThrows(PiplupBotException.class, () ->
                tasks.add(new Deadline("return book", "2019-10-15 1800")));
        assertEquals(1, tasks.size());
    }

    /**
     * The refusal shows the stored copy with the number {@code list} gives it.
     * The copy is the second of two, so a message that always said 1 -- or
     * that counted from 0 -- would fail.
     */
    @Test
    public void add_sameTaskTwice_messageShowsStoredTaskWithItsNumber() {
        TaskList tasks = createTodoList("write notes", "read book");

        PiplupBotException exception = assertThrows(PiplupBotException.class, () ->
                tasks.add(new Todo("read book")));
        assertArrayEquals(new String[] {
            "Pip... You already have this task in your list:",
            "  2.[T][ ] read book",
        }, exception.getMessageLines());
    }

    /**
     * Capitals are ignored when comparing descriptions. Comparing them exactly
     * is the more obvious code, so this case is what keeps "Read Book" from
     * slipping in beside "read book".
     */
    @Test
    public void add_sameTaskInOtherCapitals_exceptionThrown() {
        TaskList tasks = createTodoList("read book");
        assertThrows(PiplupBotException.class, () -> tasks.add(new Todo("Read Book")));
    }

    /**
     * The done status is ignored too: finishing a task does not make a second
     * copy of it welcome. Comparing the saved fields whole would include the
     * done flag, and this case would then fail.
     */
    @Test
    public void add_sameTaskAsADoneOne_exceptionThrown() throws PiplupBotException {
        TaskList tasks = createTodoList("read book");
        tasks.get(1).markAsDone();
        assertThrows(PiplupBotException.class, () -> tasks.add(new Todo("read book")));
    }

    /**
     * Only the same details make a duplicate, so a description may be reused
     * with any other kind of task or any other time. The last event starts with
     * the first one and ends later: comparing only the dates a task is sorted by
     * would wrongly refuse it.
     */
    @Test
    public void add_sameDescriptionWithOtherDetails_accepted() throws PiplupBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("team meeting"));
        tasks.add(new Deadline("team meeting", "2019-10-02 1400"));
        tasks.add(new Event("team meeting", "2019-10-02 1400", "2019-10-02 1600"));
        tasks.add(new Event("team meeting", "2019-10-09 1400", "2019-10-09 1600"));
        tasks.add(new Event("team meeting", "2019-10-02 1400", "2019-10-02 1700"));
        assertEquals(5, tasks.size());
    }

    /**
     * The constructor takes the tasks as they are, repeats included, because
     * they come from the save file: a repeat there was typed in by hand, and
     * dropping it without a word could lose the one copy that was done.
     */
    @Test
    public void constructor_givenRepeatedTasks_keepsThemAll() {
        assertEquals(2, createTodoList("read book", "read book").size());
    }

    // ---------- Reading a task by the number the user sees ----------

    /** Task 1 is the first one added, not the second. */
    @Test
    public void get_firstTaskNumber_returnsFirstTaskAdded() throws PiplupBotException {
        TaskList tasks = createTodoList("first", "second", "third");
        assertEquals("[T][ ] first", tasks.get(1).toString());
    }

    /** The last valid number is the size itself, not one less. */
    @Test
    public void get_lastTaskNumber_returnsLastTaskAdded() throws PiplupBotException {
        TaskList tasks = createTodoList("first", "second", "third");
        assertEquals("[T][ ] third", tasks.get(3).toString());
    }

    /**
     * The task handed back is the stored object rather than a copy of it, which
     * is what lets {@code mark} change the list by changing what it was given.
     */
    @Test
    public void get_taskNumber_returnsStoredTaskItself() throws PiplupBotException {
        Todo stored = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(stored);
        assertSame(stored, tasks.get(1));
    }

    /** 0 is the index the list uses internally, and is never a task number. */
    @Test
    public void get_zero_exceptionThrown() {
        TaskList tasks = createTodoList("only task");
        assertThrows(PiplupBotException.class, () -> tasks.get(0));
    }

    @Test
    public void get_numberJustPastTheEnd_exceptionThrown() {
        TaskList tasks = createTodoList("first", "second");
        assertThrows(PiplupBotException.class, () -> tasks.get(3));
    }

    @Test
    public void get_negativeNumber_exceptionThrown() {
        TaskList tasks = createTodoList("only task");
        assertThrows(PiplupBotException.class, () -> tasks.get(-1));
    }

    /** Every number is out of range when there are no tasks at all. */
    @Test
    public void get_emptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(PiplupBotException.class, () -> tasks.get(1));
    }

    /**
     * The refusal names the number the user typed, so they can see which one the
     * bot rejected rather than guessing.
     */
    @Test
    public void get_numberNamingNoTask_messageNamesThatNumber() {
        TaskList tasks = createTodoList("only task");
        PiplupBotException exception = assertThrows(PiplupBotException.class, () -> tasks.get(5));
        assertArrayEquals(new String[] {"Pip... There is no task numbered 5."},
                exception.getMessageLines());
    }

    // ---------- Removing a task ----------

    @Test
    public void remove_taskNumber_returnsRemovedTask() throws PiplupBotException {
        TaskList tasks = createTodoList("first", "second", "third");
        assertEquals("[T][ ] second", tasks.remove(2).toString());
    }

    /**
     * The tasks after the removed one move up, so the numbering has no gap in
     * it. This is what makes a second {@code delete 2} remove what used to be
     * task 3, which is what the user sees on screen.
     */
    @Test
    public void remove_middleTask_renumbersTheTasksAfterIt() throws PiplupBotException {
        TaskList tasks = createTodoList("first", "second", "third");
        tasks.remove(2);

        assertEquals(2, tasks.size());
        assertArrayEquals(new String[] {"1.[T][ ] first", "2.[T][ ] third"},
                tasks.toNumberedLines());
    }

    @Test
    public void remove_numberNamingNoTask_exceptionThrown() {
        TaskList tasks = createTodoList("first", "second");
        assertThrows(PiplupBotException.class, () -> tasks.remove(0));
        assertThrows(PiplupBotException.class, () -> tasks.remove(3));
    }

    /**
     * A refused removal must leave the list exactly as it was. Checking the
     * whole list, not just its size, is what would catch a version that removed
     * the wrong task before noticing the number was out of range.
     */
    @Test
    public void remove_numberNamingNoTask_leavesListUnchanged() {
        TaskList tasks = createTodoList("first", "second");
        assertThrows(PiplupBotException.class, () -> tasks.remove(3));

        assertArrayEquals(new String[] {"1.[T][ ] first", "2.[T][ ] second"},
                tasks.toNumberedLines());
    }

    // ---------- The numbered lines the user is shown ----------

    /**
     * An empty list produces no lines at all rather than a line saying so:
     * what to print when there is nothing is the caller's decision, not this
     * class's.
     */
    @Test
    public void toNumberedLines_emptyList_returnsNoLines() {
        assertArrayEquals(new String[0], new TaskList().toNumberedLines());
    }

    /** Numbering starts at 1 and matches the numbers {@code get} accepts. */
    @Test
    public void toNumberedLines_severalTasks_numbersFromOne() {
        assertArrayEquals(
                new String[] {"1.[T][ ] first", "2.[T][ ] second", "3.[T][ ] third"},
                createTodoList("first", "second", "third").toNumberedLines());
    }

    /** Each task prints itself, so the three kinds appear in their own shapes. */
    @Test
    public void toNumberedLines_differentKindsOfTask_showsEachInItsOwnForm()
            throws PiplupBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2019-10-15 1800"));
        tasks.add(new Event("project meeting", "2019-10-02 1400", "2019-10-02 1600"));

        assertArrayEquals(new String[] {
            "1.[T][ ] read book",
            "2.[D][ ] return book (by: Oct 15 2019 06:00 PM)",
            "3.[E][ ] project meeting (from: Oct 2 2019 02:00 PM to: Oct 2 2019 04:00 PM)",
        }, tasks.toNumberedLines());
    }

    // ---------- Searching for a keyword ----------

    /**
     * Only the matching tasks come back, and they are numbered from 1 among
     * themselves: "return book" was task 3 and is shown as 2. That renumbering
     * is the part worth pinning down, because it is what makes a number read off
     * a search result mean something different from the same number typed at
     * {@code delete}.
     */
    @Test
    public void find_keywordInSomeDescriptions_returnsOnlyThoseNumberedFromOne() {
        TaskList tasks = createTodoList("read book", "write notes", "return book");
        assertArrayEquals(new String[] {"1.[T][ ] read book", "2.[T][ ] return book"},
                tasks.find("book").toNumberedLines());
    }

    @Test
    public void find_keywordMatchingNothing_returnsEmptyList() {
        assertEquals(0, createTodoList("read book", "write notes").find("homework").size());
    }

    /** The matches keep the order they were added in. */
    @Test
    public void find_keywordMatchingEveryTask_returnsThemInTheStoredOrder() {
        TaskList tasks = createTodoList("book one", "book two", "book three");
        assertArrayEquals(
                new String[] {"1.[T][ ] book one", "2.[T][ ] book two", "3.[T][ ] book three"},
                tasks.find("book").toNumberedLines());
    }

    /**
     * The keyword reaches each task as typed, so a search through the list is as
     * forgiving about capitals as {@link Task#descriptionContains} is on its
     * own. Lower-casing the keyword here as well would be harmless; lower-casing
     * it instead of there would not, which is why this is checked from both
     * ends.
     */
    @Test
    public void find_keywordInADifferentCase_stillMatches() {
        assertArrayEquals(new String[] {"1.[T][ ] Read Book"},
                createTodoList("Read Book", "write notes").find("bOOk").toNumberedLines());
    }

    /** Searching reads the list; it must not change what is stored. */
    @Test
    public void find_anyKeyword_leavesTheStoredTasksUnchanged() {
        TaskList tasks = createTodoList("read book", "write notes");
        tasks.find("book");
        tasks.find("nothing matches this");

        assertArrayEquals(new String[] {"1.[T][ ] read book", "2.[T][ ] write notes"},
                tasks.toNumberedLines());
    }

    /**
     * A match is the stored task itself rather than a copy of it, so a search
     * shows the task's real done status rather than a snapshot of it.
     */
    @Test
    public void find_matchingTask_isTheStoredTaskItself() throws PiplupBotException {
        Todo stored = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(stored);

        assertSame(stored, tasks.find("book").get(1));
    }

    /**
     * The result is a list of its own, so changing it changes nothing stored.
     * Without that, handing out a search result would be a second way into the
     * list, past {@link TaskList#add} and {@link TaskList#remove}.
     */
    @Test
    public void find_returnedListChanged_storedTasksUnchanged() throws PiplupBotException {
        TaskList tasks = createTodoList("read book");

        TaskList matches = tasks.find("book");
        matches.add(new Todo("added to the search result"));

        assertEquals(1, tasks.size());
        assertArrayEquals(new String[] {"1.[T][ ] read book"}, tasks.toNumberedLines());
    }

    // ---------- Sorting ----------

    /**
     * Sorting rearranges the tasks this list holds rather than handing back a
     * sorted copy, so the list itself comes back in the new order.
     */
    @Test
    public void sort_byDate_reordersTheStoredTasks() throws PiplupBotException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2019-10-15 1800"));
        tasks.add(new Event("meeting", "2019-10-02 1400", "2019-10-02 1600"));

        tasks.sort(SortKey.DATE, SortDirection.ASC);

        assertEquals(List.of("meeting", "return book", "read book"), getDescriptions(tasks));
    }

    /**
     * The point of sorting in place: the numbers shown afterwards are the ones
     * {@link TaskList#get} and {@link TaskList#remove} take, so a user who sorts
     * and then types {@code mark 1} marks the first row they were just shown.
     * Sorting a copy would leave the two disagreeing, in the way a search result
     * already does.
     */
    @Test
    public void sort_thenGet_findsTheTaskShownAtThatNumber() throws PiplupBotException {
        TaskList tasks = createTodoList("c", "a", "b");

        tasks.sort(SortKey.NAME, SortDirection.ASC);

        assertEquals("a", tasks.get(1).getDescription());
        assertEquals("c", tasks.get(3).getDescription());
    }

    /** Sorting moves tasks about; it must not lose or duplicate one. */
    @Test
    public void sort_anyKey_keepsEveryTaskExactlyOnce() {
        TaskList tasks = createTodoList("c", "a", "b");

        tasks.sort(SortKey.NAME, SortDirection.DESC);

        assertEquals(3, tasks.size());
        assertEquals(List.of("c", "b", "a"), getDescriptions(tasks));
    }

    @Test
    public void sort_emptyList_leavesItEmpty() {
        TaskList tasks = new TaskList();

        tasks.sort(SortKey.DATE, SortDirection.ASC);

        assertEquals(0, tasks.size());
        assertArrayEquals(new String[] {}, tasks.toNumberedLines());
    }

    /**
     * One sort refines another rather than undoing it: sorting by done status
     * after sorting by date leaves each group still in date order. That follows
     * from {@code List.sort} being stable, which is why {@link SortKey} needs no
     * tie-breaking rule of its own -- and it is exactly what would be lost if
     * one were added.
     */
    @Test
    public void sort_twice_secondSortKeepsTheFirstOrderWithinEachGroup()
            throws PiplupBotException {
        // The descriptions deliberately run the opposite way to the dates, so a
        // tie broken by description would give a different answer from a tie
        // left alone -- without that, this case would pass either way.
        Deadline alpha = new Deadline("alpha", "2019-10-15 1800");
        Deadline pending = new Deadline("pending", "2019-10-02 1400");
        Deadline zulu = new Deadline("zulu", "2019-10-01 0900");
        alpha.markAsDone();
        zulu.markAsDone();

        TaskList tasks = new TaskList();
        tasks.add(alpha);
        tasks.add(pending);
        tasks.add(zulu);

        tasks.sort(SortKey.DATE, SortDirection.ASC);
        tasks.sort(SortKey.STATUS, SortDirection.ASC);

        // The unfinished task first, then the finished two still in date order.
        assertEquals(List.of("pending", "zulu", "alpha"), getDescriptions(tasks));
    }

    // ---------- The copies that keep the list this class's own ----------

    /**
     * Adding to the list {@link TaskList#asList} handed back must not add to the
     * stored list. Were the stored list returned directly, any caller could add
     * or remove tasks without going through the numbering rule above.
     */
    @Test
    public void asList_returnedListChanged_storedTasksUnchanged() {
        TaskList tasks = createTodoList("first");

        ArrayList<Task> copiedTasks = tasks.asList();
        copiedTasks.add(new Todo("added behind the list's back"));
        copiedTasks.remove(0);

        assertEquals(1, tasks.size());
        assertArrayEquals(new String[] {"1.[T][ ] first"}, tasks.toNumberedLines());
    }

    /**
     * The same in the other direction: the list handed to the constructor is
     * copied, so whoever still holds it -- {@link piplupbot.Storage} after a
     * load, for instance -- cannot change the task list by changing theirs.
     */
    @Test
    public void constructor_givenListChangedAfterwards_storedTasksUnchanged() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("first"));

        TaskList tasks = new TaskList(initialTasks);
        initialTasks.add(new Todo("added after construction"));

        assertEquals(1, tasks.size());
        assertArrayEquals(new String[] {"1.[T][ ] first"}, tasks.toNumberedLines());
    }
}
