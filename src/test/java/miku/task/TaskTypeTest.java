package miku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the task-type values used by Miku's model and save data. */
class TaskTypeTest {

    @Test
    void fromMarker_unknownMarker_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromMarker("X"));
    }

    @Test
    void getMarker_eachType_returnsPersistedMarker() {
        assertEquals("T", TaskType.TODO.getMarker());
        assertEquals("D", TaskType.DEADLINE.getMarker());
        assertEquals("E", TaskType.EVENT.getMarker());
    }
}
