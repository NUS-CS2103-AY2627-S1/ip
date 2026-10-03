package verity.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.task.Deadline;
import verity.task.Event;
import verity.task.TaskList;
import verity.task.Todo;

/**
 * Tests loading and saving task data.
 */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTaskLines_missingFile_returnsEmptyList() throws IOException {
        Storage storage = new Storage(
                temporaryDirectory.resolve("missing").resolve("tasks.txt"));

        assertEquals(List.of(), storage.loadTaskLines());
    }

    @Test
    void loadTaskLines_existingFile_returnsAllLines() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(
                dataFile,
                List.of("T\t0\tread book", "D\t1\tsubmit report\t2026-08-10"),
                StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile);

        assertEquals(
                List.of(
                        "T\t0\tread book",
                        "D\t1\tsubmit report\t2026-08-10"),
                storage.loadTaskLines());
    }

    @Test
    void saveTasks_createsParentDirectoriesAndWritesSerializedTasks()
            throws IOException {
        Path dataFile = temporaryDirectory
                .resolve("nested")
                .resolve("data")
                .resolve("tasks.txt");
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", LocalDate.of(2026, 8, 10)),
                new Event(
                        "project meeting",
                        LocalDate.of(2026, 8, 10),
                        LocalDate.of(2026, 8, 12))
        );
        Storage storage = new Storage(dataFile);

        storage.saveTasks(tasks);

        assertEquals(
                List.of(
                        "T\t0\tread book\t\t",
                        "D\t0\tsubmit report\t2026-08-10\t\t",
                        "E\t0\tproject meeting\t2026-08-10\t2026-08-12\t\t"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void saveTasks_emptyTaskList_createsEmptyFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(dataFile);

        storage.saveTasks(new TaskList());

        assertEquals("", Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void loadTaskLines_directoryPath_throwsIOException() throws IOException {
        Storage storage = new Storage(temporaryDirectory);

        assertThrows(IOException.class, storage::loadTaskLines);
    }

    @Test
    void saveTasks_fileParentIsAFile_throwsIOException() throws IOException {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "existing", StandardCharsets.UTF_8);
        Storage storage = new Storage(
                parentFile.resolve("tasks.txt"));

        assertThrows(
                IOException.class,
                () -> storage.saveTasks(new TaskList()));
    }

    @Test
    void saveTasks_destinationIsDirectory_preservesDirectoryContents()
            throws IOException {
        Path destination = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(destination);
        Path existingFile = destination.resolve("existing.txt");
        Files.writeString(existingFile, "keep", StandardCharsets.UTF_8);
        Storage storage = new Storage(destination);

        assertThrows(
                IOException.class,
                () -> storage.saveTasks(new TaskList(new Todo("read book"))));
        assertEquals("keep", Files.readString(existingFile, StandardCharsets.UTF_8));
    }
}
