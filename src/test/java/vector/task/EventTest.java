package vector.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toString_validEvent_correctFormat() {
        try {
            Event e = new Event("project meeting", "2023-08-01 1400", "2023-08-01 1600");
            assertEquals("[E][ ] project meeting (from: Aug 1 2023, 2:00 PM to: Aug 1 2023, 4:00 PM)", e.toString());
            e.markAsDone();
            assertEquals("[E][X] project meeting (from: Aug 1 2023, 2:00 PM to: Aug 1 2023, 4:00 PM)", e.toString());
        } catch (Exception ex) {
            fail("Exception thrown on valid event: " + ex.getMessage());
        }
    }

    @Test
    public void toFileFormat_validEvent_correctFormat() {
        try {
            Event e = new Event("project meeting", "2023-08-01 1400", "2023-08-01 1600");
            assertEquals("E | 0 | project meeting | 2023-08-01 1400 | 2023-08-01 1600", e.toFileFormat());
            e.markAsDone();
            assertEquals("E | 1 | project meeting | 2023-08-01 1400 | 2023-08-01 1600", e.toFileFormat());
        } catch (Exception ex) {
            fail("Exception thrown on valid event: " + ex.getMessage());
        }
    }

    @Test
    public void equals_sameEvents_returnsTrue() {
        try {
            Event e1 = new Event("project meeting", "2023-08-01 1400", "2023-08-01 1600");
            Event e2 = new Event("project meeting", "2023-08-01 1400", "2023-08-01 1600");
            assertTrue(e1.equals(e2));
            assertTrue(e1.equals(e1));
        } catch (Exception ex) {
            fail("Exception thrown on valid event: " + ex.getMessage());
        }
    }

    @Test
    public void equals_differentEvents_returnsFalse() {
        try {
            Event e1 = new Event("project meeting", "2023-08-01 1400", "2023-08-01 1600");
            Event e2 = new Event("team meeting", "2023-08-01 1400", "2023-08-01 1600");
            Event e3 = new Event("project meeting", "2023-08-02 1400", "2023-08-02 1600");
            assertFalse(e1.equals(e2));
            assertFalse(e1.equals(e3));
            assertFalse(e1.equals(null));
        } catch (Exception ex) {
            fail("Exception thrown on valid event: " + ex.getMessage());
        }
    }

    @Test
    public void occursOn_dateInRange_returnsTrue() {
        try {
            Event e = new Event("conference", "2023-08-01 1400", "2023-08-03 1600");
            assertTrue(e.occursOn(LocalDate.of(2023, 8, 1)));
            assertTrue(e.occursOn(LocalDate.of(2023, 8, 2)));
            assertTrue(e.occursOn(LocalDate.of(2023, 8, 3)));
        } catch (Exception ex) {
            fail("Exception thrown on valid event: " + ex.getMessage());
        }
    }

    @Test
    public void occursOn_dateOutOfRange_returnsFalse() {
        try {
            Event e = new Event("conference", "2023-08-01 1400", "2023-08-03 1600");
            assertFalse(e.occursOn(LocalDate.of(2023, 7, 31)));
            assertFalse(e.occursOn(LocalDate.of(2023, 8, 4)));
        } catch (Exception ex) {
            fail("Exception thrown on valid event: " + ex.getMessage());
        }
    }
}
