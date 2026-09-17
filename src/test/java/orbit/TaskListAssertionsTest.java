package orbit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/** Verifies that programmer errors trip the documented task-list assumptions. */
public class TaskListAssertionsTest {
    @Test
    public void assertions_testRuntime_areEnabled() {
        assertTrue(TaskList.class.desiredAssertionStatus());
    }

    @Test
    public void constructor_nullListOrEntry_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList(new Todo("read"), null)));
    }

    @Test
    public void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void getAndRemove_unvalidatedIndices_throwAssertionError() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read"));

        assertThrows(AssertionError.class, () -> tasks.get(-1));
        assertThrows(AssertionError.class, () -> tasks.get(tasks.size()));
        assertThrows(AssertionError.class, () -> tasks.remove(-1));
        assertThrows(AssertionError.class, () -> tasks.remove(tasks.size()));
    }
}
