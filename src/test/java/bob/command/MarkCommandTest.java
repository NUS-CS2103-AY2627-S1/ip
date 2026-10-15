package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;
import bob.task.TaskList;
import bob.task.ToDo;

/**
 * Tests the behavior of {@link MarkCommand}.
 */
public class MarkCommandTest extends CommandTestBase {

    @Test
    public void execute_markAsDone_updatesStatusAndSavesToStorage() throws BobException {
        tasks.add(new ToDo("task to mark"));

        MarkCommand markCmd = new MarkCommand(1, true);
        markCmd.execute(tasks, ui, storage);

        assertEquals("[T][X] task to mark", tasks.get(0).toString());

        TaskList loaded = storage.load();
        assertEquals("[T][X] task to mark", loaded.get(0).toString());
    }

    @Test
    public void execute_unmarkAsDone_updatesStatusAndSavesToStorage() throws BobException {
        ToDo todo = new ToDo("task to unmark");
        todo.mark();
        tasks.add(todo);

        MarkCommand unmarkCmd = new MarkCommand(1, false);
        unmarkCmd.execute(tasks, ui, storage);

        assertEquals("[T][ ] task to unmark", tasks.get(0).toString());

        TaskList loaded = storage.load();
        assertEquals("[T][ ] task to unmark", loaded.get(0).toString());
    }

    @Test
    public void execute_invalidTaskId_throwsBobException() {
        MarkCommand command = new MarkCommand(3, true);
        assertThrows(BobException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void isExit_returnsFalse() {
        MarkCommand command = new MarkCommand(1, true);
        assertFalse(command.isExit());
    }
}
