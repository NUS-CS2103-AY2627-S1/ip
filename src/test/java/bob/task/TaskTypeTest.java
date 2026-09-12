package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests reconstruction of task types from stored symbols. */
class TaskTypeTest {

    @Test
    void fromSymbol_knownSymbols_returnsMatchingTypes() {
        assertEquals(TaskType.TODO, TaskType.fromSymbol("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromSymbol("D"));
        assertEquals(TaskType.EVENT, TaskType.fromSymbol("E"));
    }

    @Test
    void fromSymbol_unknownSymbol_illegalArgumentExceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromSymbol("X"));
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromSymbol(""));
    }
}
