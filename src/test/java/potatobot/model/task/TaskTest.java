package potatobot.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;

/**
 * Verifies task status transitions, formatting and description-only searching.
 */
public class TaskTest {
    @Test
    public void markDoneAndReset_repeatedCalls_statusRemainsConsistent() {
        Task task = new Task("read book");
        assertEquals("read book", task.toString());
        assertEquals(" ", task.getStatusIcon());
        task.markDone();
        task.markDone();
        assertEquals("X", task.getStatusIcon());
        task.markReset();
        task.markReset();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void matchesKeyword_mixedCaseAndSubstrings_matchesDescription() {
        Task task = new Task("Read BOOK tonight");
        assertTrue(task.matchesKeyword("book"));
        assertTrue(task.matchesKeyword("READ BO"));
        assertTrue(task.matchesKeyword(""));
        assertFalse(task.matchesKeyword("movie"));
    }

    @Test
    public void matchesKeyword_turkishDefaultLocale_remainsCaseInsensitive() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(new Task("FINISH").matchesKeyword("finish"));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    public void matchesKeyword_typeAndDateMetadata_doesNotMatch() throws PotatoBotException {
        assertFalse(new Todo("read book").matchesKeyword("Todo"));
        assertFalse(new Deadline("submit report", "2026-08-31").matchesKeyword("Aug"));
        assertFalse(new Event("meeting", "2026-08-31", "2026-09-01").matchesKeyword("2026"));
        assertEquals("read book (Todo)", new Todo("read book").toString());
    }
}
