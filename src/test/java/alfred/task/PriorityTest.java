package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import alfred.AlfredException;

/**
 * Tests {@link Priority}.
 */
public class PriorityTest {
    private static final String NOT_A_LEVEL = "I know high, medium, low, and none as priorities, sir.";

    @Test
    public void parse_eachLevel_levelRead() throws AlfredException {
        assertEquals(Priority.HIGH, Priority.parse("high"));
        assertEquals(Priority.MEDIUM, Priority.parse("medium"));
        assertEquals(Priority.LOW, Priority.parse("low"));
        assertEquals(Priority.NONE, Priority.parse("none"));
    }

    @Test
    public void parse_mixedCase_levelRead() throws AlfredException {
        assertEquals(Priority.HIGH, Priority.parse("High"));
        assertEquals(Priority.MEDIUM, Priority.parse("mEdIuM"));
    }

    @Test
    public void parse_unknownWord_exceptionThrown() {
        assertRefused("urgent");
    }

    @Test
    public void parse_empty_exceptionThrown() {
        assertRefused("");
    }

    @Test
    public void box_setLevel_levelInBox() {
        assertEquals("[HIGH]", Priority.HIGH.box());
        assertEquals("[MEDIUM]", Priority.MEDIUM.box());
        assertEquals("[LOW]", Priority.LOW.box());
    }

    @Test
    public void box_none_nothingShown() {
        assertEquals("", Priority.NONE.box());
    }

    private void assertRefused(String text) {
        AlfredException e = assertThrows(AlfredException.class, () -> Priority.parse(text));
        assertEquals(NOT_A_LEVEL, e.getMessage());
    }
}
