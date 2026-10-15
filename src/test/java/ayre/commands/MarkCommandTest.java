package ayre.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.enums.AyreStatus;
import ayre.exceptions.InvalidCommandException;

// Refactored by GPT-5.6 Luna
/** Tests marking tasks as complete. */
public class MarkCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_validIndex_marksTaskComplete() throws Exception {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "study");

        CommandResult result = new MarkCommand(tasks).doCommand(List.of("1"));

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ Mission complete. Good work, Raven.\n[T][+] study", result.message());
    }

    @Test
    public void doCommand_invalidIndex_throwsException() {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "study");

        assertThrows(InvalidCommandException.class, () -> new MarkCommand(tasks).doCommand(List.of("2")));
        assertThrows(InvalidCommandException.class, () -> new MarkCommand(tasks).doCommand(List.of("not-number")));
    }
}
