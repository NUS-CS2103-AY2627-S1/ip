package harold.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    void constructor_endBeforeStart_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Event("holiday",
                        LocalDate.of(2026, 9, 20),
                        LocalDate.of(2026, 9, 19)
                )
        );

        assertEquals("Event end date cannot be before its start date", exception.getMessage());
    }

    @Test
    void constructor_sameStartAndEndDate_acceptsSingleDayEvent() {
        LocalDate date = LocalDate.of(2026, 9, 20);

        assertDoesNotThrow(() -> new Event("workshop", date, date));
    }
}
