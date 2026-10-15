package ayre.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.enums.AyreStatus;

// Refactored by GPT-5.6 Luna
/** Tests creating Todo tasks. */
public class TodoCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_validName_addsTodo() throws Exception {
        LiveTaskList tasks = CommandTestSupport.emptyTasks(tmpDir);

        CommandResult result = new TodoCommand(tasks).doCommand(List.of("read book"));

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ New mission added:\n[T][ ] read book", result.message());
        assertEquals(1, tasks.getNumTasks());
    }
}
