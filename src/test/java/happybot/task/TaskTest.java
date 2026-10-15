package happybot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void constructor_description_taskNotDone() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
    }

    @Test
    public void constructor_repeatedWhitespace_descriptionNormalized() {
        Task task = new Task("  read\t\tbook  ");

        assertEquals("read book", task.getDescription());
    }

    @Test
    public void constructor_blankOrStorageBreakingDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Task("   "));
        assertThrows(IllegalArgumentException.class, () -> new Task("read | book"));
    }

    @Test
    public void constructor_nullDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    public void markAsDone_notDoneTask_taskBecomesDone() {
        Task task = new Task("read book");

        task.markAsDone(LocalDate.of(2026, 9, 16));

        assertTrue(task.isDone());
        assertEquals(LocalDate.of(2026, 9, 16), task.getCompletionDate());
    }

    @Test
    public void markAsDone_alreadyDoneTask_taskStaysDone() {
        Task task = new Task("read book");
        task.markAsDone(LocalDate.of(2026, 9, 16));

        task.markAsDone(LocalDate.of(2026, 9, 17));

        assertTrue(task.isDone());
        assertEquals(LocalDate.of(2026, 9, 16), task.getCompletionDate());
    }

    @Test
    public void unmarkAsDone_doneTask_taskBecomesNotDone() {
        Task task = new Task("read book");
        task.markAsDone(LocalDate.of(2026, 9, 16));

        task.unmarkAsDone();

        assertFalse(task.isDone());
        assertNull(task.getCompletionDate());
    }

    @Test
    public void unmarkAsDone_notDoneTask_taskStaysNotDone() {
        Task task = new Task("read book");

        task.unmarkAsDone();

        assertFalse(task.isDone());
        assertNull(task.getCompletionDate());
    }

    @Test
    public void markAsDone_noDate_completionDateUnknown() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertNull(task.getCompletionDate());
    }

    @Test
    public void toString_notDoneTask_statusBoxEmpty() {
        assertEquals("[ ] read book", new Task("read book").toString());
    }

    @Test
    public void toString_doneTask_statusBoxHoldsCross() {
        Task task = new Task("read book");
        task.markAsDone(LocalDate.of(2026, 9, 16));

        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void hasSameDetails_sameTypeAndDescriptionIgnoringCase_tasksMatch() {
        Task task = new Task("read book");
        Task matchingTask = new Task("READ BOOK");
        matchingTask.markAsDone(LocalDate.of(2026, 9, 16));

        assertTrue(task.hasSameDetails(matchingTask));
    }

    @Test
    public void hasSameDetails_differentTypeDescriptionOrNull_tasksDoNotMatch() {
        Task task = new Task("read book");

        assertFalse(task.hasSameDetails(new Task("return book")));
        assertFalse(task.hasSameDetails(new ToDo("read book")));
        assertFalse(task.hasSameDetails(null));
    }
}
