package rex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import rex.task.Deadline;
import rex.task.Event;
import rex.task.Task;
import rex.task.TaskDateTime;
import rex.task.ToDo;

/**
 * Tests holding tasks as a group: counting them, naming one by the number the
 * user sees, and picking out the ones that fall on a day or match a search.
 *
 * The numbering is what most of these are about. A task list is asked about a
 * number counting from 1, as shown in a listing, while it stores its tasks
 * counting from 0, and the tests below pin down that the translation between
 * the two holds at both ends of the list.
 *
 * Test methods are deliberately not public, for the reason given in
 * TaskDateTimeTest.
 */
public class TaskListTest {
    /** Returns a list of three todos, described "first", "second" and "third". */
    private TaskList threeTodos() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));
        tasks.add(new ToDo("third"));
        return tasks;
    }

    @Test
    void size_newList_zero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    void add_severalTasks_keptInTheOrderAdded() {
        TaskList tasks = threeTodos();

        assertEquals(3, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("third", tasks.get(2).getDescription());
    }

    @Test
    void constructor_loadedTasks_heldInOrder() {
        ArrayList<Task> loaded = new ArrayList<>(List.of(new ToDo("read book"), new ToDo("write essay")));

        TaskList tasks = new TaskList(loaded);

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    void getByNumber_firstAndLast_returnsThatTask() throws RexException {
        TaskList tasks = threeTodos();

        assertEquals("first", tasks.getByNumber(1).getDescription());
        assertEquals("third", tasks.getByNumber(3).getDescription());
    }

    @Test
    void getByNumber_justPastTheEnd_exceptionThrown() {
        TaskList tasks = threeTodos();

        RexException thrown = assertThrows(RexException.class, () -> tasks.getByNumber(4));

        assertTrue(thrown.getMessage().contains("4"), thrown.getMessage());
    }

    @Test
    void getByNumber_zeroOrNegative_exceptionThrown() {
        // Numbering starts at 1, so 0 is not the first task but no task at all.
        TaskList tasks = threeTodos();

        assertThrows(RexException.class, () -> tasks.getByNumber(0));
        assertThrows(RexException.class, () -> tasks.getByNumber(-1));
    }

    @Test
    void getByNumber_emptyList_exceptionThrown() {
        assertThrows(RexException.class, () -> new TaskList().getByNumber(1));
    }

    @Test
    void deleteByNumber_middleTask_removedAndReturned() throws RexException {
        TaskList tasks = threeTodos();

        Task removed = tasks.deleteByNumber(2);

        assertEquals("second", removed.getDescription());
        assertEquals(2, tasks.size());
        assertEquals("third", tasks.get(1).getDescription());
    }

    @Test
    void deleteByNumber_pastTheEnd_exceptionThrownAndNothingRemoved() {
        TaskList tasks = threeTodos();

        assertThrows(RexException.class, () -> tasks.deleteByNumber(4));

        assertEquals(3, tasks.size());
    }

    @Test
    void deleteByNumber_afterADeletion_numbersHaveShiftedDown() throws RexException {
        // Deleting renumbers everything after it, so what was task 3 answers to
        // 2 afterwards. A test for this is worth having because the user types
        // these numbers from a listing that may be a few commands old.
        TaskList tasks = threeTodos();

        tasks.deleteByNumber(1);

        assertEquals("third", tasks.getByNumber(2).getDescription());
        assertThrows(RexException.class, () -> tasks.getByNumber(3));
    }

    @Test
    void findIndicesOn_tasksOnAndOffTheDay_onlyTheOnesOnIt() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));
        tasks.add(new Deadline("pay bill", TaskDateTime.parse("2019-10-16")));
        tasks.add(new Event("retreat",
                TaskDateTime.parse("2019-10-14"), TaskDateTime.parse("2019-10-18")));

        // The event covers the 15th without starting on it, and the todo has no
        // date at all, so this also checks that each kind answers for itself.
        assertEquals(List.of(1, 3), tasks.findIndicesOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    void findIndicesOn_noTaskOnThatDay_empty() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-10-15")));

        assertEquals(List.of(), tasks.findIndicesOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    void findIndicesMatching_partOfAWord_stillMatches() {
        // "book" is expected to find "bookshop" as well, so the search is on any
        // part of the description rather than on whole words.
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("visit bookshop"));
        tasks.add(new ToDo("write essay"));

        assertEquals(List.of(0, 1), tasks.findIndicesMatching("book"));
    }

    @Test
    void findIndicesMatching_differentCase_stillMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read Book"));

        assertEquals(List.of(0), tasks.findIndicesMatching("bOOk"));
    }

    @Test
    void findIndicesMatching_nothingContainsIt_empty() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertEquals(List.of(), tasks.findIndicesMatching("bicycle"));
    }

    @Test
    void getTasks_tasksHeld_sameTasksInOrder() {
        TaskList tasks = threeTodos();

        List<Task> held = tasks.getTasks();

        assertEquals(3, held.size());
        assertSame(tasks.get(0), held.get(0));
    }

    @Test
    void getTasks_changedThroughWhatIsReturned_refused() {
        // The list is handed out for saving, and is wrapped so that a caller
        // cannot reach past this class to change it. Without this test nothing
        // would notice the wrapping being dropped, since every other test still
        // passes if the real list is returned.
        TaskList tasks = threeTodos();

        List<Task> held = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> held.add(new ToDo("sneaked in")));
        assertThrows(UnsupportedOperationException.class, held::clear);
        assertEquals(3, tasks.size());
    }

    @Test
    void getTasks_taskAddedAfterwards_viewShowsIt() {
        // What is handed back is a view of the list rather than a copy, so a
        // task added later is visible through it. This is deliberate, and a
        // test says so: a reader would otherwise have no way of telling it
        // apart from an oversight.
        TaskList tasks = threeTodos();
        List<Task> held = tasks.getTasks();

        tasks.add(new ToDo("fourth"));

        assertEquals(4, held.size());
    }
}
