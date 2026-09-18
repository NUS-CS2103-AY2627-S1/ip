package hermes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class TaskTypeTest {

    @Test
    public void of_knownSymbol_returnsMatchingType() {
        assertEquals(TaskType.TODO, TaskType.of("T"));
        assertEquals(TaskType.DEADLINE, TaskType.of("D"));
        assertEquals(TaskType.EVENT, TaskType.of("E"));
    }

    @Test
    public void of_unknownSymbol_returnsNull() {
        assertNull(TaskType.of("X"));
        assertNull(TaskType.of(""));
        // Symbols are matched exactly, so case matters.
        assertNull(TaskType.of("t"));
    }

    @Test
    public void getStoredFieldCount_eachType_countsTypeFlagDescriptionAndDates() {
        assertEquals(3, TaskType.TODO.getStoredFieldCount());
        assertEquals(4, TaskType.DEADLINE.getStoredFieldCount());
        assertEquals(5, TaskType.EVENT.getStoredFieldCount());
    }

    @Test
    public void getSymbol_eachType_isTheLetterReadBackByOf() {
        for (TaskType type : TaskType.values()) {
            assertEquals(type, TaskType.of(type.getSymbol()));
        }
    }
}
