package ev.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.task.Todo;
import ev.ui.Ui;

public class AddCommandTest {

    @TempDir
    Path workFolder;

    private Storage storage() {
        return new Storage(workFolder.resolve("data").resolve("ev.txt"));
    }

    @Test
    public void execute_newTask_addedReportedAndSaved() throws EvException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = storage();

        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("Added.\n  [T][ ] read book\n1 task.", ui.takeResponse());
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_taskAlreadyOnTheList_rejectedAndNothingSaved() throws EvException {
        TaskList tasks = new TaskList();
        Storage storage = storage();
        new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage);

        assertThrows(EvException.class, () ->
                new AddCommand(new Todo("read book")).execute(tasks, new Ui(), storage));
        assertEquals(1, tasks.size());
        assertEquals(1, storage.load().size());
    }

    @Test
    public void isExit_always_false() {
        assertFalse(new AddCommand(new Todo("read book")).isExit());
    }
}
