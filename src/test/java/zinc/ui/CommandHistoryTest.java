package zinc.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests navigation through commands submitted during one session. */
public class CommandHistoryTest {
    @Test
    public void navigateUp_emptyHistory_preservesCurrentInput() {
        CommandHistory commandHistory = new CommandHistory();

        assertEquals("draft", commandHistory.navigateUp("draft"));
    }

    @Test
    public void navigateUp_multipleCommands_returnsNewestToOldest() {
        CommandHistory commandHistory = createHistory("first", "second", "third");

        assertEquals("third", commandHistory.navigateUp("draft"));
        assertEquals("second", commandHistory.navigateUp("third"));
        assertEquals("first", commandHistory.navigateUp("second"));
        assertEquals("first", commandHistory.navigateUp("first"));
    }

    @Test
    public void navigateDown_afterNavigatingUp_returnsCommandsTowardNewest() {
        CommandHistory commandHistory = createHistory("first", "second", "third");

        commandHistory.navigateUp("draft");
        commandHistory.navigateUp("third");

        assertEquals("third", commandHistory.navigateDown("second"));
        assertEquals("draft", commandHistory.navigateDown("third"));
        assertEquals("draft", commandHistory.navigateDown("draft"));
    }

    @Test
    public void navigateDown_withoutNavigation_preservesCurrentInput() {
        CommandHistory commandHistory = createHistory("first");

        assertEquals("draft", commandHistory.navigateDown("draft"));
    }

    @Test
    public void addCommand_whileNavigating_resetsToNewHistoryPosition() {
        CommandHistory commandHistory = createHistory("first", "second");
        commandHistory.navigateUp("draft");

        commandHistory.addCommand("third");

        assertEquals("third", commandHistory.navigateUp("new draft"));
    }

    /** Creates a history containing the supplied commands in submission order. */
    private CommandHistory createHistory(String... commands) {
        CommandHistory commandHistory = new CommandHistory();
        for (String command : commands) {
            commandHistory.addCommand(command);
        }
        return commandHistory;
    }
}
