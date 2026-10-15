package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests command recall boundaries and preservation of unfinished input.
 */
public class CommandHistoryTest {
    @Test
    public void navigate_emptyHistory_preservesInput() {
        CommandHistory history = new CommandHistory();
        assertEquals("draft", history.previous("draft"));
        assertEquals("draft", history.next("draft"));
    }

    @Test
    public void navigate_commands_restoresDraftAndStopsAtBoundaries() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("help");
        assertEquals("help", history.previous("todo unfinished"));
        assertEquals("list", history.previous("help"));
        assertEquals("list", history.previous("list"));
        assertEquals("help", history.next("list"));
        assertEquals("todo unfinished", history.next("help"));
        assertEquals("edited draft", history.next("edited draft"));
    }

    @Test
    public void add_whileBrowsing_resetsNavigationWithoutChangingOlderCommands() {
        CommandHistory history = new CommandHistory();
        history.add("todo first");
        history.previous("");
        history.add("todo corrected");
        assertEquals("todo corrected", history.previous(""));
        assertEquals("todo first", history.previous("todo corrected"));
    }
}
