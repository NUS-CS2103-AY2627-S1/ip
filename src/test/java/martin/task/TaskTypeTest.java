package martin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Unit tests for task type storage-code conversion. */
public class TaskTypeTest {

    @Test
    public void fromStorageCode_knownCode_returnsCorrespondingTaskType() {
        assertEquals(TaskType.TODO, TaskType.fromStorageCode(TaskType.TODO.getStorageCode()));
        assertEquals(TaskType.DEADLINE, TaskType.fromStorageCode(TaskType.DEADLINE.getStorageCode()));
        assertEquals(TaskType.EVENT, TaskType.fromStorageCode(TaskType.EVENT.getStorageCode()));
    }

    @Test
    public void fromStorageCode_unknownCode_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromStorageCode("unknown"));
    }
}
