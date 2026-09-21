package goat.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import goat.GoatException;

/**
 * Tests the two sort orders and the lookup that chooses between them.
 *
 * The cases that matter are the ones where the ordering rule has to decide
 * something the user did not state: where an undated task goes, and what
 * happens when two tasks compare equal on the main key.
 */
public class SortOrderTest {

    private static final LocalDateTime EARLY = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime LATE = LocalDateTime.of(2019, 12, 2, 18, 0);

    private static TaskList listOf(Task... tasks) {
        return new TaskList(new ArrayList<>(List.of(tasks)));
    }

    private static String descriptionsOf(TaskList tasks) throws GoatException {
        StringBuilder joined = new StringBuilder();
        for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
            joined.append(taskNumber == 1 ? "" : ",").append(tasks.get(taskNumber).getDescription());
        }
        return joined.toString();
    }

    @Test
    public void fromKeyword_knownWords_returnMatchingOrders() throws GoatException {
        assertEquals(SortOrder.DATE, SortOrder.fromKeyword("date"));
        assertEquals(SortOrder.NAME, SortOrder.fromKeyword("name"));
    }

    @Test
    public void fromKeyword_theTableAboveIsComplete() {
        assertEquals(2, SortOrder.values().length);
    }

    @Test
    public void fromKeyword_unknownWord_exceptionSuggestsBothOrders() {
        GoatException e = assertThrows(GoatException.class, () -> SortOrder.fromKeyword("priority"));
        assertTrue(e.getMessage().contains("sort date"), e.getMessage());
        assertTrue(e.getMessage().contains("sort name"), e.getMessage());
    }

    @Test
    public void fromKeyword_empty_exceptionThrown() {
        assertThrows(GoatException.class, () -> SortOrder.fromKeyword(""));
    }

    @Test
    public void fromKeyword_wrongCase_exceptionThrown() {
        assertThrows(GoatException.class, () -> SortOrder.fromKeyword("DATE"));
    }

    @Test
    public void sort_byDate_earliestFirst() throws GoatException {
        TaskList tasks = listOf(new Deadline("later", LATE), new Deadline("sooner", EARLY));

        tasks.sort(SortOrder.DATE);

        assertEquals("sooner,later", descriptionsOf(tasks));
    }

    @Test
    public void sort_byDate_undatedTasksGoLast() throws GoatException {
        TaskList tasks = listOf(new Todo("no date"), new Deadline("has a date", LATE));

        tasks.sort(SortOrder.DATE);

        // "What is coming up?" is the question this order answers, so the
        // things that are not coming up at all belong at the bottom.
        assertEquals("has a date,no date", descriptionsOf(tasks));
    }

    @Test
    public void sort_byDate_undatedTasksTieBreakOnDescription() throws GoatException {
        TaskList tasks = listOf(new Todo("banana"), new Todo("apple"));

        tasks.sort(SortOrder.DATE);

        assertEquals("apple,banana", descriptionsOf(tasks));
    }

    @Test
    public void sort_byDate_eventSortsByItsStart() throws GoatException {
        TaskList tasks = listOf(
                new Deadline("deadline in december", LATE),
                new Event("event in august", EARLY, LATE));

        tasks.sort(SortOrder.DATE);

        assertEquals("event in august,deadline in december", descriptionsOf(tasks));
    }

    @Test
    public void sort_byName_alphabetical() throws GoatException {
        TaskList tasks = listOf(new Todo("cherry"), new Todo("apple"), new Todo("banana"));

        tasks.sort(SortOrder.NAME);

        assertEquals("apple,banana,cherry", descriptionsOf(tasks));
    }

    @Test
    public void sort_byName_ignoresCase() throws GoatException {
        TaskList tasks = listOf(new Todo("Banana"), new Todo("apple"));

        tasks.sort(SortOrder.NAME);

        // Capitalization deciding position would read as a bug, not a rule.
        assertEquals("apple,Banana", descriptionsOf(tasks));
    }

    @Test
    public void sort_emptyList_doesNothing() {
        TaskList tasks = new TaskList();
        tasks.sort(SortOrder.DATE);
        assertEquals(0, tasks.size());
    }

    @Test
    public void sort_alreadySorted_leavesOrderAlone() throws GoatException {
        TaskList tasks = listOf(new Todo("apple"), new Todo("banana"));

        tasks.sort(SortOrder.NAME);

        assertEquals("apple,banana", descriptionsOf(tasks));
    }

    @Test
    public void sort_sortsInPlace_taskNumbersFollowTheNewOrder() throws GoatException {
        TaskList tasks = listOf(new Todo("zebra"), new Todo("apple"));

        tasks.sort(SortOrder.NAME);

        // A sorted copy would leave "mark 1" pointing at the old first task.
        assertEquals("apple", tasks.get(1).getDescription());
    }
}
