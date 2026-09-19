package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CommandHistoryTest {
    @Test
    public void previous_emptyHistory_returnsEmpty() {
        assertEquals("", new CommandHistory().previous());
    }

    @Test
    public void addThenPrevious_recallsTheCommand() {
        CommandHistory history = new CommandHistory();
        history.add("todo read book");
        history.add("list");
        assertEquals("list", history.previous());
        assertEquals("todo read book", history.previous());
    }

    @Test
    public void previous_pastTheOldest_staysOnTheOldest() {
        CommandHistory history = new CommandHistory();
        history.add("first");
        history.add("second");
        history.previous();
        history.previous();
        assertEquals("first", history.previous());
    }

    @Test
    public void next_movesForwardAndClearsPastTheNewest() {
        CommandHistory history = new CommandHistory();
        history.add("first");
        history.add("second");
        assertEquals("second", history.previous());
        assertEquals("first", history.previous());
        assertEquals("second", history.next());
        assertEquals("", history.next());
    }

    @Test
    public void add_ignoresBlankInputAndRepeats() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("   ");
        history.add("list");
        assertEquals("list", history.previous());
        assertEquals("list", history.previous());
    }
}
