package ev.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.storage.Storage;
import ev.task.TaskList;
import ev.task.Todo;
import ev.ui.Ui;

public class DeleteCommandTest {

    @TempDir
    Path workFolder;

    private Storage storage() {
        return new Storage(workFolder.resolve("data").resolve("ev.txt"));
    }

    private TaskList twoTasks() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("water plants"));
        return tasks;
    }

    @Test
    public void execute_existingTask_removedReportedAndSaved() throws EvException {
        TaskList tasks = twoTasks();
        Ui ui = new Ui();
        Storage storage = storage();

        new DeleteCommand(1).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] water plants", tasks.get(0).toString());
        assertEquals("Removed.\n  [T][ ] read book\n1 task.", ui.takeResponse());
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_numberPastTheEnd_rejectedAndListUnchanged() throws EvException {
        TaskList tasks = twoTasks();
        assertThrows(EvException.class, () -> new DeleteCommand(3).execute(tasks, new Ui(), storage()));
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_emptyList_rejected() {
        assertThrows(EvException.class, () ->
                new DeleteCommand(1).execute(new TaskList(), new Ui(), storage()));
    }
}
