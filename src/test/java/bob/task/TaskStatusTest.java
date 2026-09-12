package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests reconstruction of task statuses from stored codes. */
class TaskStatusTest {

    @Test
    void fromStorageCode_knownCodes_returnsMatchingStatuses() {
        assertEquals(TaskStatus.DONE, TaskStatus.fromStorageCode("1"));
        assertEquals(TaskStatus.NOT_DONE, TaskStatus.fromStorageCode("0"));
    }

    @Test
    void fromStorageCode_unknownCode_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskStatus.fromStorageCode("X"));
        assertThrows(IllegalArgumentException.class, () -> TaskStatus.fromStorageCode(""));
    }
}
