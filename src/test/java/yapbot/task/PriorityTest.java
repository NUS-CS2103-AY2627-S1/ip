package yapbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import yapbot.exception.YapBotException;

public class PriorityTest {

    @Test
    public void fromInput_lowercaseWord_returnsMatchingLevel() throws YapBotException {
        assertEquals(Priority.HIGH, Priority.fromInput("high"));
        assertEquals(Priority.MEDIUM, Priority.fromInput("medium"));
        assertEquals(Priority.LOW, Priority.fromInput("low"));
    }

    @Test
    public void fromInput_mixedCaseWord_returnsMatchingLevel() throws YapBotException {
        assertEquals(Priority.HIGH, Priority.fromInput("High"));
        assertEquals(Priority.HIGH, Priority.fromInput("HIGH"));
    }

    @Test
    public void fromInput_unrecognizedWord_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Priority.fromInput("urgent"));
    }

    @Test
    public void fromInput_emptyString_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Priority.fromInput(""));
    }

    @Test
    public void fromInput_singleLetterShorthand_exceptionThrown() {
        assertThrows(YapBotException.class, () -> Priority.fromInput("h"));
    }
}
