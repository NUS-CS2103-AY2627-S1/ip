package lebron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.exception.LeBronException;
import lebron.task.TaskList;
import lebron.task.Todo;
import lebron.ui.Ui;

public class FindCommandTest {
    @Test
    public void execute_matchingKeyword_reportsMatchingTasksToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));
        taskList.addTask(new Todo("write essay"));

        String response = new FindCommand("book").execute(taskList, new Ui());

        assertTrue(response.contains("[T][ ] read book"));
        assertFalse(response.contains("write essay"));
    }

    @Test
    public void execute_noMatch_reportsEmptyListToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));

        String response = new FindCommand("essay").execute(taskList, new Ui());

        assertFalse(response.contains("[T]"));
        assertEquals("Here's what's matching your search, chief!", response);
    }

    @Test
    public void isExit_findCommand_returnsFalse() {
        assertFalse(new FindCommand("book").isExit());
    }
}
