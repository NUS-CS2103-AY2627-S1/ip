package clover.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests the response styles declared by command types. */
class CommandResponseStyleTest {
    @Test
    void getResponseStyle_commandsWithDedicatedStyles_returnsExpectedStyle() {
        assertEquals(CommandResponseStyle.TASK_ADDED, new ToDoCommand("read book").getResponseStyle());
        assertEquals(CommandResponseStyle.TASK_ADDED,
                new DeadlineCommand("read book /by 2026-09-01").getResponseStyle());
        assertEquals(CommandResponseStyle.TASK_ADDED,
                new EventCommand("study /from 2026-09-01 /to 2026-09-02").getResponseStyle());
        assertEquals(CommandResponseStyle.TUTOREE_ADDED, new AddTutoreeCommand(
                "Alice Tan /address 12 Example Road /fee 50").getResponseStyle());
        assertEquals(CommandResponseStyle.TASK_MARKED, new MarkCommand("1").getResponseStyle());
        assertEquals(CommandResponseStyle.TASK_DELETED, new DeleteCommand("1").getResponseStyle());
    }

    @Test
    void getResponseStyle_commandWithoutDedicatedStyle_returnsStandardStyle() {
        assertEquals(CommandResponseStyle.STANDARD, new ListCommand().getResponseStyle());
    }
}
