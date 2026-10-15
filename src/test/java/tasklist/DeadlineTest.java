package tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import cxception.InvalidDateException;

public class DeadlineTest {

    @Test
    public void constructor_validDate_success() throws Exception {
        Deadline deadline = new Deadline("submit assignment", "2026-08-28");
        assertNotNull(deadline);
    }

    @Test
    public void constructor_validDateTime_success() throws Exception {
        Deadline deadline = new Deadline("submit assignment", "2026-08-28 2359");
        assertNotNull(deadline);
    }

    @Test
    public void constructor_invalidDate_exceptionThrown() {
        assertThrows(InvalidDateException.class, () ->
            new Deadline("submit assignment", "28-08-2026")
        );
    }

    @Test
    public void toString_unmarkedTask_correctFormat() throws Exception {
        Deadline deadline = new Deadline("submit assignment", "2026-08-28 2359");
        // Adjust the expected string to match MyDateTime.toDisplayString() output format
        assertEquals("[D] [ ] submit assignment (By: Aug 28 2026, 11:59PM)", deadline.toString());
    }

    @Test
    public void toString_markedTask_correctFormat() throws Exception {
        Deadline deadline = new Deadline("submit assignment", "2026-08-28 2359");
        deadline.setDone(); // Assumes mark() exists on base Task class
        assertEquals("[D] [X] submit assignment (By: Aug 28 2026, 11:59PM)", deadline.toString());
    }

    @Test
    public void fileFormatText_validDeadline_correctFormat() throws Exception {
        Deadline deadline = new Deadline("submit assignment", "2026-08-28 2359");
        // Adjust expected string to match MyDateTime.toFileString() output format
        assertEquals("D | submit assignment | 2026-08-28 2359", deadline.fileFormatText());
    }
}
