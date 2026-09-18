package tally.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/** Tests that a TaskList holds tasks in order and keeps its own copy of them. */
public class TaskListTest {
    private static final LocalDate SEP_9 = LocalDate.of(2026, 9, 9);

    @Test
    public void findFreeRun_emptyTally_offersTheDayAskedFrom() {
        assertEquals(Optional.of(SEP_9), new TaskList().findFreeRun(1, SEP_9));
    }

    @Test
    public void findPositionOf_aTaskTheTallyAlreadyHolds_namesWhereItIs() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("read book"));
        assertEquals(1, tasks.findPositionOf(new Todo("read book")).orElse(-1));
        assertTrue(tasks.findPositionOf(new Todo("buy bread")).isEmpty());
    }

    @Test
    public void findFreeRun_searchingFromTheLastWritableDate_offersNoDayBeyondIt() {
        TaskList tasks = new TaskList();
        tasks.add(new Window("busy", Task.LAST_DATE, Task.LAST_DATE));
        // Only one day is left within the form a date can be written in, and it is taken.
        // Searching on would name a day the user could read but could not type back in.
        assertEquals(Optional.empty(), tasks.findFreeRun(1, Task.LAST_DATE));
        assertEquals(Optional.empty(), new TaskList().findFreeRun(2, Task.LAST_DATE));
        assertEquals(Optional.of(Task.LAST_DATE), new TaskList().findFreeRun(1, Task.LAST_DATE));
    }

    @Test
    public void findFreeRun_deadlineAndWindow_skipsEveryDayTheyTakeUp() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("report", LocalDate.of(2026, 9, 10)),
                new Window("certificate", LocalDate.of(2026, 9, 12), LocalDate.of(2026, 9, 14)));
        // Sep 9 free, 10 taken, 11 free, 12-14 taken, so three in a row start on the 15th.
        assertEquals(Optional.of(SEP_9), tasks.findFreeRun(1, SEP_9));
        assertEquals(Optional.of(LocalDate.of(2026, 9, 15)), tasks.findFreeRun(3, SEP_9));
    }

    @Test
    public void findFreeRun_anEvent_takesUpEveryDayItRunsAcross() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("trip", Moment.read("2026-09-09").orElseThrow(),
                Moment.read("2026-09-11").orElseThrow()));
        assertEquals(Optional.of(LocalDate.of(2026, 9, 12)), tasks.findFreeRun(1, SEP_9));
    }

    @Test
    public void findFreeRun_everyDayTakenForAYear_findsNothing() {
        TaskList tasks = new TaskList();
        tasks.add(new Window("busy", SEP_9, SEP_9.plusYears(2)));
        assertEquals(Optional.empty(), tasks.findFreeRun(1, SEP_9));
    }

    @Test
    public void add_thenGet_keepsTheOrderTheyWereAdded() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"));
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] second", tasks.get(1).toString());
    }

    @Test
    public void remove_middleTask_shiftsTheRestUp() {
        TaskList tasks = new TaskList();
        Task middle = new Todo("second");
        tasks.add(new Todo("first"), middle, new Todo("third"));
        tasks.remove(middle);
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] third", tasks.get(1).toString());
    }

    @Test
    public void remove_oneOfTwoAlikeTasks_removesTheOneGiven() {
        // The two tasks read the same, so removing by value could take the wrong one.
        TaskList tasks = new TaskList();
        Task first = new Todo("same");
        Task second = new Todo("same");
        first.markAsDone();
        tasks.add(first, second);
        tasks.remove(first);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] same", tasks.get(0).toString());
    }

    @Test
    public void findPositions_wordInSomeDescriptions_returnsTheirPositions() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"), new Todo("buy bread"), new Todo("return book"));
        assertEquals(List.of(0, 2), tasks.findPositions("book"));
    }

    @Test
    public void findPositions_positionsAreOnTheWholeTally_notAmongTheMatches() {
        // The second match sits at position 2, not at position 1 of the results.
        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy bread"), new Todo("read book"), new Todo("return book"));
        assertEquals(List.of(1, 2), tasks.findPositions("book"));
    }

    @Test
    public void findPositions_differentCase_stillMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        assertEquals(List.of(0), tasks.findPositions("book"));
        assertEquals(List.of(0), tasks.findPositions("BOOK"));
    }

    @Test
    public void findPositions_wordInNoDescription_returnsNothing() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertTrue(tasks.findPositions("umbrella").isEmpty());
        assertTrue(new TaskList().findPositions("book").isEmpty());
    }

    @Test
    public void findPositions_wordInsideALongerWord_stillMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("bookshop"));
        assertEquals(List.of(0), tasks.findPositions("book"));
    }

    @Test
    public void add_severalAtOnce_keepsTheOrderGiven() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"), new Todo("third"));
        assertEquals(3, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] third", tasks.get(2).toString());
    }

    @Test
    public void add_noTasks_leavesTheTallyAlone() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));
        tasks.add();
        assertEquals(1, tasks.size());
    }

    @Test
    public void isEmpty_withAndWithoutTasks_reflectsWhatIsHeld() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        tasks.add(new Todo("something"));
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void constructor_givenList_doesNotShareItWithTheCaller() {
        List<Task> givenTasks = new ArrayList<>();
        givenTasks.add(new Todo("first"));
        TaskList tasks = new TaskList(givenTasks);
        givenTasks.add(new Todo("added behind the tally's back"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void copyAsList_changingTheResult_doesNotChangeTheTally() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.copyAsList().clear();
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_aNullTask_isRefusedWhereItHappensRatherThanLater() {
        // Nothing builds one, so a null here is a programming error. Left to itself it
        // would surface much later, as a save that cannot write the task out.
        assertThrows(AssertionError.class, () -> new TaskList().add((Task) null));
    }

    @Test
    public void findFreeRun_aRunShorterThanADay_isRefused() {
        assertThrows(AssertionError.class, () -> new TaskList().findFreeRun(0, SEP_9));
    }

    @Test
    public void get_aPlaceTheTallyDoesNotHold_isRefused() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));
        assertThrows(AssertionError.class, () -> tasks.get(1));
        assertThrows(AssertionError.class, () -> tasks.get(-1));
    }

    @Test
    public void findPositions_underATurkishLocale_stillMatchesTheSameWay() {
        // Turkish lowercases I to a dotless i, so case folding that follows the machine's
        // own language stops "INDEX" matching "index" there and nowhere else. CI runs on
        // three operating systems and all of them are English, so only this would catch it.
        Locale wasDefault = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            TaskList tasks = new TaskList();
            tasks.add(new Todo("INDEX the notes"));
            assertEquals(List.of(0), tasks.findPositions("index"));
            assertEquals(List.of(0), tasks.findPositions("INDEX"));
        } finally {
            Locale.setDefault(wasDefault);
        }
    }
}
