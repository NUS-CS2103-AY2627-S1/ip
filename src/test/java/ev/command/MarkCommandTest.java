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

public class MarkCommandTest {

    @TempDir
    Path workFolder;

    private Storage storage() {
        return new Storage(workFolder.resolve("data").resolve("ev.txt"));
    }

    private TaskList oneTask() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        return tasks;
    }

    @Test
    public void execute_mark_taskDoneAndSaved() throws EvException {
        TaskList tasks = oneTask();
        Ui ui = new Ui();
        Storage storage = storage();

        MarkCommand.mark(1).execute(tasks, ui, storage);

        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertEquals("Done.\n  [T][X] read book", ui.takeResponse());
        assertEquals("[T][X] read book", storage.load().get(0).toString());
    }

    @Test
    public void execute_unmark_taskNotDoneAgain() throws EvException {
        TaskList tasks = oneTask();
        Ui ui = new Ui();

        MarkCommand.mark(1).execute(tasks, ui, storage());
        ui.takeResponse();
        MarkCommand.unmark(1).execute(tasks, ui, storage());

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("Back to not done.\n  [T][ ] read book", ui.takeResponse());
    }

    @Test
    public void execute_markTwice_staysDone() throws EvException {
        TaskList tasks = oneTask();
        MarkCommand.mark(1).execute(tasks, new Ui(), storage());
        MarkCommand.mark(1).execute(tasks, new Ui(), storage());
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    public void execute_noSuchTask_rejected() throws EvException {
        TaskList tasks = oneTask();
        assertThrows(EvException.class, () -> MarkCommand.mark(2).execute(tasks, new Ui(), storage()));
    }
}
