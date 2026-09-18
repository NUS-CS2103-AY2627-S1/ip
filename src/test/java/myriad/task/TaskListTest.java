package myriad.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import myriad.MyriadException;

/**
 * Tests for {@link TaskList}: the index bookkeeping every mark, unmark and
 * delete depends on, the date filtering behind the "show" command, and the
 * keyword filtering behind the "find" command.
 */
public class TaskListTest {

    /** Builds a TaskDateTime, unwrapping the checked exception for brevity. */
    private static TaskDateTime at(String raw) {
        try {
            return TaskDateTime.parse(raw);
        } catch (MyriadException e) {
            throw new AssertionError("test fixture should parse: " + raw, e);
        }
    }

    /** Builds a list of ToDos with the given descriptions, in the order given. */
    private static TaskList taskListOf(String... descriptions) {
        TaskList tasks = new TaskList();
        for (String description : descriptions) {
            tasks.add(new ToDo(description));
        }
        return tasks;
    }

    /** A list of three distinct ToDos, for the index-shuffling tests. */
    private static TaskList threeToDos() {
        return taskListOf("first", "second", "third");
    }

    // ---------------------------------------------------------------
    // Construction
    // ---------------------------------------------------------------

    @Test
    public void constructor_noArguments_emptyList() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_initialTasks_holdsThoseTasks() {
        Task task = new ToDo("read book");
        TaskList tasks = new TaskList(List.of(task));
        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    public void constructor_sourceListMutatedAfterwards_taskListUnaffected() {
        // The constructor copies, so the caller's list -- for example the one
        // Storage.load built -- can be changed later without silently
        // changing this TaskList behind its own add/remove methods.
        ArrayList<Task> source = new ArrayList<>();
        source.add(new ToDo("first"));
        TaskList tasks = new TaskList(source);

        source.add(new ToDo("added after construction"));
        source.clear();

        assertEquals(1, tasks.size());
    }

    @Test
    public void constructor_emptyInitialList_emptyTaskList() {
        assertEquals(0, new TaskList(List.of()).size());
    }

    // ---------------------------------------------------------------
    // add / get / size
    // ---------------------------------------------------------------

    @Test
    public void add_severalTasks_appendedInOrder() {
        TaskList tasks = threeToDos();
        assertEquals(3, tasks.size());
        assertEquals("first", describe(tasks.get(0)));
        assertEquals("third", describe(tasks.get(2)));
    }

    @Test
    public void get_negativeIndex_assertionErrorThrown() {
        // A valid index is an asserted precondition, not a user error; Gradle
        // runs the tests with assertions enabled (-ea), so the assert fires.
        assertThrows(AssertionError.class, () -> threeToDos().get(-1));
    }

    @Test
    public void get_indexEqualToSize_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> threeToDos().get(3));
    }

    // ---------------------------------------------------------------
    // isEmpty
    // ---------------------------------------------------------------

    @Test
    public void isEmpty_newList_true() {
        assertTrue(new TaskList().isEmpty());
    }

    @Test
    public void isEmpty_listWithTasks_false() {
        assertFalse(threeToDos().isEmpty());
    }

    @Test
    public void isEmpty_lastTaskRemoved_true() {
        TaskList tasks = taskListOf("only task");
        tasks.remove(0);
        assertTrue(tasks.isEmpty());
    }

    // ---------------------------------------------------------------
    // isValidIndex -- the boundary check every task-number command uses
    // ---------------------------------------------------------------

    @Test
    public void isValidIndex_emptyList_alwaysFalse() {
        TaskList tasks = new TaskList();
        assertFalse(tasks.isValidIndex(0));
        assertFalse(tasks.isValidIndex(-1));
        assertFalse(tasks.isValidIndex(1));
    }

    @Test
    public void isValidIndex_firstAndLastIndex_true() {
        TaskList tasks = threeToDos();
        assertTrue(tasks.isValidIndex(0));
        assertTrue(tasks.isValidIndex(tasks.size() - 1));
    }

    @Test
    public void isValidIndex_indexEqualToSize_false() {
        // The off-by-one that a 1-based task number would cause if it were
        // used as an index without subtracting 1.
        TaskList tasks = threeToDos();
        assertFalse(tasks.isValidIndex(tasks.size()));
    }

    @Test
    public void isValidIndex_negativeIndex_false() {
        assertFalse(threeToDos().isValidIndex(-1));
    }

    @Test
    public void isValidIndex_extremeValues_false() {
        TaskList tasks = threeToDos();
        assertFalse(tasks.isValidIndex(Integer.MIN_VALUE));
        assertFalse(tasks.isValidIndex(Integer.MAX_VALUE));
    }

    // ---------------------------------------------------------------
    // markDone / markNotDone
    // ---------------------------------------------------------------

    @Test
    public void markDone_validIndex_taskShownAsDone() {
        TaskList tasks = threeToDos();
        tasks.markDone(1);
        assertTrue(tasks.get(1).toString().startsWith("[T][X]"));
    }

    @Test
    public void markDone_onlyNamedTaskAffected() {
        TaskList tasks = threeToDos();
        tasks.markDone(1);
        assertTrue(tasks.get(0).toString().startsWith("[T][ ]"));
        assertTrue(tasks.get(2).toString().startsWith("[T][ ]"));
    }

    @Test
    public void markDone_alreadyDone_stillDone() {
        // Marking twice is not an error; the second mark is a no-op.
        TaskList tasks = threeToDos();
        tasks.markDone(0);
        tasks.markDone(0);
        assertTrue(tasks.get(0).toString().startsWith("[T][X]"));
    }

    @Test
    public void markNotDone_previouslyDone_taskShownAsNotDone() {
        TaskList tasks = threeToDos();
        tasks.markDone(0);
        tasks.markNotDone(0);
        assertTrue(tasks.get(0).toString().startsWith("[T][ ]"));
    }

    @Test
    public void markNotDone_alreadyNotDone_stillNotDone() {
        TaskList tasks = threeToDos();
        tasks.markNotDone(0);
        assertTrue(tasks.get(0).toString().startsWith("[T][ ]"));
    }

    @Test
    public void markDone_outOfRangeIndex_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> threeToDos().markDone(3));
    }

    @Test
    public void markNotDone_outOfRangeIndex_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> threeToDos().markNotDone(-1));
    }

    // ---------------------------------------------------------------
    // remove
    // ---------------------------------------------------------------

    @Test
    public void remove_validIndex_removedTaskReturned() {
        TaskList tasks = threeToDos();
        assertEquals("second", describe(tasks.remove(1)));
    }

    @Test
    public void remove_validIndex_sizeDecreases() {
        TaskList tasks = threeToDos();
        tasks.remove(1);
        assertEquals(2, tasks.size());
    }

    @Test
    public void remove_firstTask_laterTasksShiftDown() {
        // The shift is what makes a stale task number refer to the wrong
        // task, so it is worth stating explicitly.
        TaskList tasks = threeToDos();
        tasks.remove(0);
        assertEquals("second", describe(tasks.get(0)));
        assertEquals("third", describe(tasks.get(1)));
    }

    @Test
    public void remove_lastTask_remainingTasksUnchanged() {
        TaskList tasks = threeToDos();
        tasks.remove(2);
        assertEquals("first", describe(tasks.get(0)));
        assertEquals("second", describe(tasks.get(1)));
    }

    @Test
    public void remove_everyTask_listBecomesEmpty() {
        TaskList tasks = threeToDos();
        tasks.remove(0);
        tasks.remove(0);
        tasks.remove(0);
        assertEquals(0, tasks.size());
        assertFalse(tasks.isValidIndex(0));
    }

    @Test
    public void remove_outOfRangeIndex_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> threeToDos().remove(3));
    }

    @Test
    public void remove_outOfRangeIndex_listUnchanged() {
        TaskList tasks = threeToDos();
        assertThrows(AssertionError.class, () -> tasks.remove(3));
        assertEquals(3, tasks.size());
    }

    // ---------------------------------------------------------------
    // asList
    // ---------------------------------------------------------------

    @Test
    public void asList_returnsEveryTaskInOrder() {
        TaskList tasks = threeToDos();
        List<Task> listed = tasks.asList();
        assertEquals(3, listed.size());
        assertSame(tasks.get(0), listed.get(0));
    }

    @Test
    public void asList_mutationAttempted_exceptionThrownAndTaskListUnchanged() {
        // Tasks can only be changed through TaskList's own methods, so the
        // view it hands out refuses every change.
        TaskList tasks = threeToDos();
        List<Task> listed = tasks.asList();

        assertThrows(UnsupportedOperationException.class, listed::clear);
        assertThrows(UnsupportedOperationException.class, () -> listed.add(new ToDo("sneaky")));
        assertThrows(UnsupportedOperationException.class, () -> listed.remove(0));
        assertEquals(3, tasks.size());
    }

    @Test
    public void asList_taskAddedAfterViewTaken_viewShowsIt() {
        // The view is live rather than a snapshot.
        TaskList tasks = threeToDos();
        List<Task> listed = tasks.asList();

        tasks.add(new ToDo("fourth"));

        assertEquals(4, listed.size());
    }

    @Test
    public void asList_emptyList_emptyResult() {
        assertTrue(new TaskList().asList().isEmpty());
    }

    // ---------------------------------------------------------------
    // getTasksOccurringOn -- the filtering behind "show"
    // ---------------------------------------------------------------

    @Test
    public void getTasksOccurringOn_toDoInList_neverMatches() {
        // A ToDo has no date at all, so it can never occur on a given day.
        TaskList tasks = taskListOf("read book");
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-02")).size());
    }

    @Test
    public void getTasksOccurringOn_dateOnlyQuery_matchesTimedDeadlineSameDay() {
        // A date-only query spans the whole day, so it catches a deadline at
        // any time on that day.
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-02 1800")));
        assertEquals(1, tasks.getTasksOccurringOn(at("2019-12-02")).size());
    }

    @Test
    public void getTasksOccurringOn_dateOnlyQuery_doesNotMatchAdjacentDay() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-02 1800")));
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-01")).size());
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-03")).size());
    }

    @Test
    public void getTasksOccurringOn_timedQuery_matchesDateOnlyDeadlineSameDay() {
        // The reverse pairing: a deadline with no time spans the whole day,
        // so any time on that day falls inside it.
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-02")));
        assertEquals(1, tasks.getTasksOccurringOn(at("2019-12-02 0900")).size());
    }

    @Test
    public void getTasksOccurringOn_timedQuery_matchesDeadlineAtExactSameInstant() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-02 1800")));
        assertEquals(1, tasks.getTasksOccurringOn(at("2019-12-02 1800")).size());
    }

    @Test
    public void getTasksOccurringOn_timedQuery_doesNotMatchDeadlineAtDifferentTime() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-02 1800")));
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-02 1759")).size());
    }

    @Test
    public void getTasksOccurringOn_queryAtEventStart_matches() {
        // The event span is a closed interval, so both endpoints count.
        TaskList tasks = new TaskList();
        tasks.add(new Event("party", at("2019-12-02 1400"), at("2019-12-02 1600")));
        assertEquals(1, tasks.getTasksOccurringOn(at("2019-12-02 1400")).size());
    }

    @Test
    public void getTasksOccurringOn_queryAtEventEnd_matches() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("party", at("2019-12-02 1400"), at("2019-12-02 1600")));
        assertEquals(1, tasks.getTasksOccurringOn(at("2019-12-02 1600")).size());
    }

    @Test
    public void getTasksOccurringOn_queryInsideEvent_matches() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("party", at("2019-12-02 1400"), at("2019-12-02 1600")));
        assertEquals(1, tasks.getTasksOccurringOn(at("2019-12-02 1500")).size());
    }

    @Test
    public void getTasksOccurringOn_queryJustBeforeEventStart_doesNotMatch() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("party", at("2019-12-02 1400"), at("2019-12-02 1600")));
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-02 1359")).size());
    }

    @Test
    public void getTasksOccurringOn_queryJustAfterEventEnd_doesNotMatch() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("party", at("2019-12-02 1400"), at("2019-12-02 1600")));
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-02 1601")).size());
    }

    @Test
    public void getTasksOccurringOn_queryOnMiddleDayOfMultiDayEvent_matches() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("conference", at("2019-12-01"), at("2019-12-05")));
        assertEquals(1, tasks.getTasksOccurringOn(at("2019-12-03")).size());
    }

    @Test
    public void getTasksOccurringOn_eventEndingBeforeItStarts_neverMatches() {
        // The Parser and Storage reject an inverted event before building it
        // (see Event.checkTimesInOrder), but the constructor itself does not.
        // This records what the overlap check does with one built directly:
        // it fails for every query, so "show" could never find the task.
        TaskList tasks = new TaskList();
        tasks.add(new Event("impossible", at("2019-12-05"), at("2019-12-01")));
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-01")).size());
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-03")).size());
        assertEquals(0, tasks.getTasksOccurringOn(at("2019-12-05")).size());
    }

    @Test
    public void getTasksOccurringOn_mixedList_returnsOnlyMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("early homework", at("2019-12-02 0900")));
        tasks.add(new ToDo("read book"));
        tasks.add(new Event("party", at("2019-12-05 1400"), at("2019-12-05 1600")));
        tasks.add(new Deadline("late homework", at("2019-12-02 1800")));

        List<Task> matches = tasks.getTasksOccurringOn(at("2019-12-02"));
        assertEquals(2, matches.size());
        assertEquals("early homework", describe(matches.get(0)));
        assertEquals("late homework", describe(matches.get(1)));
    }

    @Test
    public void getTasksOccurringOn_noMatches_emptyListReturned() {
        TaskList tasks = threeToDos();
        assertTrue(tasks.getTasksOccurringOn(at("2019-12-02")).isEmpty());
    }

    @Test
    public void getTasksOccurringOn_mutationAttempted_exceptionThrownAndTaskListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-02")));
        List<Task> matches = tasks.getTasksOccurringOn(at("2019-12-02"));

        assertThrows(UnsupportedOperationException.class, matches::clear);
        assertEquals(1, tasks.size());
    }

    // ---------------------------------------------------------------
    // getTasksMatching (the "find" command)
    // ---------------------------------------------------------------

    @Test
    public void getTasksMatching_keywordInsideDescription_matches() {
        TaskList tasks = taskListOf("read book");

        assertEquals(1, tasks.getTasksMatching("book").size());
    }

    @Test
    public void getTasksMatching_keywordInDifferentCase_matches() {
        // Matching ignores case in both directions, so neither the typed
        // keyword nor the stored description has to be lower case.
        TaskList tasks = taskListOf("Read Book");

        assertEquals(1, tasks.getTasksMatching("book").size());
        assertEquals(1, tasks.getTasksMatching("BOOK").size());
    }

    @Test
    public void getTasksMatching_partialWord_matches() {
        // Plain substring matching, not whole-word matching.
        TaskList tasks = taskListOf("bookkeeping");

        assertEquals(1, tasks.getTasksMatching("ook").size());
    }

    @Test
    public void getTasksMatching_mixedList_returnsOnlyMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("join sports club"));
        tasks.add(new Deadline("return book", at("2019-06-06")));

        List<Task> matches = tasks.getTasksMatching("book");
        assertEquals(2, matches.size());
        assertEquals("read book", describe(matches.get(0)));
        assertEquals("return book", describe(matches.get(1)));
    }

    @Test
    public void getTasksMatching_keywordAppearsOnlyInDate_doesNotMatch() {
        // Only the description is searched, so a deadline is not found by the
        // year or month text that appears in its displayed date.
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", at("2019-06-06")));

        assertTrue(tasks.getTasksMatching("2019").isEmpty());
        assertTrue(tasks.getTasksMatching("Jun").isEmpty());
    }

    @Test
    public void getTasksMatching_keywordIsTypeMarker_doesNotMatch() {
        // "[T]" and "[X]" belong to the display format, not the description.
        TaskList tasks = taskListOf("read book");

        assertTrue(tasks.getTasksMatching("[T]").isEmpty());
    }

    @Test
    public void getTasksMatching_noMatches_emptyListReturned() {
        TaskList tasks = threeToDos();
        assertTrue(tasks.getTasksMatching("book").isEmpty());
    }

    @Test
    public void getTasksMatching_emptyList_emptyListReturned() {
        assertTrue(new TaskList().getTasksMatching("book").isEmpty());
    }

    @Test
    public void getTasksMatching_mutationAttempted_exceptionThrownAndTaskListUnchanged() {
        TaskList tasks = taskListOf("read book");
        List<Task> matches = tasks.getTasksMatching("book");

        assertThrows(UnsupportedOperationException.class, matches::clear);
        assertEquals(1, tasks.size());
    }

    @Test
    public void statsQueries_mutationAttempted_exceptionThrown() {
        // The three queries behind "stats" hand out read-only lists too.
        TaskList tasks = taskListOf("read book");
        List<List<Task>> results = List.of(
                tasks.getTasksOfType(TaskType.TODO),
                tasks.getTasksDueWithin(LocalDate.parse("2019-12-02"), 7),
                tasks.getOldestUndone(5));

        for (List<Task> result : results) {
            assertThrows(UnsupportedOperationException.class, () -> result.add(new ToDo("sneaky")));
        }
        assertEquals(1, tasks.size());
    }

    @Test
    public void getTasksMatching_matchedTask_isTheSameTaskObject() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("read book");
        tasks.add(task);

        assertSame(task, tasks.getTasksMatching("book").get(0));
    }

    // ---------------------------------------------------------------
    // getTasksOfType -- the per-type breakdown behind "stats"
    // ---------------------------------------------------------------

    @Test
    public void getTasksOfType_mixedList_returnsOnlyMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return book", at("2019-06-06")));
        tasks.add(new ToDo("join sports club"));

        List<Task> matches = tasks.getTasksOfType(TaskType.TODO);
        assertEquals(2, matches.size());
        assertEquals("read book", describe(matches.get(0)));
        assertEquals("join sports club", describe(matches.get(1)));
    }

    @Test
    public void getTasksOfType_noMatches_emptyListReturned() {
        TaskList tasks = taskListOf("read book");
        assertTrue(tasks.getTasksOfType(TaskType.DEADLINE).isEmpty());
    }

    @Test
    public void getTasksOfType_emptyList_emptyListReturned() {
        assertTrue(new TaskList().getTasksOfType(TaskType.TODO).isEmpty());
    }

    // ---------------------------------------------------------------
    // getTasksDueWithin -- the due-soon window behind "stats"
    // ---------------------------------------------------------------

    @Test
    public void getTasksDueWithin_emptyList_emptyListReturned() {
        assertTrue(new TaskList().getTasksDueWithin(LocalDate.parse("2019-12-02"), 7).isEmpty());
    }

    @Test
    public void getTasksDueWithin_deadlineOnLastDayOfWindow_matches() {
        // The window runs to the *end* of date.plusDays(days), not its start,
        // so a deadline any time on day 7 still counts.
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-09 2300")));
        assertEquals(1, tasks.getTasksDueWithin(LocalDate.parse("2019-12-02"), 7).size());
    }

    @Test
    public void getTasksDueWithin_deadlineOnDayAfterWindow_doesNotMatch() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("homework", at("2019-12-10")));
        assertTrue(tasks.getTasksDueWithin(LocalDate.parse("2019-12-02"), 7).isEmpty());
    }

    @Test
    public void getTasksDueWithin_eventStartingBeforeWindowButEndingInside_matches() {
        // Overlap, not containment: only part of the event needs to fall in
        // the window for it to count.
        TaskList tasks = new TaskList();
        tasks.add(new Event("conference", at("2019-11-30"), at("2019-12-03")));
        assertEquals(1, tasks.getTasksDueWithin(LocalDate.parse("2019-12-02"), 7).size());
    }

    @Test
    public void getTasksDueWithin_doneTaskStillCounted() {
        // getTasksDueWithin filters by date only; whether a task is done is
        // the caller's business (getOldestUndone is the one that filters on
        // done-ness), so a done deadline in range still matches here.
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("homework", at("2019-12-03"));
        deadline.setDone(true);
        tasks.add(deadline);
        assertEquals(1, tasks.getTasksDueWithin(LocalDate.parse("2019-12-02"), 7).size());
    }

    // ---------------------------------------------------------------
    // getOldestUndone -- the "oldest not done" section behind "stats"
    // ---------------------------------------------------------------

    @Test
    public void getOldestUndone_emptyList_emptyListReturned() {
        assertTrue(new TaskList().getOldestUndone(5).isEmpty());
    }

    @Test
    public void getOldestUndone_doneTaskExcluded() {
        TaskList tasks = threeToDos();
        tasks.markDone(0);
        List<Task> oldest = tasks.getOldestUndone(5);
        assertEquals(2, oldest.size());
        assertEquals("second", describe(oldest.get(0)));
        assertEquals("third", describe(oldest.get(1)));
    }

    @Test
    public void getOldestUndone_moreUndoneThanLimit_onlyFirstLimitReturnedInOrder() {
        TaskList tasks = taskListOf("first", "second", "third", "fourth");
        List<Task> oldest = tasks.getOldestUndone(2);
        assertEquals(2, oldest.size());
        assertEquals("first", describe(oldest.get(0)));
        assertEquals("second", describe(oldest.get(1)));
    }

    @Test
    public void getOldestUndone_fewerUndoneThanLimit_allUndoneReturned() {
        TaskList tasks = threeToDos();
        assertEquals(3, tasks.getOldestUndone(5).size());
    }

    @Test
    public void getOldestUndone_limitZero_emptyListReturned() {
        assertTrue(threeToDos().getOldestUndone(0).isEmpty());
    }

    @Test
    public void getOldestUndone_negativeLimit_assertionFails() {
        // The limit is a constant in code, so a negative one is a bug to
        // surface, not an input to quietly answer with an empty list.
        assertThrows(AssertionError.class, () -> threeToDos().getOldestUndone(-1));
    }

    /**
     * Returns a task's description, recovered from its save format. The Task
     * classes expose no getter, and the save format's last field is the
     * description for the types used here.
     */
    private static String describe(Task task) {
        String[] fields = task.toSaveFormat().split("\\s*\\|\\s*");
        return fields[2];
    }

    // ---------------------------------------------------------------
    // findTaskWithSameDetails
    // ---------------------------------------------------------------

    @Test
    public void findTaskWithSameDetails_emptyList_empty() {
        assertTrue(new TaskList().findTaskWithSameDetails(new ToDo("read book")).isEmpty());
    }

    @Test
    public void findTaskWithSameDetails_matchPresent_existingTaskReturned() {
        TaskList tasks = threeToDos();
        Task existing = tasks.get(1);

        Optional<Task> found = tasks.findTaskWithSameDetails(new ToDo(describe(existing)));

        assertTrue(found.isPresent());
        assertSame(existing, found.get());
    }

    @Test
    public void findTaskWithSameDetails_noMatch_empty() {
        assertTrue(threeToDos().findTaskWithSameDetails(new ToDo("something else")).isEmpty());
    }
}
