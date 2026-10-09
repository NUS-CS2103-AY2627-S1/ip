package sumo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests conversion between stored task metadata and {@link TaskType}. */
public class TaskTypeTest {
    @Test
    public void metadata_allTaskTypes_expectedValuesReturned() {
        assertEquals("T", TaskType.TODO.getStorageCode());
        assertEquals(3, TaskType.TODO.getStoredFieldCount());
        assertEquals("D", TaskType.DEADLINE.getStorageCode());
        assertEquals(4, TaskType.DEADLINE.getStoredFieldCount());
        assertEquals("E", TaskType.EVENT.getStorageCode());
        assertEquals(5, TaskType.EVENT.getStoredFieldCount());
    }

    @Test
    public void fromStorageCode_knownAndUnknownCodes_expectedResult() {
        assertEquals(TaskType.TODO, TaskType.fromStorageCode("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromStorageCode("D"));
        assertEquals(TaskType.EVENT, TaskType.fromStorageCode("E"));
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromStorageCode("X"));
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromStorageCode(null));
    }
}
