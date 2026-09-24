package dobby.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/** Tests invariants maintained by {@link TaskList}. */
class TaskListTest {
    @Test
    void constructor_listContainingNull_assertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList(new ToDo("read book"), null)));
    }

    @Test
    void add_nullTask_assertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }
}
