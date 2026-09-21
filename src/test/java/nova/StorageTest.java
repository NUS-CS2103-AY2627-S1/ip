package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {

    @TempDir
    private Path tempDirectory;

    @Test
    public void load_missingFile_createsEmptyStorageFile() throws IOException {
        Path storagePath = tempDirectory.resolve("nested/data/nova.txt");
        Storage storage = new Storage(storagePath);

        TaskList tasks = storage.load();

        assertEquals(0, tasks.size());
        assertTrue(Files.isRegularFile(storagePath));
        assertEquals("", Files.readString(storagePath));
    }

    @Test
    public void saveAndLoad_multipleTaskTypes_preservesTasks() throws IOException {
        Path storagePath = tempDirectory.resolve("nova.txt");
        Storage storage = new Storage(storagePath);
        Task todo = Task.todo("read book");
        Task deadline = Task.deadline(
                "return book", LocalDateTime.of(2026, 9, 18, 21, 30));
        Task event = Task.event("consultation", "2pm", "4pm");
        deadline.markDone();
        TaskList originalTasks = new TaskList(List.of(todo, deadline, event));

        storage.save(originalTasks);
        TaskList loadedTasks = storage.load();

        assertEquals(originalTasks.size(), loadedTasks.size());
        for (int i = 1; i <= originalTasks.size(); i++) {
            assertEquals(originalTasks.get(i).toStorageString(),
                    loadedTasks.get(i).toStorageString());
        }
    }

    @Test
    public void load_blankAndCorruptedLines_ignoresInvalidEntries() throws IOException {
        Path storagePath = tempDirectory.resolve("nova.txt");
        Files.write(storagePath, List.of(
                "TODO\t0\tread book\t",
                "",
                "invalid stored task",
                "DEADLINE\t0\tbad date\ttomorrow",
                "EVENT\t1\tconsultation\t(from: 2pm to: 4pm)"));
        Storage storage = new Storage(storagePath);

        TaskList tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals("TODO\t0\tread book\t", tasks.get(1).toStorageString());
        assertEquals("EVENT\t1\tconsultation\t(from: 2pm to: 4pm)",
                tasks.get(2).toStorageString());
        assertTrue(storage.hasCorruptedEntries());
    }

    @Test
    public void load_validEntries_reportsNoCorruption() throws IOException {
        Path storagePath = tempDirectory.resolve("nova.txt");
        Files.writeString(storagePath, "TODO\t0\tread book\t");
        Storage storage = new Storage(storagePath);

        storage.load();

        assertFalse(storage.hasCorruptedEntries());
    }

    @Test
    public void save_existingFile_replacesPreviousContents() throws IOException {
        Path storagePath = tempDirectory.resolve("nova.txt");
        Files.writeString(storagePath,
                "TODO\t0\told task\t" + System.lineSeparator()
                        + "TODO\t0\tsecond old task\t" + System.lineSeparator());
        Storage storage = new Storage(storagePath);

        storage.save(new TaskList(List.of(Task.todo("new task"))));

        assertEquals(List.of("TODO\t0\tnew task\t"),
                Files.readAllLines(storagePath));
    }
}
