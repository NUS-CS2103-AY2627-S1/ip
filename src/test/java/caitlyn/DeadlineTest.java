package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests deadline construction, accessors, display output, and persistence output. */
class DeadlineTest {
    @Test
    void deadline_secondsAndFractions_preservesVisiblePrecision() {
        Deadline deadline = new Deadline("precise", "2026-09-21T18:00:30.000000001");
        assertEquals("[D][ ] precise (by: Sep 21 2026 6:00:30.000000001 PM)", deadline.toString());
        assertEquals("D | 0 | precise | 2026-09-21T18:00:30.000000001", deadline.toStorageString());
    }

    @Test
    void deadline_dateOnlyInputPreservesDateOnlyFormatting() {
        Deadline deadline = new Deadline("submit report", "2025-03-14");

        assertEquals(LocalDateTime.of(2025, 3, 14, 0, 0), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Mar 14 2025)", deadline.toString());
        assertEquals("D | 0 | submit report | 2025-03-14", deadline.toStorageString());
    }

    @Test
    void deadline_constructorsAcceptDateAndDateTimeValues() {
        Deadline fromDate = new Deadline("renew passport", LocalDate.of(2025, 3, 14));
        Deadline fromDateTime = new Deadline("join call", LocalDateTime.of(2025, 3, 14, 9, 26));

        assertEquals(LocalDateTime.of(2025, 3, 14, 0, 0), fromDate.getBy());
        assertEquals("[D][ ] renew passport (by: Mar 14 2025)", fromDate.toString());
        assertEquals(LocalDateTime.of(2025, 3, 14, 9, 26), fromDateTime.getBy());
        assertEquals("[D][ ] join call (by: Mar 14 2025 9:26 AM)", fromDateTime.toString());
        assertEquals("D | 0 | join call | 2025-03-14T09:26", fromDateTime.toStorageString());
    }

    @Test
    void deadline_rejectsNullArgumentsAndInvalidDateText() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline(null, "2025-03-14"));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("task", (String) null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("task", (LocalDate) null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("task", (LocalDateTime) null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("task", "2025-02-30"));
    }
}
