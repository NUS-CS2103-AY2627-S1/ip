package alzara.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
 * Tests for {@link AddCommand}. Each test points {@link Storage} at a fresh
 * JUnit {@code @TempDir}, so persistence is checked against an isolated save
 * file rather than the real one. {@link Ui} is used in GUI mode so the exact
 * reported text can be read back via {@link Ui#getAndClearResponse()}.
 */
class AddCommandTest {
    // A new, non-duplicate task should be added, persisted, and reported
    // with its flavour text, its own display line, and the new task count.
    @Test
    void execute_newTask_addsReportsAndPersistsTask(@TempDir File dataDir) throws AlzaraException {
        TaskList memory = new TaskList(new ArrayList<>());
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        ToDo task = new ToDo("read book");
        AddCommand command = new AddCommand(task, "You have something to do...");

        command.execute(memory, ui, storage);

        assertEquals(1, memory.size());
        assertEquals("You have something to do...\n[T][ ] read book\nYou have 1 tasks.",
                ui.getAndClearResponse());
        assertEquals(1, storage.load(ui).size());
        assertEquals("T | N | read book", storage.load(ui).get(0).toSaveFormat());
    }

    // Adding a task with the same details as one already in memory should
    // throw instead of adding a second copy.
    @Test
    void execute_duplicateTask_exceptionThrownAndNotAdded(@TempDir File dataDir) throws AlzaraException {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        AddCommand command = new AddCommand(new ToDo("read book"), "You have something to do...");

        AlzaraException exception = assertThrows(AlzaraException.class, () ->
                command.execute(memory, ui, storage));

        assertEquals(AlzaraException.DUPLICATE_TASK_MESSAGE, exception.getMessage());
        assertEquals(1, memory.size());
    }

    // Adding a second, different task should report the updated task count,
    // not reset it.
    @Test
    void execute_secondTask_reportsUpdatedTaskCount(@TempDir File dataDir) throws AlzaraException {
        TaskList memory = new TaskList(new ArrayList<>());
        memory.add(new ToDo("read book"));
        Ui ui = new Ui(true);
        Storage storage = new Storage(dataDir);
        AddCommand command = new AddCommand(new ToDo("borrow book"), "You have something to do...");

        command.execute(memory, ui, storage);

        assertEquals(2, memory.size());
        assertEquals("You have something to do...\n[T][ ] borrow book\nYou have 2 tasks.",
                ui.getAndClearResponse());
    }
}
