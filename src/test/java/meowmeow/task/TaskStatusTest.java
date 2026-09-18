package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskStatus#fromFileFlag(String)} - the inverse of
 * {@link TaskStatus#getFileFlag()} that {@link meowmeow.storage.Storage Storage}
 * uses to read a saved line's done-flag back.
 *
 * <p>Worth testing for the same reason as {@link TaskTypeTest}: a mismatch
 * between {@code fromFileFlag} and {@code getFileFlag} silently breaks
 * loading of previously saved tasks.
 */
public class TaskStatusTest {

    @Test
    public void fromFileFlag_knownFlag_returnsMatchingStatus() {
        assertEquals(TaskStatus.DONE, TaskStatus.fromFileFlag("1"));
        assertEquals(TaskStatus.NOT_DONE, TaskStatus.fromFileFlag("0"));
    }

    @Test
    public void fromFileFlag_unknownFlag_returnsNull() {
        assertNull(TaskStatus.fromFileFlag("2"));
        assertNull(TaskStatus.fromFileFlag(""));
        assertNull(TaskStatus.fromFileFlag("true"));
    }

    @Test
    public void fromFileFlag_isInverseOfGetFileFlag_forEveryStatus() {
        for (TaskStatus status : TaskStatus.values()) {
            assertSame(status, TaskStatus.fromFileFlag(status.getFileFlag()));
        }
    }
}
