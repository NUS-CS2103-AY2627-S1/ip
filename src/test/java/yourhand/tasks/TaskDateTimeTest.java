package yourhand.tasks;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests for date-only and date-time task values. */
class TaskDateTimeTest {

    @Test
    public void dateOnlyValue_formatsForStorageAndDisplayWithoutTime() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDate.of(2026, 8, 26));

        assertEquals(LocalDateTime.of(2026, 8, 26, 0, 0), taskDateTime.getValue());
        assertEquals("2026-08-26", taskDateTime.toStorageString());
        assertEquals("Aug 26 2026", taskDateTime.toDisplayString());
    }

    @Test
    public void dateTimeValue_formatsForStorageAndDisplayWithTime() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 26, 18, 5));

        assertEquals(LocalDateTime.of(2026, 8, 26, 18, 5), taskDateTime.getValue());
        assertEquals("2026-08-26T18:05", taskDateTime.toStorageString());
        assertEquals("Aug 26 2026 6:05pm", taskDateTime.toDisplayString());
    }

    @Test
    public void nullDateValues_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskDateTime((LocalDate) null));
        assertThrows(IllegalArgumentException.class, () -> new TaskDateTime((LocalDateTime) null));
    }

    @Test
    public void dateTimeValue_midnightUsesLowercaseMeridiem() {
        TaskDateTime taskDateTime = new TaskDateTime(LocalDateTime.of(2026, 8, 26, 0, 0));

        assertEquals("Aug 26 2026 12:00am", taskDateTime.toDisplayString());
    }
}
