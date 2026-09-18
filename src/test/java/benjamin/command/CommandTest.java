package benjamin.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import benjamin.BenjaminException;
import benjamin.storage.Storage;
import benjamin.task.TaskList;
import benjamin.task.Todo;
import benjamin.ui.Ui;

public class CommandTest {
    @TempDir
    Path root;

    private Storage storage() {
        return new Storage(root.toString(), "data", "benjamin.txt");
    }

    private static TaskList listOfOne() {
        return TaskList.of(new Todo("read book"));
    }

    @Test
    public void execute_taskNumberAboveSize_exceptionNamesTheValidRange() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> new MarkCommand(2).execute(listOfOne(), new Ui(), storage()));

        assertEquals("Choose a task number between 1 and 1.", exception.getMessage());
    }

    @Test
    public void execute_taskNumberZero_exceptionThrown() {
        assertThrows(
                BenjaminException.class, () -> new DeleteCommand(0).execute(listOfOne(), new Ui(), storage()));
    }

    @Test
    public void execute_emptyList_exceptionNamesTheAction() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> new UnmarkCommand(1).execute(new TaskList(), new Ui(), storage()));

        assertEquals("There are no tasks to unmark.", exception.getMessage());
    }

    @Test
    public void execute_markThenUnmark_statusFollowsTheCommands() throws BenjaminException {
        TaskList tasks = listOfOne();
        Storage storage = storage();

        new MarkCommand(1).execute(tasks, new Ui(), storage);
        assertEquals("[T][X] read book", tasks.get(0).toString());

        new UnmarkCommand(1).execute(tasks, new Ui(), storage);
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void execute_delete_removesOnlyTheChosenTask() throws BenjaminException {
        TaskList tasks = listOfOne();
        tasks.add(new Todo("join sports club"));

        new DeleteCommand(1).execute(tasks, new Ui(), storage());

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] join sports club", tasks.get(0).toString());
    }

    @Test
    public void execute_addCommand_appendsToTheList() throws BenjaminException {
        TaskList tasks = listOfOne();

        new AddCommand(new Todo("join sports club")).execute(tasks, new Ui(), storage());

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] join sports club", tasks.get(1).toString());
    }

    @Test
    public void isExit_exitCommandOnly_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
        assertFalse(new ListCommand().isExit());
        assertFalse(new MarkCommand(1).isExit());
    }
}
