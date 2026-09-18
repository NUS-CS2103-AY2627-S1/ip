package lloyd.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests navigation through previously submitted commands. */
public class CommandHistoryTest {

    /** Verifies that navigation without history preserves the current input. */
    @Test
    public void navigate_noCommands_preservesCurrentInput() {
        CommandHistory history = new CommandHistory();

        assertEquals("unfinished command", history.previous("unfinished command"));
        assertEquals("unfinished command", history.next("unfinished command"));
    }

    /** Verifies backward and forward navigation, including restoration of unfinished input. */
    @Test
    public void navigate_multipleCommands_movesInOrderAndRestoresDraft() {
        CommandHistory history = new CommandHistory();
        history.add("todo read book");
        history.add("list");

        assertEquals("list", history.previous("todo unfinished"));
        assertEquals("todo read book", history.previous("list"));
        assertEquals("todo read book", history.previous("todo read book"));
        assertEquals("list", history.next("todo read book"));
        assertEquals("todo unfinished", history.next("list"));
        assertEquals("todo unfinished", history.next("todo unfinished"));
    }

    /** Verifies that a newly submitted command becomes the newest history entry. */
    @Test
    public void add_afterNavigation_resetsToNewestPosition() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.previous("");

        history.add("help");

        assertEquals("help", history.previous(""));
    }
}
