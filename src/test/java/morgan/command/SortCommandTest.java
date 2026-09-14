package morgan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import morgan.storage.Storage;
import morgan.task.Task;
import morgan.task.TaskList;
import morgan.task.ToDo;
import morgan.ui.Ui;

public class SortCommandTest {

    @TempDir
    private Path tempDirectory;

    @Test
    public void execute_unsortedTasks_sortsAndSavesTasks() throws Exception {
        TaskList tasks = new TaskList(new ArrayList<>(List.of(
                new ToDo("write report"),
                new ToDo("Attend meeting")
        )));
        Storage storage = new Storage(tempDirectory.resolve("tasks.txt").toString());
        Ui ui = new Ui();

        boolean isExit = new SortCommand().execute(tasks, ui, storage);
        List<Task> savedTasks = storage.load();

        assertFalse(isExit);
        assertEquals("Attend meeting", savedTasks.get(0).getName());
        assertEquals("write report", savedTasks.get(1).getName());
    }
}
