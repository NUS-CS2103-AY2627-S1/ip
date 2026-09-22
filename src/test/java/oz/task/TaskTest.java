package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the base {@link Task} class.
 */
public class TaskTest {

    @Test
    public void toString_unmarkedAndMarked_correctStringRepresentation() {
        Task task = new Task("generic task");
        assertEquals("[ ] generic task", task.toString());

        task.markAsDone();
        assertEquals("[X] generic task", task.toString());
    }

    @Test
    public void occursOn_anyDate_returnsFalse() {
        Task task = new Task("generic task");
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 17)));
    }

    @Test
    public void containsKeyword_nullOrBlankKeyword_returnsFalse() {
        Task task = new Task("read book");
        assertFalse(task.containsKeyword(null));
        assertFalse(task.containsKeyword(""));
        assertFalse(task.containsKeyword("   "));
    }

    @Test
    public void containsKeyword_matchingAndNonMatchingKeyword_correctBoolean() {
        Task task = new Task("Read CS2103 textbook");
        assertTrue(task.containsKeyword("read"));
        assertTrue(task.containsKeyword("CS2103"));
        assertTrue(task.containsKeyword("textbook"));
        assertFalse(task.containsKeyword("physics"));
    }

    @Test
    public void containsKeyword_turkishDefaultLocale_remainsCaseInsensitive() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            Task task = new Task("FILE report");

            assertTrue(task.containsKeyword("file"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void markAndUnmark_stateTransitions_correctStatusAndIcon() {
        Task task = new Task("submit report");
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void toFileFormat_unmarkedAndMarked_correctFormat() {
        Task task = new Task("submit report");
        assertEquals("0 | submit report", task.toFileFormat());

        task.markAsDone();
        assertEquals("1 | submit report", task.toFileFormat());
    }
}
