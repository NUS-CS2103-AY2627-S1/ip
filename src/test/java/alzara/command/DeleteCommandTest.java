package alzara.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.AlzaraException;
import alzara.storage.Storage;
import alzara.task.Task;
import alzara.task.TaskList;
import alzara.task.ToDo;
import alzara.ui.Ui;

/**
 * Tests for {@link DeleteCommand}. Each test points {@link Storage} at a
 * fresh JUnit {@code @TempDir} so the persisted save file can be checked
 * without touching the real one.
 */
class DeleteCommandTest {
    // A valid index should remove that task, shift the rest down, persist
    // the change, and report the removed task and the remaining count.
    @Test
    void execute_validIndex_removesReportsAndPersistsChange(@TempDir File dataDir) throws AlzaraException {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        memory.add(new ToDo("borrow book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        DeleteCommand command = new DeleteCommand(0);

        command.execute(memory, ui, storage);

        assertEquals(1, memory.size());
        assertEquals("[T][ ] borrow book", memory.get(0).toString());
        assertEquals("I have removed the task [T][ ] read book\n1 tasks remain.",
                ui.getAndClearResponse());
        ArrayList<Task> persisted = storage.load(ui);
        assertEquals(1, persisted.size());
        assertEquals("T | N | borrow book", persisted.get(0).toSaveFormat());
    }

    // A negative index should throw without deleting any task.
    @Test
    void execute_negativeIndex_exceptionThrownAndNotDeleted(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        DeleteCommand command = new DeleteCommand(-1);

        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                command.execute(memory, ui, storage));

        assertEquals(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE, exception.getMessage());
        assertEquals(1, memory.size());
    }

    // An index past the end of the list should throw without deleting any task.
    @Test
    void execute_indexPastEnd_exceptionThrownAndNotDeleted(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        DeleteCommand command = new DeleteCommand(1);

        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                command.execute(memory, ui, storage));

        assertEquals(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE, exception.getMessage());
        assertEquals(1, memory.size());
    }
}
