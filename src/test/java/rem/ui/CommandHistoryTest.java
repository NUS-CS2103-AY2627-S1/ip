package rem.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {
    @Test
    public void navigation_emptyHistory_preservesInput() {
        CommandHistory history = new CommandHistory();
        history.add("  ");
        assertEquals("draft", history.previous("draft"));
        assertEquals("draft", history.next("draft"));
    }

    @Test
    public void navigation_multipleCommands_clampsAndRestoresDraft() {
        CommandHistory history = new CommandHistory();
        history.add("todo read book");
        history.add("list");
        assertEquals("list", history.previous("unfinished"));
        assertEquals("todo read book", history.previous("list"));
        assertEquals("todo read book", history.previous("todo read book"));
        assertEquals("list", history.next("todo read book"));
        assertEquals("unfinished", history.next("list"));
        assertEquals("edited draft", history.next("edited draft"));
    }

    @Test
    public void add_repeatedAndEditedCommands_resetsNavigationWithoutConsecutiveDuplicates() {
        CommandHistory history = new CommandHistory();
        history.add("todo read book");
        history.add("list");
        history.add("list");
        assertEquals("list", history.previous(""));
        assertEquals("todo read book", history.previous("list"));
        history.add("todo read paper");
        assertEquals("todo read paper", history.previous("new draft"));
        assertEquals("new draft", history.next("todo read paper"));
    }
}
