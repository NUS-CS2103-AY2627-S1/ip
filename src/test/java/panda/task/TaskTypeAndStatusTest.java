package panda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion between task enum values and their stored markers.
 */
class TaskTypeAndStatusTest {

    @Test
    void taskType_validMarkers_roundTrip() {
        for (TaskType taskType : TaskType.values()) {
            assertEquals(taskType, TaskType.fromMarker(taskType.getMarker()));
        }
    }

    @Test
    void taskType_invalidMarker_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromMarker("X"));
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromMarker("t"));
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromMarker(""));
    }

    @Test
    void taskStatus_validValues_roundTrip() {
        for (TaskStatus taskStatus : TaskStatus.values()) {
            assertEquals(taskStatus,
                    TaskStatus.fromStorageValue(taskStatus.getStorageValue()));
        }
    }

    @Test
    void taskStatus_invalidValue_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                TaskStatus.fromStorageValue("2"));
        assertThrows(IllegalArgumentException.class, () ->
                TaskStatus.fromStorageValue("done"));
        assertThrows(IllegalArgumentException.class, () ->
                TaskStatus.fromStorageValue(""));
    }
}
