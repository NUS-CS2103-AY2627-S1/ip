package charliek.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests task-type markers used by display and storage.
 */
class TaskTypeTest {
    /**
     * Verifies every supported marker resolves to its corresponding task type.
     */
    @Test
    void fromIcon_supportedMarkers_returnsMatchingTypes() {
        assertEquals(Optional.of(TaskType.TODO), TaskType.fromIcon("T"));
        assertEquals(Optional.of(TaskType.DEADLINE), TaskType.fromIcon("D"));
        assertEquals(Optional.of(TaskType.EVENT), TaskType.fromIcon("E"));
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals("D", TaskType.DEADLINE.getIcon());
        assertEquals("E", TaskType.EVENT.getIcon());
    }

    /**
     * Verifies unsupported and null markers are rejected without an exception.
     */
    @Test
    void fromIcon_unsupportedMarkers_returnsEmptyOptional() {
        assertTrue(TaskType.fromIcon("todo").isEmpty());
        assertTrue(TaskType.fromIcon("").isEmpty());
        assertTrue(TaskType.fromIcon(null).isEmpty());
    }
}
