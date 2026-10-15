package yapbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import yapbot.exception.YapBotException;
import yapbot.task.TaskList;
import yapbot.task.Todo;
import yapbot.ui.Ui;

public class ListCommandTest {

    @Test
    public void execute_doesNotModifyTaskList() throws YapBotException {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        ListCommand command = new ListCommand();

        command.execute(taskList, new Ui());

        assertEquals(1, taskList.size());
    }
}
