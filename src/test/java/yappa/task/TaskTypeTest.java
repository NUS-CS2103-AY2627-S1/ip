package yappa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import yappa.exception.YappaException;

/** Tests conversion between task types and their storage codes. */
public class TaskTypeTest {

    /** Verifies that each task type exposes its expected storage code. */
    @Test
    public void getCode_allTaskTypes_returnsExpectedCodes() {
        assertEquals("T", TaskType.TODO.getCode());
        assertEquals("D", TaskType.DEADLINE.getCode());
        assertEquals("E", TaskType.EVENT.getCode());
    }

    /** Verifies that known storage codes map to their task types. */
    @Test
    public void fromStorageCode_knownCodes_returnsMatchingTypes() throws YappaException {
        assertEquals(TaskType.TODO, TaskType.fromStorageCode("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromStorageCode("D"));
        assertEquals(TaskType.EVENT, TaskType.fromStorageCode("E"));
    }

    /** Verifies that unknown storage codes are rejected. */
    @Test
    public void fromStorageCode_unknownCode_throwsYappaException() {
        assertThrows(YappaException.class, () -> TaskType.fromStorageCode("X"));
    }
}
