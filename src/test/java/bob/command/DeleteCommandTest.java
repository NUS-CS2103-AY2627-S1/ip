package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;
import bob.task.TaskList;
import bob.task.ToDo;

/**
 * Tests the behavior of {@link DeleteCommand}.
 */
public class DeleteCommandTest extends CommandTestBase {

    @Test
    public void execute_validTaskId_deletesTaskAndSavesToStorage() throws BobException {
        tasks.add(new ToDo("task 1"));
        tasks.add(new ToDo("task 2"));

        DeleteCommand command = new DeleteCommand(1);
        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("T\u00010\u0001task 2", tasks.get(0).export());

        TaskList loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("T\u00010\u0001task 2", loaded.get(0).export());
    }

    @Test
    public void execute_invalidTaskId_throwsBobException() {
        DeleteCommand command = new DeleteCommand(5);
        assertThrows(BobException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void isExit_returnsFalse() {
        DeleteCommand command = new DeleteCommand(1);
        assertFalse(command.isExit());
    }
}
