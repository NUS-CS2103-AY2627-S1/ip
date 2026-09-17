package yachiyo.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests invariant checks performed by {@link NumberedTask}.
 */
public class NumberedTaskTest {
    @Test
    public void constructor_nonPositiveNumber_assertionErrorThrown() {
        Task task = new ToDo("Read book");

        assertThrows(AssertionError.class, () -> new NumberedTask(0, task));
        assertThrows(AssertionError.class, () -> new NumberedTask(-1, task));
    }

    @Test
    public void constructor_nullTask_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new NumberedTask(1, null));
    }
}
