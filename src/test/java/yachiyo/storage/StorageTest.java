package yachiyo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static yachiyo.TestAssertions.assertYachiyoException;
import static yachiyo.exception.ErrorCategory.SYSTEM_ERROR;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yachiyo.exception.YachiyoException;
import yachiyo.task.Deadline;
import yachiyo.task.Event;
import yachiyo.task.Task;
import yachiyo.task.ToDo;

/**
 * Tests loading and saving tasks through {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void loadTasks_fileMissing_emptyListReturned() throws YachiyoException {
        Storage storage = new Storage(dataFilePath());

        assertTrue(storage.loadTasks().isEmpty());
    }

    @Test
    public void loadTasks_validTaskData_tasksReconstructed() throws Exception {
        writeData(
                "TODO | 1 | Read book",
                "DEADLINE | 0 | Submit report | 2026-08-20T17:00",
                "EVENT | 1 | Orientation | 2026-08-20T09:00 | 2026-08-22T17:00"
        );
        Storage storage = new Storage(dataFilePath());

        List<Task> tasks = storage.loadTasks();

        assertEquals(3, tasks.size());
        assertInstanceOf(ToDo.class, tasks.get(0));
        assertInstanceOf(Deadline.class, tasks.get(1));
        assertInstanceOf(Event.class, tasks.get(2));
        assertEquals("TODO | 1 | Read book", tasks.get(0).toFileFormat());
        assertEquals(
                "DEADLINE | 0 | Submit report | 2026-08-20T17:00",
                tasks.get(1).toFileFormat()
        );
        assertEquals(
                "EVENT | 1 | Orientation | 2026-08-20T09:00 | 2026-08-22T17:00",
                tasks.get(2).toFileFormat()
        );
    }

    @Test
    public void loadTasks_blankLinesPresent_blankLinesIgnored() throws Exception {
        writeData("", "TODO | 0 | Read book", "   ");
        Storage storage = new Storage(dataFilePath());

        List<Task> tasks = storage.loadTasks();

        assertEquals(1, tasks.size());
        assertEquals("TODO | 0 | Read book", tasks.get(0).toFileFormat());
    }

    @Test
    public void loadTasks_tooFewFields_exceptionThrown() throws IOException {
        writeData("TODO | 0");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_unknownTaskType_exceptionThrown() throws IOException {
        writeData("NOTE | 0 | Read book");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_invalidCompletionStatus_exceptionThrown() throws IOException {
        writeData("TODO | 2 | Read book");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_incorrectFieldCount_exceptionThrown() throws IOException {
        writeData("TODO | 0 | Read book | extra field");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_blankRequiredField_exceptionThrown() throws IOException {
        writeData("TODO | 0 | ");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_invalidDateTime_exceptionThrown() throws IOException {
        writeData("DEADLINE | 0 | Submit report | not-a-date");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_descriptionContainsPipe_exceptionThrown() throws IOException {
        writeData("TODO | 0 | Compare option A|option B");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_eventEndNotAfterStart_exceptionThrown() throws IOException {
        writeData("EVENT | 0 | Orientation | 2026-08-20T17:00 | 2026-08-20T09:00");

        assertInvalidDataRejected();
    }

    @Test
    public void loadTasks_fileCannotBeRead_exceptionThrown() {
        Storage storage = new Storage(tempDirectory);

        assertYachiyoException(SYSTEM_ERROR, storage::loadTasks);
    }

    @Test
    public void saveTasks_parentDirectoryMissing_directoryCreatedAndTasksSaved()
            throws Exception {
        ToDo toDo = new ToDo("Read book");
        toDo.markAsDone();
        Deadline deadline = new Deadline(
                "Submit report",
                LocalDateTime.of(2026, 8, 20, 17, 0)
        );
        Storage storage = new Storage(dataFilePath());

        storage.saveTasks(List.of(toDo, deadline));

        assertEquals(
                List.of(
                        "TODO | 1 | Read book",
                        "DEADLINE | 0 | Submit report | 2026-08-20T17:00"
                ),
                Files.readAllLines(dataFilePath(), StandardCharsets.UTF_8)
        );
    }

    @Test
    public void saveTasks_fileAlreadyContainsData_existingDataOverwritten() throws Exception {
        writeData("TODO | 0 | Old task");
        Storage storage = new Storage(dataFilePath());

        storage.saveTasks(List.of(new ToDo("New task")));

        assertEquals(
                List.of("TODO | 0 | New task"),
                Files.readAllLines(dataFilePath(), StandardCharsets.UTF_8)
        );
    }

    @Test
    public void saveAndLoadTasks_validTasks_tasksPreserved() throws YachiyoException {
        Event event = new Event(
                "Orientation",
                LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 22, 17, 0)
        );
        event.markAsDone();
        List<Task> originalTasks = List.of(new ToDo("Read book"), event);
        Storage storage = new Storage(dataFilePath());

        storage.saveTasks(originalTasks);
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(
                originalTasks.stream().map(Task::toFileFormat).toList(),
                loadedTasks.stream().map(Task::toFileFormat).toList()
        );
    }

    @Test
    public void saveTasks_fileCannotBeWritten_exceptionThrownAndTemporaryFileRemoved()
            throws IOException {
        Path directoryPath = tempDirectory.resolve("directory-target");
        Files.createDirectories(directoryPath);
        Files.writeString(directoryPath.resolve("existing-file"), "Keep me");
        Storage storage = new Storage(directoryPath);

        assertYachiyoException(
                SYSTEM_ERROR, () -> storage.saveTasks(List.of(new ToDo("Read book"))));
        try (Stream<Path> files = Files.list(tempDirectory)) {
            assertEquals(List.of(directoryPath), files.toList());
        }
    }

    @Test
    public void saveTasks_taskDescriptionContainsPipe_exceptionThrownWithoutWriting() {
        Task task = new ToDo("Safe description") {
            @Override
            public String getDescription() {
                return "Compare option A | option B";
            }
        };
        Storage storage = new Storage(dataFilePath());

        assertYachiyoException(SYSTEM_ERROR, () -> storage.saveTasks(List.of(task)));
        assertTrue(Files.notExists(dataFilePath()));
    }

    /**
     * Returns the nested data-file path used by most storage tests.
     *
     * @return temporary data-file path.
     */
    private Path dataFilePath() {
        return tempDirectory.resolve("data").resolve("yachiyo.txt");
    }

    /**
     * Writes test data to the temporary data file.
     *
     * @param lines lines to write.
     * @throws IOException if the fixture cannot be created.
     */
    private void writeData(String... lines) throws IOException {
        Files.createDirectories(dataFilePath().getParent());
        Files.write(dataFilePath(), List.of(lines), StandardCharsets.UTF_8);
    }

    /**
     * Verifies that the current temporary data file is rejected as malformed.
     */
    private void assertInvalidDataRejected() {
        Storage storage = new Storage(dataFilePath());

        assertYachiyoException(SYSTEM_ERROR, storage::loadTasks);
    }
}
