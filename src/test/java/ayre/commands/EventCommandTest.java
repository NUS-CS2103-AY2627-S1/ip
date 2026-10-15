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
/** Tests creating event tasks and validating their date ranges. */
public class EventCommandTest {
    @TempDir
    Path tmpDir;

    @Test
    public void doCommand_validArguments_addsEvent() throws Exception {
        LiveTaskList tasks = CommandTestSupport.emptyTasks(tmpDir);

        CommandResult result = new EventCommand(tasks).doCommand(
                List.of("conference", "2005-05-05", "2005-05-06"));

        assertEquals(AyreStatus.CONTINUE, result.status());
        assertEquals("~ New mission added:\n[E][ ] conference <from: May 05, 2005 to: May 06, 2005>",
                result.message());
    }

    @Test
    public void doCommand_endDateBeforeStartDate_throwsException() {
        LiveTaskList tasks = CommandTestSupport.emptyTasks(tmpDir);

        assertThrows(InvalidCommandException.class, () ->
                new EventCommand(tasks).doCommand(List.of("conference", "2005-05-06", "2005-05-05")));
    }
}
