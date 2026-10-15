package yapbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import yapbot.task.TaskList;
import yapbot.ui.Ui;

public class ByeCommandTest {

    @Test
    public void isExit_always_returnsTrue() {
        ByeCommand command = new ByeCommand();

        assertTrue(command.isExit());
    }

    @Test
    public void execute_doesNotModifyTaskList() {
        ByeCommand command = new ByeCommand();
        TaskList taskList = new TaskList();

        command.execute(taskList, new Ui());

        assertEquals(0, taskList.size());
    }
}
