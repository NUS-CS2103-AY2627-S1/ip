package answerme.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import answerme.exception.AnswerMeException;
import answerme.task.TaskList;
import answerme.task.ToDo;

public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveTasks_missingParentDirectory_createsDataFile() throws AnswerMeException, IOException {
        File dataFile = temporaryDirectory.resolve("data/tasks.txt").toFile();
        Storage storage = new Storage(dataFile);
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("write test"));

        storage.saveTasks(taskList);

        assertTrue(dataFile.isFile());
        assertEquals("Todo | Incomplete | write test\n", Files.readString(dataFile.toPath()));
    }

    @Test
    public void saveTasks_dataFileCannotBeCreated_throwsException() throws IOException {
        File parentFile = temporaryDirectory.resolve("parent-file").toFile();
        assertTrue(parentFile.createNewFile());

        File dataFile = new File(parentFile, "tasks.txt");
        Storage storage = new Storage(new File(parentFile, "tasks.txt"));

        AnswerMeException exception = assertThrows(AnswerMeException.class, () ->
                storage.saveTasks(new TaskList()));

        assertEquals(
                "File: " + dataFile + " cannot be created!",
                exception.getMessage()
        );
    }

    @Test
    public void saveTasks_parentDirectoryCannotBeCreated_throwsException() throws IOException {
        File parentFile = temporaryDirectory.resolve("parent-file").toFile();
        assertTrue(parentFile.createNewFile());

        File parentDirectory = new File(parentFile, "data");
        File dataFile = new File(parentDirectory, "tasks.txt");
        Storage storage = new Storage(dataFile);

        AnswerMeException exception = assertThrows(AnswerMeException.class, () ->
                storage.saveTasks(new TaskList()));

        assertEquals(
                "Unable to create data directory.",
                exception.getMessage()
        );
    }

    @Test
    public void loadTasks_eventStartAfterEnd_reportsWarning() throws AnswerMeException, IOException {
        File dataFile = temporaryDirectory.resolve("tasks.txt").toFile();
        Files.writeString(dataFile.toPath(),
                "Event | Incomplete | project meeting | 2/9/2026 17:00 | 1/9/2026 02:00\n");
        Storage storage = new Storage(dataFile);

        TaskLoadResult result = storage.loadTasks();

        assertEquals(0, result.taskList().size());
        assertEquals(1, result.warnings().size());
        assertEquals(
                "Event start cannot occur after its end on line 1.",
                result.warnings().get(0)
        );
    }

    @Test
    public void loadTasks_blankTaskDescription_reportsWarning() throws AnswerMeException, IOException {
        File dataFile = temporaryDirectory.resolve("tasks.txt").toFile();
        Files.writeString(dataFile.toPath(), "Todo | Incomplete | \n");
        Storage storage = new Storage(dataFile);

        TaskLoadResult result = storage.loadTasks();

        assertTrue(result.taskList().isEmpty());
        assertEquals(1, result.warnings().size());
        assertEquals("Task description cannot be empty on line 1.",
                result.warnings().get(0));
    }

    @Test
    public void loadTasks_malformedTaskBetweenValidTasks_loadsValidTasksAndReportsWarning()
            throws AnswerMeException, IOException {
        File dataFile = temporaryDirectory.resolve("tasks.txt").toFile();
        Files.writeString(dataFile.toPath(),
                "Todo | Incomplete | first valid task\n"
                        + "Event | Incomplete | project meeting | 2/9/2026 17:00 | 1/9/2026 02:00\n"
                        + "Todo | Complete | second valid task\n");
        Storage storage = new Storage(dataFile);

        TaskLoadResult result = storage.loadTasks();

        assertEquals(2, result.taskList().size());
        assertEquals("Todo | Incomplete | first valid task",
                result.taskList().get(0).toStorageFormat());
        assertEquals("Todo | Complete | second valid task",
                result.taskList().get(1).toStorageFormat());
        assertEquals(1, result.warnings().size());
        assertEquals("Event start cannot occur after its end on line 2.",
                result.warnings().get(0));
    }

    @Test
    public void loadTasks_dataPathIsDirectory_throwsException() {
        File dataFile = temporaryDirectory.resolve("tasks.txt").toFile();
        assertTrue(dataFile.mkdir());
        Storage storage = new Storage(dataFile);

        AnswerMeException exception =
                assertThrows(AnswerMeException.class, storage::loadTasks);

        assertEquals(
                "Unable to open " + dataFile + " for loading.",
                exception.getMessage()
        );
    }
}
