package dukey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void toString_newTask_returnsUndoneStatusAndDescription() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void markAsDone_newTask_updatesStatusIcon() {
        Task task = new Task("read book");

        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void markAsDone_withCompletionTime_recordsCompletionTime() {
        Task task = new Task("read book");
        LocalDateTime doneAt = LocalDateTime.of(2026, 9, 16, 14, 30);

        task.markAsDone(doneAt);

        assertTrue(task.isDone());
        assertEquals(doneAt, task.getDoneAt());
    }

    @Test
    public void markAsUndone_doneTask_updatesStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone(LocalDateTime.of(2026, 9, 16, 14, 30));

        task.markAsUndone();

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
        assertFalse(task.isDone());
        assertEquals(null, task.getDoneAt());
    }

    @Test
    public void wasCompletedInCalendarWeek_completedInSameWeek_returnsTrue() {
        Task task = new Task("read book");
        task.markAsDone(LocalDateTime.of(2026, 9, 16, 14, 30));

        assertTrue(task.wasCompletedInCalendarWeek(LocalDate.of(2026, 9, 14)));
        assertTrue(task.wasCompletedInCalendarWeek(LocalDate.of(2026, 9, 20)));
    }

    @Test
    public void wasCompletedInCalendarWeek_completedOutsideSameWeek_returnsFalse() {
        Task task = new Task("read book");
        task.markAsDone(LocalDateTime.of(2026, 9, 13, 23, 59));

        assertFalse(task.wasCompletedInCalendarWeek(LocalDate.of(2026, 9, 14)));
    }

    @Test
    public void wasCompletedInCalendarWeek_completedWithoutTimestamp_returnsFalse() {
        Task task = new Task("read book");
        task.markAsDone();

        assertFalse(task.wasCompletedInCalendarWeek(LocalDate.of(2026, 9, 14)));
    }

    @Test
    public void wasCompletedInCalendarMonth_completedInSameMonth_returnsTrue() {
        Task task = new Task("read book");
        task.markAsDone(LocalDateTime.of(2026, 9, 1, 0, 0));

        assertTrue(task.wasCompletedInCalendarMonth(LocalDate.of(2026, 9, 30)));
    }

    @Test
    public void wasCompletedInCalendarMonth_completedOutsideSameMonth_returnsFalse() {
        Task task = new Task("read book");
        task.markAsDone(LocalDateTime.of(2026, 8, 31, 23, 59));

        assertFalse(task.wasCompletedInCalendarMonth(LocalDate.of(2026, 9, 1)));
    }

    @Test
    public void occursOn_plainTask_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2099, 12, 6)));
    }

    @Test
    public void containsKeyword_matchingKeyword_returnsTrue() {
        Task task = new Task("read book");

        assertTrue(task.containsKeyword("book"));
    }

    @Test
    public void containsKeyword_nonMatchingKeyword_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.containsKeyword("bread"));
    }

    @Test
    public void toFileString_plainTask_returnsEmptyString() {
        Task task = new Task("read book");

        assertEquals("", task.toFileString());
    }
}
