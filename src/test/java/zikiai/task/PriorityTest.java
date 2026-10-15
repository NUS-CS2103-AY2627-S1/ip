package zikiai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests every display and storage representation of task priorities.
 */
class PriorityTest {

    @Test
    void priorityTags_allLevels_expectedRepresentationsReturned() {
        assertEquals("", Priority.NONE.getDisplayTag());
        assertEquals("", Priority.NONE.getStorageTag());
        assertEquals("[LOW]", Priority.LOW.getDisplayTag());
        assertEquals("[L]", Priority.LOW.getStorageTag());
        assertEquals("[MEDIUM]", Priority.MEDIUM.getDisplayTag());
        assertEquals("[M]", Priority.MEDIUM.getStorageTag());
        assertEquals("[HIGH]", Priority.HIGH.getDisplayTag());
        assertEquals("[H]", Priority.HIGH.getStorageTag());
    }
}
