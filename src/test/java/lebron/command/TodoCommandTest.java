package lebron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.exception.LeBronException;
import lebron.task.TaskList;
import lebron.ui.Ui;

public class TodoCommandTest {
    @Test
    public void execute_validDescription_taskAddedToListAndReportedToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Command command = new TodoCommand("read book");

        String response = command.execute(taskList, new Ui());

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("1 tasks left to grind now!"));
    }

    @Test
    public void execute_duplicateTodo_exceptionThrownAndListUnchanged() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        new TodoCommand("read book").execute(taskList, new Ui());
        Command duplicate = new TodoCommand("read book");

        assertThrows(LeBronException.class, () -> duplicate.execute(taskList, new Ui()));
        assertEquals(1, taskList.size());
    }

    @Test
    public void isExit_todoCommand_returnsFalse() {
        assertEquals(false, new TodoCommand("read book").isExit());
    }
}
