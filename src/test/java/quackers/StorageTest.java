package quackers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws QuackersException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("tasks.txt"));

        assertTrue(storage.load().isEmpty());
    }

    @Test
    void saveAndLoad_mixedTasks_preservesTaskData() throws QuackersException {
        Path filePath = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read book");
        Note note = new Note("bring laptop charger");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2027, 2, 28));
        Event event = new Event("project meeting", "2pm", "4pm");
        deadline.markAsDone();

        storage.save(List.of(todo, note, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertTrue(Files.exists(filePath));
        assertEquals(4, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[N] bring laptop charger", loadedTasks.get(1).toString());
        assertEquals("[D][X] submit report (by: Feb 28 2027)",
                loadedTasks.get(2).toString());
        assertEquals("[E][ ] project meeting (from: 2pm to: 4pm)",
                loadedTasks.get(3).toString());
    }

    @Test
    void load_invalidRecord_exceptionThrown() throws IOException {
        Path filePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(filePath, "D\t1\tsubmit report\tnot-a-date",
                StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath);

        QuackersException error = assertThrows(QuackersException.class, storage::load);

        assertEquals("Quack! The saved task file contains invalid data.", error.getMessage());
    }
}
