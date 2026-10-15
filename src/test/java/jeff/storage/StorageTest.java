package jeff.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jeff.task.Deadline;
import jeff.task.Event;
import jeff.task.Task;
import jeff.task.Todo;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() {
        Storage storage = new Storage(tempDir.resolve("jeff.txt").toString());
        assertEquals(List.of(), storage.load());
        assertEquals(Optional.empty(), storage.getLastLoadWarning());
    }

    @Test
    public void saveThenLoad_roundTripsTasksCorrectly() throws IOException {
        Path file = tempDir.resolve("jeff.txt");
        Storage storage = new Storage(file.toString());

        List<Task> original = List.of(
                new Todo("read book"),
                new Deadline("return book", "2019-10-15"),
                new Event("trip", "2019-11-01", "2019-11-05"));
        original.get(0).markAsDone();

        Optional<String> saveError = storage.save(original);
        List<Task> loaded = new Storage(file.toString()).load();

        assertEquals(Optional.empty(), saveError);
        assertEquals(original.size(), loaded.size());
        for (int i = 0; i < original.size(); i++) {
            assertEquals(original.get(i).toSaveFormat(), loaded.get(i).toSaveFormat());
        }
    }

    @Test
    public void save_parentDirectoryDoesNotExist_isCreatedAutomatically() {
        Path file = tempDir.resolve("nested/subdir/jeff.txt");
        Storage storage = new Storage(file.toString());

        storage.save(List.of(new Todo("read book")));

        assertTrue(Files.exists(file));
    }

    @Test
    public void load_lineWithUnrecognizedTypeLetter_isSkippedAndWarningIsSet() throws IOException {
        Path file = tempDir.resolve("jeff.txt");
        Files.writeString(file, "X | 0 | not a real type\nT | 0 | read book\n");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toSaveFormat());
        assertTrue(storage.getLastLoadWarning().isPresent());
    }

    @Test
    public void load_lineWithTooFewFields_isSkippedButOtherLinesStillLoad() throws IOException {
        Path file = tempDir.resolve("jeff.txt");
        Files.writeString(file, "D | 0 | missing the by field\nT | 1 | write essay\n");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | 1 | write essay", loaded.get(0).toSaveFormat());
        assertTrue(storage.getLastLoadWarning().isPresent());
    }

    @Test
    public void load_allLinesValid_noLoadWarningIsSet() throws IOException {
        Path file = tempDir.resolve("jeff.txt");
        Files.writeString(file, "T | 0 | read book\n");
        Storage storage = new Storage(file.toString());

        storage.load();

        assertEquals(Optional.empty(), storage.getLastLoadWarning());
    }

    @Test
    public void load_deadlineLineWithInvalidDate_isSkippedButOtherLinesStillLoad() throws IOException {
        Path file = tempDir.resolve("jeff.txt");
        Files.writeString(file, "D | 0 | bad date | not-a-date\nT | 0 | read book\n");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toSaveFormat());
        assertTrue(storage.getLastLoadWarning().isPresent());
    }

    @Test
    public void load_pathIsADirectoryNotAFile_setsLoadWarningAndReturnsEmptyList() throws IOException {
        Path directory = tempDir.resolve("jeff.txt");
        Files.createDirectory(directory);
        Storage storage = new Storage(directory.toString());

        List<Task> loaded = storage.load();

        assertEquals(List.of(), loaded);
        assertTrue(storage.getLastLoadWarning().isPresent());
    }

    @Test
    public void save_pathIsADirectoryNotAFile_returnsErrorMessage() throws IOException {
        Path directory = tempDir.resolve("jeff.txt");
        Files.createDirectory(directory);
        Storage storage = new Storage(directory.toString());

        Optional<String> saveError = storage.save(List.of(new Todo("read book")));

        assertTrue(saveError.isPresent());
    }
}
