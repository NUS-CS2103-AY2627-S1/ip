package martin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import martin.exception.IllegalCommandException;

/** Unit tests for priority parsing and storage-code conversion. */
public class PriorityTest {

    @Test
    public void fromUserInput_mixedCaseName_returnsCorrespondingPriority() {
        assertEquals(Priority.HIGH, Priority.fromUserInput("hIgH"));
        assertEquals(Priority.MEDIUM, Priority.fromUserInput("medium"));
        assertEquals(Priority.LOW, Priority.fromUserInput("low"));
    }

    @Test
    public void fromUserInput_unknownName_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () -> Priority.fromUserInput("urgent"));
    }
}
