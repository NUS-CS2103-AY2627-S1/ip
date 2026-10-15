package taterror.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Priority#fromString(String)}.
 */
public class PriorityTest {

    @Test
    public void fromString_exactCase_returnsMatchingPriority() {
        assertEquals(Priority.HIGH, Priority.fromString("HIGH"));
    }

    @Test
    public void fromString_differentCase_returnsMatchingPriority() {
        assertEquals(Priority.MEDIUM, Priority.fromString("medium"));
        assertEquals(Priority.LOW, Priority.fromString("Low"));
        assertEquals(Priority.NONE, Priority.fromString("none"));
    }

    @Test
    public void fromString_unrecognizedText_returnsNull() {
        assertNull(Priority.fromString("urgent"));
    }
}
