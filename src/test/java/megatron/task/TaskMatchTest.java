package megatron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests validation and value behavior for {@link TaskMatch}.
 */
class TaskMatchTest {
    @Test
    void constructor_validValues_storesNumberAndTask() {
        Task task = new Todo("read book");

        TaskMatch match = new TaskMatch(2, task);

        assertEquals(2, match.taskNumber());
        assertSame(task, match.task());
    }

    @Test
    void constructor_zeroTaskNumber_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskMatch(0, new Todo("task")));
    }

    @Test
    void constructor_negativeTaskNumber_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskMatch(-1, new Todo("task")));
    }

    @Test
    void constructor_nullTask_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskMatch(1, null));
    }
}
