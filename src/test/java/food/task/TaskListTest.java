package food.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import food.exception.FoodException;
import food.exception.FoodInputException;

/**
 * Tests {@link TaskList#find(String)} and the {@link TaskList#snapshot()} /
 * {@link TaskList#restore(List)} pair behind "undo".
 *
 * <p>The search is worth testing on its own because it is the one part of the feature with real
 * logic: matching is case-insensitive and looks only at the description, never at the dates.
 * Snapshot and restore are tested together because a snapshot is only useful if restoring it
 * really brings the earlier list back, including after the tasks themselves were changed.
 */
public class TaskListTest {

    /** Builds a list holding one matching todo, one matching deadline, and one non-match. */
    private TaskList buildSampleList() throws FoodInputException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("todo read book"));
        tasks.add(new Deadline("deadline return book /by 2026-09-07"));
        tasks.add(new Todo("todo buy milk"));
        return tasks;
    }

    @Test
    public void find_keywordInSeveralTasks_allMatchesReturnedInListOrder() throws FoodInputException {
        List<Task> matches = this.buildSampleList().find("book");

        assertEquals(2, matches.size());
        assertEquals("[T] [] read book", matches.get(0).toString());
        assertEquals("[D] [] return book (by: 7/9/2026)", matches.get(1).toString());
    }

    @Test
    public void find_keywordDifferentCase_matchesAnyway() throws FoodInputException {
        assertEquals(2, this.buildSampleList().find("BOOK").size());
    }

    @Test
    public void find_keywordIsPartOfAWord_matchesAsSubstring() throws FoodInputException {
        // "boo" is not a whole word in "read book", but a substring search still finds it.
        assertEquals(2, this.buildSampleList().find("boo").size());
    }

    @Test
    public void find_keywordAbsent_emptyListReturned() throws FoodInputException {
        assertTrue(this.buildSampleList().find("homework").isEmpty());
    }

    @Test
    public void find_keywordMatchesOnlyADate_emptyListReturned() throws FoodInputException {
        // Only descriptions are searched, so the deadline's 2026 date is not a match.
        assertTrue(this.buildSampleList().find("2026").isEmpty());
    }

    @Test
    public void find_emptyList_emptyListReturned() {
        assertTrue(new TaskList().find("book").isEmpty());
    }

    // --- add ----------------------------------------------------------------

    @Test
    public void add_identicalTask_exceptionThrown() throws FoodInputException {
        TaskList tasks = this.buildSampleList();

        assertThrows(FoodInputException.class, () -> tasks.add(new Todo("todo read book")));
        assertEquals(3, tasks.size());
    }

    @Test
    public void add_identicalTaskAlreadyDone_exceptionThrown() throws FoodInputException {
        // Being done does not make it a different task.
        TaskList tasks = this.buildSampleList();
        tasks.get(0, "mark").markComplete();

        assertThrows(FoodInputException.class, () -> tasks.add(new Todo("todo read book")));
    }

    @Test
    public void add_sameDescriptionDifferentKind_added() throws FoodInputException {
        // A todo and a deadline with the same words are different tasks.
        TaskList tasks = this.buildSampleList();
        tasks.add(new Deadline("deadline read book /by 2026-10-01"));
        assertEquals(4, tasks.size());
    }

    // --- snapshot / restore -------------------------------------------------

    @Test
    public void snapshot_sampleList_oneSaveFormatLinePerTask() throws FoodInputException {
        List<String> snapshot = this.buildSampleList().snapshot();

        assertEquals(List.of(
                "0 | todo read book",
                "0 | deadline return book /by 2026-09-07",
                "0 | todo buy milk"), snapshot);
    }

    @Test
    public void restore_afterAddAndDelete_originalListBack() throws FoodException {
        TaskList tasks = this.buildSampleList();
        List<String> before = tasks.snapshot();
        tasks.add(new Todo("todo walk dog"));
        tasks.delete(0, "delete");

        tasks.restore(before);

        assertEquals(3, tasks.size());
        assertEquals("[T] [] read book", tasks.get(0, "mark").toString());
        assertEquals("[T] [] buy milk", tasks.get(2, "mark").toString());
    }

    @Test
    public void restore_afterMarkingTask_taskIsUnmarkedAgain() throws FoodException {
        // The important case: the snapshot must not share Task objects with the live list,
        // or marking the live task would mark the "backup" too.
        TaskList tasks = this.buildSampleList();
        List<String> before = tasks.snapshot();
        tasks.get(0, "mark").markComplete();

        tasks.restore(before);

        assertEquals("[T] [] read book", tasks.get(0, "mark").toString());
    }

    @Test
    public void restore_completedTaskInSnapshot_staysCompleted() throws FoodException {
        TaskList tasks = this.buildSampleList();
        tasks.get(1, "mark").markComplete();
        List<String> before = tasks.snapshot();
        tasks.get(1, "unmark").markIncomplete();

        tasks.restore(before);

        assertEquals("[D] [X] return book (by: 7/9/2026)", tasks.get(1, "mark").toString());
    }

    @Test
    public void restore_emptySnapshot_listEmptied() throws FoodException {
        TaskList tasks = this.buildSampleList();

        tasks.restore(List.of());

        assertEquals(0, tasks.size());
    }
}
