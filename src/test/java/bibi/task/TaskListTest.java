package bibi.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
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

    private static Task deadline(String description, String dueTimeText) throws BibiException {
        return new Deadline(description, dueTimeText);
    }

    private static Task event(String description, String startTimeText, String endTimeText)
            throws BibiException {
        return new Event(description, startTimeText, endTimeText);
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

    @Test
    public void sortBySchedule_datedTasks_earliestFirst() throws BibiException {
        TaskList tasks = new TaskList(
                deadline("submit report", "2019-12-01"),
                event("orientation", "2019-08-06 1400", "2019-08-06 1600"),
                deadline("pay fees", "2019-10-15"));

        tasks.sortBySchedule();

        assertEquals("[E][ ] orientation (from: 06 Aug 2019 2:00PM to: 06 Aug 2019 4:00PM)",
                tasks.get(1).toString());
        assertEquals("[D][ ] pay fees (by: 15 Oct 2019)", tasks.get(2).toString());
        assertEquals("[D][ ] submit report (by: 01 Dec 2019)", tasks.get(3).toString());
    }

    @Test
    public void sortBySchedule_undatedTasks_placedLast() throws BibiException {
        TaskList tasks = new TaskList(
                todo("borrow book"),
                deadline("pay fees", "2019-10-15"));

        tasks.sortBySchedule();

        assertEquals("[D][ ] pay fees (by: 15 Oct 2019)", tasks.get(1).toString());
        assertEquals("[T][ ] borrow book", tasks.get(2).toString());
    }

    @Test
    public void sortBySchedule_sameMoment_keepsTheOrderTheyWereAddedIn() throws BibiException {
        TaskList tasks = new TaskList(
                deadline("first added", "2019-10-15"),
                deadline("second added", "2019-10-15"));

        tasks.sortBySchedule();

        // The sort is stable, so tasks sharing a moment must not swap around.
        assertEquals("[D][ ] first added (by: 15 Oct 2019)", tasks.get(1).toString());
        assertEquals("[D][ ] second added (by: 15 Oct 2019)", tasks.get(2).toString());
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
        assertEquals("[D][ ] whole day (by: 15 Oct 2019)", tasks.get(1).toString());
    }

    @Test
    public void findSameTaskNumber_identicalTodo_returnsItsNumber() throws BibiException {
        TaskList tasks = new TaskList(todo("first"), todo("read book"));

        assertEquals(2, tasks.findSameTaskNumber(todo("read book")));
    }

    @Test
    public void findSameTaskNumber_differingOnlyByCase_stillMatches() throws BibiException {
        TaskList tasks = new TaskList(todo("read book"));

        assertEquals(1, tasks.findSameTaskNumber(todo("READ BOOK")));
    }

    @Test
    public void findSameTaskNumber_sameDescriptionDifferentType_noMatch() throws BibiException {
        TaskList tasks = new TaskList(todo("pay fees"));

        // A ToDo and a deadline are different commitments even when worded alike.
        assertEquals(0, tasks.findSameTaskNumber(deadline("pay fees", "2019-10-15")));
    }

    @Test
    public void findSameTaskNumber_sameDescriptionDifferentDate_noMatch() throws BibiException {
        TaskList tasks = new TaskList(deadline("pay fees", "2019-10-15"));

        assertEquals(0, tasks.findSameTaskNumber(deadline("pay fees", "2019-11-15")));
    }

    @Test
    public void findSameTaskNumber_completedTask_stillCountsAsTheSame() throws BibiException {
        Task done = todo("read book");
        done.markComplete();
        TaskList tasks = new TaskList(done);

        // Ticking a task off does not make re-adding it a different task.
        assertEquals(1, tasks.findSameTaskNumber(todo("read book")));
    }

    @Test
    public void findSameTaskNumber_emptyList_returnsZero() throws BibiException {
        assertEquals(0, new TaskList().findSameTaskNumber(todo("anything")));
    }

    @Test
    public void undo_newListWithoutChanges_exceptionThrown() throws BibiException {
        Task initial = todo("initial");
        TaskList tasks = new TaskList(initial);
        TaskList loadedTasks = new TaskList(List.of(initial));

        BibiException thrown = assertThrows(BibiException.class, tasks::undo);

        assertTrue(thrown.getMessage().contains("no change to undo in this session"));
        assertThrows(BibiException.class, loadedTasks::undo);
        assertThrows(BibiException.class, new TaskList()::undo);
        assertEquals(List.of(initial), tasks.getTasks());
    }

    @Test
    public void undo_addedTask_restoresPreviousList() throws BibiException {
        Task initial = todo("initial");
        TaskList tasks = new TaskList(initial);
        tasks.add(todo("added"));

        tasks.undo();

        assertEquals(List.of(initial), tasks.getTasks());
        assertSame(initial, tasks.get(1));
    }

    @Test
    public void undo_removedTask_restoresPositionDatesAndCompletion() throws BibiException {
        Task first = todo("first");
        Task middle = event("camp", "2019-12-02 1800", "2019-12-03");
        middle.markComplete();
        Task last = deadline("last", "2019-12-04");
        TaskList tasks = new TaskList(first, middle, last);
        tasks.remove(2);

        tasks.undo();

        assertEquals(List.of(first, middle, last), tasks.getTasks());
        assertEquals("E | 1 | camp | 2019-12-02 1800 | 2019-12-03", tasks.get(2).toFileFormat());
    }

    @Test
    public void undo_markedTask_restoresIncompleteStatus() throws BibiException {
        Task initial = todo("initial");
        TaskList tasks = new TaskList(initial);

        Task marked = tasks.markComplete(1);

        assertSame(initial, marked);
        assertTrue(marked.isComplete());

        tasks.undo();

        assertSame(initial, tasks.get(1));
        assertFalse(initial.isComplete());
    }

    @Test
    public void undo_unmarkedTask_restoresCompleteStatus() throws BibiException {
        Task initial = todo("initial");
        initial.markComplete();
        TaskList tasks = new TaskList(initial);

        Task reopened = tasks.markIncomplete(1);

        assertSame(initial, reopened);
        assertFalse(reopened.isComplete());

        tasks.undo();

        assertTrue(initial.isComplete());
        assertEquals("T | 1 | initial", tasks.get(1).toFileFormat());
    }

    @Test
    public void undo_sortedTasks_restoresOriginalOrderAndFlags() throws BibiException {
        Task undated = todo("undated");
        Task later = deadline("later", "2019-12-02");
        later.markComplete();
        Task sooner = deadline("sooner", "2019-10-15");
        TaskList tasks = new TaskList(undated, later, sooner);
        tasks.sortBySchedule();

        assertEquals(List.of(sooner, later, undated), tasks.getTasks());

        tasks.undo();

        assertEquals(List.of(undated, later, sooner), tasks.getTasks());
        assertTrue(later.isComplete());
        assertFalse(sooner.isComplete());
    }

    @Test
    public void undo_twoChanges_revertsOnlyLatestAndCannotRedo() throws BibiException {
        Task first = todo("first");
        TaskList tasks = new TaskList();
        tasks.add(first);
        tasks.add(todo("second"));

        tasks.undo();

        assertEquals(List.of(first), tasks.getTasks());
        assertThrows(BibiException.class, tasks::undo);
        assertEquals(List.of(first), tasks.getTasks());
    }

    @Test
    public void undo_failedMutations_preservePreviousChange() throws BibiException {
        TaskList tasks = new TaskList();
        tasks.add(todo("added"));

        assertThrows(BibiException.class, () -> tasks.remove(2));
        assertThrows(BibiException.class, () -> tasks.markComplete(0));
        assertThrows(BibiException.class, () -> tasks.markIncomplete(2));

        tasks.undo();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void undo_noOpsAndQueries_preservePreviousChange() throws BibiException {
        Task complete = todo("complete");
        complete.markComplete();
        Task incomplete = todo("incomplete");
        TaskList tasks = new TaskList(complete, incomplete);
        tasks.add(todo("added"));

        tasks.markComplete(1);
        tasks.markIncomplete(2);
        tasks.sortBySchedule();
        assertEquals(3, tasks.size());
        assertEquals(1, tasks.findSameTaskNumber(todo("complete")));
        assertSame(incomplete, tasks.get(2));

        tasks.undo();

        assertEquals(List.of(complete, incomplete), tasks.getTasks());
        assertTrue(complete.isComplete());
        assertFalse(incomplete.isComplete());
    }

    @Test
    public void undo_onlyNoOpChanges_exceptionThrown() throws BibiException {
        TaskList tasks = new TaskList(todo("already incomplete"));

        tasks.markIncomplete(1);
        tasks.sortBySchedule();

        assertThrows(BibiException.class, tasks::undo);
        assertEquals("[T][ ] already incomplete", tasks.get(1).toString());
    }

    @Test
    public void undo_changeAfterUndo_canUndoNewChange() throws BibiException {
        Task initial = todo("initial");
        TaskList tasks = new TaskList(initial);
        tasks.markComplete(1);
        tasks.undo();

        tasks.remove(1);
        tasks.undo();

        assertEquals(List.of(initial), tasks.getTasks());
        assertFalse(initial.isComplete());
        assertThrows(BibiException.class, tasks::undo);
    }
}
