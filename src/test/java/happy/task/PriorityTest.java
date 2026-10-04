package happy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Priority enum class.
 */
public class PriorityTest {

    @Test
    public void parse_validPriorityStrings_returnsCorrespondingEnum() {
        assertEquals(Priority.HIGH, Priority.parse("high"));
        assertEquals(Priority.HIGH, Priority.parse("h"));
        assertEquals(Priority.MEDIUM, Priority.parse("medium"));
        assertEquals(Priority.MEDIUM, Priority.parse("med"));
        assertEquals(Priority.LOW, Priority.parse("low"));
        assertEquals(Priority.LOW, Priority.parse("l"));
        assertEquals(Priority.NONE, Priority.parse("none"));
    }

    @Test
    public void parse_unknownPriorityString_returnsNone() {
        assertEquals(Priority.NONE, Priority.parse("invalid_priority"));
    }
}
