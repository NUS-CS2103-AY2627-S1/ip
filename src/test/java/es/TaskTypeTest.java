package es;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TaskTypeTest {
    @Test
    void icons_roundTripToTaskTypes() throws EsException {
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals(TaskType.DEADLINE, TaskType.fromIcon("D"));
        assertEquals(TaskType.EVENT, TaskType.fromIcon("E"));
    }

    @Test
    void fromIcon_rejectsUnknownIcon() {
        assertThrows(EsException.class, () -> TaskType.fromIcon("X"));
    }
}
