package odysseus.task;

import odysseus.OdysseusException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DeadlineTest {

    @Test
    public void occursOn_sameDate_returnsTrue() throws OdysseusException {
        assertTrue(new Deadline("deadline task", "2026-06-06")
                .occursOn(LocalDate.of(2026, 6, 6)));
    }

    @Test
    public void occursOn_differentDate_returnsFalse() throws OdysseusException {
        assertFalse(new Deadline("deadline task", "2026-07-06")
                .occursOn(LocalDate.of(2026, 6, 6)));
    }

    @Test
    public void toString_displaysFriendlyDate() throws OdysseusException {
        assertEquals("[D][ ] deadline task (by: Jun 6 2026)",
                new Deadline("deadline task", "2026-06-06").toString());
    }

    @Test
    public void toSaveFormat_usesIsoDate() throws OdysseusException {
        assertEquals("D | 0 | deadline task | 2026-06-06",
                new Deadline("deadline task", "2026-06-06").toSaveFormat());
    }
}
