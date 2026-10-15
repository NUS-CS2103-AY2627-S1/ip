package squirtlebot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests value equality for command classes that override {@link Object#equals(Object)}.
 */
public class CommandEqualityTest {
    @Test
    public void addToDoCommand_sameAttributes_commandsEqual() {
        Command first = new AddToDoCommand("todo read textbook".split(" "));
        Command equivalent = new AddToDoCommand("todo read textbook".split(" "));
        Command different = new AddToDoCommand("todo buy textbook".split(" "));

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void addDeadlineCommand_sameAttributes_commandsEqual() {
        Command first = new AddDeadlineCommand("deadline submit report /by 30-09-2026".split(" "));
        Command equivalent = new AddDeadlineCommand("deadline submit report /by 30-09-2026".split(" "));
        Command different = new AddDeadlineCommand("deadline submit report /by 01-10-2026".split(" "));

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void addEventCommand_sameAttributes_commandsEqual() {
        Command first = new AddEventCommand(
                "event meeting /from 30-09-2026 /to 01-10-2026".split(" "));
        Command equivalent = new AddEventCommand(
                "event meeting /from 30-09-2026 /to 01-10-2026".split(" "));
        Command different = new AddEventCommand(
                "event meeting /from 02-10-2026 /to 03-10-2026".split(" "));

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void markCommand_sameIndex_commandsEqual() {
        Command first = new MarkCommand(new String[]{"mark", "1"});
        Command equivalent = new MarkCommand(new String[]{"mark", "1"});
        Command different = new MarkCommand(new String[]{"mark", "2"});

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void unmarkCommand_sameIndex_commandsEqual() {
        Command first = new UnmarkCommand(new String[]{"unmark", "1"});
        Command equivalent = new UnmarkCommand(new String[]{"unmark", "1"});
        Command different = new UnmarkCommand(new String[]{"unmark", "2"});

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void deleteCommand_sameIndex_commandsEqual() {
        Command first = new DeleteCommand(new String[]{"delete", "1"});
        Command equivalent = new DeleteCommand(new String[]{"delete", "1"});
        Command different = new DeleteCommand(new String[]{"delete", "2"});

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void findCommand_sameSearchPattern_commandsEqual() {
        Command first = new FindCommand(new String[]{"find", "project", "meeting"});
        Command equivalent = new FindCommand(new String[]{"find", "project", "meeting"});
        Command different = new FindCommand(new String[]{"find", "team", "meeting"});

        assertEqualityBehavior(first, equivalent, different);
    }

    @Test
    public void confirmCommand_sameIndices_commandsEqual() {
        Command first = new ConfirmEventDateCommand(new String[]{"confirm", "1", "2"});
        Command equivalent = new ConfirmEventDateCommand(new String[]{"confirm", "1", "2"});
        Command different = new ConfirmEventDateCommand(new String[]{"confirm", "1", "1"});

        assertEqualityBehavior(first, equivalent, different);
    }

    /**
     * Verifies the common equality behavior expected from each value-based command.
     */
    private void assertEqualityBehavior(Command first, Command equivalent, Command different) {
        assertTrue(first.equals(first));
        assertEquals(first, equivalent);
        assertEquals(equivalent, first);
        assertNotEquals(first, different);
        assertNotEquals(first, null);
        assertNotEquals(first, new Object());
    }
}
