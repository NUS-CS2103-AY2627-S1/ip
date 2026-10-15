package magnus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion between task types and their persistent-storage codes.
 */
public class TaskTypeTest {
    @Test
    public void parseStorageCode_supportedCodes_returnsMatchingTaskTypes() {
        assertEquals(TaskType.TODO, TaskType.parseStorageCode("T"));
        assertEquals(TaskType.DEADLINE, TaskType.parseStorageCode("D"));
        assertEquals(TaskType.EVENT, TaskType.parseStorageCode("E"));
    }

    @Test
    public void getStorageCode_taskTypes_returnsMatchingCodes() {
        assertEquals("T", TaskType.TODO.getStorageCode());
        assertEquals("D", TaskType.DEADLINE.getStorageCode());
        assertEquals("E", TaskType.EVENT.getStorageCode());
    }

    @Test
    public void parseStorageCode_unknownCode_throwsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class, () -> TaskType.parseStorageCode("X"));
    }
}
