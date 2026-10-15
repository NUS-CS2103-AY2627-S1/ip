package thomas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

/**
 * Tests TaskList.
 * The class holds no resources and talks to nothing, so a case is a list built
 * by hand, one call and one assertion. What makes it worth testing is that two
 * numbering schemes meet here, the user counting from 1 and the list from 0, and
 * the conversion between them happens in this class and nowhere else. An
 * off-by-one would mark or delete the wrong task, which is the kind of mistake
 * that survives a run-through where every number falls in the middle.
 * The boundaries are therefore tested on both sides throughout: the first and
 * last task as well as one past each end, and the days an event starts and ends
 * as well as the days around them.
 * The size, get and add methods are one-line delegations and are not tested for
 * their own sake, appearing only as the way the other cases are set up.
 */
public class TaskListTest {

    /** Day used throughout, with the days either side of it for the range tests */
    private static final LocalDate DEC_02 = LocalDate.of(2019, 12, 2);
    private static final LocalDate DEC_03 = LocalDate.of(2019, 12, 3);
    private static final LocalDate DEC_04 = LocalDate.of(2019, 12, 4);

    /**
     * Returns a list holding the given tasks, in the order given.
     * The tasks are taken as arguments rather than added one by one in each
     * test, so that a case reads as the list it is about rather than as the
     * steps that built it.
     *
     * @param tasks Tasks to hold, in list order.
     * @return List holding those tasks.
     */
    private static TaskList listOf(Task... tasks) {
        // Built through the constructor rather than add(), which refuses a duplicate and so would make every test
        // declare the exception; what a duplicate does is tested by the add tests themselves.
        return new TaskList(new ArrayList<>(List.of(tasks)));
    }

    /**
     * Returns how a list's tasks show themselves, in order, for comparing lists.
     *
     * @param list List to read.
     * @return One entry per task, in list order.
     */
    private static List<String> contentsOf(TaskList list) {
        List<String> descriptions = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            descriptions.add(list.get(i).toString());
        }
        return descriptions;
    }

    /**
     * Returns a deadline due at 6pm on the given day.
     *
     * @param description Task text.
     * @param day Day the task is due.
     * @return New deadline.
     */
    private static Task deadlineOn(String description, LocalDate day) {
        return new DeadlineTask(description, day.atTime(18, 0));
    }

    /**
     * Returns an event running from 2pm on one day to 4pm on another.
     *
     * @param description Task text.
     * @param start Day the event starts.
     * @param end Day the event ends.
     * @return New event.
     * @throws ThomasException If the end day falls before the start day.
     */
    private static Task eventFrom(String description, LocalDate start, LocalDate end)
            throws ThomasException {
        return new EventTask(description, start.atTime(14, 0), end.atTime(16, 0));
    }

    // ---- construction ----

    @Test
    public void constructor_noArguments_listIsEmpty() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_existingTasks_holdsThemInOrder() {
        // The constructor taking tasks is how the save file is loaded, so the list must start out holding exactly what
        // it was handed, in the same order.
        ArrayList<Task> loaded = new ArrayList<>();
        loaded.add(new TodoTask("read book"));
        loaded.add(new TodoTask("return book"));

        TaskList list = new TaskList(loaded);

        assertEquals(2, list.size());
        assertEquals(List.of("[T][ ] read book", "[T][ ] return book"), contentsOf(list));
    }

    @Test
    public void add_toEmptyList_appendsTask() throws ThomasException {
        TaskList list = new TaskList();
        Task task = new TodoTask("read book");

        list.add(task);

        assertEquals(1, list.size());
        assertSame(task, list.get(0));
    }

    @Test
    public void add_toNonEmptyList_appendsToTheEnd() throws ThomasException {
        // Tasks are kept in the order they were added, which is the order shown.
        TaskList list = listOf(new TodoTask("read book"));
        Task second = new TodoTask("return book");

        list.add(second);

        assertSame(second, list.get(1));
    }

    @Test
    public void add_taskAlreadyOnTheList_exceptionThrown() {
        // The same task a second time is refused, and the message says where the first one sits, counting from 1.
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        ThomasException e = assertThrows(ThomasException.class, () -> list.add(new TodoTask("return book")));

        assertEquals("Bust my buffers! That wagon is already on my train, at number 2:\n   [T][ ] return book",
                e.getMessage());
    }

    @Test
    public void add_taskAlreadyOnTheList_listUnchanged() {
        TaskList list = listOf(new TodoTask("read book"));

        assertThrows(ThomasException.class, () -> list.add(new TodoTask("read book")));

        assertEquals(List.of("[T][ ] read book"), contentsOf(list));
    }

    @Test
    public void add_duplicateOfADoneTask_exceptionThrown() {
        // Done or not makes no difference to whether it is the same task; the message shows the ticked one.
        Task done = new TodoTask("read book");
        done.markAsDone();
        TaskList list = listOf(done);

        ThomasException e = assertThrows(ThomasException.class, () -> list.add(new TodoTask("read book")));

        assertEquals("Bust my buffers! That wagon is already on my train, at number 1:\n   [T][X] read book",
                e.getMessage());
    }

    @Test
    public void add_sameDescriptionDifferentType_appendsTask() throws ThomasException {
        // A todo and a deadline with the same text are different tasks.
        TaskList list = listOf(new TodoTask("read book"));

        list.add(deadlineOn("read book", DEC_02));

        assertEquals(2, list.size());
    }

    @Test
    public void add_sameDescriptionDifferentDate_appendsTask() throws ThomasException {
        TaskList list = listOf(deadlineOn("read book", DEC_02));

        list.add(deadlineOn("read book", DEC_03));

        assertEquals(2, list.size());
    }

    @Test
    public void add_sameTaskAfterDeletingIt_appendsTask() throws ThomasException {
        // Once the first is gone it is no longer a duplicate.
        TaskList list = listOf(new TodoTask("read book"));
        list.deleteByNumber(1);

        list.add(new TodoTask("read book"));

        assertEquals(1, list.size());
    }

    // ---- getByNumber ----

    @Test
    public void getByNumber_firstTask_returnsTaskAtPositionZero() throws ThomasException {
        // Task 1 is position 0: the conversion this class exists to make.
        Task first = new TodoTask("read book");
        TaskList list = listOf(first, new TodoTask("return book"));

        assertSame(first, list.getByNumber(1));
    }

    @Test
    public void getByNumber_lastTask_returnsTaskAtEnd() throws ThomasException {
        // The last task is numbered size(), not size() - 1.
        Task last = new TodoTask("return book");
        TaskList list = listOf(new TodoTask("read book"), last);

        assertSame(last, list.getByNumber(2));
    }

    @Test
    public void getByNumber_middleTask_returnsThatTask() throws ThomasException {
        Task middle = new TodoTask("return book");
        TaskList list = listOf(new TodoTask("read book"), middle, new TodoTask("buy book"));

        assertSame(middle, list.getByNumber(2));
    }

    @Test
    public void getByNumber_zero_exceptionThrown() {
        // Zero is a position, not a task number, so it must be rejected rather than quietly reaching the first task.
        TaskList list = listOf(new TodoTask("read book"));

        ThomasException e = assertThrows(ThomasException.class, () -> list.getByNumber(0));
        assertEquals("There's no wagon 0 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void getByNumber_negativeNumber_exceptionThrown() {
        TaskList list = listOf(new TodoTask("read book"));

        ThomasException e = assertThrows(ThomasException.class, () -> list.getByNumber(-1));
        assertEquals("There's no wagon -1 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void getByNumber_onePastEnd_exceptionThrown() {
        // One past the end: the number of tasks is the largest number that works.
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        ThomasException e = assertThrows(ThomasException.class, () -> list.getByNumber(3));
        assertEquals("There's no wagon 3 on my train! I'm only pulling 2 wagon(s).", e.getMessage());
    }

    @Test
    public void getByNumber_emptyList_exceptionThrown() {
        // The range is checked against the tasks that exist, so every number fails on an empty list rather than any of
        // them reaching the underlying list.
        TaskList list = new TaskList();

        ThomasException e = assertThrows(ThomasException.class, () -> list.getByNumber(1));
        assertEquals("There's no wagon 1 on my train! I'm only pulling 0 wagon(s).", e.getMessage());
    }

    @Test
    public void getByNumber_validNumber_listUnchanged() throws ThomasException {
        // Reading a task must not remove it, unlike deleteByNumber below.
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        list.getByNumber(1);

        assertEquals(List.of("[T][ ] read book", "[T][ ] return book"), contentsOf(list));
    }

    // ---- deleteByNumber ----

    @Test
    public void deleteByNumber_firstTask_returnsThatTask() throws ThomasException {
        Task first = new TodoTask("read book");
        TaskList list = listOf(first, new TodoTask("return book"));

        assertSame(first, list.deleteByNumber(1));
    }

    @Test
    public void deleteByNumber_validNumber_listShrinks() throws ThomasException {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        list.deleteByNumber(1);

        assertEquals(1, list.size());
    }

    @Test
    public void deleteByNumber_middleTask_laterTasksShiftDown() throws ThomasException {
        // Removing closes the gap, so the tasks after the deleted one shift down and the numbering never develops
        // holes. Deleting the middle of three is what shows this: task 3 must become task 2, not stay at 3.
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"),
                new TodoTask("buy book"));

        list.deleteByNumber(2);

        assertEquals(List.of("[T][ ] read book", "[T][ ] buy book"), contentsOf(list));
        assertEquals("[T][ ] buy book", list.getByNumber(2).toString());
    }

    @Test
    public void deleteByNumber_lastTask_earlierTasksKeepTheirNumbers() throws ThomasException {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        list.deleteByNumber(2);

        assertEquals(List.of("[T][ ] read book"), contentsOf(list));
        assertEquals("[T][ ] read book", list.getByNumber(1).toString());
    }

    @Test
    public void deleteByNumber_everyTask_leavesEmptyList() throws ThomasException {
        // Deleting task 1 repeatedly empties the list, one task at a time.
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        list.deleteByNumber(1);
        list.deleteByNumber(1);

        assertEquals(0, list.size());
    }

    @Test
    public void deleteByNumber_onePastEnd_exceptionThrown() {
        TaskList list = listOf(new TodoTask("read book"));

        ThomasException e = assertThrows(ThomasException.class, () -> list.deleteByNumber(2));
        assertEquals("There's no wagon 2 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void deleteByNumber_zero_exceptionThrown() {
        TaskList list = listOf(new TodoTask("read book"));

        ThomasException e = assertThrows(ThomasException.class, () -> list.deleteByNumber(0));
        assertEquals("There's no wagon 0 on my train! I'm only pulling 1 wagon(s).", e.getMessage());
    }

    @Test
    public void deleteByNumber_emptyList_exceptionThrown() {
        TaskList list = new TaskList();

        ThomasException e = assertThrows(ThomasException.class, () -> list.deleteByNumber(1));
        assertEquals("There's no wagon 1 on my train! I'm only pulling 0 wagon(s).", e.getMessage());
    }

    @Test
    public void deleteByNumber_numberOutOfRange_listUnchanged() {
        // A rejected delete must leave the list exactly as it was.
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        assertThrows(ThomasException.class, () -> list.deleteByNumber(5));

        assertEquals(List.of("[T][ ] read book", "[T][ ] return book"), contentsOf(list));
    }

    // ---- insertByNumber ----

    @Test
    public void insertByNumber_middleNumber_taskTakesThatNumber() {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("pay fine"));
        Task returning = new TodoTask("buy milk");

        list.insertByNumber(2, returning);

        assertSame(returning, list.get(1));
    }

    @Test
    public void insertByNumber_middleNumber_laterTasksShiftUp() {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("pay fine"));

        list.insertByNumber(2, new TodoTask("buy milk"));

        // Shifted, not overwritten: the task that held number 2 is still there,
        // now at 3, so undoing a delete costs nothing that was not deleted.
        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk", "[T][ ] pay fine"),
                contentsOf(list));
    }

    @Test
    public void insertByNumber_firstNumber_taskGoesToTheFront() {
        TaskList list = listOf(new TodoTask("buy milk"));

        list.insertByNumber(1, new TodoTask("read book"));

        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk"), contentsOf(list));
    }

    @Test
    public void insertByNumber_onePastTheLastTask_taskGoesOnTheEnd() {
        TaskList list = listOf(new TodoTask("read book"));

        // One past the end is allowed here, where getByNumber and deleteByNumber
        // refuse it, because putting back the task that used to be last means
        // reaching a number the list no longer has.
        list.insertByNumber(2, new TodoTask("buy milk"));

        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk"), contentsOf(list));
    }

    @Test
    public void insertByNumber_intoEmptyList_holdsThatTaskAlone() {
        TaskList list = listOf();

        list.insertByNumber(1, new TodoTask("read book"));

        assertEquals(List.of("[T][ ] read book"), contentsOf(list));
    }

    @Test
    public void insertByNumber_doneTask_taskIsStillDone() {
        TaskList list = listOf(new TodoTask("read book"));
        Task returning = new TodoTask("buy milk");
        returning.markAsDone();

        list.insertByNumber(2, returning);

        assertTrue(list.get(1).isDone());
    }

    @Test
    public void insertByNumber_afterDeleteByNumber_listIsAsItWas() throws ThomasException {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("buy milk"),
                new TodoTask("pay fine"));

        Task removed = list.deleteByNumber(2);
        list.insertByNumber(2, removed);

        // The pair is what undo relies on, so it is tested as a pair: deleting a
        // task and putting it back at the number it held leaves no trace.
        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk", "[T][ ] pay fine"),
                contentsOf(list));
    }

    // ---- positionsOn ----

    @Test
    public void positionsOn_emptyList_returnsNoPositions() {
        assertTrue(new TaskList().positionsOn(DEC_02).isEmpty());
    }

    @Test
    public void positionsOn_todosOnly_returnsNoPositions() {
        // A todo carries no date, so it falls on no day at all.
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("return book"));

        assertTrue(list.positionsOn(DEC_02).isEmpty());
    }

    @Test
    public void positionsOn_deadlineDueThatDay_returnsItsPosition() {
        TaskList list = listOf(deadlineOn("return book", DEC_02));

        assertEquals(List.of(0), list.positionsOn(DEC_02));
    }

    @Test
    public void positionsOn_deadlineDueAnotherDay_returnsNoPositions() {
        TaskList list = listOf(deadlineOn("return book", DEC_02));

        assertTrue(list.positionsOn(DEC_03).isEmpty());
    }

    @Test
    public void positionsOn_deadlineAtMidnightAndLastMinute_bothMatch() {
        // The time of day is dropped, so a deadline at any hour counts as on that day.
        TaskList list = listOf(
                new DeadlineTask("first thing", DEC_02.atTime(0, 0)),
                new DeadlineTask("last thing", DEC_02.atTime(23, 59)));

        assertEquals(List.of(0, 1), list.positionsOn(DEC_02));
    }

    @Test
    public void positionsOn_eventFirstDay_returnsItsPosition() throws ThomasException {
        // An event covers every day it spans, so the day it starts is one of them.
        TaskList list = listOf(eventFrom("project meeting", DEC_02, DEC_04));

        assertEquals(List.of(0), list.positionsOn(DEC_02));
    }

    @Test
    public void positionsOn_eventMiddleDay_returnsItsPosition() throws ThomasException {
        // The middle of an event matches, which the wrong range test also gets right.
        TaskList list = listOf(eventFrom("project meeting", DEC_02, DEC_04));

        assertEquals(List.of(0), list.positionsOn(DEC_03));
    }

    @Test
    public void positionsOn_eventLastDay_returnsItsPosition() throws ThomasException {
        // Both ends count, so the last day matches too -- the case a strict range test drops.
        TaskList list = listOf(eventFrom("project meeting", DEC_02, DEC_04));

        assertEquals(List.of(0), list.positionsOn(DEC_04));
    }

    @Test
    public void positionsOn_dayBeforeEventStarts_returnsNoPositions() throws ThomasException {
        TaskList list = listOf(eventFrom("project meeting", DEC_03, DEC_04));

        assertTrue(list.positionsOn(DEC_02).isEmpty());
    }

    @Test
    public void positionsOn_dayAfterEventEnds_returnsNoPositions() throws ThomasException {
        TaskList list = listOf(eventFrom("project meeting", DEC_02, DEC_03));

        assertTrue(list.positionsOn(DEC_04).isEmpty());
    }

    @Test
    public void positionsOn_severalMatches_returnsWholeListPositions() throws ThomasException {
        // The positions returned are positions in the whole list, not the count of matches so far. Non-matching tasks
        // are placed first and in between, so numbering the matches 1, 2, 3 would give [0, 1] and be caught here.
        TaskList list = listOf(
                new TodoTask("read book"),
                deadlineOn("return book", DEC_02),
                deadlineOn("pay fine", DEC_04),
                eventFrom("project meeting", DEC_02, DEC_02));

        assertEquals(List.of(1, 3), list.positionsOn(DEC_02));
    }

    @Test
    public void positionsOn_matchesOutOfDateOrder_returnsPositionsInListOrder() {
        // Matches come back in list order, whatever order the dates fall in.
        TaskList list = listOf(
                new DeadlineTask("evening", DEC_02.atTime(20, 0)),
                new DeadlineTask("morning", DEC_02.atTime(8, 0)));

        assertEquals(List.of(0, 1), list.positionsOn(DEC_02));
    }

    @Test
    public void positionsOn_anyDay_listUnchanged() {
        // Asking which tasks fall on a day must not change the list.
        TaskList list = listOf(new TodoTask("read book"), deadlineOn("return book", DEC_02));

        list.positionsOn(DEC_02);

        assertEquals(List.of("[T][ ] read book",
                "[D][ ] return book (by: Dec 02 2019, 6:00 PM)"), contentsOf(list));
    }

    // ---- positionsMatching ----

    @Test
    public void positionsMatching_emptyList_returnsNoPositions() {
        assertTrue(new TaskList().positionsMatching("book").isEmpty());
    }

    @Test
    public void positionsMatching_noTaskContainsKeyword_returnsNoPositions() {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("buy milk"));

        assertTrue(list.positionsMatching("homework").isEmpty());
    }

    @Test
    public void positionsMatching_oneTaskContainsKeyword_returnsItsPosition() {
        TaskList list = listOf(new TodoTask("buy milk"), new TodoTask("read book"));

        assertEquals(List.of(1), list.positionsMatching("book"));
    }

    /**
     * The positions returned are positions in the whole list, not the count of
     * matches so far, as for positionsOn. Non-matching tasks are placed first
     * and in between, so numbering the matches 1, 2, 3 would give [0, 1] and be
     * caught here.
     */
    @Test
    public void positionsMatching_severalMatches_returnsWholeListPositions() {
        TaskList list = listOf(
                new TodoTask("buy milk"),
                new TodoTask("read book"),
                new TodoTask("call mum"),
                deadlineOn("return book", DEC_02));

        assertEquals(List.of(1, 3), list.positionsMatching("book"));
    }

    /** Every task type is searched, not only to-dos. */
    @Test
    public void positionsMatching_everyTaskType_allSearched() throws ThomasException {
        TaskList list = listOf(
                new TodoTask("book a room"),
                deadlineOn("return book", DEC_02),
                eventFrom("book club", DEC_02, DEC_03));

        assertEquals(List.of(0, 1, 2), list.positionsMatching("book"));
    }

    /** A keyword inside a word still matches, since the test is on substrings. */
    @Test
    public void positionsMatching_keywordInsideWord_matches() {
        TaskList list = listOf(new TodoTask("read bookmark"));

        assertEquals(List.of(0), list.positionsMatching("book"));
    }

    /** Matching is case sensitive, as command keywords are. */
    @Test
    public void positionsMatching_differentCase_doesNotMatch() {
        TaskList list = listOf(new TodoTask("read Book"));

        assertTrue(list.positionsMatching("book").isEmpty());
    }

    /** Matches come back in list order, whatever order they were added in. */
    @Test
    public void positionsMatching_severalMatches_returnsPositionsInListOrder() {
        TaskList list = listOf(new TodoTask("book two"), new TodoTask("book one"));

        assertEquals(List.of(0, 1), list.positionsMatching("book"));
    }

    /** Searching must not change the list. */
    @Test
    public void positionsMatching_anyKeyword_listUnchanged() {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("buy milk"));

        list.positionsMatching("book");

        assertEquals(List.of("[T][ ] read book", "[T][ ] buy milk"), contentsOf(list));
    }

    // The cases below pin the assertions rather than the behavior. They pass
    // only with assertions enabled, which the Gradle test task does by
    // default; run without -ea they would fail, which is the point of having
    // them -- an assertion nothing exercises is a comment that compiles.

    /** A position below the first task is a caller's mistake, not the user's. */
    @Test
    public void get_positionBeforeTheFirstTask_assertionThrown() {
        TaskList list = listOf(new TodoTask("read book"));

        AssertionError error = assertThrows(AssertionError.class, () -> list.get(-1));

        assertEquals("Position -1 is outside a list of 1 task(s)", error.getMessage());
    }

    /** The position one past the last task is the end the off-by-one lands on. */
    @Test
    public void get_positionPastTheLastTask_assertionThrown() {
        TaskList list = listOf(new TodoTask("read book"));

        AssertionError error = assertThrows(AssertionError.class, () -> list.get(1));

        assertEquals("Position 1 is outside a list of 1 task(s)", error.getMessage());
    }

    /** Both ends of the valid range are accepted, so the assertion is not too tight. */
    @Test
    public void get_firstAndLastPosition_returnsTheTask() {
        TaskList list = listOf(new TodoTask("read book"), new TodoTask("buy milk"));

        assertEquals("[T][ ] read book", list.get(0).toString());
        assertEquals("[T][ ] buy milk", list.get(list.size() - 1).toString());
    }

    /** An empty list has no valid position at all, not even 0. */
    @Test
    public void get_anyPositionInAnEmptyList_assertionThrown() {
        TaskList list = new TaskList();

        assertThrows(AssertionError.class, () -> list.get(0));
    }

    /** A null task would only surface later, when the list is printed. */
    @Test
    public void add_nullTask_assertionThrown() {
        TaskList list = new TaskList();

        AssertionError error = assertThrows(AssertionError.class, () -> list.add(null));

        assertEquals("Cannot add a null task to the list", error.getMessage());
    }

    /** Loading hands its list straight to this constructor, so a null must not pass. */
    @Test
    public void constructor_nullArrayList_assertionThrown() {
        AssertionError error = assertThrows(AssertionError.class, () -> new TaskList(null));

        assertEquals("Cannot build a task list over a null ArrayList", error.getMessage());
    }
}
