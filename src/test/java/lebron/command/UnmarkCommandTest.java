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

public class UnmarkCommandTest {
    @Test
    public void execute_validTaskNumber_taskMarkedNotDoneAndReportedToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo todo = new Todo("read book");
        todo.setStatus(true);
        taskList.addTask(todo);
        Command command = new UnmarkCommand(1);

        String response = command.execute(taskList, new Ui());

        assertEquals("[T][ ] read book", taskList.getTask(1).toString());
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void execute_taskNumberOutOfRange_exceptionThrown() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));
        Command command = new UnmarkCommand(0);

        assertThrows(TaskListException.class, () -> command.execute(taskList, new Ui()));
    }
}
