package lynn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_preservesAllTaskTypesAndStatus() throws LynnException {
        Storage storage = new Storage(temporaryDirectory.resolve("data/lynn.txt"));
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read | annotate book"));
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 10, 15));
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("team meeting", "Mon 2pm", "4pm"));

        storage.save(tasks);
        TaskList loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read | annotate book", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Oct 15 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] team meeting (from: Mon 2pm to: 4pm)", loadedTasks.get(2).toString());
    }

    @Test
    void load_malformedRecord_returnsHelpfulExceptionInsteadOfCrashing()
            throws IOException, LynnException {
        Path storagePath = temporaryDirectory.resolve("data/lynn.txt");
        Files.createDirectories(storagePath.getParent());
        Files.writeString(storagePath, "T|0\n", StandardCharsets.UTF_8);

        Storage storage = new Storage(storagePath);

        LynnException exception = assertThrows(LynnException.class, storage::load);

        assertEquals("Your saved task data is invalid.", exception.getMessage());
    }

    @Test
    void load_missingFile_createsStorageAndStartsWithAnEmptyList() throws IOException, LynnException {
        Path storagePath = temporaryDirectory.resolve("nested/data/lynn.txt");

        TaskList loadedTasks = new Storage(storagePath).load();

        assertEquals(0, loadedTasks.size());
        assertTrue(Files.exists(storagePath));
    }
}
