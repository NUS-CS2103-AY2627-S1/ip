package gatsby.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import gatsby.exception.EmptyMarkingException;
import gatsby.model.TaskList;
import gatsby.model.Todo;
import gatsby.testutil.RecordingUi;

/** Tests unmarking tasks and reporting repeated or invalid unmarking requests. */
class UnmarkCommandTest extends AbstractCommandTest {

    /** Verifies that a done task becomes unfinished and is reported as changed. */
    @Test
    void execute_doneTask_unmarksTaskAndReportsChange() throws Exception {
        Todo todo = new Todo("read book");
        todo.markDone();
        TaskList tasks = new TaskList(List.of(todo));
        RecordingUi ui = recordingUi();

        new UnmarkCommand("1").execute(tasks, ui);

        assertFalse(tasks.get(0).isDone());
        assertEquals(List.of(" OK, I've marked this task as not done yet:",
                "  [T][ ] read book"), ui.messages());
    }

    /** Verifies that unmarking an unfinished task reports the unchanged state. */
    @Test
    void execute_alreadyUnfinishedTask_reportsAlreadyUnfinished() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        RecordingUi ui = recordingUi();

        new UnmarkCommand("1").execute(tasks, ui);

        assertEquals(List.of(" That one wasn't done yet, but sure:",
                "  [T][ ] read book"), ui.messages());
    }

    /** Verifies that unmarking without a task number is rejected. */
    @Test
    void execute_missingTaskNumber_throwsException() {
        assertThrows(EmptyMarkingException.class, () ->
                new UnmarkCommand("").execute(new TaskList(), recordingUi()));
    }
}
