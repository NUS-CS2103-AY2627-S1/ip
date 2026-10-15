package orbit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TaskEnumStorageTest {
    @Test
    void fromStorageCode_knownAndUnknownCodes_returnsTypeOrThrowsException() {
        assertEquals(TaskType.TODO, TaskType.fromStorageCode("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromStorageCode("D"));
        assertEquals(TaskType.EVENT, TaskType.fromStorageCode("E"));
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromStorageCode("unknown"));
    }

    @Test
    void fromStorageValue_knownAndUnknownValues_returnsStatusOrThrowsException() {
        assertEquals(TaskStatus.NOT_DONE, TaskStatus.fromStorageValue("0"));
        assertEquals(TaskStatus.DONE, TaskStatus.fromStorageValue("1"));
        assertThrows(IllegalArgumentException.class, () -> TaskStatus.fromStorageValue("unknown"));
    }
}
