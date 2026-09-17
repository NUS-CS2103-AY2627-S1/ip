package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Verifies the display marker associated with each supported task category. */
class TaskTypeTest {
    /** Each task category exposes the short marker used by list and storage output. */
    @Test
    void getDisplayCode_supportedTypes_returnsExpectedMarkers() {
        assertEquals("T", TaskType.TODO.getDisplayCode());
        assertEquals("D", TaskType.DEADLINE.getDisplayCode());
        assertEquals("E", TaskType.EVENT.getDisplayCode());
    }
}
