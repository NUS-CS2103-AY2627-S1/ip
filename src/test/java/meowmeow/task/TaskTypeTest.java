package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskType#fromTag(String)} - the inverse of
 * {@link TaskType#getTag()} that {@link meowmeow.storage.Storage Storage}
 * uses to read a saved line's type back.
 *
 * <p>Worth testing because it guards the save format: if {@code fromTag} and
 * {@code getTag} ever disagree, previously saved tasks stop loading.
 */
public class TaskTypeTest {

    @Test
    public void fromTag_knownTag_returnsMatchingType() {
        assertEquals(TaskType.TODO, TaskType.fromTag("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromTag("D"));
        assertEquals(TaskType.EVENT, TaskType.fromTag("E"));
    }

    @Test
    public void fromTag_unknownTag_returnsNull() {
        assertNull(TaskType.fromTag("X"));
        assertNull(TaskType.fromTag(""));
        assertNull(TaskType.fromTag("t"));
    }

    @Test
    public void fromTag_isInverseOfGetTag_forEveryType() {
        for (TaskType type : TaskType.values()) {
            assertSame(type, TaskType.fromTag(type.getTag()));
        }
    }
}
