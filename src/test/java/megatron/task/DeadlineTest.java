package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests date parsing, storage data, status, and display behavior of {@link Deadline}.
 */
class DeadlineTest {
    @Test
    void constructor_textDateTime_parsesAndDisplaysDeadline() {
        Deadline deadline = new Deadline("submit report", "2026-08-06 1400");

        assertEquals("submit report", deadline.getDescription());
        assertFalse(deadline.isDone());
        assertEquals("D", deadline.getTypeCode());
        assertEquals("2026-08-06T14:00", deadline.getExtra());
        assertEquals("[ ] submit report (by: 06 Aug 26, 1400hrs)", deadline.toString());
        assertEquals("[D][ ] submit report (by: 06 Aug 26, 1400hrs)", deadline.displayText());
    }

    @Test
    void constructor_dateOnly_defaultsToMidnight() {
        Deadline deadline = new Deadline("submit report", "2026-08-06");

        assertEquals("2026-08-06T00:00", deadline.getExtra());
        assertEquals("[D][ ] submit report (by: 06 Aug 26, 0000hrs)", deadline.displayText());
    }

    @Test
    void constructor_localDateTime_preservesStorageValue() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 6, 14, 5));

        assertEquals("2026-08-06T14:05", deadline.getExtra());
        assertEquals("[D][ ] submit report (by: 06 Aug 26, 1405hrs)", deadline.displayText());
    }

    @Test
    void markAsDone_notDoneDeadline_updatesStatusAndDisplay() {
        Deadline deadline = new Deadline("submit report", "2026-08-06 1400");

        deadline.markAsDone();

        assertTrue(deadline.isDone());
        assertEquals("[D][X] submit report (by: 06 Aug 26, 1400hrs)", deadline.displayText());
    }

    @Test
    void constructor_invalidDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit report", "31/2/2026"));
    }

    @Test
    void constructor_nullDateText_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("submit report", (String) null));
    }
}
