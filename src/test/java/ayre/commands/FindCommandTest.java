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
/** Tests searching tasks by name. */
public class FindCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_matchingName_returnsMatchingTasks() throws Exception {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "Study Java");

        CommandResult result = new FindCommand(tasks).doCommand(List.of("java"));

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ These are the matching missions:\n1. [T][ ] Study Java\n"
                + "~ Did you find what you were looking for, Raven?", result.message());
    }
}
