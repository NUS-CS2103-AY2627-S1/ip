package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import duke.task.TaskList;
import duke.ui.CapturingUi;

public class ListCommandTest {

    @Test
    public void execute_showsTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTodo("read book");
        tasks.addTodo("write notes");

        CapturingUi ui = new CapturingUi();

        new ListCommand().execute(tasks, ui);

        assertEquals(2, ui.getLastShownTaskList().size());
        assertEquals("T | 0 | read book", ui.getLastShownTaskList().get(0).toDataString());
        assertEquals("T | 0 | write notes", ui.getLastShownTaskList().get(1).toDataString());
    }
}
