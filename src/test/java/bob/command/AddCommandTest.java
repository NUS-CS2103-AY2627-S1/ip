package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;
import bob.task.TaskList;
import bob.task.ToDo;

/**
 * Tests the behavior of {@link AddCommand}.
 */
public class AddCommandTest extends CommandTestBase {

    @Test
    public void execute_validTask_addsTaskAndSavesToStorage() throws BobException {
        AddCommand command = new AddCommand(new ToDo("read book"));
        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("T\u00010\u0001read book", tasks.get(0).export());

        TaskList loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("T\u00010\u0001read book", loaded.get(0).export());
    }

    @Test
    public void isExit_returnsFalse() {
        AddCommand command = new AddCommand(new ToDo("sample"));
        assertFalse(command.isExit());
    }
}
