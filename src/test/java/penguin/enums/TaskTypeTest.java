package penguin.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests task-type symbols and lookup behavior.
 */
class TaskTypeTest {
    @Test
    void taskType_symbols_returnsCorrectSymbols() {
        assertEquals("T", TaskType.TODO.getSymbol());
        assertEquals("D", TaskType.DEADLINE.getSymbol());
        assertEquals("E", TaskType.EVENT.getSymbol());
    }

    @Test
    void taskType_values_returnsAllSupportedTypes() {
        assertEquals(3, TaskType.values().length);
        assertSame(TaskType.TODO, TaskType.valueOf("TODO"));
        assertSame(TaskType.DEADLINE, TaskType.valueOf("DEADLINE"));
        assertSame(TaskType.EVENT, TaskType.valueOf("EVENT"));
    }

    @Test
    void taskType_fromSymbol_validSymbol_returnsMatchingType() {
        assertEquals(TaskType.DEADLINE, TaskType.fromSymbol("D"));
    }

    @Test
    void taskType_fromSymbol_unknownSymbol_returnsNull() {
        assertNull(TaskType.fromSymbol("X"));
    }
}
