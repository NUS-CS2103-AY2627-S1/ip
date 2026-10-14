package baby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {
    @TempDir
    private Path tempDirectory;

    @Test
    public void loadTasks_missingFile_returnsEmptyList() {
        ArrayList<Task> tasks = Storage.loadTasks(tempDirectory.resolve("missing.txt"));

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void loadTasks_corruptedLines_skipsInvalidAndLoadsValid() throws IOException {
        Path dataFile = tempDirectory.resolve("baby.txt");
        Files.write(dataFile, List.of(
                "T | 0 | read book",
                "X | 0 | unknown type",
                "D | yes | invalid status | 2026-08-30 1200",
                "D | 0 | impossible date | 2026-02-30 1200",
                "E | 0 | backwards event | 2026-08-30 1400 | 2026-08-30 1200",
                "D | 1 | return book | 2026-08-30 1200"));

        ArrayList<Task> tasks = Storage.loadTasks(dataFile);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[D][X] return book (by: Aug 30 2026, 12:00 PM)", tasks.get(1).toString());
    }

    @Test
    public void loadTasks_pathIsDirectory_returnsEmptyList() {
        ArrayList<Task> tasks = Storage.loadTasks(tempDirectory);

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void saveTasks_missingParentDirectory_createsFile() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        Path dataFile = tempDirectory.resolve("nested/data/baby.txt");

        boolean isSaved = Storage.saveTasks(tasks, dataFile);

        assertTrue(isSaved);
        assertTrue(Files.exists(dataFile));
        assertEquals(1, Storage.loadTasks(dataFile).size());
    }

    @Test
    public void saveTasks_pathIsDirectory_returnsFalse() {
        boolean isSaved = Storage.saveTasks(new ArrayList<>(), tempDirectory);

        assertFalse(isSaved);
    }
}
