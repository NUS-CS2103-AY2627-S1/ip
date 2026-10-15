package jonk;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests loading and saving tasks through {@link Storage}.
 */
public class StorageTest {

    @TempDir
    private Path tempDirectory;

    @ParameterizedTest
    @ValueSource(strings = {
            "", "\n \t\n", "\r\n\t\r\n"
    })
    public void load_emptyOrWhitespaceFile_returnsEmptyList(String data) throws IOException, JonkException {
        assertEquals(List.of(), createStorageWithData(data).load());
    }

    @Test
    public void load_crlfAndUnescapedBackslashes_preservesLiteralText() throws IOException, JonkException {
        Storage storage = createStorageWithData("  T | 0 | C:\\notes\\ \r\n\r\nT|1|复习 🚀\r\n");

        List<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("C:\\notes\\", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
        assertEquals("复习 🚀", tasks.get(1).getDescription());
        assertTrue(tasks.get(1).isDone());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "T | 0 | ends in \\", "T | 0 | unknown \\q escape"
    })
    public void load_literalBackslash_preservesTrailingAndUnknownEscapes(String data)
            throws IOException, JonkException {
        Task task = createStorageWithData(data).load().getFirst();

        assertEquals(data.substring("T | 0 | ".length()), task.getDescription());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "D | 0 | book | ", "E | 0 | meeting | | 2026-09-17",
            "E | 0 | meeting | 2026-09-17 |", "D | 0 | | 2026-09-17"
    })
    public void load_emptyDateOrDescription_rejectsRecord(String data) throws IOException {
        Storage storage = createStorageWithData(data);

        JonkException error = assertThrows(JonkException.class, storage::load);

        assertTrue(error.getMessage().contains("task details cannot be empty"));
        assertInstanceOf(JonkException.class, error.getCause());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "E | 0 | meeting | 2026-02-30 | 2026-03-01",
            "E | 0 | meeting | 2026-03-01 | 2026-13-01"
    })
    public void load_invalidEventDate_rejectsEitherDate(String data) throws IOException {
        Storage storage = createStorageWithData(data);

        JonkException error = assertThrows(JonkException.class, storage::load);

        assertTrue(error.getMessage().contains("invalid data at line 1"));
        assertInstanceOf(java.time.format.DateTimeParseException.class, error.getCause());
    }

    @Test
    public void load_directoryInsteadOfFile_preservesIoCause() {
        Storage storage = new Storage(tempDirectory.toString());

        JonkException error = assertThrows(JonkException.class, storage::load);

        assertEquals("Could not load tasks from " + tempDirectory
                + ". Starting with an empty task list.", error.getMessage());
        assertInstanceOf(IOException.class, error.getCause());
    }

    @Test
    public void load_invalidUtf8_rejectsWithoutModifyingFile() throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        byte[] invalidData = {(byte) 0xc3, (byte) 0x28};
        Files.write(dataFile, invalidData);
        Storage storage = new Storage(dataFile.toString());

        JonkException error = assertThrows(JonkException.class, storage::load);

        assertInstanceOf(IOException.class, error.getCause());
        assertArrayEquals(invalidData, Files.readAllBytes(dataFile));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    public void saveAndLoad_allTaskTypesAndEscapes_roundTripsData(boolean isDone)
            throws IOException, JonkException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Storage storage = new Storage(dataFile.toString());
        String description = "复习 🚀 | slash \\ | together \\| end\\";
        List<Task> tasks = List.of(new Todo(description), new Deadline(description, "2024-02-29"),
                new Event(description, "2024-12-31", "2025-01-01"));
        if (isDone) {
            tasks.forEach(Task::markAsDone);
        }

        storage.save(tasks);
        List<Task> loadedTasks = new Storage(dataFile.toString()).load();

        assertEquals(3, loadedTasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).getClass(), loadedTasks.get(i).getClass());
            assertEquals(description, loadedTasks.get(i).getDescription());
            assertEquals(isDone, loadedTasks.get(i).isDone());
            assertEquals(tasks.get(i).toFileString(), loadedTasks.get(i).toFileString());
        }
        storage.save(List.of());
        assertEquals(0, Files.size(dataFile));
        assertEquals(List.of(), storage.load());
    }

    @Test
    public void save_targetIsNonEmptyDirectory_cleansTemporaryFileAndPreservesContents() throws IOException {
        Path dataFile = Files.createDirectory(tempDirectory.resolve("jonk.txt"));
        Path sentinel = dataFile.resolve("keep.txt");
        Files.writeString(sentinel, "keep me");
        Storage storage = new Storage(dataFile.toString());

        JonkException error = assertThrows(JonkException.class,
                () -> storage.save(List.of(new Todo("new task"))));

        assertInstanceOf(IOException.class, error.getCause());
        assertEquals("Could not save tasks to " + dataFile
                + ". Your latest change was not applied.", error.getMessage());
        assertEquals("keep me", Files.readString(sentinel));
        try (Stream<Path> remainingFiles = Files.list(tempDirectory)) {
            assertEquals(List.of(dataFile), remainingFiles.toList());
        }
    }

    @Test
    public void load_missingDataFile_returnsEmptyList() throws JonkException {
        Storage storage = new Storage(tempDirectory.resolve("missing/jonk.txt").toString());

        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void load_validData_recreatesTasksAndIgnoresBlankLines()
            throws IOException, JonkException {
        Storage storage = createStorageWithData("""
                T | 1 | write \\| report
                D | 0 | return notes \\\\ room | 2019-10-15

                E | 1 | project demo | 2019-10-16 | 2019-10-17
                """);

        List<Task> tasks = storage.load();

        assertEquals(3, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals(List.of(
                "T | 1 | write \\| report",
                "D | 0 | return notes \\\\ room | 2019-10-15",
                "E | 1 | project demo | 2019-10-16 | 2019-10-17"),
                tasks.stream().map(Task::toFileString).toList());
    }

    @Test
    public void load_unknownTaskType_throwsJonkExceptionWithLineNumber() throws IOException {
        Storage storage = createStorageWithData("X | 0 | unknown task\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("invalid data at line 1"));
        assertTrue(exception.getMessage().contains("unknown task type 'X'"));
    }

    @Test
    public void load_wrongFieldCount_throwsJonkException() throws IOException {
        Storage storage = createStorageWithData("D | 0 | return book\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("task type D requires 4 fields, but found 3"));
    }

    @Test
    public void load_invalidCompletionStatus_throwsJonkException() throws IOException {
        Storage storage = createStorageWithData("T | yes | read book\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("completion status must be 0 or 1"));
    }

    @Test
    public void load_extraTodoField_throwsJonkException() throws IOException {
        Storage storage = createStorageWithData("T | 0 | read book | unexpected\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("task type T requires 3 fields, but found 4"));
    }

    @Test
    public void load_invalidLaterRecord_reportsLineAndPreservesFile() throws IOException {
        String data = "T | 0 | read book\n\nE | 1 | meeting | 2019-12-03\n";
        Storage storage = createStorageWithData(data);

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("invalid data at line 3"));
        assertTrue(exception.getMessage().contains("task type E requires 5 fields, but found 4"));
        assertEquals(data, Files.readString(tempDirectory.resolve("jonk.txt")));
    }

    @Test
    public void load_emptyTaskDetails_throwsJonkException() throws IOException {
        Storage storage = createStorageWithData("T | 0 |   \n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("task details cannot be empty"));
    }

    @Test
    public void load_invalidDate_throwsJonkExceptionWithLineNumber() throws IOException {
        Storage storage = createStorageWithData(
                "D | 0 | return book | 2019-02-29\n");

        JonkException exception = assertThrows(JonkException.class, storage::load);

        assertTrue(exception.getMessage().contains("invalid data at line 1"));
        assertInstanceOf(java.time.format.DateTimeParseException.class, exception.getCause());
    }

    @Test
    public void save_tasks_createsParentDirectoryAndWritesEscapedData()
            throws IOException, JonkException {
        Path dataFile = tempDirectory.resolve("nested/data/jonk.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("write | report \\ draft");
        todo.markAsDone();

        storage.save(List.of(todo, new Deadline("return book", "2019-12-02")));

        assertEquals("""
                T | 1 | write \\| report \\\\ draft
                D | 0 | return book | 2019-12-02
                """, Files.readString(dataFile).replace("\r\n", "\n"));
    }

    @Test
    public void save_emptyList_createsEmptyFile() throws IOException, JonkException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of());

        assertTrue(Files.exists(dataFile));
        assertEquals("", Files.readString(dataFile));
    }

    @Test
    public void save_existingData_replacesFileContents() throws IOException, JonkException {
        Path dataFile = tempDirectory.resolve("data/jonk.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "old data\n");
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(new Todo("new task")));

        assertEquals(List.of("T | 0 | new task"), Files.readAllLines(dataFile));
    }

    @Test
    public void save_parentPathIsFile_throwsJonkExceptionAndPreservesBlocker()
            throws IOException {
        Path blocker = tempDirectory.resolve("blocker");
        Files.writeString(blocker, "keep me");
        Storage storage = new Storage(blocker.resolve("jonk.txt").toString());

        JonkException exception = assertThrows(JonkException.class,
                () -> storage.save(List.of(new Todo("new task"))));

        assertTrue(exception.getMessage().contains("Could not save tasks"));
        assertFalse(Files.isDirectory(blocker));
        assertEquals("keep me", Files.readString(blocker));
    }

    /**
     * Creates a storage instance whose data file contains the supplied test fixture.
     *
     * @param data Exact data-file contents.
     * @return Storage backed by the created data file.
     * @throws IOException If the fixture cannot be written.
     */
    private Storage createStorageWithData(String data) throws IOException {
        Path dataFile = tempDirectory.resolve("jonk.txt");
        Files.writeString(dataFile, data);
        return new Storage(dataFile.toString());
    }
}
