package kafka.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import kafka.exception.CorruptedTaskDataException;
import kafka.exception.KafkaException;
import kafka.task.Deadline;
import kafka.task.Event;
import kafka.task.Task;
import kafka.task.TaskList;
import kafka.task.Todo;

/**
 * Tests task persistence and rejection of corrupted storage records.
 */
class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadMissingFileReturnsEmptyTaskList() throws KafkaException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("missing.txt"));

        TaskList tasks = storage.load();

        assertEquals(0, tasks.size());
    }

    @Test
    void saveCreatesParentDirectoryAndWritesAllTaskTypes()
            throws KafkaException, IOException {
        Path dataFile = temporaryDirectory.resolve("data").resolve("kafka.txt");
        TaskStorage storage = new TaskStorage(dataFile);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Deadline("submit report", "Friday"));
        tasks.addTask(new Event("meeting", "2pm", "3pm"));
        tasks.markTask(2);

        storage.save(tasks);

        assertTrue(Files.exists(dataFile));
        assertEquals(List.of(
                "T | 0 | read book",
                "D | 1 | submit report | Friday",
                "E | 0 | meeting | 2pm | 3pm"), Files.readAllLines(dataFile));
    }

    @Test
    void saveThenLoadRestoresTypesDetailsAndCompletionStates() throws KafkaException {
        Path dataFile = temporaryDirectory.resolve("kafka.txt");
        TaskStorage storage = new TaskStorage(dataFile);
        TaskList originalTasks = new TaskList();
        originalTasks.addTask(new Todo("read book"));
        originalTasks.addTask(new Deadline("submit report", "Friday"));
        originalTasks.addTask(new Event("meeting", "2pm", "3pm"));
        originalTasks.markTask(1);
        originalTasks.markTask(3);

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        List<Task> loaded = loadedTasks.getTasks();
        assertEquals(3, loaded.size());
        assertInstanceOf(Todo.class, loaded.get(0));
        assertInstanceOf(Deadline.class, loaded.get(1));
        assertInstanceOf(Event.class, loaded.get(2));
        assertEquals("[T][X] read book", loaded.get(0).display());
        assertEquals("[D][ ] submit report (by: Friday)", loaded.get(1).display());
        assertEquals("[E][X] meeting (from: 2pm to: 3pm)", loaded.get(2).display());
    }

    @Test
    void saveReplacesStaleFileContents() throws KafkaException, IOException {
        Path dataFile = temporaryDirectory.resolve("kafka.txt");
        Files.writeString(dataFile, "old data");
        TaskStorage storage = new TaskStorage(dataFile);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("current task"));

        storage.save(tasks);

        assertEquals(List.of("T | 0 | current task"), Files.readAllLines(dataFile));
    }

    @Test
    void save_emptyList_clearsPreviouslySavedTasks() throws KafkaException, IOException {
        Path dataFile = temporaryDirectory.resolve("kafka.txt");
        Files.writeString(dataFile, "T | 1 | old task");
        TaskStorage storage = new TaskStorage(dataFile);

        storage.save(new TaskList());

        assertEquals("", Files.readString(dataFile));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void load_blankLines_skipsThemAndPreservesTaskOrder() throws KafkaException, IOException {
        Path dataFile = temporaryDirectory.resolve("kafka.txt");
        Files.write(dataFile, List.of("", "T|0|first", "   ", "\t", "D | 1 | second | Sunday", ""));

        TaskList tasks = new TaskStorage(dataFile).load();

        assertEquals(List.of("T | 0 | first", "D | 1 | second | Sunday"),
                tasks.getTasks().stream().map(Task::toDataString).toList());
    }

    @Test
    void load_corruptionAfterBlankLines_reportsPhysicalLineAndPreservesFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("kafka.txt");
        String contents = "\nT | 0 | valid\n   \ninvalid record\n";
        Files.writeString(dataFile, contents);

        CorruptedTaskDataException exception = assertThrows(CorruptedTaskDataException.class, () ->
                new TaskStorage(dataFile).load());

        assertEquals("Malformed task data on line 4 of " + dataFile, exception.getMessage());
        assertEquals(contents, Files.readString(dataFile));
    }

    @Test
    void saveThenLoad_unicodeDetails_preservesUtf8Text() throws KafkaException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("kafka.txt"));
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read caf\u00e9 notes \u732b"));

        storage.save(tasks);

        assertEquals("T | 0 | read caf\u00e9 notes \u732b", storage.load().getTasks().getFirst().toDataString());
    }

    @Test
    void load_directoryInsteadOfFile_wrapsIoFailure() {
        TaskStorage storage = new TaskStorage(temporaryDirectory);

        KafkaException exception = assertThrows(KafkaException.class, storage::load);

        assertEquals("Could not read tasks from " + temporaryDirectory, exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
    }

    @Test
    void save_parentIsFile_wrapsIoFailureAndPreservesParent() throws IOException {
        Path parentFile = temporaryDirectory.resolve("parent.txt");
        Files.writeString(parentFile, "keep me");
        Path dataFile = parentFile.resolve("kafka.txt");
        TaskStorage storage = new TaskStorage(dataFile);

        KafkaException exception = assertThrows(KafkaException.class, () -> storage.save(new TaskList()));

        assertEquals("Could not save tasks to " + dataFile, exception.getMessage());
        assertInstanceOf(IOException.class, exception.getCause());
        assertEquals("keep me", Files.readString(parentFile));
    }

    @Test
    void save_failedReplacement_preservesDestinationAndRemovesTemporaryFile() throws IOException {
        Path destination = temporaryDirectory.resolve("tasks.txt");
        Files.createDirectory(destination);
        Path existingFile = destination.resolve("keep.txt");
        Files.writeString(existingFile, "keep me");
        TaskStorage storage = new TaskStorage(destination);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("new task"));

        assertThrows(KafkaException.class, () -> storage.save(tasks));

        assertEquals("keep me", Files.readString(existingFile));
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            assertEquals(List.of(destination), files.toList());
        }
    }

    @Test
    void getFilePath_redundantSegments_returnsNormalizedAbsolutePath() {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("data").resolve("..").resolve("kafka.txt"));

        assertEquals(temporaryDirectory.resolve("kafka.txt").toAbsolutePath().normalize(), storage.getFilePath());
    }

    @ParameterizedTest
    @MethodSource("invalidStorageRecords")
    void loadInvalidRecordReportsOnlyItsStorageLineError(String invalidRecord)
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("kafka.txt");
        Files.write(dataFile, List.of("T | 0 | valid task", invalidRecord));

        CorruptedTaskDataException exception = assertThrows(CorruptedTaskDataException.class, () ->
            new TaskStorage(dataFile).load());

        assertEquals("Malformed task data on line 2 of " + dataFile,
                exception.getMessage());
    }

    /**
     * Supplies malformed records that should all trigger the same safe failure path.
     *
     * @return Invalid storage records for the parameterized loading test.
     */
    private static Stream<Arguments> invalidStorageRecords() {
        return Stream.of(
                Arguments.of("invalid record"),
                Arguments.of("T | 0"),
                Arguments.of("X | 0 | unknown type"),
                Arguments.of("T | 2 | invalid status"),
                Arguments.of("D | done | report | Sunday"),
                Arguments.of("E | | meeting | Monday | Tuesday"),
                Arguments.of("T | 0 |"),
                Arguments.of("T | 0 |   "),
                Arguments.of("T | 0 | task | extra"),
                Arguments.of("D | 0 | missing deadline"),
                Arguments.of("D | 0 | report |"),
                Arguments.of("D | 0 | | Sunday"),
                Arguments.of("D | 0 | report | Sunday | extra"),
                Arguments.of("E | 0 | meeting | missing end"),
                Arguments.of("E | 0 | meeting | | Tuesday"),
                Arguments.of("E | 0 | meeting | Monday |"),
                Arguments.of("E | 0 | | Monday | Tuesday"),
                Arguments.of("E | 0 | meeting | Monday | Tuesday | extra"));
    }
}
