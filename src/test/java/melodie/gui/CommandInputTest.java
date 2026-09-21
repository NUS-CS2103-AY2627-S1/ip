package melodie.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class CommandInputTest {
    @Test
    public void historyNavigation_multipleCommands_walksBothDirectionsAndRestoresDraft() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find book");

        assertEquals(Optional.of("find book"), history.getPreviousCommand("todo unfinished"));
        assertEquals(Optional.of("list"), history.getPreviousCommand("find book"));
        assertEquals(Optional.empty(), history.getPreviousCommand("list"));
        assertEquals(Optional.of("find book"), history.getNextCommand());
        assertEquals(Optional.of("todo unfinished"), history.getNextCommand());
        assertEquals(Optional.empty(), history.getNextCommand());
    }

    @Test
    public void historyNavigation_recalledCommandEdited_startsAgainFromNewestCommand() {
        CommandHistory history = new CommandHistory();
        history.record("list");
        history.record("find book");
        history.getPreviousCommand("");

        assertTrue(history.isBrowsing());
        history.stopBrowsing();
        assertFalse(history.isBrowsing());
        assertEquals(Optional.of("find book"), history.getPreviousCommand("find edited"));
    }

    @Test
    public void commandHint_deadlineFields_showsOnlyMissingFields() {
        assertEquals("<description> /by <due date d/M/yyyy|today|tomorrow|weekday HHmm>",
                CommandHint.getMissingFields("deadline"));
        assertEquals("/by <due date d/M/yyyy|today|tomorrow|weekday HHmm>",
                CommandHint.getMissingFields("deadline return book"));
        assertEquals("<due date d/M/yyyy|today|tomorrow|weekday HHmm>",
                CommandHint.getMissingFields("deadline return book /by"));
        assertEquals("<due time HHmm>",
                CommandHint.getMissingFields("deadline return book /by 2/12/2019"));
        assertEquals("<due time HHmm>",
                CommandHint.getMissingFields("deadline return book /by tomorrow"));
        assertEquals("",
                CommandHint.getMissingFields("deadline return book /by 2/12/2019 1800"));
    }

    @Test
    public void commandHint_eventFields_showsOnlyMissingFields() {
        assertEquals("<description> /from <start date d/M/yyyy|today|tomorrow|weekday HHmm> "
                        + "/to <end date d/M/yyyy|today|tomorrow|weekday HHmm>",
                CommandHint.getMissingFields("event"));
        assertEquals("/from <start date d/M/yyyy|today|tomorrow|weekday HHmm> "
                        + "/to <end date d/M/yyyy|today|tomorrow|weekday HHmm>",
                CommandHint.getMissingFields("event meeting"));
        assertEquals("/to <end date d/M/yyyy|today|tomorrow|weekday HHmm>",
                CommandHint.getMissingFields("event meeting /from 2/12/2019 1400"));
        assertEquals("<end time HHmm>",
                CommandHint.getMissingFields("event meeting /from 2/12/2019 1400 /to 2/12/2019"));
        assertEquals("",
                CommandHint.getMissingFields(
                        "event meeting /from 2/12/2019 1400 /to 2/12/2019 1600"));
    }

    @Test
    public void commandHint_simpleCommands_showsRequiredArgumentOnlyWhenMissing() {
        assertEquals("<description>", CommandHint.getMissingFields("todo"));
        assertEquals("", CommandHint.getMissingFields("todo read book"));
        assertEquals("<task number>", CommandHint.getMissingFields("MARK"));
        assertEquals("", CommandHint.getMissingFields("mark 2"));
        assertEquals("<keyword>", CommandHint.getMissingFields("find"));
        assertEquals("", CommandHint.getMissingFields("find book"));
        assertEquals("", CommandHint.getMissingFields("list"));
        assertEquals("", CommandHint.getMissingFields("bye"));
    }

    @Test
    public void commandHint_updateFields_showsOnlyMissingFields() {
        assertEquals("<task number> </description|/by|/from|/to> <new value>",
                CommandHint.getMissingFields("update"));
        assertEquals("</description|/by|/from|/to> <new value>",
                CommandHint.getMissingFields("update 2"));
        assertEquals("<new value>",
                CommandHint.getMissingFields("update 2 /description"));
        assertEquals("",
                CommandHint.getMissingFields("update 2 /description submit final report"));
    }
}
