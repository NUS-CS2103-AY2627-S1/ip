package ev.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ev.EvException;
import ev.storage.Storage;
import ev.task.Deadline;
import ev.task.Task;
import ev.task.TaskList;
import ev.task.Todo;
import ev.ui.Ui;

public class UpdateCommandTest {

    @TempDir
    Path workFolder;

    private Storage storage() {
        return new Storage(workFolder.resolve("data").resolve("ev.txt"));
    }

    private TaskList twoTasks() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 6, 6, 0, 0)));
        return tasks;
    }

    @Test
    public void execute_newDueDate_changedReportedAndSaved() throws EvException {
        TaskList tasks = twoTasks();
        Ui ui = new Ui();
        Storage storage = storage();

        new UpdateCommand(2, Deadline.OPTION_BY, "2019-12-05 1800").execute(tasks, ui, storage);

        assertEquals("[D][ ] return book (by: Dec 5 2019, 6:00 PM)", tasks.get(1).toString());
        assertEquals("Updated.\n  [D][ ] return book (by: Dec 5 2019, 6:00 PM)", ui.takeResponse());
        assertEquals("[D][ ] return book (by: Dec 5 2019, 6:00 PM)", storage.load().get(1).toString());
    }

    @Test
    public void execute_newDescription_doneStatusKept() throws EvException {
        TaskList tasks = twoTasks();
        tasks.get(0).markAsDone();

        new UpdateCommand(1, Task.OPTION_DESC, "read the whole book").execute(tasks, new Ui(), storage());

        assertEquals("[T][X] read the whole book", tasks.get(0).toString());
    }

    @Test
    public void execute_optionThatTypeLacks_rejectedAndNothingSaved() throws EvException {
        TaskList tasks = twoTasks();
        Storage storage = storage();

        assertThrows(EvException.class, () ->
                new UpdateCommand(1, Deadline.OPTION_BY, "2019-12-05").execute(tasks, new Ui(), storage));

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals(0, storage.load().size());
    }

    @Test
    public void execute_noSuchTask_rejected() throws EvException {
        TaskList tasks = twoTasks();
        assertThrows(EvException.class, () ->
                new UpdateCommand(9, Task.OPTION_DESC, "x").execute(tasks, new Ui(), storage()));
    }
}
