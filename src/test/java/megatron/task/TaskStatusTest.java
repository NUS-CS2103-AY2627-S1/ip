package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the display markers for each {@link TaskStatus}.
 */
class TaskStatusTest {
    @Test
    void getMarker_eachTaskStatus_returnsExpectedMarker() {
        assertEquals("[ ]", TaskStatus.NOT_DONE.getMarker());
        assertEquals("[X]", TaskStatus.DONE.getMarker());
    }
}
