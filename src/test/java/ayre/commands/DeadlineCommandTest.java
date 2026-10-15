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
/** Tests creating deadline tasks and validating their dates. */
public class DeadlineCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_validArguments_addsDeadline() throws Exception {
        LiveTaskList tasks = CommandTestSupport.emptyTasks(tmpDir);

        CommandResult result = new DeadlineCommand(tasks).doCommand(List.of("submit", "2005-05-05"));

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ New mission added:\n[D][ ] submit <by: May 05, 2005>", result.message());
    }

    @Test
    public void doCommand_invalidDate_throwsException() {
        LiveTaskList tasks = CommandTestSupport.emptyTasks(tmpDir);

        assertThrows(InvalidCommandException.class, () ->
                new DeadlineCommand(tasks).doCommand(List.of("submit", "2005-13-05")));
    }
}
