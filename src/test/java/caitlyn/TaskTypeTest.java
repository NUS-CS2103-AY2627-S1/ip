package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the storage/display markers assigned to every task type. */
class TaskTypeTest {
    @Test
    void getMarker_returnsTheExpectedMarkerForEveryTaskType() {
        assertEquals("T", TaskType.TODO.getMarker());
        assertEquals("D", TaskType.DEADLINE.getMarker());
        assertEquals("E", TaskType.EVENT.getMarker());
        assertEquals("W", TaskType.WITHIN.getMarker());
    }

    @Test
    void parseMarker_returnsTheMatchingTaskType() {
        assertEquals(TaskType.TODO, TaskType.parseMarker("T"));
        assertEquals(TaskType.DEADLINE, TaskType.parseMarker("D"));
        assertEquals(TaskType.EVENT, TaskType.parseMarker("E"));
        assertEquals(TaskType.WITHIN, TaskType.parseMarker("W"));
    }

    @Test
    void parseMarker_rejectsUnknownMarkers() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.parseMarker("X"));
    }
}
