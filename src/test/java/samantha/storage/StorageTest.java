package samantha.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import samantha.exception.CorruptedTaskFileException;
import samantha.exception.TaskFileReadException;
import samantha.exception.TaskFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Deadline;
import samantha.model.Event;
import samantha.model.Task;
import samantha.model.Todo;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyTaskList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertEquals(List.of(), storage.load());
    }

    @Test
    void saveAndLoad_allTaskTypesAndStatuses_roundTrips() throws Exception {
        Path file = temporaryDirectory.resolve("nested/tasks.txt");
        Storage storage = new Storage(file);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");
        Event event = new Event("meeting", "3/12/2019 1400", "3/12/2019 1600");
        deadline.markDone();

        storage.save(List.of(todo, deadline, event));
        ArrayList<Task> loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("T | 0 | read book", loaded.get(0).toFileString());
        assertEquals("D | 1 | return book | 2/12/2019 1800", loaded.get(1).toFileString());
        assertEquals("E | 0 | meeting | 3/12/2019 1400 to 3/12/2019 1600",
                loaded.get(2).toFileString());
        assertEquals(List.of("T | 0 | read book", "D | 1 | return book | 2/12/2019 1800",
                "E | 0 | meeting | 3/12/2019 1400 to 3/12/2019 1600"), Files.readAllLines(file));
    }

    @Test
    void save_emptyTaskList_createsEmptyFile() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");

        new Storage(file).save(List.of());

        assertEquals(List.of(), Files.readAllLines(file));
    }

    @Test
    void load_legacyFiveFieldEvent_returnsEvent() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "E | 1 | meeting | 3/12/2019 1400 | 3/12/2019 1600\n");

        Task task = new Storage(file).load().getFirst();

        assertInstanceOf(Event.class, task);
        assertEquals("E | 1 | meeting | 3/12/2019 1400 to 3/12/2019 1600", task.toFileString());
    }

    @ParameterizedTest
    @MethodSource("malformedRecords")
    void load_malformedRecord_corruptedTaskFileExceptionThrown(String record) throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, record + "\n");

        CorruptedTaskFileException exception = assertThrows(CorruptedTaskFileException.class, () ->
                new Storage(file).load());
        assertEquals(1, exception.getLineNumber());
    }

    private static Stream<Arguments> malformedRecords() {
        return Stream.of(
                Arguments.of("X | 0 | unknown"),
                Arguments.of("T | 2 | bad status"),
                Arguments.of("T | 0"),
                Arguments.of("D | 0 | deadline | "),
                Arguments.of("E | 0 | meeting | 3/12/2019 1400"),
                Arguments.of("E | 0 | meeting | 3/12/2019 1400 until 3/12/2019 1600"));
    }

    @Test
    void load_corruptionAfterValidRecord_reportsCorruptedLineNumber() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | valid\nX | 0 | invalid\n");

        CorruptedTaskFileException exception = assertThrows(CorruptedTaskFileException.class, () ->
                new Storage(file).load());
        assertEquals(2, exception.getLineNumber());
    }

    @Test
    void load_directoryInsteadOfFile_taskFileReadExceptionThrown() throws IOException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("task-directory"));

        assertThrows(TaskFileReadException.class, () -> new Storage(directory).load());
    }

    @Test
    void save_directoryInsteadOfFile_taskFileWriteExceptionThrown() throws IOException, TaskValidationException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("task-directory"));

        assertThrows(TaskFileWriteException.class, () ->
                new Storage(directory).save(List.of(new Todo("task"))));
    }
}
