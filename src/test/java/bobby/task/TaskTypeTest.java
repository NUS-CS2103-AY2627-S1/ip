package bobby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests task type symbol conversion used by storage.
 */
class TaskTypeTest {
    @Test
    void fromSymbol_knownSymbols_returnsTaskTypes() {
        assertEquals(TaskType.TODO, TaskType.fromSymbol("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromSymbol("D"));
        assertEquals(TaskType.EVENT, TaskType.fromSymbol("E"));
    }

    @Test
    void fromSymbol_unknownSymbol_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromSymbol("X"));
    }
}
