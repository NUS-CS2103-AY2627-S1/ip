package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import friday.FridayException;

/**
 * Tests task state, date-time conversion, and recurrence behavior.
 */
class TaskModelTest {

    @Test
    void todo_markAndUnmark_updatesStatusAndSaveText() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileString());
        todo.markAsNotDone();
        assertEquals("T | 0 | read book", todo.toFileString());
        assertTrue(todo.containsKeyword("book"));
        assertFalse(todo.containsKeyword("Book"));
    }

    @Test
    void deadline_validDate_exposesReminderAndFormattedText() throws FridayException {
        Deadline deadline = new Deadline("submit report", "2/12/2025 1800");

        assertEquals(LocalDateTime.of(2025, 12, 2, 18, 0), deadline.getReminderDateTime().orElseThrow());
        assertEquals("D | 0 | submit report | 2025-12-02 1800", deadline.toFileString());
        assertTrue(deadline.toString().contains("Dec 02 2025 6:00pm"));
    }

    @Test
    void event_validAndInvalidRanges_formatsOrRejectsRange() throws FridayException {
        Event event = new Event("meeting", "2/12/2025 1800", "2/12/2025 1900");

        assertEquals("E | 0 | meeting | 2025-12-02 1800 | 2025-12-02 1900", event.toFileString());
        assertTrue(event.toString().contains("from: Dec 02 2025 6:00pm to: Dec 02 2025 7:00pm"));
        assertThrows(FridayException.class, () -> new Event("meeting", "2/12/2025 1900", "2/12/2025 1800"));
    }

    @Test
    void taskDateTime_supportedFormats_parseAndFormatConsistently() throws FridayException {
        LocalDateTime inputFormat = TaskDateTime.parse("2/12/2025 1800");
        LocalDateTime fileFormat = TaskDateTime.parse("2025-12-02 1800");

        assertEquals(inputFormat, fileFormat);
        assertEquals("2025-12-02 1800", TaskDateTime.formatForFile(inputFormat));
        assertEquals("Dec 02 2025 6:00pm", TaskDateTime.formatForDisplay(inputFormat));
        assertThrows(FridayException.class, () -> TaskDateTime.parse("30/2/2025 1800"));
    }

    @Test
    void repeatFrequency_validAndInvalidText_parsesOrThrows() throws FridayException {
        assertEquals(RepeatFrequency.DAILY, RepeatFrequency.parse(" DAILY "));
        assertEquals(RepeatFrequency.WEEKLY, RepeatFrequency.parse("weekly"));
        assertEquals(RepeatFrequency.BIWEEKLY, RepeatFrequency.parse("biweekly"));
        assertEquals(RepeatFrequency.MONTHLY, RepeatFrequency.parse("monthly"));
        assertEquals(RepeatFrequency.YEARLY, RepeatFrequency.parse("yearly"));
        assertEquals("monthly", RepeatFrequency.MONTHLY.getText());
        assertThrows(FridayException.class, () -> RepeatFrequency.parse("hourly"));
    }
}
