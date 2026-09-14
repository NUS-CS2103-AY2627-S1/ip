package arthur.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

// AI-assisted: Tests event date-range matching and display formatting.
public class EventTaskTest {
    @Test
    public void isOn_datesAcrossInclusiveRange_returnsExpectedResult() {
        EventTask event = new EventTask("conference", LocalDateTime.of(2026, 9, 15, 9, 0),
                LocalDateTime.of(2026, 9, 17, 17, 0));

        assertFalse(event.isOn(LocalDate.of(2026, 9, 14)));
        assertTrue(event.isOn(LocalDate.of(2026, 9, 15)));
        assertTrue(event.isOn(LocalDate.of(2026, 9, 16)));
        assertTrue(event.isOn(LocalDate.of(2026, 9, 17)));
        assertFalse(event.isOn(LocalDate.of(2026, 9, 18)));
    }

    @Test
    public void toString_unmarkedEvent_formatsTaskAndDateRange() {
        EventTask event = new EventTask("conference", LocalDateTime.of(2026, 9, 15, 9, 0),
                LocalDateTime.of(2026, 9, 17, 17, 0));

        assertEquals("[E][ ] conference (from Sep 15 2026 @ 09:00, to Sep 17 2026 @ 17:00)", event.toString());
    }
}
