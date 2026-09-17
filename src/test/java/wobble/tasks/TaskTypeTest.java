package wobble.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the display icon associated with each task type. */
class TaskTypeTest {
    @Test
    void getIcon_allTaskTypes_returnsExpectedIcons() {
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals("D", TaskType.DEADLINE.getIcon());
        assertEquals("E", TaskType.EVENT.getIcon());
    }
}
