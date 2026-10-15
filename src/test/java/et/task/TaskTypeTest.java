package et.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests conversion between task types and their display codes.
 */
class TaskTypeTest {
    @Test
    void fromDisplayCode_knownCodes_returnsMatchingTaskTypes() {
        assertEquals(TaskType.TODO, TaskType.fromDisplayCode("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromDisplayCode("D"));
        assertEquals(TaskType.EVENT, TaskType.fromDisplayCode("E"));
    }

    @Test
    void fromDisplayCode_unknownCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromDisplayCode("X"));
    }
}
