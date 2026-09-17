package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the display markers for each {@link TaskType}.
 */
class TaskTypeTest {
    @Test
    void getMarker_eachTaskType_returnsExpectedMarker() {
        assertEquals("[T]", TaskType.TODO.getMarker());
        assertEquals("[D]", TaskType.DEADLINE.getMarker());
        assertEquals("[E]", TaskType.EVENT.getMarker());
        assertEquals("[-]", TaskType.TASK.getMarker());
    }
}
