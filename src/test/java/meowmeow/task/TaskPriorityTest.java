package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskPriority#fromInput(String)} and
 * {@link TaskPriority#acceptedLevels()} - the two entry points
 * {@link meowmeow.parser.Parser Parser} and
 * {@link meowmeow.storage.Storage Storage} use to turn user or save-file
 * text into a priority, and the flip side an unrecognised token should
 * never crash either of them.
 */
public class TaskPriorityTest {

    @Test
    public void fromInput_fullWord_returnsMatchingPriority() {
        assertEquals(TaskPriority.HIGH, TaskPriority.fromInput("high"));
        assertEquals(TaskPriority.MEDIUM, TaskPriority.fromInput("medium"));
        assertEquals(TaskPriority.LOW, TaskPriority.fromInput("low"));
        assertEquals(TaskPriority.NONE, TaskPriority.fromInput("none"));
    }

    @Test
    public void fromInput_shorthand_returnsMatchingPriority() {
        assertEquals(TaskPriority.HIGH, TaskPriority.fromInput("h"));
        assertEquals(TaskPriority.MEDIUM, TaskPriority.fromInput("m"));
        assertEquals(TaskPriority.LOW, TaskPriority.fromInput("l"));
        assertEquals(TaskPriority.NONE, TaskPriority.fromInput("n"));
    }

    @Test
    public void fromInput_mixedCase_isCaseInsensitive() {
        assertEquals(TaskPriority.HIGH, TaskPriority.fromInput("HIGH"));
        assertEquals(TaskPriority.HIGH, TaskPriority.fromInput("High"));
        assertEquals(TaskPriority.HIGH, TaskPriority.fromInput("H"));
    }

    @Test
    public void fromInput_unknownOrBlankToken_returnsNull() {
        assertNull(TaskPriority.fromInput("urgent"));
        assertNull(TaskPriority.fromInput("1"));
        assertNull(TaskPriority.fromInput(""));
    }

    @Test
    public void isSet_none_false() {
        assertFalse(TaskPriority.NONE.isSet());
    }

    @Test
    public void isSet_everyOtherPriority_true() {
        assertTrue(TaskPriority.HIGH.isSet());
        assertTrue(TaskPriority.MEDIUM.isSet());
        assertTrue(TaskPriority.LOW.isSet());
    }

    @Test
    public void acceptedLevels_listsEveryPriorityWithItsShorthand() {
        assertEquals("high/h, medium/m, low/l, none/n", TaskPriority.acceptedLevels());
    }
}
