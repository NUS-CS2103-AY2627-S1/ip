package woofer.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import woofer.task.Deadline;
import woofer.task.Event;
import woofer.task.TaskList;
import woofer.task.Todo;

/** Tests storage recovery without touching the user's actual task file. */
public class StorageTest {
    @TempDir
    private Path directory;

    /**
     * Checks first-run creation and repeated saves of every task type and completion status.
     *
     * @throws IOException when temporary storage fails.
     */
    @Test
    public void missingFileAndRoundTrip() throws IOException {
        Storage storage = new Storage(directory.resolve("data/tasks.txt"));
        TaskList tasks = storage.load();
        assertEquals(0, tasks.size());
        Todo todo = new Todo("walk dog");
        todo.markAsDone();
        tasks.addTask(todo);
        tasks.addTask(new Deadline("food", LocalDate.of(2026, 9, 16)));
        tasks.addTask(new Event("trip", LocalDate.of(2026, 9, 16), LocalDate.of(2026, 9, 17)));
        storage.save(tasks);
        assertEquals(tasks.getTasks().stream().map(task -> task.getDisplayText()).toList(),
                storage.load().getTasks().stream().map(task -> task.getDisplayText()).toList());
        tasks.deleteTask(1);
        storage.save(tasks);
        assertEquals(2, storage.load().size());
    }

    /**
     * Ensures malformed records are reported and cannot be overwritten by subsequent saves.
     *
     * @throws IOException when creating the fixtures fails.
     */
    @Test
    public void invalidRecordsProtectOriginalFile() throws IOException {
        String[] records = {
            "T | 2 | bad status", "T | 0 |", "X | 0 | unknown", "T | 0 | a | b",
            "D | 0 | bad date | 2026-02-30",
            "E | 0 | reversed | 2026-09-17 | 2026-09-16",
            "E | 0 | equal | 2026-09-16 | 2026-09-16"
        };
        Path file = directory.resolve("tasks.txt");
        for (String record : records) {
            String original = "T | 0 | valid\n" + record;
            Files.writeString(file, original);
            Storage storage = new Storage(file);
            IOException error = assertThrows(IOException.class, storage::load);
            assertEquals("Invalid saved task on line 2.", error.getMessage());
            assertThrows(IOException.class, () -> storage.save(new TaskList()));
            assertEquals(original, Files.readString(file));
        }
    }

    /**
     * Ensures excess saved tasks are not silently discarded and persisted as a shorter list.
     *
     * @throws IOException when creating the fixture fails.
     */
    @Test
    public void oversizedListIsProtected() throws IOException {
        Path file = directory.resolve("tasks.txt");
        String original = "T | 0 | task\n".repeat(101);
        Files.writeString(file, original);
        Storage storage = new Storage(file);
        assertThrows(IOException.class, storage::load);
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
        assertEquals(original, Files.readString(file));
    }

    /**
     * Checks read failure when the expected data file is actually a directory.
     *
     * @throws IOException when creating the fixture fails.
     */
    @Test
    public void directoryCannotBeLoadedOrOverwritten() throws IOException {
        Path file = Files.createDirectory(directory.resolve("tasks.txt"));
        Storage storage = new Storage(file);
        assertThrows(IOException.class, storage::load);
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
    }

    /**
     * Checks blank lines and UTF-8 text, and verifies exact serialization of completed dated tasks.
     *
     * @throws IOException when temporary storage fails.
     */
    @Test
    public void unicodeAndCompletedDatesRoundTrip() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "\nD | 1 | 买食物 | 2028-02-29\n\n"
                + "E | 1 | café | 2026-12-31 | 2027-01-01\n");
        Storage storage = new Storage(file);
        TaskList tasks = storage.load();
        assertEquals(2, tasks.size());
        storage.save(tasks);
        assertEquals(List.of("D | 1 | 买食物 | 2028-02-29",
                "E | 1 | café | 2026-12-31 | 2027-01-01"), Files.readAllLines(file));
        assertEquals("买食物", storage.load().getTask(1).getDescription());
    }

    /**
     * Checks a repaired file can be loaded again and an empty save clears all old records.
     *
     * @throws IOException when temporary storage fails.
     */
    @Test
    public void repairedFileCanBeLoadedAndCleared() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken");
        Storage storage = new Storage(file);
        assertThrows(IOException.class, storage::load);
        Files.writeString(file, "T | 0 | repaired");
        assertEquals(1, storage.load().size());
        storage.save(new TaskList());
        assertEquals("", Files.readString(file));
        assertEquals(0, storage.load().size());
    }

    /**
     * Checks malformed UTF-8 and missing fields are rejected without modifying the file.
     *
     * @throws IOException when creating fixtures fails.
     */
    @Test
    public void invalidEncodingAndMissingFieldsAreRejected() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.write(file, new byte[]{(byte) 0xc3, (byte) 0x28});
        Storage storage = new Storage(file);
        assertThrows(IOException.class, storage::load);
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
        for (String record : new String[]{"broken", "T | 0", "D | 0 | task |", "E | 0 | task | 2026-09-17"}) {
            Files.writeString(file, record);
            assertThrows(IOException.class, storage::load, record);
            assertEquals(record, Files.readString(file));
        }
    }

    /**
     * Checks a failed replacement retains the target and removes the temporary save file.
     *
     * @throws IOException when creating fixtures fails.
     */
    @Test
    public void failedReplacementCleansTemporaryFile() throws IOException {
        Path target = directory.resolve("tasks.txt");
        Storage storage = new Storage(target);
        storage.load();
        Files.createDirectory(target);
        Files.writeString(target.resolve("keep.txt"), "keep");
        assertThrows(IOException.class, () -> storage.save(new TaskList()));
        assertEquals("keep", Files.readString(target.resolve("keep.txt")));
        try (var files = Files.list(directory)) {
            assertEquals(List.of(target), files.toList());
        }
    }

}
