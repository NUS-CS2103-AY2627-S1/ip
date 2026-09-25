package shan.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shan.task.Deadline;
import shan.task.Event;
import shan.task.Task;

class StorageEdgeCaseTest {
    @TempDir
    Path tempDir;

    @Test
    void load_blankMalformedAndCompletedEntries_skipsOnlyMalformedEntries() throws Exception {
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of(
                "",
                "T | 0 | todo | unexpected",
                "T | 0 |    ",
                "D | 0 | deadline | invalid date",
                "E | 0 | event | invalid start | 2026-09-15 1900",
                "D | 1 | completed deadline | 2026-09-15 18:00",
                "E | 1 | completed event | 2026-09-15 18:00 | 2026-09-15 19:00"));

        Storage.LoadResult result = new Storage(dataFile).load();

        assertEquals(4, result.skippedTasks());
        assertEquals(2, result.tasks().size());
        assertTrue(result.tasks().get(0) instanceof Deadline);
        assertTrue(result.tasks().get(0).isDone());
        assertTrue(result.tasks().get(1) instanceof Event);
        assertTrue(result.tasks().get(1).isDone());
    }

    @Test
    void load_fieldsWithSurroundingWhitespace_trimsEveryField() throws Exception {
        Path dataFile = tempDir.resolve("data.txt");
        Files.writeString(dataFile, " D | 0 | submit report | 2026-09-15 18:00 ");

        Storage.LoadResult result = new Storage(dataFile).load();

        assertEquals(0, result.skippedTasks());
        assertEquals(1, result.tasks().size());
        assertTrue(result.tasks().get(0).toString().contains("submit report"));
        assertFalse(result.tasks().get(0).isDone());
    }

    @Test
    void save_completedTasks_preservesCompletionStatusWhenReloaded() throws Exception {
        Path dataFile = tempDir.resolve("data.txt");
        Task completedTask = new Deadline("submit report",
                java.time.LocalDateTime.of(2026, 9, 15, 18, 0));
        completedTask.markDone();
        Storage storage = new Storage(dataFile);

        storage.save(List.of(completedTask));
        Storage.LoadResult result = storage.load();

        assertEquals(1, result.tasks().size());
        assertTrue(result.tasks().get(0).isDone());
        assertEquals(completedTask.toFileString(), result.tasks().get(0).toFileString());
    }
}
