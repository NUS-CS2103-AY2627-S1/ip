package ayre.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ayre.CommandResult;
import ayre.LiveTaskList;
import ayre.enums.AyreStatus;

// Refactored by GPT-5.6 Luna
/** Tests listing the current tasks. */
public class ListCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_withTasks_returnsTaskList() throws Exception {
        LiveTaskList tasks = CommandTestSupport.tasksWithTodo(tmpDir, "study");

        CommandResult result = new ListCommand(tasks).doCommand(new ArrayList<>());

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ Current missions:\n1. [T][ ] study\n"
                + "~ You have 1 pending mission. Let's do this.", result.message());
    }
}
