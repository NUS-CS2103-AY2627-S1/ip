package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests saving and loading task data. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoad_mixedTaskList_preservesTaskData() throws HabiException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/habi.txt"));
        Todo todo = new Todo("read book");
        todo.markAsDone();
        List<Task> originalTasks = List.of(
                todo,
                new Deadline("return book", "2026-09-15"),
                new Event("project meeting", "Mon 2pm", "4pm"));

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(originalTasks.stream().map(Task::toDataString).toList(),
                loadedTasks.stream().map(Task::toDataString).toList());
    }

    @Test
    public void load_missingFile_createsEmptyDataFile() throws HabiException {
        Path dataFile = temporaryDirectory.resolve("nested/data/habi.txt");
        Storage storage = new Storage(dataFile);

        ArrayList<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
        assertTrue(dataFile.toFile().isFile());
    }

    @Test
    public void load_invalidTaskStatus_throwsDataFileError() throws IOException {
        Path dataFile = temporaryDirectory.resolve("habi.txt");
        Files.writeString(dataFile, "T\t2\tread book");

        Storage storage = new Storage(dataFile);
        HabiException exception = assertThrows(HabiException.class, storage::loadData);

        assertEquals("OOPS! I could not load tasks from the data file.", exception.getMessage());
    }

    @Test
    public void loadRecordWithUnexpectedFieldCount_throwsDataFileError() throws IOException {
        Path dataFile = temporaryDirectory.resolve("habi.txt");
        Files.writeString(dataFile, "N\tbuy milk\textra");

        Storage storage = new Storage(dataFile);
        HabiException exception = assertThrows(HabiException.class, storage::loadData);

        assertEquals("OOPS! I could not load tasks from the data file.", exception.getMessage());
    }

    @Test
    public void load_blankLinesBetweenRecords_ignoresBlankLines() throws IOException, HabiException {
        Path dataFile = temporaryDirectory.resolve("habi.txt");
        Files.writeString(dataFile, "\nT\t0\tread book\n\nN\tbuy milk\n");

        HabiData data = new Storage(dataFile).loadData();

        assertEquals(List.of("T\t0\tread book"),
                data.getTasks().stream().map(Task::toDataString).toList());
        assertEquals(List.of("N\tbuy milk"),
                data.getNotes().stream().map(Note::toDataString).toList());
    }

    @Test
    public void load_malformedRecordVariants_throwDataFileError() throws IOException {
        List<String> invalidRecords = List.of(
                "unknown\t0\tread",
                "T\t0",
                "D\t0\tread",
                "E\t0\tmeeting\t09:00",
                "T\t0\t   ",
                "D\t0\tread\t   ",
                "E\t0\tmeeting\t09:00\t   ");

        for (String record : invalidRecords) {
            Path dataFile = temporaryDirectory.resolve("invalid-" + invalidRecords.indexOf(record));
            Files.writeString(dataFile, record);
            Storage storage = new Storage(dataFile);

            HabiException exception = assertThrows(HabiException.class, storage::loadData);

            assertEquals("OOPS! I could not load tasks from the data file.", exception.getMessage());
        }
    }
}
