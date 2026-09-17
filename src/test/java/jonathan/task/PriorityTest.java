package jonathan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PriorityTest {

    @Test
    public void fromInput_validNames_returnsCorrectPriority() {
        assertEquals(Priority.HIGH, Priority.fromInput("high"));
        assertEquals(Priority.MEDIUM, Priority.fromInput("medium"));
        assertEquals(Priority.LOW, Priority.fromInput("low"));
        assertEquals(Priority.NONE, Priority.fromInput("none"));
    }

    @Test
    public void fromInput_validNumbers_returnsCorrectPriority() {
        assertEquals(Priority.HIGH, Priority.fromInput("1"));
        assertEquals(Priority.MEDIUM, Priority.fromInput("2"));
        assertEquals(Priority.LOW, Priority.fromInput("3"));
        assertEquals(Priority.NONE, Priority.fromInput("0"));
    }

    @Test
    public void fromInput_invalidValue_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Priority.fromInput("urgent");
        });
    }
}
