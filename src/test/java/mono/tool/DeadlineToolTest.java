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
 * Tests deadline command validation and task creation.
 */
public class DeadlineToolTest {
    private static final String FORMAT_MESSAGE =
            "Deadline format: deadline <description> /by <date>";
    private static final String DATE_MESSAGE =
            "Deadline date must use the format yyyy-MM-dd (for example, 2019-10-15)";

    @BeforeEach
    public void resetStorage() throws IOException {
        StorageTestSupport.resetStorage();
    }

    @Test
    public void invoke_validArguments_addsDeadlineAndContinues() throws MonoException {
        ToolSignal signal = new DeadlineTool().invoke(
                "submit report /by 2026-09-01", new MonoBot());

        assertEquals(ToolSignal.CONTINUE, signal);
        assertEquals(
                "D | 0 | submit report | 2026-09-01",
                onlyStoredTask().toFileString());
    }

    @Test
    public void invoke_descriptionContainingByMarker_usesLastMarkerAsSeparator()
            throws MonoException {
        new DeadlineTool().invoke(
                "discuss /by marker /by 2026-09-01", new MonoBot());

        assertEquals(
                "D | 0 | discuss /by marker | 2026-09-01",
                onlyStoredTask().toFileString());
    }

    @Test
    public void invoke_missingByMarker_throwsWrongFormatException() {
        assertFormatRejected("submit report 2026-09-01");
    }

    @Test
    public void invoke_emptyDescription_throwsWrongFormatException() {
        assertFormatRejected(" /by 2026-09-01");
    }

    @Test
    public void invoke_emptyDate_throwsWrongFormatException() {
        assertFormatRejected("submit report /by ");
    }

    @Test
    public void invoke_descriptionWithStorageDelimiter_throwsWrongFormatException() {
        assertFormatRejected("submit | report /by 2026-09-01");
    }

    @Test
    public void invoke_invalidDate_throwsWrongFormatException() {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new DeadlineTool().invoke(
                        "submit report /by 2026-09-31", new MonoBot()));

        assertEquals(DATE_MESSAGE, exception.getMessage());
    }

    private void assertFormatRejected(String arguments) {
        WrongFormatException exception = assertThrows(WrongFormatException.class, () ->
                new DeadlineTool().invoke(arguments, new MonoBot()));

        assertEquals(FORMAT_MESSAGE, exception.getMessage());
    }

    private Task onlyStoredTask() {
        Task[] tasks = new Storage().readStorage();
        assertEquals(1, tasks.length);
        return tasks[0];
    }
}
