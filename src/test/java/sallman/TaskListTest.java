package sallman;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sallman.command.SortOrder;
import sallman.task.Deadline;
import sallman.task.Event;
import sallman.task.Task;
import sallman.task.Todo;

/**
 * Tests that {@link TaskList} keeps its contents in order as tasks are added
 * and removed, selects the right tasks for a given date, and can be put back
 * the way it was.
 */
public class TaskListTest {

    @Test
    public void sort_byDate_soonestFirstAndUndatedLast() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("pay bill", LocalDate.of(2019, 12, 1)));
        tasks.add(new Event("conference", LocalDate.of(2019, 10, 14),
                LocalDate.of(2019, 10, 17)));

        tasks.sort(SortOrder.DATE.getComparator());

        // An event is ordered by when it starts, and a todo has no date at
        // all, so it goes last rather than being dropped or coming first.
        assertEquals("[E][ ] conference (from: Oct 14 2019 to: Oct 17 2019)",
                tasks.get(0).toString());
        assertEquals("[D][ ] pay bill (by: Dec 01 2019)", tasks.get(1).toString());
        assertEquals("[T][ ] read book", tasks.get(2).toString());
    }

    @Test
    public void sort_tasksTheOrderCannotSeparate_keptInTheOrderAdded() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));

        tasks.sort(SortOrder.DATE.getComparator());

        assertEquals("[T][ ] zebra", tasks.get(0).toString());
        assertEquals("[T][ ] apple", tasks.get(1).toString());
    }

    @Test
    public void sort_byName_alphabeticalIgnoringCase() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Zebra"));
        tasks.add(new Todo("apple"));

        tasks.sort(SortOrder.NAME.getComparator());

        assertEquals("[T][ ] apple", tasks.get(0).toString());
        assertEquals("[T][ ] Zebra", tasks.get(1).toString());
    }

    @Test
    public void sort_byStatus_unfinishedFirst() {
        TaskList tasks = new TaskList();
        Todo done = new Todo("read book");
        done.markAsDone();
        tasks.add(done);
        tasks.add(new Todo("buy milk"));

        tasks.sort(SortOrder.STATUS.getComparator());

        assertEquals("[T][ ] buy milk", tasks.get(0).toString());
        assertEquals("[T][X] read book", tasks.get(1).toString());
    }

    @Test
    public void indexOf_taskInTheList_itsPositionEvenBesideAnIdenticalTask() {
        // Duplicates can still come from older data files, so the position is
        // found by identity rather than by equal contents.
        TaskList tasks = new TaskList();
        Task first = new Todo("read book");
        Task second = new Todo("read book");
        tasks.add(first);
        tasks.add(second);

        assertEquals(1, tasks.indexOf(second));
        assertEquals(-1, tasks.indexOf(new Todo("read book")));
    }

    @Test
    public void isSortedBy_orderAlreadyHolds_trueUntilATaskBreaksIt() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("apple"));
        tasks.add(new Todo("zebra"));
        assertTrue(tasks.isSortedBy(SortOrder.NAME.getComparator()));

        tasks.add(new Todo("mango"));
        assertFalse(tasks.isSortedBy(SortOrder.NAME.getComparator()));
    }

    @Test
    public void isSortedBy_tasksTheOrderCannotSeparate_countAsSorted() {
        // A stable sort leaves such tasks where they are, so sorting would change nothing.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));

        assertTrue(tasks.isSortedBy(SortOrder.DATE.getComparator()));
    }

    @Test
    public void undo_afterSort_originalOrderBack() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("zebra"));
        tasks.add(new Todo("apple"));
        tasks.saveSnapshot();
        tasks.sort(SortOrder.NAME.getComparator());

        tasks.undo();

        assertEquals("[T][ ] zebra", tasks.get(0).toString());
        assertEquals("[T][ ] apple", tasks.get(1).toString());
    }

    @Test
    public void get_indexOutsideTheList_assertionFails() {
        // Callers must check the number first; an index out of range here is a
        // bug, and the assertion reports it where it happens.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(AssertionError.class, () -> tasks.get(1));
        assertThrows(AssertionError.class, () -> tasks.remove(-1));
    }

    @Test
    public void undo_afterAdd_taskGone() throws Exception {
        TaskList tasks = new TaskList();
        tasks.saveSnapshot();
        tasks.add(new Todo("read book"));

        tasks.undo();

        assertEquals(0, tasks.size());
    }

    @Test
    public void undo_afterRemove_taskBackInItsPlace() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        tasks.saveSnapshot();
        tasks.remove(0);

        tasks.undo();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] buy milk", tasks.get(1).toString());
    }

    @Test
    public void undo_afterChangingATaskInPlace_changeReversed() throws Exception {
        // The list still holds the same tasks after a mark or a tag, so undo
        // only works if the remembered state holds copies rather than the
        // tasks themselves.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.saveSnapshot();
        tasks.get(0).markAsDone();
        tasks.get(0).addTag("fun");

        tasks.undo();

        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void undo_severalChanges_walkedBackOneAtATime() throws Exception {
        TaskList tasks = new TaskList();
        tasks.saveSnapshot();
        tasks.add(new Todo("read book"));
        tasks.saveSnapshot();
        tasks.add(new Todo("buy milk"));

        tasks.undo();
        assertEquals(1, tasks.size());

        tasks.undo();
        assertEquals(0, tasks.size());
    }

    @Test
    public void undo_nothingRemembered_rejected() {
        assertThrows(SallmanException.class, () -> new TaskList().undo());
    }

    @Test
    public void saveSnapshot_pastTheLimit_oldestForgotten() {
        // The cap keeps a long session from holding a copy of the list for
        // every change ever made.
        TaskList tasks = new TaskList();
        for (int i = 0; i < 30; i++) {
            tasks.saveSnapshot();
            tasks.add(new Todo("task " + i));
        }

        assertEquals(20, tasks.getUndoCount());
    }

    @Test
    public void remove_fromTheMiddle_laterTasksMoveUp() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        Task removed = tasks.remove(1);

        // The gap must close, or later task numbers would point at the wrong
        // tasks for the rest of the session.
        assertEquals("[T][ ] second", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] third", tasks.get(1).toString());
    }

    @Test
    public void remove_theOnlyTask_listBecomesEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));

        tasks.remove(0);

        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_fromExistingTasks_contentsCopied() {
        // Loading from disk hands over a plain list; the TaskList must hold its
        // own copy so later changes to either do not affect the other.
        List<Task> loaded = new ArrayList<>(List.of(new Todo("read book")));
        TaskList tasks = new TaskList(loaded);

        loaded.clear();

        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_modified_originalUnaffected() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.asList().clear();

        assertEquals(1, tasks.size());
    }

    @Test
    public void find_keywordInDescription_matchingTasksReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));
        tasks.add(new Todo("buy bread"));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", matches.get(1).toString());
    }

    @Test
    public void find_differentCase_stillMatched() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        assertEquals(1, tasks.find("book").size());
        assertEquals(1, tasks.find("BOOK").size());
    }

    @Test
    public void find_keywordMatchesNothing_emptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.find("zzz").isEmpty());
    }

    @Test
    public void tasksOn_deadlineDueThatDay_matched() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDate.of(2019, 10, 15)));

        assertEquals(1, tasks.tasksOn(LocalDate.of(2019, 10, 15)).size());
        assertTrue(tasks.tasksOn(LocalDate.of(2019, 10, 16)).isEmpty());
    }

    @Test
    public void tasksOn_dateInsideAnEvent_matched() {
        // An event covers every day it spans, so a date in the middle counts.
        // This is the case most likely to be got wrong.
        TaskList tasks = new TaskList();
        tasks.add(new Event("conference", LocalDate.of(2019, 10, 14),
                LocalDate.of(2019, 10, 17)));

        assertEquals(1, tasks.tasksOn(LocalDate.of(2019, 10, 14)).size());
        assertEquals(1, tasks.tasksOn(LocalDate.of(2019, 10, 15)).size());
        assertEquals(1, tasks.tasksOn(LocalDate.of(2019, 10, 17)).size());
        assertTrue(tasks.tasksOn(LocalDate.of(2019, 10, 13)).isEmpty());
        assertTrue(tasks.tasksOn(LocalDate.of(2019, 10, 18)).isEmpty());
    }

    @Test
    public void tasksOn_todoWithNoDate_neverMatched() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.tasksOn(LocalDate.of(2019, 10, 15)).isEmpty());
    }

    @Test
    public void tasksOn_severalMatches_returnedInListOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("first", LocalDate.of(2019, 10, 15)));
        tasks.add(new Todo("no date"));
        tasks.add(new Event("second", LocalDate.of(2019, 10, 15),
                LocalDate.of(2019, 10, 15)));

        List<Task> matches = tasks.tasksOn(LocalDate.of(2019, 10, 15));

        assertEquals(2, matches.size());
        assertEquals("[D][ ] first (by: Oct 15 2019)", matches.get(0).toString());
        assertEquals("[E][ ] second (from: Oct 15 2019 to: Oct 15 2019)",
                matches.get(1).toString());
    }
}
