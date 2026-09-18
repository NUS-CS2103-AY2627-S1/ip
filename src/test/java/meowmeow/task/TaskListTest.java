package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import meowmeow.MeowmeowException;

/**
 * Tests for {@link TaskList} - the collection of tasks plus the operations
 * on it.
 *
 * <p>The valuable behaviour here is the <em>1-based</em> position handling:
 * {@link TaskList#get(int)} and {@link TaskList#delete(int)} translate the
 * numbers the user sees in "list" into list indices and reject anything out
 * of range. {@link TaskList#findOn(LocalDate)} and the defensive copy made
 * by the {@code List}-taking constructor are also checked.
 */
public class TaskListTest {

    /** A deadline due on the given d/M/yyyy date - a small test helper. */
    private static Deadline deadlineOn(String description, String date) throws MeowmeowException {
        return new Deadline(description, TaskDateTime.parse(date));
    }

    // ---- size / add ----

    @Test
    public void size_newList_isZero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void add_task_appearsAtEndAndGrowsSize() throws MeowmeowException {
        TaskList list = new TaskList();

        list.add(new Todo("first"));
        list.add(new Todo("second"));

        assertEquals(2, list.size());
        assertEquals("second", list.get(2).getDescription());
    }

    // ---- get: 1-based lookup ----

    @Test
    public void get_firstPosition_returnsFirstTask() throws MeowmeowException {
        TaskList list = new TaskList();
        Todo only = new Todo("only");
        list.add(only);

        assertEquals(only, list.get(1));
    }

    @Test
    public void get_zero_exceptionThrown() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(MeowmeowException.class, () -> list.get(0));
    }

    @Test
    public void get_pastEnd_exceptionThrown() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(MeowmeowException.class, () -> list.get(2));
    }

    @Test
    public void get_negative_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> new TaskList().get(-1));
    }

    @Test
    public void get_onEmptyList_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> new TaskList().get(1));
    }

    // ---- delete: 1-based removal ----

    @Test
    public void delete_validPosition_returnsRemovedTaskAndShrinks() throws MeowmeowException {
        TaskList list = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        list.add(first);
        list.add(second);

        Task removed = list.delete(1);

        assertEquals(first, removed);
        assertEquals(1, list.size());
        // Positions renumber: what was #2 is now #1.
        assertEquals(second, list.get(1));
    }

    @Test
    public void delete_pastEnd_exceptionThrown() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(MeowmeowException.class, () -> list.delete(2));
    }

    @Test
    public void delete_zero_exceptionThrown() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));

        assertThrows(MeowmeowException.class, () -> list.delete(0));
    }

    // ---- findOn: tasks occurring on a date ----

    @Test
    public void findOn_returnsOnlyTasksOnThatDay_inListOrder() throws MeowmeowException {
        TaskList list = new TaskList();
        list.add(new Todo("no date")); // never matches
        list.add(deadlineOn("due that day", "2/12/2019")); // matches
        list.add(new Event("spans the day",
                TaskDateTime.parse("1/12/2019"), TaskDateTime.parse("3/12/2019"))); // matches
        list.add(deadlineOn("another day", "5/12/2019")); // no match

        List<Task> matches = list.findOn(LocalDate.of(2019, 12, 2));

        assertEquals(2, matches.size());
        assertEquals("due that day", matches.get(0).getDescription());
        assertEquals("spans the day", matches.get(1).getDescription());
    }

    @Test
    public void findOn_noTasksOnThatDay_returnsEmptyList() throws MeowmeowException {
        TaskList list = new TaskList();
        list.add(deadlineOn("due", "2/12/2019"));

        assertEquals(0, list.findOn(LocalDate.of(2020, 1, 1)).size());
    }

    // ---- findByKeyword: tasks whose description contains a keyword ----

    @Test
    public void findByKeyword_returnsMatchingTasks_inListOrder() throws MeowmeowException {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("buy milk"));
        list.add(deadlineOn("return book", "2/12/2019"));

        List<Task> matches = list.findByKeyword("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matches.get(0).getDescription());
        assertEquals("return book", matches.get(1).getDescription());
    }

    @Test
    public void findByKeyword_noDescriptionContainsKeyword_returnsEmptyList() {
        TaskList list = new TaskList();
        list.add(new Todo("read book"));
        list.add(new Todo("buy milk"));

        assertEquals(0, list.findByKeyword("essay").size());
    }

    @Test
    public void findByKeyword_differentCasing_stillMatches() {
        TaskList list = new TaskList();
        list.add(new Todo("Read Book"));

        assertEquals(1, list.findByKeyword("book").size());
        assertEquals(1, list.findByKeyword("BOOK").size());
    }

    @Test
    public void findByKeyword_partialWord_matchesAsSubstring() {
        TaskList list = new TaskList();
        list.add(new Todo("booking a room"));

        assertEquals(1, list.findByKeyword("book").size());
    }

    @Test
    public void findByKeyword_emptyList_returnsEmptyList() {
        assertEquals(0, new TaskList().findByKeyword("book").size());
    }

    // ---- asList: read-only view ----

    @Test
    public void asList_reflectsContentsInOrder() {
        TaskList list = new TaskList();
        list.add(new Todo("a"));
        list.add(new Todo("b"));

        assertEquals(2, list.asList().size());
        assertEquals("a", list.asList().get(0).getDescription());
    }

    @Test
    public void asList_isUnmodifiable() {
        TaskList list = new TaskList();

        assertThrows(UnsupportedOperationException.class, () -> list.asList().add(new Todo("x")));
    }

    // ---- constructor: defensive copy of the supplied list ----

    @Test
    public void constructor_copiesSuppliedList_laterChangesToSourceIgnored() {
        ArrayList<Task> source = new ArrayList<>();
        source.add(new Todo("a"));
        TaskList list = new TaskList(source);

        source.add(new Todo("b"));

        assertEquals(1, list.size());
    }
}
