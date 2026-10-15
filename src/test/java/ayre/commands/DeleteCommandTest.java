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
/** Tests deleting tasks. */
public class DeleteCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_validIndex_deletesTask() throws Exception {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "study");

        CommandResult result = new DeleteCommand(tasks).doCommand(List.of("1"));

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ The mission has been dropped.\nDeleted: [T][ ] study", result.message());
        assertEquals(0, tasks.getNumTasks());
    }

    @Test
    public void doCommand_invalidIndex_throwsException() {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "study");

        assertThrows(InvalidCommandException.class, () -> new DeleteCommand(tasks).doCommand(List.of("2")));
    }
}
