package fox.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import fox.task.Deadline;
import fox.task.Event;
import fox.task.FoxDate;
import fox.task.Task;
import fox.task.Todo;

/** JUnit tests for Level-7 persistence and Level-8 typed deadlines. */
class StorageJUnitTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void createsParentDirectoriesAndRoundTripsAllTaskTypes() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("fox.txt");
        Storage storage = new Storage(file);
        Task doneTodo = new Todo("buy | milk");
        doneTodo.markAsDone();
        Task[] original = {
            doneTodo,
            new Deadline("submit", FoxDate.parse("2026-12-02")),
            new Event("meeting", "10am", "11am")};

        storage.save(original, original.length);
        Task[] loaded = storage.load();

        assertTrue(Files.isRegularFile(file));
        assertEquals("[T][X] buy | milk", loaded[0].toString());
        assertEquals("[D][ ] submit (by: Dec 02 2026)", loaded[1].toString());
        assertEquals("[E][ ] meeting (from: 10am to: 11am)", loaded[2].toString());
        assertEquals(3, loaded.length);
    }

    @Test
    void missingAndEmptyFilesLoadAsEmpty() throws Exception {
        Path file = temporaryDirectory.resolve("missing").resolve("fox.txt");
        Storage storage = new Storage(file);

        assertEquals(0, storage.load().length);
        Files.createFile(file);
        assertEquals(0, storage.load().length);
    }

    @Test
    void rejectsCorruptDataWithoutOverwritingIt() throws Exception {
        Path file = temporaryDirectory.resolve("corrupt.txt");
        String original = "D|false|not-base64|also-not-base64\n";
        Files.writeString(file, original, StandardCharsets.UTF_8);

        Storage.StorageException exception = assertThrows(Storage.StorageException.class, () ->
                new Storage(file).load());

        assertTrue(exception.getMessage().contains("Invalid data"));
        assertEquals(original, Files.readString(file));
    }

    @Test
    void reportsReadAndWriteFailuresClearly() throws Exception {
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.createFile(parentFile);

        Storage.StorageException writeException = assertThrows(Storage.StorageException.class, () ->
                new Storage(parentFile.resolve("fox.txt")).save(new Task[0], 0));
        Storage.StorageException readException = assertThrows(Storage.StorageException.class, () ->
                new Storage(temporaryDirectory).load());

        assertTrue(writeException.getMessage().contains("Could not write"));
        assertTrue(readException.getMessage().contains("Could not read"));
    }

    @Test
    void rejectsImpossibleStoredDates() throws Exception {
        Path file = temporaryDirectory.resolve("bad-date.txt");
        String description = java.util.Base64.getEncoder()
                .encodeToString("submit".getBytes(StandardCharsets.UTF_8));
        Files.writeString(file, "D|false|" + description + "|MjAyNi0wMi0zMA==\n");

        Storage.StorageException exception = assertThrows(Storage.StorageException.class, () ->
                new Storage(file).load());

        assertTrue(exception.getMessage().contains("deadline date is not valid"));
    }

    @Test
    void rejectsInvalidSaveArguments() {
        Storage storage = new Storage(temporaryDirectory.resolve("invalid-save.txt"));

        assertThrows(Storage.StorageException.class, () -> storage.save(null, 0));
        assertThrows(Storage.StorageException.class, () -> storage.save(new Task[0], -1));
        assertThrows(Storage.StorageException.class, () -> storage.save(new Task[0], 1));
    }
}
