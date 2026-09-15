package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import duke.task.TaskList;
import duke.ui.CapturingUi;

public class TodoCommandTest {

    @Test
    public void execute_validDescription_addsTask() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new TodoCommand("read book").execute(tasks, ui);

        assertNull(ui.getLastErrorMessage());
        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.getLast().toDataString());
        assertEquals(tasks.getLast(), ui.getLastAddedTask());
        assertEquals(1, ui.getLastAddedTaskCount());
    }

    @Test
    public void execute_descriptionWithWhitespace_trimsAndAddsTask() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new TodoCommand("  read book  ").execute(tasks, ui);

        assertNull(ui.getLastErrorMessage());
        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book", tasks.getLast().toDataString());
    }

    @Test
    public void execute_emptyDescription_showsError() {
        TaskList tasks = new TaskList();
        CapturingUi ui = new CapturingUi();

        new TodoCommand("   ").execute(tasks, ui);

        assertEquals(0, tasks.size());
        assertEquals("The Todo description cannot be empty!", ui.getLastErrorMessage());
        assertNull(ui.getLastAddedTask());
        assertNull(ui.getLastAddedTaskCount());
    }
}
