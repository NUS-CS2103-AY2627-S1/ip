package neil.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests for storing and navigating recently submitted commands.
 */
class CommandHistoryTest {
    @Test
    void getPreviousAndNext_emptyHistory_emptyResultsReturned() {
        CommandHistory history = new CommandHistory();

        assertTrue(history.getPrevious("").isEmpty());
        assertTrue(history.getNext().isEmpty());
    }

    @Test
    void getPreviousAndNext_populatedHistory_terminalNavigationUsed() {
        CommandHistory history = new CommandHistory();
        history.add("first");
        history.add("second");
        history.add("third");

        assertEquals("third", history.getPrevious("unfinished command").orElseThrow());
        assertEquals("second", history.getPrevious("third").orElseThrow());
        assertEquals("first", history.getPrevious("second").orElseThrow());
        assertEquals("first", history.getPrevious("first").orElseThrow());
        assertEquals("second", history.getNext().orElseThrow());
        assertEquals("third", history.getNext().orElseThrow());
        assertEquals("unfinished command", history.getNext().orElseThrow());
        assertTrue(history.getNext().isEmpty());
    }

    @Test
    void add_moreThanMaximumCommands_onlyLatestTenRetained() {
        CommandHistory history = new CommandHistory();
        for (int i = 1; i <= 11; i++) {
            history.add("command " + i);
        }

        List<String> navigatedCommands = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            navigatedCommands.add(history.getPrevious("").orElseThrow());
        }

        assertEquals(
                List.of(
                        "command 11", "command 10", "command 9", "command 8", "command 7",
                        "command 6", "command 5", "command 4", "command 3", "command 2"),
                navigatedCommands);
        assertEquals("command 2", history.getPrevious("").orElseThrow());
    }

    @Test
    void add_afterNavigation_navigationRestartsFromNewestCommand() {
        CommandHistory history = new CommandHistory();
        history.add("first");
        history.add("second");
        assertEquals("second", history.getPrevious("").orElseThrow());
        assertEquals("first", history.getPrevious("second").orElseThrow());

        history.add("third");

        assertEquals("third", history.getPrevious("").orElseThrow());
    }

    @Test
    void add_duplicateCommands_bothCommandsRetained() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.add("help");
        history.add("list");

        assertEquals("list", history.getPrevious("").orElseThrow());
        assertEquals("help", history.getPrevious("list").orElseThrow());
        assertEquals("list", history.getPrevious("help").orElseThrow());
    }

    @Test
    void add_blankCommand_commandNotStored() {
        CommandHistory history = new CommandHistory();

        history.add("   ");

        assertTrue(history.getPrevious("").isEmpty());
    }
}
