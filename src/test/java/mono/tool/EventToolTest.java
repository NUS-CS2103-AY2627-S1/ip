package mono.tool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mono.MonoBot;
import mono.exception.MonoException;
import mono.exception.WrongFormatException;
import mono.storage.Storage;
import mono.task.Task;
import mono.testutil.StorageTestSupport;

/**
 * Tests event command validation and task creation.
 */
public class EventToolTest {
    private static final String FORMAT_MESSAGE =
            "Event format: event <description> /from <start-date> /to <end-date>";
    private static final String DATE_MESSAGE =
            "Event dates must use the format yyyy-MM-dd (for example, 2019-10-15)";

    @BeforeEach
    public void resetStorage() throws IOException {
        StorageTestSupport.resetStorage();
    }

    @Test
    public void invoke_validArguments_addsEventAndContinues() throws MonoException {
        ToolSignal signal = new EventTool().invoke(
                "project retreat /from 2026-09-01 /to 2026-09-03", new MonoBot());

        assertEquals(ToolSignal.CONTINUE, signal);
        assertEquals(
                "E | 0 | project retreat | 2026-09-01 | 2026-09-03",
                onlyStoredTask().toFileString());
    }

    @Test
    public void invoke_missingFromMarker_throwsWrongFormatException() {
        assertFormatRejected("project retreat /to 2026-09-03");
    }

    @Test
    public void invoke_missingToMarker_throwsWrongFormatException() {
        assertFormatRejected("project retreat /from 2026-09-01");
    }

    @Test
    public void invoke_markersInWrongOrder_throwsWrongFormatException() {
        assertFormatRejected("project retreat /to 2026-09-03 /from 2026-09-01");
    }

    @Test
    public void invoke_emptyDescription_throwsWrongFormatException() {
        assertFormatRejected(" /from 2026-09-01 /to 2026-09-03");
    }

    @Test
    public void invoke_emptyStartDate_throwsWrongFormatException() {
        assertFormatRejected("project retreat /from  /to 2026-09-03");
    }

    @Test
    public void invoke_emptyEndDate_throwsWrongFormatException() {
        assertFormatRejected("project retreat /from 2026-09-01 /to ");
    }

    @Test
    public void invoke_descriptionWithStorageDelimiter_throwsWrongFormatException() {
        assertFormatRejected("project | retreat /from 2026-09-01 /to 2026-09-03");
    }

    @Test
    public void invoke_invalidStartDate_throwsWrongFormatException() {
        assertDateRejected("project retreat /from invalid /to 2026-09-03");
    }

    @Test
    public void invoke_invalidEndDate_throwsWrongFormatException() {
        assertDateRejected("project retreat /from 2026-09-01 /to invalid");
    }

    private void assertFormatRejected(String arguments) {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new EventTool().invoke(arguments, new MonoBot()));

        assertEquals(FORMAT_MESSAGE, exception.getMessage());
    }

    private void assertDateRejected(String arguments) {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new EventTool().invoke(arguments, new MonoBot()));

        assertEquals(DATE_MESSAGE, exception.getMessage());
    }

    private Task onlyStoredTask() {
        Task[] tasks = new Storage().readStorage();
        assertEquals(1, tasks.length);
        return tasks[0];
    }
}
