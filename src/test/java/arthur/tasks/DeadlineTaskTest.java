package arthur.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

// AI-assisted: Tests deadline date matching and display formatting.
public class DeadlineTaskTest {
    @Test
    public void isOn_matchingDeadlineDate_returnsTrue() {
        DeadlineTask deadline = new DeadlineTask("submit report", LocalDateTime.of(2026, 9, 15, 14, 30));

        assertTrue(deadline.isOn(LocalDate.of(2026, 9, 15)));
        assertFalse(deadline.isOn(LocalDate.of(2026, 9, 16)));
    }

    @Test
    public void toString_unmarkedDeadline_formatsTaskAndDate() {
        DeadlineTask deadline = new DeadlineTask("submit report", LocalDateTime.of(2026, 9, 15, 14, 30));

        assertEquals("[D][ ] submit report (by Sep 15 2026 @ 14:30)", deadline.toString());
    }
}
