package ff15.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/**
 * Tests the collection behaviour of {@link TaskList}.
 *
 * <p>The point worth guarding is that task numbers are 1-based here: the list
 * translates the number the user typed into the 0-based index the underlying
 * ArrayList wants, and an off-by-one in that translation would silently act on
 * the wrong task rather than fail loudly.
 */
public class TaskListTest {

    /** Builds a list of plain todos described "a", "b", "c", ... for positional checks. */
    private static TaskList listOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new Todo(description));
        }
        return tasks;
    }

    @Test
    public void size_newList_isZero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void size_afterAdds_countsEveryTask() {
        assertEquals(3, listOf("a", "b", "c").size());
    }

    @Test
    public void constructor_existingTasks_adoptsThem() throws FF15Exception {
        ArrayList<Task> loaded = new ArrayList<>();
        loaded.add(new Todo("read book"));
        TaskList tasks = new TaskList(loaded);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    @Test
    public void get_firstTask_returnsTheOneAddedFirst() throws FF15Exception {
        TaskList tasks = listOf("a", "b", "c");
        assertEquals("[T][ ] a", tasks.get(1).toString());
    }

    @Test
    public void get_lastTask_returnsTheOneAddedLast() throws FF15Exception {
        TaskList tasks = listOf("a", "b", "c");
        assertEquals("[T][ ] c", tasks.get(3).toString());
    }

    @Test
    public void get_zero_throwsException() {
        TaskList tasks = listOf("a", "b");
        assertThrows(FF15Exception.class, () -> tasks.get(0));
    }

    @Test
    public void get_negativeNumber_throwsException() {
        TaskList tasks = listOf("a", "b");
        assertThrows(FF15Exception.class, () -> tasks.get(-1));
    }

    @Test
    public void get_onePastTheEnd_throwsException() {
        TaskList tasks = listOf("a", "b");
        assertThrows(FF15Exception.class, () -> tasks.get(3));
    }

    @Test
    public void get_onEmptyList_throwsException() {
        TaskList tasks = new TaskList();
        assertThrows(FF15Exception.class, () -> tasks.get(1));
    }

    @Test
    public void get_outOfRange_messageReportsTheNumberAndTheCount() {
        TaskList tasks = listOf("a", "b");
        FF15Exception thrown = assertThrows(FF15Exception.class, () -> tasks.get(5));
        assertTrue(thrown.getMessage().contains("5"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("2"), thrown.getMessage());
    }

    @Test
    public void delete_middleTask_returnsItAndRenumbersTheRest() throws FF15Exception {
        TaskList tasks = listOf("a", "b", "c");
        Task removed = tasks.delete(2);
        assertEquals("[T][ ] b", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] a", tasks.get(1).toString());
        assertEquals("[T][ ] c", tasks.get(2).toString());
    }

    @Test
    public void delete_lastRemainingTask_leavesAnEmptyList() throws FF15Exception {
        TaskList tasks = listOf("a");
        tasks.delete(1);
        assertEquals(0, tasks.size());
        assertThrows(FF15Exception.class, () -> tasks.get(1));
    }

    @Test
    public void delete_outOfRange_throwsAndLeavesTheListAlone() {
        TaskList tasks = listOf("a", "b");
        assertThrows(FF15Exception.class, () -> tasks.delete(0));
        assertThrows(FF15Exception.class, () -> tasks.delete(3));
        assertEquals(2, tasks.size());
    }

    @Test
    public void tasksIn_mixedTasks_returnsOnlyTheDatedMatchesInListOrder() throws FF15Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date at all"),
                new Deadline("due that day", TaskTime.parse("2019-12-02")),
                new Deadline("due another day", TaskTime.parse("2019-12-09")),
                new Event("running through", TaskTime.parse("2019-12-01"), TaskTime.parse("2019-12-05")));

        List<Task> matches = tasks.tasksIn(DateRange.parse("2019-12-02"));

        assertEquals(2, matches.size());
        assertEquals("[D][ ] due that day (by: Dec 02 2019)", matches.get(0).toString());
        assertEquals("[E][ ] running through (from: Dec 01 2019 to: Dec 05 2019)", matches.get(1).toString());
    }

    @Test
    public void tasksIn_nothingMatches_returnsEmptyList() throws FF15Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date at all"),
                new Deadline("due later", TaskTime.parse("2020-06-01")));
        assertTrue(tasks.tasksIn(DateRange.parse("2019-12-02")).isEmpty());
    }

    @Test
    public void tasksIn_emptyList_returnsEmptyList() throws FF15Exception {
        assertTrue(new TaskList().tasksIn(DateRange.parse("2019")).isEmpty());
    }

    @Test
    public void tasksIn_returnedList_isSeparateFromTheTaskList() throws FF15Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("due that day", TaskTime.parse("2019-12-02")));
        List<Task> matches = tasks.tasksIn(DateRange.parse("2019-12-02"));
        matches.clear();
        assertEquals(1, tasks.size());
    }

    @Test
    public void find_matchingDescriptions_returnsThemInListOrder() throws FF15Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"),
                new Todo("buy milk"),
                new Deadline("return book", TaskTime.parse("2019-12-02")));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", matches.get(1).toString());
    }

    @Test
    public void find_differentCase_stillMatches() {
        TaskList tasks = listOf("Read Book");
        assertEquals(1, tasks.find("book").size());
        assertEquals(1, tasks.find("BOOK").size());
    }

    @Test
    public void find_nothingMatches_returnsEmptyList() {
        TaskList tasks = listOf("read book", "buy milk");
        assertTrue(tasks.find("homework").isEmpty());
    }

    @Test
    public void find_emptyList_returnsEmptyList() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    @Test
    public void find_keywordOnlyInADate_doesNotMatch() throws FF15Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", TaskTime.parse("2019-12-02")));
        assertTrue(tasks.find("2019").isEmpty());
        assertTrue(tasks.find("Dec").isEmpty());
    }

    @Test
    public void find_returnedList_isSeparateFromTheTaskList() {
        TaskList tasks = listOf("read book");
        List<Task> matches = tasks.find("book");
        matches.clear();
        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_everyTask_appearsInOrder() {
        TaskList tasks = listOf("a", "b");
        List<Task> view = tasks.asList();
        assertEquals(2, view.size());
        assertEquals("[T][ ] a", view.get(0).toString());
        assertEquals("[T][ ] b", view.get(1).toString());
    }

    @Test
    public void asList_attemptToModify_throwsException() {
        TaskList tasks = listOf("a");
        List<Task> view = tasks.asList();
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("sneaked in")));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_taskAddedAfterwards_showsUpInTheView() {
        TaskList tasks = listOf("a");
        List<Task> view = tasks.asList();
        tasks.add(new Todo("b"));
        assertEquals(2, view.size());
    }

    @Test
    public void get_sameNumberTwice_returnsTheSameObject() throws FF15Exception {
        TaskList tasks = listOf("a");
        assertSame(tasks.get(1), tasks.get(1));
    }

    @Test
    public void findSame_taskPresent_returnsItsNumber() {
        TaskList tasks = listOf("a", "b", "c");
        assertEquals(2, tasks.findSame(new Todo("b")).getAsInt());
    }

    @Test
    public void findSame_taskAbsent_returnsEmpty() {
        assertTrue(listOf("a", "b").findSame(new Todo("z")).isEmpty());
    }

    @Test
    public void findSame_listHoldsEveryKind_comparesAcrossKinds() throws FF15Exception {
        // The task asked about is compared against each task already there, whatever its kind.
        TaskList tasks = new TaskList();
        tasks.add(new Event("a", TaskTime.parse("2019-12-05"), TaskTime.parse("2019-12-06")),
                new Deadline("a", TaskTime.parse("2019-12-05")), new Todo("a"));
        assertEquals(3, tasks.findSame(new Todo("a")).getAsInt());
        assertEquals(2, tasks.findSame(new Deadline("a", TaskTime.parse("2019-12-05"))).getAsInt());
        assertTrue(tasks.findSame(new Deadline("a", TaskTime.parse("2019-12-06"))).isEmpty());
    }

    @Test
    public void findSame_twoMatches_returnsTheFirst() {
        TaskList tasks = listOf("a", "b", "b");
        assertEquals(2, tasks.findSame(new Todo("b")).getAsInt());
    }

    @Test
    public void add_noArguments_tripsTheGuard() {
        // Varargs makes add() with nothing legal; the assertion is what stops it passing silently.
        assertThrows(AssertionError.class, () -> new TaskList().add());
    }
}
