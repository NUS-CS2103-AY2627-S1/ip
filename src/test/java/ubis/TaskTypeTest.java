package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests the mapping between task types and their persistent symbols.
 */
public class TaskTypeTest {
    @Test
    public void fromSymbol_supportedSymbols_returnsMatchingType() {
        assertEquals("T", TaskType.TODO.getSymbol());
        assertEquals("D", TaskType.DEADLINE.getSymbol());
        assertEquals("E", TaskType.EVENT.getSymbol());
        for (TaskType type : TaskType.values()) {
            assertEquals(type, TaskType.fromSymbol(type.getSymbol()));
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"X", "t", "TODO", " T", "T "})
    void fromSymbol_unsupportedSymbol_returnsNull(String symbol) {
        assertNull(TaskType.fromSymbol(symbol));
    }
}
