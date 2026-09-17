package dog.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void toSaveFormat_validDescription_returnsSaveFormat() {
        // Test done deadline
        Deadline doneDeadline = new Deadline("cry to sleep", java.time.LocalDate.of(2026, 8, 8), true);
        assertEquals("D | X | 2026-08-08 | cry to sleep", doneDeadline.toSaveFormat());

        // Test pending deadline
        Deadline pendingDeadline = new Deadline("survive 2103t", java.time.LocalDate.of(2026, 8, 9), false);
        assertEquals("D |   | 2026-08-09 | survive 2103t", pendingDeadline.toSaveFormat());

        // Test description with spaces
        Deadline spacesDeadline = new Deadline("  a  b   c ", java.time.LocalDate.of(2026, 8, 10), false);
        assertEquals("D |   | 2026-08-10 |   a  b   c ", spacesDeadline.toSaveFormat());

        // Test description with pipes
        Deadline pipesDeadline = new Deadline(" || d | e |   | f ", java.time.LocalDate.of(2026, 8, 11), true);
        assertEquals("D | X | 2026-08-11 |  || d | e |   | f ", pipesDeadline.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_validFormat_returnsDeadline() {
        // Test done deadline
        Deadline doneDeadline = Deadline.fromSaveFormat("D | X | 2026-08-08 | cry to sleep");
        assertEquals("D | X | 2026-08-08 | cry to sleep", doneDeadline.toSaveFormat());

        // Test pending deadline
        Deadline pendingDeadline = Deadline.fromSaveFormat("D |   | 2026-08-09 | survive 2103t");
        assertEquals("D |   | 2026-08-09 | survive 2103t", pendingDeadline.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_specialCharacters_preservesDescription() {
        // Test description with special characters
        Deadline specialCharDeadline = Deadline.fromSaveFormat("D | X | 2026-08-08 | cry @home #urgent!#%&^}{|");
        assertEquals("D | X | 2026-08-08 | cry @home #urgent!#%&^}{|", specialCharDeadline.toSaveFormat());

        // Test description with pipe characters
        Deadline pipeCharPendingDeadline = Deadline.fromSaveFormat("D |   | 2026-08-08 | X | cry");
        assertEquals("D |   | 2026-08-08 | X | cry", pipeCharPendingDeadline.toSaveFormat());

        // Test description with pipe characters
        Deadline pipeCharDoneDeadline = Deadline.fromSaveFormat("D | X | 2026-08-08 |   | fard");
        assertEquals("D | X | 2026-08-08 |   | fard", pipeCharDoneDeadline.toSaveFormat());
    }

    @Test
    public void fromSaveFormat_invalidFormat_returnsNull() {
        // Test missing parts
        assertNull(Deadline.fromSaveFormat("D | 2026-08-08 | broken"));

        // Test missing spaces
        assertNull(Deadline.fromSaveFormat("D| X | 2026-08-08 | i need space"));
        assertNull(Deadline.fromSaveFormat("D| | 2026-08-08 | give me space"));

        // Test invalid/missing status icon
        assertNull(Deadline.fromSaveFormat("D |  | give me more space"));
        assertNull(Deadline.fromSaveFormat("D | F | what the F"));

        // Test invalid/missing task type icon
        assertNull(Deadline.fromSaveFormat("T | X | 2026-08-08 | im dead :skull:"));
        assertNull(Deadline.fromSaveFormat("E |   | 2026-08-08 | e"));
        assertNull(Deadline.fromSaveFormat(" |   | 2026-08-08 | where is the t"));

        // Test invalid/missing date
        assertNull(Deadline.fromSaveFormat("D | X | 08-08-2026 | invalid date"));
        assertNull(Deadline.fromSaveFormat("D | X | cant find a date"));

        // Test empty string
        assertNull(Deadline.fromSaveFormat(""));

        // Test null
        assertNull(Deadline.fromSaveFormat(null));
    }

    @Test
    public void fromSaveFormat_roundTrip_producesSameString() {
        // Test round-trip consistency
        String original = "D | X | 2026-08-08 | Test task";
        Deadline deadline = Deadline.fromSaveFormat(original);
        assertEquals(original, deadline.toSaveFormat());
    }
}
