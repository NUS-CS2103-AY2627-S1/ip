package bibi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bibi.BibiException;

/**
 * Tests the one-based numbering that the task list presents to the user, and
 * the boundaries it has to reject.
 */
public class TaskListTest {

    private static Task todo(String description) throws BibiException {
        return new Todo(description);
    }

    @Test
    public void newList_noArguments_empty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void newList_severalTasks_holdsThemInOrder() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("second"));

        assertEquals(2, tasks.size());
        assertFalse(tasks.isEmpty());
        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][ ] second", tasks.get(2).toString());
    }

    @Test
    public void newList_fromExistingTasks_laterChangesDoNotAffectTheSource()
            throws BibiException {
        List<Task> source = new ArrayList<>(List.of(todo("first")));
        TaskList tasks = new TaskList(source);

        tasks.add(todo("second"));

        assertEquals(1, source.size());
        assertEquals(2, tasks.size());
    }

    @Test
    public void get_firstAndLastNumbers_returnsTheMatchingTask() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("second"), todo("third"));

        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][ ] third", tasks.get(3).toString());
    }

    @Test
    public void get_numberOutsideTheList_exceptionThrown() throws BibiException {
        TaskList tasks = new TaskList(todo("only"));

        assertThrows(BibiException.class, () -> tasks.get(0));
        assertThrows(BibiException.class, () -> tasks.get(2));
        assertThrows(BibiException.class, () -> tasks.get(-1));
    }

    @Test
    public void get_anyNumberOnEmptyList_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(BibiException.class, () -> tasks.get(1));
    }

    @Test
    public void remove_middleTask_laterTasksShiftDown() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("second"), todo("third"));

        Task removed = tasks.remove(2);

        assertEquals("[T][ ] second", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] third", tasks.get(2).toString());
    }

    @Test
    public void remove_numberOutsideTheList_listUnchanged() throws BibiException {
        TaskList tasks = new TaskList(todo("only"));

        assertThrows(BibiException.class, () -> tasks.remove(2));
        assertEquals(1, tasks.size());
    }

    @Test
    public void getTasks_returnedList_cannotBeChanged() throws BibiException {
        TaskList tasks = new TaskList(todo("first"));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.getTasks().add(todo("sneaked in")));
    }

    @Test
    public void getTasks_afterAdding_reflectsTheCurrentContents() throws BibiException {
        TaskList tasks = new TaskList();
        tasks.add(todo("first"));

        assertEquals(1, tasks.getTasks().size());
        assertEquals("[T][ ] first", tasks.getTasks().get(0).toString());
    }

    @Test
    public void newList_fromAList_holdsThemInOrder() throws BibiException {
        // The List overload is still the one Bibi uses for tasks read from disk.
        TaskList tasks = new TaskList(List.of(todo("first"), todo("second")));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] second", tasks.get(2).toString());
    }

    @Test
    public void newList_fromAnArray_laterChangesDoNotAffectTheSource() throws BibiException {
        // Varargs hands the constructor an array the caller still holds, so the
        // copying the List overload does has to happen here too.
        Task[] source = {todo("first"), todo("second")};
        TaskList tasks = new TaskList(source);

        source[0] = todo("swapped in");

        assertEquals("[T][ ] first", tasks.get(1).toString());
    }
    private static Task deadline(String description, String by) throws BibiException {
        return new Deadline(description, by);
    }

    private static Task event(String description, String from, String to) throws BibiException {
        return new Event(description, from, to);
    }

    @Test
    public void sortBySchedule_datedTasks_earliestFirst() throws BibiException {
        TaskList tasks = new TaskList(
                deadline("submit report", "2019-12-01"),
                event("orientation", "2019-08-06 1400", "2019-08-06 1600"),
                deadline("pay fees", "2019-10-15"));

        tasks.sortBySchedule();

        assertEquals("[E][ ] orientation (from: Aug 06 2019 2:00PM to: Aug 06 2019 4:00PM)",
                tasks.get(1).toString());
        assertEquals("[D][ ] pay fees (by: Oct 15 2019)", tasks.get(2).toString());
        assertEquals("[D][ ] submit report (by: Dec 01 2019)", tasks.get(3).toString());
    }

    @Test
    public void sortBySchedule_undatedTasks_placedLast() throws BibiException {
        TaskList tasks = new TaskList(
                todo("borrow book"),
                deadline("pay fees", "2019-10-15"));

        tasks.sortBySchedule();

        assertEquals("[D][ ] pay fees (by: Oct 15 2019)", tasks.get(1).toString());
        assertEquals("[T][ ] borrow book", tasks.get(2).toString());
    }

    @Test
    public void sortBySchedule_sameMoment_keepsTheOrderTheyWereAddedIn() throws BibiException {
        TaskList tasks = new TaskList(
                deadline("first added", "2019-10-15"),
                deadline("second added", "2019-10-15"));

        tasks.sortBySchedule();

        // The sort is stable, so tasks sharing a moment must not swap around.
        assertEquals("[D][ ] first added (by: Oct 15 2019)", tasks.get(1).toString());
        assertEquals("[D][ ] second added (by: Oct 15 2019)", tasks.get(2).toString());
    }

    @Test
    public void sortBySchedule_severalUndatedTasks_keepTheirRelativeOrder() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("second"), todo("third"));

        tasks.sortBySchedule();

        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][ ] third", tasks.get(3).toString());
    }

    @Test
    public void sortBySchedule_emptyList_stillEmpty() {
        TaskList tasks = new TaskList();

        tasks.sortBySchedule();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void sortBySchedule_wholeDayAgainstTimeOnSameDay_wholeDayComesFirst()
            throws BibiException {
        TaskList tasks = new TaskList(
                deadline("has a time", "2019-10-15 1800"),
                deadline("whole day", "2019-10-15"));

        tasks.sortBySchedule();

        // A whole-day value counts as the start of its day, matching isBefore.
        assertEquals("[D][ ] whole day (by: Oct 15 2019)", tasks.get(1).toString());
    }
}
