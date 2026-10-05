package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import gatsby.exception.EmptyMarkingException;
import gatsby.exception.InvalidTaskException;
import gatsby.model.TaskList;
import gatsby.model.Todo;
import gatsby.testutil.RecordingUi;

/** Tests marking tasks and reporting repeated or invalid marking requests. */
class MarkCommandTest extends AbstractCommandTest {

    /** Verifies that an unfinished task becomes done and is reported as changed. */
    @Test
    void execute_unfinishedTask_marksTaskAndReportsChange() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        RecordingUi ui = recordingUi();

        new MarkCommand("1").execute(tasks, ui);

        assertTrue(tasks.get(0).isDone());
        assertEquals(List.of(" Nice! I've marked this task as done:",
                "  [T][X] read book"), ui.messages());
    }

    /** Verifies that marking an already done task reports the unchanged state. */
    @Test
    void execute_alreadyDoneTask_reportsAlreadyDone() throws Exception {
        Todo todo = new Todo("read book");
        todo.markDone();
        TaskList tasks = new TaskList(List.of(todo));
        RecordingUi ui = recordingUi();

        new MarkCommand("1").execute(tasks, ui);

        assertEquals(List.of(" That one was already done, but sure:",
                "  [T][X] read book"), ui.messages());
    }

    /** Verifies that marking without a task number is rejected. */
    @Test
    void execute_missingTaskNumber_throwsException() {
        assertThrows(EmptyMarkingException.class, () ->
                new MarkCommand("").execute(new TaskList(), recordingUi()));
    }

    /** Verifies that a task number outside the list is rejected. */
    @Test
    void execute_outOfRangeTaskNumber_throwsException() {
        assertThrows(InvalidTaskException.class, () ->
                new MarkCommand("2").execute(
                        new TaskList(List.of(new Todo("read book"))), recordingUi()));
    }
}
