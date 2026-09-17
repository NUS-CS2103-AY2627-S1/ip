package lebron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.exception.LeBronException;
import lebron.task.TaskList;
import lebron.task.TaskListException;
import lebron.task.Todo;
import lebron.ui.Ui;

public class DeleteCommandTest {
    @Test
    public void execute_validTaskNumber_taskRemovedAndReportedToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));
        Command command = new DeleteCommand(1);

        String response = command.execute(taskList, new Ui());

        assertEquals(0, taskList.size());
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("0 tasks left to grind now!"));
    }

    @Test
    public void execute_taskNumberOutOfRange_exceptionThrownAndListUnchanged() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));
        Command command = new DeleteCommand(5);

        assertThrows(TaskListException.class, () -> command.execute(taskList, new Ui()));
        assertEquals(1, taskList.size());
    }
}
