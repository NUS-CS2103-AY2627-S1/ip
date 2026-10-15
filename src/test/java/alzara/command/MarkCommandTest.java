package alzara.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alzara.AlzaraException;
import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.task.ToDo;
import alzara.ui.Ui;

/**
 * Tests for {@link MarkCommand}. Each test points {@link Storage} at a fresh
 * JUnit {@code @TempDir} so the persisted save file can be checked without
 * touching the real one.
 */
class MarkCommandTest {
    // A valid index should mark the task done, persist the change, and
    // report it with its updated display line.
    @Test
    void execute_validIndex_marksReportsAndPersistsTask(@TempDir File dataDir) throws AlzaraException {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        MarkCommand command = new MarkCommand(0);

        command.execute(memory, ui, storage);

        assertTrue(memory.get(0).toString().startsWith("[T][X]"));
        assertEquals("You have satisfied the great Alzara.\n[T][X] read book", ui.getAndClearResponse());
        assertEquals("T | Y | read book", storage.load(ui).get(0).toSaveFormat());
    }

    // A negative index should throw without marking any task.
    @Test
    void execute_negativeIndex_exceptionThrownAndNotMarked(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        MarkCommand command = new MarkCommand(-1);

        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                command.execute(memory, ui, storage));

        assertEquals(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE, exception.getMessage());
        assertTrue(memory.get(0).toString().startsWith("[T][ ]"));
    }

    // An index past the end of the list should throw without marking any task.
    @Test
    void execute_indexPastEnd_exceptionThrownAndNotMarked(@TempDir File dataDir) {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        MarkCommand command = new MarkCommand(1);

        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                command.execute(memory, ui, storage));

        assertEquals(AlzaraException.TASK_DOES_NOT_EXIST_MESSAGE, exception.getMessage());
        assertTrue(memory.get(0).toString().startsWith("[T][ ]"));
    }
}
