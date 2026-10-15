package jiji.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TaskType}.
 */
public class TaskTypeTest {

    @Test
    public void getCode_returnsExpectedCodes() {
        assertEquals("T", TaskType.TODO.getCode());
        assertEquals("D", TaskType.DEADLINE.getCode());
        assertEquals("E", TaskType.EVENT.getCode());
    }

    @Test
    public void fromCode_validCodes_resolvesCorrectType() {
        assertEquals(TaskType.TODO, TaskType.fromCode("T"));
        assertEquals(TaskType.TODO, TaskType.fromCode("t"));
        assertEquals(TaskType.DEADLINE, TaskType.fromCode("D"));
        assertEquals(TaskType.DEADLINE, TaskType.fromCode("  d  "));
        assertEquals(TaskType.EVENT, TaskType.fromCode("E"));
        assertEquals(TaskType.EVENT, TaskType.fromCode("e"));
    }

    @Test
    public void fromCode_invalidOrNullCode_returnsNull() {
        assertNull(TaskType.fromCode(null));
        assertNull(TaskType.fromCode(""));
        assertNull(TaskType.fromCode("X"));
        assertNull(TaskType.fromCode("TODO"));
    }
}
