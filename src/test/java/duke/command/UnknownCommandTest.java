package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import duke.exception.GaryException;
import duke.task.TaskList;
import duke.ui.CapturingUi;

public class UnknownCommandTest {

    @Test
    public void execute_throwsGaryExceptionWithMessage() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        GaryException exception = assertThrows(GaryException.class, () ->
                new UnknownCommand().execute(tasks, ui));

        assertEquals("I'm sorry, but Gary doesn't know what that means!", exception.getMessage());
    }
}
