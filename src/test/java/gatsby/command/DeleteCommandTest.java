package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import gatsby.exception.EmptyPayloadException;
import gatsby.exception.InvalidTaskException;
import gatsby.model.TaskList;
import gatsby.model.Todo;
import gatsby.testutil.RecordingUi;

/** Tests deleting tasks and validating task selections. */
class DeleteCommandTest extends AbstractCommandTest {

    /** Verifies that deleting a task removes it and reports the new count. */
    @Test
    void execute_validTaskNumber_removesTaskAndReportsCount() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));
        RecordingUi ui = recordingUi();

        new DeleteCommand("1").execute(tasks, ui);

        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getTaskName());
        assertEquals(List.of(" Noted. I've removed this task:",
                "  [T][ ] first", " Now you have 1 task in the list."), ui.messages());
    }

    /** Verifies that deleting without a task number is rejected. */
    @Test
    void execute_missingTaskNumber_throwsException() {
        assertThrows(EmptyPayloadException.class, () ->
                new DeleteCommand("").execute(new TaskList(), recordingUi()));
    }

    /** Verifies that deleting an out-of-range task is rejected. */
    @Test
    void execute_outOfRangeTaskNumber_throwsException() {
        assertThrows(InvalidTaskException.class, () ->
                new DeleteCommand("3").execute(
                        new TaskList(List.of(new Todo("only task"))), recordingUi()));
    }
}
