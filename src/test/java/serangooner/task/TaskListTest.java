package serangooner.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import serangooner.SerangoonerException;

public class TaskListTest {
    @Test
    public void constructor_noArguments_startsEmpty() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void constructor_givenTasks_holdsThemInOrder() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
        assertEquals("[T][ ] write essay", tasks.getTasks().get(1).toString());
    }

    @Test
    public void getTasks_always_cannotBeEditedThrough() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.getTasks().add(new Todo("sneak in")));
    }

    @Test
    public void add_task_appendsItAndReturnsIt() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        assertSame(todo, tasks.add(todo));
        assertEquals(1, tasks.size());
        assertSame(todo, tasks.getTasks().get(0));
    }

    @Test
    public void mark_validNumber_marksTaskDone() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.mark(1);
        assertTrue(tasks.getTasks().get(0).isDone());
    }

    @Test
    public void mark_numberOutOfRange_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertThrows(SerangoonerException.class, () -> tasks.mark(2));
        assertThrows(SerangoonerException.class, () -> tasks.mark(0));
    }

    @Test
    public void unmark_markedTask_marksTaskIncomplete() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.mark(1);
        tasks.unmark(1);
        assertFalse(tasks.getTasks().get(0).isDone());
    }

    @Test
    public void delete_validNumber_removesTaskAndReturnsIt() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        assertEquals("[T][ ] read book", tasks.delete(1).toString());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] write essay", tasks.getTasks().get(0).toString());
    }

    @Test
    public void delete_emptyList_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> new TaskList().delete(1));
    }

    @Test
    public void undo_afterDelete_putsTaskBackInPlace() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        tasks.delete(1);
        assertTrue(tasks.undo());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    public void undo_afterAdd_removesTheTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertTrue(tasks.undo());
        assertEquals(0, tasks.size());
    }

    @Test
    public void undo_afterMark_leavesTaskIncomplete() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.mark(1);
        assertTrue(tasks.undo());
        assertFalse(tasks.getTasks().get(0).isDone());
    }

    @Test
    public void undo_nothingDoneYet_returnsFalse() {
        assertFalse(new TaskList().undo());
    }

    @Test
    public void getEntries_always_numbersFromOne() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));

        List<TaskList.Entry> entries = tasks.getEntries();

        assertEquals(2, entries.size());
        assertEquals(1, entries.get(0).number());
        assertEquals(2, entries.get(1).number());
        assertEquals("[T][ ] write essay", entries.get(1).task().toString());
    }

    @Test
    public void getEntriesWithin_singleDate_returnsOnlyMatchingTasks() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit ip", "2026-09-01"),
                new Deadline("submit tp", "2026-09-02")));

        List<TaskList.Entry> entries =
                tasks.getEntriesWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 1));

        assertEquals(1, entries.size());
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026)", entries.get(0).task().toString());
    }

    @Test
    public void getEntriesWithin_range_keepsNumbersFromTheFullList() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit ip", "2026-09-01"),
                new Deadline("submit tp", "2026-09-02")));

        List<TaskList.Entry> entries =
                tasks.getEntriesWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));

        assertEquals(2, entries.size());
        assertEquals(2, entries.get(0).number());
        assertEquals(3, entries.get(1).number());
    }

    @Test
    public void getEntriesWithin_noMatchingTask_returnsNothing() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertTrue(tasks.getEntriesWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 1)).isEmpty());
    }

    @Test
    public void undo_afterUnmark_leavesTaskDoneAgain() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.mark(1);
        tasks.unmark(1);

        assertTrue(tasks.undo());

        assertTrue(tasks.getTasks().get(0).isDone());
    }

    @Test
    public void undo_severalEdits_reversesThemMostRecentFirst() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        tasks.delete(1);

        assertTrue(tasks.undo());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());

        assertTrue(tasks.undo());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    public void undo_everyEditUndone_returnsFalseOnceMore() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.undo());
        assertFalse(tasks.undo());
    }

    @Test
    public void getEntriesWithin_rangeEndingOnTheTaskDate_includesIt() {
        TaskList tasks = new TaskList(List.of(new Deadline("submit ip", "2026-09-05")));
        assertEquals(1, tasks.getEntriesWithin(LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 5)).size());
    }

    @Test
    public void getEntriesWithin_rangeStartingOnTheTaskDate_includesIt() {
        TaskList tasks = new TaskList(List.of(new Deadline("submit ip", "2026-09-01")));
        assertEquals(1, tasks.getEntriesWithin(LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 9, 5)).size());
    }

    @Test
    public void mark_alreadyDoneTask_leavesItDone() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.mark(1);
        tasks.mark(1);
        assertTrue(tasks.getTasks().get(0).isDone());
    }

    @Test
    public void delete_numberOutOfRange_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertThrows(SerangoonerException.class, () -> tasks.delete(2));
        assertThrows(SerangoonerException.class, () -> tasks.delete(0));
    }

    @Test
    public void unmark_numberOutOfRange_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertThrows(SerangoonerException.class, () -> tasks.unmark(2));
    }

    @Test
    public void size_afterAddAndDelete_tracksTheCount() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));
        assertEquals(2, tasks.size());
        tasks.delete(1);
        assertEquals(1, tasks.size());
    }

    @Test
    public void getEntries_emptyList_returnsNothing() {
        assertEquals(List.of(), new TaskList().getEntries());
    }

    @Test
    public void getEntries_always_pairsEachNumberWithItsOwnTask() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("write essay")));
        List<TaskList.Entry> entries = tasks.getEntries();
        assertEquals(2, entries.get(1).number());
        assertSame(tasks.getTasks().get(1), entries.get(1).task());
    }

    @Test
    public void mark_validNumber_returnsTheTaskItMarked() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertSame(tasks.getTasks().get(0), tasks.mark(1));
    }

    @Test
    public void unmark_validNumber_returnsTheTaskItUnmarked() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.mark(1);
        assertSame(tasks.getTasks().get(0), tasks.unmark(1));
    }

    @Test
    public void getEntriesWithin_taskCarryingNoDate_leavesItOut() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertEquals(List.of(),
                tasks.getEntriesWithin(LocalDate.MIN, LocalDate.MAX));
    }

    @Test
    public void undo_afterAFailedEdit_hasNothingToReverse() {
        // A refused edit must leave no undo action behind, or undo would reverse
        // a change that never happened.
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(SerangoonerException.class, () -> tasks.mark(99));
        assertThrows(SerangoonerException.class, () -> tasks.unmark(0));
        assertThrows(SerangoonerException.class, () -> tasks.delete(2));

        assertFalse(tasks.undo());
    }

    @Test
    public void undo_failedEditAfterASuccessfulOne_stillReversesTheSuccessfulOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(SerangoonerException.class, () -> tasks.mark(5));

        assertTrue(tasks.undo());

        assertEquals(0, tasks.size());
    }

    @Test
    public void delete_middleTaskThenUndo_putsItBackAtItsOldPosition() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second"),
                new Todo("third")));
        tasks.delete(2);
        assertEquals("[T][ ] third", tasks.getTasks().get(1).toString());

        assertTrue(tasks.undo());

        assertEquals(3, tasks.size());
        assertEquals("[T][ ] second", tasks.getTasks().get(1).toString());
    }

    @Test
    public void getEntriesMatching_keywordInSomeDescriptions_returnsOnlyThoseTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"),
                new Todo("write essay"), new Todo("return book")));

        List<TaskList.Entry> matches = tasks.getEntriesMatching("book");

        assertEquals(2, matches.size());
        assertEquals("[T][ ] read book", matches.get(0).task().toString());
        assertEquals("[T][ ] return book", matches.get(1).task().toString());
    }

    @Test
    public void getEntriesMatching_always_keepsTheNumbersFromTheFullList() {
        // A number read off the matches has to stay usable with mark and delete.
        TaskList tasks = new TaskList(List.of(new Todo("write essay"),
                new Todo("read book"), new Todo("nap"), new Todo("return book")));

        List<TaskList.Entry> matches = tasks.getEntriesMatching("book");

        assertEquals(2, matches.get(0).number());
        assertEquals(4, matches.get(1).number());
    }

    @Test
    public void getEntriesMatching_keywordInAnotherCase_stillFindsTheTask() {
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));
        assertEquals(1, tasks.getEntriesMatching("book").size());
        assertEquals(1, tasks.getEntriesMatching("BOOK").size());
        assertEquals(1, tasks.getEntriesMatching("bOoK").size());
    }

    @Test
    public void getEntriesMatching_keywordInNoDescription_returnsNothing() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertEquals(List.of(), tasks.getEntriesMatching("essay"));
    }

    @Test
    public void getEntriesMatching_partOfAWord_stillFindsTheTask() {
        TaskList tasks = new TaskList(List.of(new Todo("reading")));
        assertEquals(1, tasks.getEntriesMatching("read").size());
    }

    @Test
    public void getEntriesMatching_everyTaskType_searchesTheDescriptionAlone() {
        // The date of a deadline is not part of its description, so a keyword
        // that only appears in the date must not match.
        TaskList tasks = new TaskList(List.of(new Todo("read book"),
                new Deadline("return book", "2026-09-01"),
                new Event("book fair", "2026-09-03", "2026-09-04")));

        assertEquals(3, tasks.getEntriesMatching("book").size());
        assertEquals(List.of(), tasks.getEntriesMatching("2026"));
    }

    @Test
    public void getEntriesMatching_emptyList_returnsNothing() {
        assertEquals(List.of(), new TaskList().getEntriesMatching("book"));
    }

    @Test
    public void getEntriesMatching_always_leavesTheListUntouched() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.getEntriesMatching("book");
        assertEquals(1, tasks.size());
        assertFalse(tasks.undo());
    }

    @Test
    public void add_duplicateOfAnExistingTask_exceptionThrownNamingIt() {
        TaskList tasks = new TaskList(List.of(new Todo("write essay"), new Todo("read book")));

        SerangoonerException exception = assertThrows(SerangoonerException.class, () ->
                tasks.add(new Todo("Read Book")));

        assertTrue(exception.getMessage().contains("task 2"));
        assertTrue(exception.getMessage().contains("read book"));
    }

    @Test
    public void add_duplicate_leavesTheListAndItsUndoHistoryUntouched() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));

        assertThrows(SerangoonerException.class, () -> tasks.add(new Todo("read book")));

        assertEquals(2, tasks.size());
        // The refused add left nothing to undo, so undo reverses the add before it.
        assertTrue(tasks.undo());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    public void add_sameDescriptionAsAnotherKindOfTask_addsIt() {
        TaskList tasks = new TaskList(List.of(new Todo("submit ip")));
        tasks.add(new Deadline("submit ip", "2026-09-01"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void add_duplicateOfADeletedTask_addsIt() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        tasks.delete(1);
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void constructor_duplicateTasks_keepsBoth() {
        // Tasks loaded from an older save file are taken as they are.
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("read book")));
        assertEquals(2, tasks.size());
    }

    @Test
    public void getEntriesMatching_defaultLocaleTurkish_stillIgnoresCase() {
        // Turkish lower-cases "I" to a dotless "ı", which would stop "INVOICE" matching "invoice".
        TaskList tasks = new TaskList(List.of(new Todo("pay INVOICE")));
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));
        try {
            assertEquals(1, tasks.getEntriesMatching("invoice").size());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void getEntriesMatching_chineseKeyword_findsTheTask() {
        TaskList tasks = new TaskList(List.of(new Todo("买菜"), new Todo("交报告")));
        List<TaskList.Entry> matches = tasks.getEntriesMatching("报告");
        assertEquals(1, matches.size());
        assertEquals(2, matches.get(0).number());
    }
}
