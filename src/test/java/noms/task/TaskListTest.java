package noms.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}, focusing on its non-trivial behavior:
 * {@link TaskList#tasksOn(LocalDate)} (the date filter behind the {@code on}
 * command), {@link TaskList#delete(int)} (removal and renumbering), and the
 * defensive copying done by the list-taking constructor and
 * {@link TaskList#asList()}. The simple pass-through accessors are exercised
 * incidentally by these cases.
 */
public class TaskListTest {

    private final Deadline returnBook = new Deadline("return book", LocalDate.of(2019, 12, 1));
    private final Deadline payRent = new Deadline("pay rent", LocalDate.of(2019, 12, 15));
    private final Event conference = new Event("conference",
            LocalDate.of(2019, 11, 30), LocalDate.of(2019, 12, 2));
    private final ToDo chore = new ToDo("standalone chore");

    private TaskList populatedList() {
        TaskList list = new TaskList();
        list.add(chore);
        list.add(returnBook);
        list.add(payRent);
        list.add(conference);
        return list;
    }

    // --- tasksOn ---

    @Test
    public void tasksOn_matchingDeadlineAndEvent_returnedInListOrder() {
        List<Task> matches = populatedList().tasksOn(LocalDate.of(2019, 12, 1));
        // The deadline "return book" and the spanning event "conference"
        // match; the todo and the other deadline do not.
        assertEquals(List.of(returnBook, conference), matches);
    }

    @Test
    public void tasksOn_noMatches_returnsEmptyList() {
        assertEquals(List.of(), populatedList().tasksOn(LocalDate.of(2019, 6, 15)));
    }

    @Test
    public void tasksOn_todoNeverMatches() {
        // A todo has no date, so no query date can match it.
        TaskList list = new TaskList();
        list.add(chore);
        assertEquals(List.of(), list.tasksOn(LocalDate.of(2019, 12, 1)));
    }

    // --- find ---

    @Test
    public void find_matchingDescriptions_returnedInListOrder() {
        List<Task> matches = populatedList().find("book");
        // Only "return book" contains the keyword; results keep list order.
        assertEquals(List.of(returnBook), matches);
    }

    @Test
    public void find_isCaseInsensitive() {
        // An upper-case keyword still matches a lower-case description.
        assertEquals(List.of(returnBook), populatedList().find("BOOK"));
    }

    @Test
    public void find_matchesSubstringAcrossTasks() {
        TaskList list = new TaskList();
        ToDo readBook = new ToDo("read book");
        ToDo bookFlight = new ToDo("book flight");
        list.add(readBook);
        list.add(chore);
        list.add(bookFlight);
        // "book" appears in two descriptions, returned in their list order.
        assertEquals(List.of(readBook, bookFlight), list.find("book"));
    }

    @Test
    public void find_noMatches_returnsEmptyList() {
        assertEquals(List.of(), populatedList().find("holiday"));
    }

    // --- delete ---

    @Test
    public void delete_removesAndReturnsTask() {
        TaskList list = populatedList();
        Task removed = list.delete(1);
        assertSame(returnBook, removed);
        assertEquals(3, list.size());
    }

    @Test
    public void delete_renumbersRemainingTasks() {
        TaskList list = populatedList();
        list.delete(1);
        // After removing index 1, the tasks after it shift down by one.
        assertSame(chore, list.get(0));
        assertSame(payRent, list.get(1));
        assertSame(conference, list.get(2));
    }

    @Test
    public void get_invalidIndex_assertionFails() {
        TaskList list = populatedList();

        assertThrows(AssertionError.class, () -> list.get(list.size()));
    }

    @Test
    public void get_negativeIndex_assertionFails() {
        TaskList list = populatedList();

        assertThrows(AssertionError.class, () -> list.get(-1));
    }

    @Test
    public void delete_invalidIndex_assertionFails() {
        TaskList list = populatedList();

        assertThrows(AssertionError.class, () -> list.delete(-1));
    }

    @Test
    public void delete_indexEqualToSize_assertionFails() {
        TaskList list = populatedList();

        assertThrows(AssertionError.class, () -> list.delete(list.size()));
    }

    @Test
    public void add_nullTask_assertionFails() {
        TaskList list = new TaskList();

        assertThrows(AssertionError.class, () -> list.add(null));
    }

    // --- add / size ---

    @Test
    public void add_increasesSizeAndAppends() {
        TaskList list = new TaskList();
        assertEquals(0, list.size());
        list.add(chore);
        assertEquals(1, list.size());
        assertSame(chore, list.get(0));
    }

    @Test
    public void add_atIndex_insertsAndShiftsLaterTasks() {
        TaskList list = new TaskList(chore, conference);

        list.add(1, returnBook);

        assertSame(chore, list.get(0));
        assertSame(returnBook, list.get(1));
        assertSame(conference, list.get(2));
    }

    // --- duplicate details ---

    @Test
    public void hasMatchingTask_sameDetailsIgnoringCompletion_returnsTrue() {
        Deadline completedCopy = new Deadline("return book", LocalDate.of(2019, 12, 1));
        completedCopy.markAsDone();

        assertTrue(populatedList().hasMatchingTask(completedCopy));
    }

    @Test
    public void hasMatchingTask_differentTaskTypeOrDate_returnsFalse() {
        TaskList list = populatedList();

        assertFalse(list.hasMatchingTask(new ToDo("return book")));
        assertFalse(list.hasMatchingTask(
                new Deadline("return book", LocalDate.of(2019, 12, 2))));
    }

    // --- defensive copying ---

    @Test
    public void constructor_copiesInputList_soLaterChangesDoNotLeakIn() {
        List<Task> source = new ArrayList<>();
        source.add(chore);
        TaskList list = new TaskList(source);

        source.add(returnBook); // must not affect the already-built TaskList
        assertEquals(1, list.size());
    }

    @Test
    public void constructor_withVarargs_preservesTaskOrder() {
        TaskList list = new TaskList(chore, returnBook, conference);

        assertEquals(3, list.size());
        assertSame(chore, list.get(0));
        assertSame(returnBook, list.get(1));
        assertSame(conference, list.get(2));
    }

    @Test
    public void asList_returnsCopy_soChangesDoNotLeakOut() {
        TaskList list = new TaskList();
        list.add(chore);

        List<Task> snapshot = list.asList();
        snapshot.clear(); // must not affect the TaskList
        assertEquals(1, list.size());
    }
}
