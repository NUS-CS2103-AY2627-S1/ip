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
/** Tests marking tasks as incomplete. */
public class UnmarkCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_validIndex_unmarksTask() throws Exception {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "study");
        new MarkCommand(tasks).doCommand(List.of("1"));

        CommandResult result = new UnmarkCommand(tasks).doCommand(List.of("1"));

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ The mission is still pending, Raven. Let's get to it.\n[T][ ] study",
                result.message());
    }

    @Test
    public void doCommand_invalidIndex_throwsException() {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "study");

        assertThrows(InvalidCommandException.class, () -> new UnmarkCommand(tasks).doCommand(List.of("2")));
    }
}
