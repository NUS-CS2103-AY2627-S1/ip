package catgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests task persistence and corrupted-data handling in {@link Storage}.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void loadMissingFileReturnsEmptyList() throws CatGPTException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    void saveAndLoadAllTaskTypesPreservesDetailsAndStatus() throws CatGPTException {
        Path dataFile = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(dataFile.toString());
        Todo todo = new Todo("read | review notes");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 18));
        deadline.markAsDone();
        Event event = new Event("project meeting", "Monday 2pm", "Monday 3pm");

        storage.save(new TaskList(List.of(todo, deadline, event)));
        List<Task> loadedTasks = storage.load();

        assertTrue(Files.exists(dataFile));
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read | review notes", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Sep 18 2026)", loadedTasks.get(1).toString());
        assertEquals(
                "[E][ ] project meeting (from: Monday 2pm to: Monday 3pm)",
                loadedTasks.get(2).toString());
    }

    @Test
    void loadMalformedLineThrowsCorruptionException() throws IOException {
        assertCorruptedData("TODO | 0 | missing-field");
    }

    @Test
    void loadUnknownTaskTypeThrowsCorruptionException() throws IOException {
        assertCorruptedData("NOTE | 0 | dGFzaw== | ");
    }

    @Test
    void loadInvalidCompletionStatusThrowsCorruptionException() throws IOException {
        assertCorruptedData("TODO | yes | dGFzaw== | ");
    }

    @Test
    void loadInvalidBase64ThrowsCorruptionException() throws IOException {
        assertCorruptedData("TODO | 0 | *** | ");
    }

    @Test
    void loadInvalidDeadlineDateThrowsCorruptionException() throws IOException {
        assertCorruptedData("DEADLINE | 0 | dGFzaw== | MjAyNi0wMi0zMA==");
    }

    @Test
    void loadTooManyLinesThrowsException() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        List<String> lines = new ArrayList<>();
        for (int i = 0; i <= TaskList.MAX_TASKS; i++) {
            lines.add("TODO | 0 | dGFzaw== | ");
        }
        Files.write(dataFile, lines);
        Storage storage = new Storage(dataFile.toString());

        CatGPTException exception = assertThrows(CatGPTException.class, storage::load);

        assertEquals("The saved task list contains too many tasks.", exception.getMessage());
    }

    private void assertCorruptedData(String contents) throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, contents);
        Storage storage = new Storage(dataFile.toString());

        CatGPTException exception = assertThrows(CatGPTException.class, storage::load);

        assertEquals("The saved task data is corrupted at line 1.", exception.getMessage());
    }
}
