package monkey.command;

import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TodoCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validDescription_addsTodoAndSavesIt() throws Exception {
        TaskList tasks = new TaskList();
        Path saveFile = temporaryDirectory.resolve("monkey.txt");

        new TodoCommand("buy milk").execute(tasks, new Ui(), new Storage(saveFile.toString()));

        assertEquals(1, tasks.size());
        assertEquals("buy milk", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
        assertTrue(Files.exists(saveFile));
        assertEquals("T | 0 | buy milk", Files.readString(saveFile).trim());
    }

    @Test
    void execute_emptyDescription_doesNotChangeTasksOrSaveFile() throws Exception {
        TaskList tasks = new TaskList();
        Path saveFile = temporaryDirectory.resolve("monkey.txt");

        new TodoCommand("").execute(tasks, new Ui(), new Storage(saveFile.toString()));

        assertTrue(tasks.isEmpty());
        assertFalse(Files.exists(saveFile));
    }

    @Test
    void execute_existingTasks_appendsTodoAndPreservesExistingTasks() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("existing task"));
        Path saveFile = temporaryDirectory.resolve("monkey.txt");

        new TodoCommand("new task").execute(tasks, new Ui(), new Storage(saveFile.toString()));

        assertEquals(2, tasks.size());
        assertEquals("existing task", tasks.get(0).getDescription());
        assertEquals("new task", tasks.get(1).getDescription());
        assertEquals("T | 0 | existing task\nT | 0 | new task", Files.readString(saveFile).trim());
    }
}
