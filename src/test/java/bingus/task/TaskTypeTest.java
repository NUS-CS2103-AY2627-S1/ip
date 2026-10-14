package bingus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion between task types and their symbols.
 */
class TaskTypeTest {

    @Test
    void fromSymbol_knownSymbols_returnsCorrespondingTaskTypes() {
        assertEquals(TaskType.TODO, TaskType.fromSymbol("T"));
        assertEquals(TaskType.DEADLINE, TaskType.fromSymbol("D"));
        assertEquals(TaskType.EVENT, TaskType.fromSymbol("E"));
    }

    @Test
    void fromSymbol_unknownSymbol_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> TaskType.fromSymbol("X"));
    }

    /**
     * Verifies that each task type exposes its display symbol.
     */
    @Test
    void getSymbol_taskTypes_returnsExpectedSymbols() {
        assertEquals("T", TaskType.TODO.getSymbol());
        assertEquals("D", TaskType.DEADLINE.getSymbol());
        assertEquals("E", TaskType.EVENT.getSymbol());
    }
}
