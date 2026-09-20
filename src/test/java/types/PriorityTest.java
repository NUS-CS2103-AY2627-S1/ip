package types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import recordbase.exceptions.RecordException;
import recordbase.types.Priority;

/**
 * Tests priority names, numeric levels, and validation.
 */
public class PriorityTest {
    @Test
    void getLevel_allPriorities_returnsExpectedLevels() {
        assertEquals(5, Priority.LOW.getLevel());
        assertEquals(4, Priority.LOW_MEDIUM.getLevel());
        assertEquals(3, Priority.MEDIUM.getLevel());
        assertEquals(2, Priority.MEDIUM_HIGH.getLevel());
        assertEquals(1, Priority.HIGH.getLevel());
    }

    @Test
    void fromString_namesAndNumbers_returnsMatchingPriority() {
        assertEquals(Priority.HIGH, Priority.fromString("1"));
        assertEquals(Priority.MEDIUM_HIGH, Priority.fromString("medium-high"));
        assertEquals(Priority.MEDIUM, Priority.fromString("Medium"));
        assertEquals(Priority.LOW_MEDIUM, Priority.fromString("4"));
        assertEquals(Priority.LOW, Priority.fromString("LOW"));
    }

    @Test
    void fromString_invalidPriority_throwsRecordException() {
        assertThrows(RecordException.class, () -> Priority.fromString("urgent"));
        assertThrows(RecordException.class, () -> Priority.fromString("6"));
    }
}
