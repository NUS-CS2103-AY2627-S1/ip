package kiaw.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kiaw.task.Task;
import kiaw.task.Todo;

public class StorageTest {

    @TempDir
    Path tempDirectory;

    @Test
    public void load_missingFile_createsFileAndReturnsEmptyList()
            throws IOException {

        Storage storage = new Storage(
                tempDirectory.toString(),
                "tasks.txt"
        );

        ArrayList<Task> tasks = storage.load();

        assertTrue(tasks.isEmpty());
        assertTrue(Files.exists(
                tempDirectory.resolve("tasks.txt")
        ));
    }

    @Test
    public void saveAndLoad_validTask_taskPreserved()
            throws IOException {

        Storage storage = new Storage(
                tempDirectory.toString(),
                "tasks.txt"
        );

        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals(
                "read book",
                loadedTasks.get(0).getDescription()
        );
    }

    @Test
    public void load_invalidDate_skipsCorruptedTask()
            throws IOException {

        Path file = tempDirectory.resolve("tasks.txt");

        Files.write(
                file,
                List.of(
                        "T | 0 | valid todo",
                        "D | 0 | broken deadline | 2026-02-30",
                        "T | 0 | another valid todo"
                )
        );

        Storage storage = new Storage(
                tempDirectory.toString(),
                "tasks.txt"
        );

        ArrayList<Task> tasks = storage.load();

        assertEquals(2, tasks.size());
        assertEquals(
                "valid todo",
                tasks.get(0).getDescription()
        );
        assertEquals(
                "another valid todo",
                tasks.get(1).getDescription()
        );
    }

    @Test
    public void load_invalidStatus_skipsCorruptedTask()
            throws IOException {

        Path file = tempDirectory.resolve("tasks.txt");

        Files.write(
                file,
                List.of(
                        "T | 5 | corrupted",
                        "T | 0 | valid"
                )
        );

        Storage storage = new Storage(
                tempDirectory.toString(),
                "tasks.txt"
        );

        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(
                "valid",
                tasks.get(0).getDescription()
        );
    }

    @Test
    public void load_eventEndBeforeStart_skipsCorruptedTask()
            throws IOException {

        Path file = tempDirectory.resolve("tasks.txt");

        Files.write(
                file,
                List.of(
                        "E | 0 | broken event"
                                + " | 2026-10-10 | 2026-10-01",
                        "T | 0 | valid"
                )
        );

        Storage storage = new Storage(
                tempDirectory.toString(),
                "tasks.txt"
        );

        ArrayList<Task> tasks = storage.load();

        assertEquals(1, tasks.size());
        assertEquals(
                "valid",
                tasks.get(0).getDescription()
        );
    }
}
