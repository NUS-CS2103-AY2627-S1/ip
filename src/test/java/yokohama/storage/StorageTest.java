package yokohama.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yokohama.exceptions.YokohamaException;
import yokohama.task.Deadline;
import yokohama.task.Event;
import yokohama.task.Task;
import yokohama.task.Todo;

/**
 * Tests reading and writing the task data file.
 */
public class StorageTest {
    private final Storage storage = new Storage();

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void writeToFile_andLoadFile_roundTripsAllTaskTypes() throws Exception {
        Path file = temporaryDirectory.resolve("nested/tasks.txt");
        List<Todo> expected = List.of(
                new Task("buy milk", false),
                new Deadline("submit report", true, LocalDateTime.of(2026, 9, 3, 23, 59)),
                new Event("conference", false,
                        LocalDateTime.of(2026, 9, 10, 9, 0),
                        LocalDateTime.of(2026, 9, 10, 17, 0)));

        storage.writeToFile(file.toString(), expected);

        List<Todo> actual = storage.loadFile(file.toFile());
        assertEquals(expected.stream().map(Todo::toDbString).toList(),
                actual.stream().map(Todo::toDbString).toList());
    }

    @Test
    public void loadFile_blankLines_areIgnored() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "\nT | 0 | buy milk\n\n", StandardCharsets.UTF_8);

        assertEquals(1, storage.loadFile(file.toFile()).size());
    }

    @Test
    public void loadFile_missingFile_throwsIoException() {
        File missingFile = temporaryDirectory.resolve("missing.txt").toFile();

        assertThrows(IOException.class, () -> storage.loadFile(missingFile));
    }

    @Test
    public void loadFile_directory_throwsIoException() {
        assertThrows(IOException.class, () -> storage.loadFile(temporaryDirectory.toFile()));
    }

    @Test
    public void loadFile_malformedRow_throwsYokohamaException() throws IOException {
        assertInvalidRow("T | 0 |");
    }

    @Test
    public void loadFile_unknownTaskType_throwsYokohamaException() throws IOException {
        assertInvalidRow("X | 0 | unknown");
    }

    @Test
    public void loadFile_invalidCompletionValue_throwsYokohamaException() throws IOException {
        assertInvalidRow("T | yes | unknown");
    }

    @Test
    public void loadFile_extraFields_throwsYokohamaException() throws IOException {
        assertInvalidRow("T | 0 | unknown | extra");
    }

    @Test
    public void loadFile_invalidDeadlineDate_throwsYokohamaException() throws IOException {
        assertInvalidRow("D | 0 | report | not-a-date");
    }

    @Test
    public void loadFile_invalidEventRange_throwsYokohamaException() throws IOException {
        assertInvalidRow("E | 0 | meeting | 2026-09-10T17:00 | 2026-09-10T09:00");
    }

    private void assertInvalidRow(String row) throws IOException {
        Path file = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(file, row + "\n", StandardCharsets.UTF_8);

        assertThrows(YokohamaException.class, () -> storage.loadFile(file.toFile()));
    }
}
