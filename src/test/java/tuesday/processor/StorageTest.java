package tuesday.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tuesday.task.TaskList;

/**
 * Tests compatibility of storage with pre-existing duplicate tasks.
 */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadData_overlappingEventMarkers_skipsRowAndContinues() throws IOException {
        Path saveFile = temporaryDirectory.resolve("Tuesday.txt");
        Files.writeString(saveFile, "[T][ ] before\n[E][ ] x (from: to: 10:00)\n[T][X] after\n");
        TaskList tasks = new TaskList();

        Storage.loadData(saveFile.toString(), tasks);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] before", tasks.get(0).toString());
        assertEquals("[T][X] after", tasks.get(1).toString());
    }

    @Test
    public void loadData_fileContainsDuplicates_preservesAllDuplicates() throws IOException {
        Path saveFile = temporaryDirectory.resolve("Tuesday.txt");
        Files.writeString(saveFile, "[T][ ] Read Book" + System.lineSeparator()
                + "[D][X] read book (by: Sep 20 2026)" + System.lineSeparator());
        TaskList tasks = new TaskList();

        Storage.loadData(saveFile.toString(), tasks);

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] Read Book", tasks.get(0).toString());
        assertEquals("[D][X] read book (by: Sep 20 2026)", tasks.get(1).toString());
    }
}
