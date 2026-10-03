package ramly.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import ramly.model.Task;
import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Tests the shared contract for commands that add tasks. */
public class AddTaskCommandTest {
    @Test
    public void execute_createTaskReturnsNull_throwsAssertionError() {
        AddTaskCommand command = new AddTaskCommand() {
            @Override
            protected Task createTask() {
                return null;
            }
        };

        assertThrows(AssertionError.class, () -> command.execute(
                new TaskList(new ArrayList<>()), new Ui(message -> { }), new Storage("unused.txt")));
    }
}
