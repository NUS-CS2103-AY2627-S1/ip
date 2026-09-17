package rei.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import rei.task.Deadlines;
import rei.task.Events;
import rei.task.Task;

/** Tests saving, loading, and recovery behavior of {@link Storage}. */
public class StorageTest {
    @TempDir
    Path testDirectory;

    @Test
    public void load_invalidDateAndEventRange_skipsRecordsWithoutCrashing() throws IOException {
        Path dataFile = testDirectory.resolve("invalid-dates.txt");
        String invalidDate = Base64.getEncoder().encodeToString("2026-02-30T18:00".getBytes(StandardCharsets.UTF_8));
        String start = Base64.getEncoder().encodeToString("2026-09-18T18:00".getBytes(StandardCharsets.UTF_8));
        Files.write(dataFile, List.of("D | 0 | dGFzaw== | " + invalidDate,
                "E | 0 | dGFzaw== | " + start + " | " + start));

        Storage.LoadResult result = new Storage(dataFile).load();

        assertEquals(2, result.skippedLines());
        assertEquals(0, result.tasks().size());
    }

    @Test
    public void load_missingFile_returnsEmptyResult() throws IOException {
        Storage storage = new Storage(testDirectory.resolve("missing").resolve("tasks.txt"));

        Storage.LoadResult result = storage.load();

        assertEquals(0, result.tasks().size());
        assertEquals(0, result.skippedLines());
    }

    @Test
    public void saveAndLoad_allTaskTypesAndStates_restoresEveryField() throws IOException {
        Storage storage = new Storage(testDirectory.resolve("nested").resolve("tasks.txt"));
        Task todo = new Task("read | revise 雷");
        todo.markAsDone();
        todo.addTag("#school");
        Deadlines deadline = new Deadlines("submit report", LocalDateTime.of(2026, 8, 28, 18, 0));
        deadline.addTag("#urgent");
        Events event = new Events("project meeting", LocalDateTime.of(2026, 8, 29, 10, 0),
                LocalDateTime.of(2026, 8, 29, 12, 0));
        event.addTag("#team");
        List<Task> original = List.of(todo, deadline, event);

        storage.save(original);
        Storage.LoadResult result = storage.load();

        assertEquals(0, result.skippedLines());
        assertEquals(original.size(), result.tasks().size());
        for (int i = 0; i < original.size(); i++) {
            Task expected = original.get(i);
            Task actual = result.tasks().get(i);
            assertEquals(expected.getTaskType(), actual.getTaskType());
            assertEquals(expected.getDescription(), actual.getDescription());
            assertEquals(expected.getTags(), actual.getTags());
            assertEquals(expected.toString(), actual.toString());
            assertEquals(expected.isDone(), actual.isDone());
        }
    }

    @Test
    public void save_emptyList_truncatesExistingTasks() throws IOException {
        Storage storage = new Storage(testDirectory.resolve("tasks.txt"));
        storage.save(List.of(new Task("temporary")));

        storage.save(List.of());

        assertEquals(0, storage.load().tasks().size());
    }

    @Test
    public void load_mixedValidAndCorruptedRecords_skipsOnlyCorruptedRecords() throws IOException {
        Path dataFile = testDirectory.resolve("corrupted.txt");
        Storage storage = new Storage(dataFile);
        storage.save(List.of(new Task("valid task")));
        String validRecord = Files.readAllLines(dataFile, StandardCharsets.UTF_8).get(0);
        Files.write(dataFile, List.of(
                validRecord,
                "not a task record",
                "T | 9 | dmFsaWQ=",
                "D | 0 | c2hvcnQ=",
                "T | 0 | "), StandardCharsets.UTF_8);

        Storage.LoadResult result = storage.load();

        assertEquals(1, result.tasks().size());
        assertEquals("valid task", result.tasks().get(0).getDescription());
        assertEquals(4, result.skippedLines());
    }

    @Test
    public void load_legacyRecordWithoutTags_restoresTask() throws IOException {
        Path dataFile = testDirectory.resolve("legacy.txt");
        Files.writeString(dataFile, "T | 0 | bGVnYWN5IHRhc2s=", StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(dataFile).load();

        assertEquals(1, result.tasks().size());
        assertEquals("legacy task", result.tasks().get(0).getDescription());
        assertEquals(List.of(), result.tasks().get(0).getTags());
        assertEquals(0, result.skippedLines());
    }

    @Test
    public void save_parentPathIsAFile_throwsIoException() throws IOException {
        Path regularFile = testDirectory.resolve("regular-file");
        Files.writeString(regularFile, "content", StandardCharsets.UTF_8);
        Storage storage = new Storage(regularFile.resolve("tasks.txt"));

        assertThrows(IOException.class, () -> storage.save(List.of(new Task("cannot save"))));
    }
}
