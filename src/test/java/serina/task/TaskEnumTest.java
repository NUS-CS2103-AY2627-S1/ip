package serina.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import serina.exception.SerinaException;

/** Tests parsing and display values for task enums. */
public class TaskEnumTest {
    @Test
    public void parseFileValue_validValues_returnsMatchingEnum() throws SerinaException {
        assertEquals(TaskType.TODO, TaskType.parseFileValue(" T "));
        assertEquals(TaskType.DEADLINE, TaskType.parseFileValue("D"));
        assertEquals(TaskType.EVENT, TaskType.parseFileValue("E"));
        assertEquals(TaskStatus.NOT_DONE, TaskStatus.parseFileValue("0"));
        assertEquals(TaskStatus.DONE, TaskStatus.parseFileValue(" 1 "));
        assertEquals(" ", TaskStatus.NOT_DONE.getIcon());
        assertEquals("1", TaskStatus.DONE.getFileValue());
    }

    @Test
    public void parseFileValue_unknownValues_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> TaskType.parseFileValue("X"));
        assertThrows(SerinaException.class, () -> TaskStatus.parseFileValue("2"));
    }
}
