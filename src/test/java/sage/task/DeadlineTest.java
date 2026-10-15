package sage.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    @Test
    void parseDateTime_acceptsSupportedFormats() {
        assertEquals(LocalDateTime.of(2025, 1, 2, 9, 30), Deadline.parseDateTime("2/1/2025 0930"));
        assertEquals(LocalDateTime.of(2025, 1, 2, 9, 30), Deadline.parseDateTime("2025-01-02 09:30"));
        assertEquals(LocalDateTime.of(2025, 1, 2, 0, 0), Deadline.parseDateTime("2025-01-02"));
        assertNull(Deadline.parseDateTime("not-a-date"));
    }

    @Test
    void constructorAndToString_handleDateAndTimeFormatting() {
        Deadline timeDeadline = new Deadline("submit report", "2/1/2025 1530");
        assertNotNull(timeDeadline.getBy());
        assertEquals("2/1/2025 1530", timeDeadline.getByText());
        assertTrue(timeDeadline.toString().contains("submit report"));
        assertTrue(timeDeadline.toString().contains("Jan 2 2025, 3:30PM"));

        Deadline dateOnlyDeadline = new Deadline("review notes", "2025-01-02");
        assertEquals(LocalDateTime.of(2025, 1, 2, 0, 0), dateOnlyDeadline.getBy());
        assertTrue(dateOnlyDeadline.toString().contains("Jan 2 2025"));
    }

    @Test
    void constructor_explicitDateTime_retainsIsoStorageTextAndReadableDisplay() {
        LocalDateTime time = LocalDateTime.of(2026, 9, 18, 14, 30);
        Deadline deadline = new Deadline("report", time);

        assertEquals(time, deadline.getBy());
        assertEquals("2026-09-18T14:30:00", deadline.getByText());
        assertEquals("[D][ ] report (by: Sep 18 2026, 2:30PM)", deadline.toString());
    }

    @Test
    void constructor_missingExplicitDateTime_retainsEmptyFallback() {
        Deadline deadline = new Deadline("report", (LocalDateTime) null);

        assertNull(deadline.getBy());
        assertEquals("", deadline.getByText());
        assertEquals("[D][ ] report (by: )", deadline.toString());
    }
}
