package jeryl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import jeryl.exception.JerylException;

public class PriorityTest {
    @Test
    public void fromKeyword_validLowercaseKeyword_returnsMatchingPriority() throws JerylException {
        assertEquals(Priority.HIGH, Priority.fromKeyword("high"));
        assertEquals(Priority.MEDIUM, Priority.fromKeyword("medium"));
        assertEquals(Priority.LOW, Priority.fromKeyword("low"));
    }

    @Test
    public void fromKeyword_mixedCaseKeyword_isCaseInsensitive() throws JerylException {
        assertEquals(Priority.HIGH, Priority.fromKeyword("High"));
    }

    @Test
    public void fromKeyword_none_throwsJerylException() {
        assertThrows(JerylException.class, () -> Priority.fromKeyword("none"));
    }

    @Test
    public void fromKeyword_unknownKeyword_throwsJerylException() {
        JerylException e = assertThrows(JerylException.class, () -> Priority.fromKeyword("urgent"));
        assertEquals("Invalid priority \"urgent\". Use high, medium, or low.", e.getMessage());
    }
}
