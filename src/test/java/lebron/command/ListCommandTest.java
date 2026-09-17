package lebron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.exception.LeBronException;
import lebron.task.TaskList;
import lebron.task.Todo;
import lebron.ui.Ui;

public class ListCommandTest {
    @Test
    public void execute_reportsTaskListToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));

        String response = new ListCommand().execute(taskList, new Ui());

        assertEquals(taskList.toString(), response);
    }

    @Test
    public void isExit_listCommand_returnsFalse() {
        assertFalse(new ListCommand().isExit());
    }
}
