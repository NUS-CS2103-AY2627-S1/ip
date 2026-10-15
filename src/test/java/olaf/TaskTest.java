package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-specific state changes and validation.
 */
class TaskTest {
    @Test
    void rescheduleDeadline_nullDate_exceptionThrownAndOriginalDatePreserved() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 1));
        List<String> originalDate = deadline.getAdditionalStorageFields();

        assertThrows(NullPointerException.class, () -> deadline.reschedule(null));

        assertEquals(originalDate, deadline.getAdditionalStorageFields());
    }

    @Test
    void rescheduleEvent_eitherDateNull_exceptionThrownAndOriginalRangePreserved() {
        LocalDate originalStartDate = LocalDate.of(2026, 9, 1);
        LocalDate originalEndDate = LocalDate.of(2026, 9, 2);
        Event event = new Event("meeting", originalStartDate, originalEndDate);
        List<String> originalRange = event.getAdditionalStorageFields();

        assertThrows(NullPointerException.class,
                () -> event.reschedule(null, originalEndDate));
        assertEquals(originalRange, event.getAdditionalStorageFields());

        assertThrows(NullPointerException.class,
                () -> event.reschedule(originalStartDate, null));
        assertEquals(originalRange, event.getAdditionalStorageFields());
    }
}
