package chudgpt.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chudgpt.exception.ChudException;
import chudgpt.storage.Storage;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;
import chudgpt.ui.Ui;

/** Tests priority confirmations, persistence, and invalid commands without side effects. */
public class ChangeTaskPriorityCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_changeRepeatAndClear_updatesAndSavesSelectedTask() throws Exception {
        Task first = new ToDo("first");
        Task second = new ToDo("second").setCompleted(true);
        TaskList tasks = new TaskList(List.of(first, second));
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        Ui ui = new Ui();
        ChangeTaskPriorityCommand command = new ChangeTaskPriorityCommand(1, Priority.EXTREME);

        String response = command.execute(tasks, ui, storage);

        assertEquals("Got it. I've set this task's priority to EXTREME:\n  [T][X][P:EXTREME] second", response);
        assertEquals(Priority.NONE, first.getPriority());
        assertTrue(second.isCompleted());
        assertEquals(Priority.EXTREME, storage.load().get(1).getPriority());
        assertEquals(response, command.execute(tasks, ui, storage));
        assertEquals("Got it. I've removed the priority from this task:\n  [T][X][P:NONE   ] second",
                new ChangeTaskPriorityCommand(1, Priority.NONE).execute(tasks, ui, storage));
        assertEquals(Priority.NONE, storage.load().get(1).getPriority());
    }

    @Test
    public void execute_invalidIndex_doesNotModifyOrSave() throws Exception {
        TaskList tasks = new TaskList(List.of(new ToDo("task")));
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "unchanged sentinel");
        Storage storage = new Storage(file.toString());

        assertThrows(ChudException.class, () ->
                new ChangeTaskPriorityCommand(1, Priority.HIGH).execute(tasks, new Ui(), storage));

        assertEquals(Priority.NONE, tasks.getTask(0).getPriority());
        assertEquals("unchanged sentinel", Files.readString(file));
    }
}
