package lenzabot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lenzabot.task.Deadline;
import lenzabot.task.Event;
import lenzabot.task.Task;
import lenzabot.task.Todo;

class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void saveTasks_multipleTaskTypes_writesAllTasksInOrder() throws IOException {
        Path saveFilePath = temporaryDirectory.resolve("data").resolve("lenzabot.txt");
        Storage storage = new Storage(saveFilePath);
        Task completedDeadline = new Deadline(
                "return book",
                LocalDateTime.of(2019, 12, 2, 18, 0)
        );
        completedDeadline.markAsCompleted();
        List<Task> tasks = List.of(
                new Todo("read book"),
                completedDeadline,
                new Event(
                        "project meeting",
                        LocalDateTime.of(2019, 12, 3, 14, 0),
                        LocalDateTime.of(2019, 12, 3, 16, 0)
                )
        );

        assertTrue(storage.saveTasks(tasks));

        assertEquals(
                List.of(
                        "T | 0 | read book",
                        "D | 1 | return book | 2019-12-02T18:00",
                        "E | 0 | project meeting | 2019-12-03T14:00 | 2019-12-03T16:00"
                ),
                Files.readAllLines(saveFilePath)
        );
    }

    @Test
    void loadTasks_validAndDamagedLines_loadsValidTasksAndSetsWarning() throws IOException {
        Path saveFilePath = temporaryDirectory.resolve("lenzabot.txt");
        Files.write(saveFilePath, List.of(
                "T | 0 | read book",
                "damaged line",
                "E | 0 | meeting | 2020-01-02T16:00 | 2020-01-02T15:00",
                "D | 1 | return book | 2020-01-03T18:00"
        ));
        Storage storage = new Storage(saveFilePath);

        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(2, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] return book (by: Jan 3 2020, 6:00 PM)", loadedTasks.get(1).toString());
        assertEquals("Skipped 2 damaged line(s) in the save file.", storage.getLoadWarning());
    }

    @Test
    void loadTasks_missingFile_returnsEmptyListWithoutWarning() {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt"));

        assertTrue(storage.loadTasks().isEmpty());
        assertNull(storage.getLoadWarning());
    }

    @Test
    void saveTasks_parentPathIsFile_returnsFalse() throws IOException {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "content");
        Storage storage = new Storage(blockingFile.resolve("data.txt"));

        assertFalse(storage.saveTasks(List.of(new Todo("read book"))));
    }
}
