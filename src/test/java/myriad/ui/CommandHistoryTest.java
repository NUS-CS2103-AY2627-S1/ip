package myriad.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

// AI-assisted: most of this file was written with Claude Code (Claude Opus),
// used by Ryan Chua, who reviewed and adapted the output.
/**
 * Tests for {@link CommandHistory}: the cursor bookkeeping behind recalling
 * earlier commands with the Up and Down keys.
 */
public class CommandHistoryTest {

    /** Builds a history holding the given commands, sent in the order given. */
    private static CommandHistory historyOf(String... commands) {
        CommandHistory history = new CommandHistory();
        for (String command : commands) {
            history.add(command);
        }
        return history;
    }

    @Test
    public void getPrevious_emptyHistory_empty() {
        assertEquals(Optional.empty(), new CommandHistory().getPrevious());
    }

    @Test
    public void getNext_emptyHistory_empty() {
        assertEquals(Optional.empty(), new CommandHistory().getNext());
    }

    @Test
    public void getPrevious_repeatedCalls_newestFirstThenStopsAtOldest() {
        CommandHistory history = historyOf("list", "todo read book", "mark 1");
        String[] expected = {"mark 1", "todo read book", "list", "list"};
        for (String command : expected) {
            assertEquals(Optional.of(command), history.getPrevious());
        }
    }

    @Test
    public void getNext_notBrowsing_empty() {
        // Down on a fresh line must not wipe out what the user is typing.
        assertEquals(Optional.empty(), historyOf("list").getNext());
    }

    @Test
    public void getNext_afterGoingBack_newerCommandsThenFreshLine() {
        CommandHistory history = historyOf("list", "todo read book", "mark 1");
        history.getPrevious();
        history.getPrevious();
        history.getPrevious();
        assertEquals(Optional.of("todo read book"), history.getNext());
        assertEquals(Optional.of("mark 1"), history.getNext());
        assertEquals(Optional.of(""), history.getNext());
        assertEquals(Optional.empty(), history.getNext());
    }

    @Test
    public void add_whileBrowsing_cursorResetToNewest() {
        CommandHistory history = historyOf("list", "todo read book");
        history.getPrevious();
        history.getPrevious();
        history.add("bye");
        assertEquals(Optional.of("bye"), history.getPrevious());
    }

    @Test
    public void add_blankCommand_notRecorded() {
        CommandHistory history = historyOf("list", "   ");
        assertEquals(Optional.of("list"), history.getPrevious());
        assertEquals(Optional.of("list"), history.getPrevious());
    }

    @Test
    public void add_consecutiveRepeat_recordedOnce() {
        CommandHistory history = historyOf("todo read book", "list", "list");
        assertEquals(Optional.of("list"), history.getPrevious());
        assertEquals(Optional.of("todo read book"), history.getPrevious());
    }

    @Test
    public void add_nonConsecutiveRepeat_recordedAgain() {
        CommandHistory history = historyOf("list", "todo read book", "list");
        assertEquals(Optional.of("list"), history.getPrevious());
        assertEquals(Optional.of("todo read book"), history.getPrevious());
        assertEquals(Optional.of("list"), history.getPrevious());
    }
}
