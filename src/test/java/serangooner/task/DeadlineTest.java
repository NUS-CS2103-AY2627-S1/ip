package serangooner.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import serangooner.SerangoonerException;

public class DeadlineTest {
    @Test
    public void constructor_unreadableDate_exceptionThrown() {
        assertThrows(SerangoonerException.class, () -> new Deadline("submit ip", "next week"));
    }

    @Test
    public void toSaveFormat_bareDate_writesTheDateBackTheWayItWasTyped() {
        assertEquals("D | 0 | submit ip | 2026-09-01",
                new Deadline("submit ip", "2026-09-01").toSaveFormat());
    }

    @Test
    public void toSaveFormat_dateWithTime_keepsTheTimeOfDay() {
        assertEquals("D | 0 | submit ip | 2026-09-01 1800",
                new Deadline("submit ip", "2026-09-01 1800").toSaveFormat());
    }

    @Test
    public void toString_bareDate_showsTheDueDate() {
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026)",
                new Deadline("submit ip", "2026-09-01").toString());
    }

    @Test
    public void toString_dateWithTime_showsTheTimeOfDay() {
        assertEquals("[D][ ] submit ip (by: 01 Sep 2026, 6:00PM)",
                new Deadline("submit ip", "2026-09-01 1800").toString());
    }

    @Test
    public void isWithin_rangeContainingTheDueDate_returnsTrue() {
        Task deadline = new Deadline("submit ip", "2026-09-03");
        assertTrue(deadline.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_rangeStartingOnTheDueDate_countsAsWithin() {
        Task deadline = new Deadline("submit ip", "2026-09-01");
        assertTrue(deadline.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_rangeEndingOnTheDueDate_countsAsWithin() {
        Task deadline = new Deadline("submit ip", "2026-09-05");
        assertTrue(deadline.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_dueDateWithATimeOfDay_ignoresTheTime() {
        Task deadline = new Deadline("submit ip", "2026-09-05 2359");
        assertTrue(deadline.isWithin(LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_rangeEndingBeforeTheDueDate_returnsFalse() {
        Task deadline = new Deadline("submit ip", "2026-09-06");
        assertFalse(deadline.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void isWithin_rangeStartingAfterTheDueDate_returnsFalse() {
        Task deadline = new Deadline("submit ip", "2026-08-31");
        assertFalse(deadline.isWithin(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5)));
    }

    @Test
    public void parseSaveFields_validLine_returnsDeadline() {
        Optional<Task> task = Deadline.parseSaveFields(
                new String[] {"D", "1", "submit ip", "2026-09-01 1800"});
        assertTrue(task.isPresent());
        assertTrue(task.get().isDone());
        assertEquals("[D][✓] submit ip (by: 01 Sep 2026, 6:00PM)", task.get().toString());
    }

    @Test
    public void parseSaveFields_unreadableDate_returnsNothing() {
        assertTrue(Deadline.parseSaveFields(
                new String[] {"D", "0", "submit ip", "not-a-date"}).isEmpty());
    }

    @Test
    public void parseSaveFields_missingDateField_returnsNothing() {
        assertTrue(Deadline.parseSaveFields(new String[] {"D", "0", "submit ip"}).isEmpty());
    }

    @Test
    public void getTypeName_always_namesTheKindForMessages() {
        assertEquals("deadline", new Deadline("submit ip", "2026-09-01").getTypeName());
    }

    @Test
    public void isWithin_singleDayRangeOnTheDueDate_returnsTrue() {
        Task deadline = new Deadline("submit ip", "2026-09-03");
        assertTrue(deadline.isWithin(LocalDate.of(2026, 9, 3), LocalDate.of(2026, 9, 3)));
    }

    @Test
    public void parseSaveFields_blankDescription_returnsNothing() {
        assertTrue(Deadline.parseSaveFields(
                new String[] {"D", "0", "  ", "2026-09-01"}).isEmpty());
    }

    @Test
    public void isDuplicateOf_sameDescriptionAndDate_returnsTrue() {
        assertTrue(new Deadline("Submit IP", "2026-09-01")
                .isDuplicateOf(new Deadline("submit ip", "2026-09-01")));
    }

    @Test
    public void isDuplicateOf_differentDate_returnsFalse() {
        assertFalse(new Deadline("submit ip", "2026-09-01")
                .isDuplicateOf(new Deadline("submit ip", "2026-09-02")));
    }

    @Test
    public void isDuplicateOf_bareDateAgainstMidnight_returnsFalse() {
        // The two are shown to the user differently, so they are not the same deadline.
        assertFalse(new Deadline("submit ip", "2026-09-01")
                .isDuplicateOf(new Deadline("submit ip", "2026-09-01 0000")));
    }

    @Test
    public void isDuplicateOf_todoWithTheSameDescription_returnsFalse() {
        Deadline deadline = new Deadline("submit ip", "2026-09-01");
        Todo todo = new Todo("submit ip");
        assertFalse(deadline.isDuplicateOf(todo));
        assertFalse(todo.isDuplicateOf(deadline));
    }

    @Test
    public void isDuplicateOf_differentDescriptionSameDate_returnsFalse() {
        assertFalse(new Deadline("submit ip", "2026-09-01")
                .isDuplicateOf(new Deadline("submit tp", "2026-09-01")));
    }
}
