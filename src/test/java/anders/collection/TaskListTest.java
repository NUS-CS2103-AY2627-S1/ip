package anders.collection;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import anders.task.Todo;

/** Tests task-list invariants and basic collection behavior. */
public class TaskListTest {

    @Test
    public void add_nullTask_failsFastWithAssertion() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void constructor_taskCollectionWithNull_failsFastWithAssertion() {
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList(new Todo("valid"), null)));
    }
}
