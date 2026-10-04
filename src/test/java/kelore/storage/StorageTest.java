package kelore.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kelore.exception.CorruptedDataException;
import kelore.task.TaskList;

/** Tests persistence and validation of Kelore's task data file. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_missingFile_returnsEmptyTaskList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(List.of(), storage.load().toStorageLines());
    }

    @Test
    public void saveThenLoad_allTaskTypesAndStatuses_preservesTasks() throws Exception {
        Path file = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(file);
        TaskList original = new TaskList();
        original.addTodo("todo read a book");
        original.addDeadline("deadline submit report /by 2/9/2026 1800");
        original.addEvent("event conference /from 3/9/2026 0900 /to 4/9/2026 1700");
        original.mark(2);

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(original.toStorageLines(), loaded.toStorageLines());
    }

    @Test
    public void load_blankLines_ignoresBlankLines() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of("", "T | 0 | read a book", "   "));

        TaskList loaded = new Storage(file).load();

        assertEquals(List.of("T | 0 | read a book"), loaded.toStorageLines());
    }

    @Test
    public void save_fileDirectlyInExistingDirectory_writesStorageLines() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.addTodo("todo read a book");

        new Storage(file).save(tasks);

        assertEquals(List.of("T | 0 | read a book"), Files.readAllLines(file));
    }

    @Test
    public void load_eachStoredTaskTypeAndStatus_parsesRecords() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "T | 1 | read a book",
                "D | 0 | submit report | 2026-09-02T18:00",
                "E | 1 | conference | 2026-09-03T09:00 | 2026-09-04T17:00"));

        TaskList tasks = new Storage(file).load();

        assertEquals(List.of(
                "T | 1 | read a book",
                "D | 0 | submit report | 2026-09-02T18:00",
                "E | 1 | conference | 2026-09-03T09:00 | 2026-09-04T17:00"),
                tasks.toStorageLines());
    }

    @Test
    public void load_corruptedRecords_throwsIoExceptionWithLineNumber() throws Exception {
        List<String> corruptedRecords = List.of(
                "T | 0",
                "T | 0 | description | extra",
                "D | 0 | description | invalid-date",
                "D | 0 | description | 2026-09-02T18:00 | extra",
                "E | 0 | description | invalid-date | 2026-09-03T10:00",
                "E | 0 | description | 2026-09-03T10:00 | invalid-date",
                "E | 0 | description | 2026-09-04T10:00 | 2026-09-03T10:00",
                "E | 0 | description | 2026-09-03T10:00 | 2026-09-03T10:00",
                "E | 0 | description | 2026-09-03T10:00",
                "X | 0 | description",
                "T | maybe | description",
                "T | 0 | ",
                "D | 0 |     | 2026-09-02T18:00",
                "T | 0 | invalid\tdescription");

        for (int i = 0; i < corruptedRecords.size(); i++) {
            Path file = temporaryDirectory.resolve("corrupted-" + i + ".txt");
            Files.writeString(file, System.lineSeparator() + corruptedRecords.get(i));

            IOException exception = assertThrows(
                    IOException.class, () -> new Storage(file).load());
            assertEquals("The data file is corrupted at line 2.", exception.getMessage());
        }
    }

    @Test
    public void load_duplicateTaskDetails_throwsCorruptedDataException() throws Exception {
        Path file = temporaryDirectory.resolve("duplicates.txt");
        Files.write(file, List.of(
                "T | 0 | Read   book",
                "T | 1 | read book"));

        CorruptedDataException exception = assertThrows(
                CorruptedDataException.class, () -> new Storage(file).load());

        assertEquals("The data file is corrupted at line 2.", exception.getMessage());
    }

    @Test
    public void joinFields_multipleAndEmptyFields_usesStorageDelimiter() {
        assertEquals("T | 0 | description", Storage.joinFields("T", "0", "description"));
        assertEquals("left | ", Storage.joinFields("left", ""));
        assertEquals("", Storage.joinFields());
    }

    @Test
    public void constructorAndJoinFields_nullValues_assertionError() {
        assertThrows(AssertionError.class, () -> new Storage(null));
        assertThrows(AssertionError.class, () -> Storage.joinFields((String[]) null));
        assertThrows(AssertionError.class, () -> Storage.joinFields("valid", null));
    }
}
