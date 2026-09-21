package chris;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the task collection's internal invariants. */
public class TaskListTest {
    @Test
    public void constructor_nullCollection_failsAssertion() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }
}
