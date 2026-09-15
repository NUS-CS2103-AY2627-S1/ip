package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    private TaskList emptyList() {
        return new TaskList(new ArrayList<>());
    }

    private TaskList listOf(Task... tasks) {
        ArrayList<Task> contents = new ArrayList<>();
        for (Task task : tasks) {
            contents.add(task);
        }
        return new TaskList(contents);
    }

    private TaskList sampleList() {
        return listOf(
                new ToDo("read book", true),
                new Deadline("return book", true, LocalDateTime.of(2019, 6, 6, 0, 0)),
                new ToDo("join sports club", false));
    }

    private Event eventFrom(String name, int startHour, int endHour, boolean isDone) {
        return new Event(name, isDone,
                LocalDateTime.of(2026, 1, 5, startHour, 0),
                LocalDateTime.of(2026, 1, 5, endHour, 0));
    }

    // ---------------------------------------------------------------- //
    // Basic list operations                                             //
    // ---------------------------------------------------------------- //

    @Test
    public void isEmpty_newlyCreatedList_isTrue() {
        assertTrue(emptyList().isEmpty());
        assertEquals(0, emptyList().size());
    }

    @Test
    public void add_aTask_growsTheListAndKeepsOrder() {
        TaskList tasks = emptyList();
        tasks.add(new ToDo("first", false));
        tasks.add(new ToDo("second", false));

        assertFalse(tasks.isEmpty());
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getTaskName());
        assertEquals("second", tasks.get(1).getTaskName());
    }

    @Test
    public void get_anIndex_returnsThatSameTaskObject() {
        ToDo task = new ToDo("read book", false);

        assertSame(task, listOf(task).get(0));
    }

    @Test
    public void remove_anIndex_returnsTheRemovedTaskAndShrinksTheList() {
        TaskList tasks = listOf(
                new ToDo("first", false),
                new ToDo("second", false),
                new ToDo("third", false));

        Task removed = tasks.remove(1);

        assertEquals("second", removed.getTaskName());
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).getTaskName());
        assertEquals("third", tasks.get(1).getTaskName());
    }

    @Test
    public void remove_theOnlyTask_leavesAnEmptyList() {
        TaskList tasks = listOf(new ToDo("only one", false));
        tasks.remove(0);

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void mark_anIndex_setsThatTaskDoneAndReturnsIt() {
        TaskList tasks = listOf(new ToDo("read book", false));

        Task marked = tasks.mark(0);

        assertTrue(marked.isDone());
        assertTrue(tasks.get(0).isDone());
        assertSame(tasks.get(0), marked);
    }

    @Test
    public void unmark_anIndex_clearsThatTaskAndReturnsIt() {
        TaskList tasks = listOf(new ToDo("read book", true));

        Task unmarked = tasks.unmark(0);

        assertFalse(unmarked.isDone());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void mark_oneTask_leavesTheOthersAlone() {
        TaskList tasks = listOf(new ToDo("first", false), new ToDo("second", false));

        tasks.mark(0);

        assertFalse(tasks.get(1).isDone());
    }

    // ---------------------------------------------------------------- //
    // findClashes                                                       //
    // ---------------------------------------------------------------- //

    @Test
    public void findClashes_severalOverlappingEvents_returnsAllInListOrder() {
        TaskList tasks = listOf(
                eventFrom("standup", 9, 10, false),
                eventFrom("interview", 11, 12, false),
                eventFrom("review", 9, 13, false));

        ArrayList<Event> clashes = tasks.findClashes(eventFrom("new", 9, 12, false));

        assertEquals(3, clashes.size());
        assertEquals("standup", clashes.get(0).getTaskName());
        assertEquals("interview", clashes.get(1).getTaskName());
        assertEquals("review", clashes.get(2).getTaskName());
    }

    @Test
    public void findClashes_noOverlap_returnsEmptyList() {
        TaskList tasks = listOf(eventFrom("standup", 9, 10, false));

        assertTrue(tasks.findClashes(eventFrom("new", 14, 15, false)).isEmpty());
    }

    @Test
    public void findClashes_touchingAtABoundary_returnsEmptyList() {
        TaskList tasks = listOf(eventFrom("standup", 9, 10, false));

        assertTrue(tasks.findClashes(eventFrom("new", 10, 11, false)).isEmpty());
    }

    @Test
    public void findClashes_overlappingDoneEvent_isSkipped() {
        TaskList tasks = listOf(eventFrom("finished", 9, 12, true));

        assertTrue(tasks.findClashes(eventFrom("new", 10, 11, false)).isEmpty());
    }

    @Test
    public void findClashes_deadlinesAndTodos_areIgnored() {
        TaskList tasks = listOf(
                new ToDo("read book", false),
                new Deadline("report", false, LocalDateTime.of(2026, 1, 5, 10, 0)));

        assertTrue(tasks.findClashes(eventFrom("new", 9, 12, false)).isEmpty());
    }

    @Test
    public void findClashes_zeroLengthCandidate_matchesNothing() {
        TaskList tasks = listOf(eventFrom("standup", 9, 12, false));

        assertTrue(tasks.findClashes(eventFrom("instant", 10, 10, false)).isEmpty());
    }

    @Test
    public void findClashes_zeroLengthEventInTheList_isNeverAMatch() {
        TaskList tasks = listOf(eventFrom("instant", 10, 10, false));

        assertTrue(tasks.findClashes(eventFrom("new", 9, 12, false)).isEmpty());
    }

    @Test
    public void findClashes_emptyList_returnsEmptyList() {
        assertTrue(emptyList().findClashes(eventFrom("new", 9, 12, false)).isEmpty());
    }

    // ---------------------------------------------------------------- //
    // countClashingPairs                                                //
    // ---------------------------------------------------------------- //

    @Test
    public void countClashingPairs_emptyList_returnsZero() {
        assertEquals(0, emptyList().countClashingPairs());
    }

    @Test
    public void countClashingPairs_oneEventOnly_returnsZero() {
        assertEquals(0, listOf(eventFrom("a", 9, 10, false)).countClashingPairs());
    }

    @Test
    public void countClashingPairs_noClashes_returnsZero() {
        TaskList tasks = listOf(eventFrom("a", 9, 10, false), eventFrom("b", 10, 11, false));

        assertEquals(0, tasks.countClashingPairs());
    }

    @Test
    public void countClashingPairs_onePair_returnsOne() {
        TaskList tasks = listOf(
                eventFrom("a", 9, 11, false),
                eventFrom("b", 10, 12, false),
                eventFrom("c", 14, 15, false));

        assertEquals(1, tasks.countClashingPairs());
    }

    @Test
    public void countClashingPairs_threeMutuallyOverlapping_returnsThree() {
        TaskList tasks = listOf(
                eventFrom("a", 9, 12, false),
                eventFrom("b", 10, 13, false),
                eventFrom("c", 11, 14, false));

        assertEquals(3, tasks.countClashingPairs());
    }

    @Test
    public void countClashingPairs_doneEventsExcluded_returnsZero() {
        TaskList tasks = listOf(eventFrom("a", 9, 12, true), eventFrom("b", 10, 13, false));

        assertEquals(0, tasks.countClashingPairs());
    }

    @Test
    public void countClashingPairs_countsEachPairOnce() {
        // Two events overlapping each other are one pair, not two.
        TaskList tasks = listOf(eventFrom("a", 9, 12, false), eventFrom("b", 10, 13, false));

        assertEquals(1, tasks.countClashingPairs());
    }

    // ---------------------------------------------------------------- //
    // find                                                              //
    // ---------------------------------------------------------------- //

    @Test
    public void find_keywordInSeveralTasks_matchesInOriginalOrder() {
        TaskList matches = sampleList().find("book");

        assertEquals(2, matches.size());
        assertEquals("[T][X] read book", matches.get(0).toString());
        assertEquals("[D][X] return book (by: Jun 06 2019)", matches.get(1).toString());
    }

    @Test
    public void find_differentCase_stillMatches() {
        assertEquals(2, sampleList().find("BOOK").size());
        assertEquals(2, sampleList().find("Book").size());
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        assertTrue(sampleList().find("holiday").isEmpty());
    }

    @Test
    public void find_emptyList_returnsEmptyList() {
        assertTrue(emptyList().find("book").isEmpty());
    }

    @Test
    public void find_partialWord_matchesSubstring() {
        assertEquals(1, sampleList().find("sport").size());
    }

    @Test
    public void find_anyKeyword_leavesOriginalListUntouched() {
        TaskList original = sampleList();
        original.find("book");

        assertEquals(3, original.size());
    }

    @Test
    public void find_theResult_canBeModifiedWithoutTouchingTheOriginal() {
        // The result is handed to a TaskList, which adds to and removes from
        // the list it is given, so it must not be an immutable one.
        TaskList original = sampleList();

        TaskList matches = original.find("book");
        matches.remove(0);

        assertEquals(1, matches.size());
        assertEquals(3, original.size());
    }
}
