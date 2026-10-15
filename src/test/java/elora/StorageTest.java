package elora;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elora.task.Deadline;
import elora.task.Event;
import elora.task.Task;
import elora.task.Todo;

class StorageTest {

    @TempDir
    Path tempDir;

    private String savePath() {
        return tempDir.resolve("elora.txt").toString();
    }

    @Test
    void load_missingFile_returnsEmptyListWithNoWarnings() {
        Storage storage = new Storage(savePath());
        assertTrue(storage.load().isEmpty());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void saveThenLoad_mixOfTaskTypes_roundTripsExactly() throws EloraException {
        Storage storage = new Storage(savePath());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        deadline.markAsDone();
        tasks.add(deadline);
        tasks.add(new Event("team meeting", "Mon 2pm", "4pm"));

        storage.save(tasks);
        List<Task> loaded = storage.load();

        assertEquals(tasks, loaded);
    }

    @Test
    void save_parentFolderMissing_createsItAndWritesFile() throws EloraException {
        Storage storage = new Storage(tempDir.resolve("nested").resolve("elora.txt").toString());
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertEquals(tasks, storage.load());
    }

    @Test
    void load_corruptedLine_isSkippedAndReportedAsWarning() throws IOException {
        Path file = tempDir.resolve("elora.txt");
        Files.writeString(file, "T | 0 | read book\nnot a valid line\nT | 1 | return book\n");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("not a valid line"));
    }

    @Test
    void load_blankLines_areSkippedSilently() throws IOException {
        Path file = tempDir.resolve("elora.txt");
        Files.writeString(file, "T | 0 | read book\n\n   \nT | 0 | return book\n");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertEquals(2, loaded.size());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void load_deadlineLineMissingDate_isSkippedAsWarning() throws IOException {
        Path file = tempDir.resolve("elora.txt");
        Files.writeString(file, "D | 0 | return book\n");
        Storage storage = new Storage(file.toString());

        List<Task> loaded = storage.load();

        assertTrue(loaded.isEmpty());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    void load_unreadableDeadlineDate_isSkippedAsWarning() throws IOException {
        Path file = tempDir.resolve("elora.txt");
        Files.writeString(file, "D | 0 | return book | not-a-date\n");
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    void load_unrecognizedTaskType_isSkippedAsWarning() throws IOException {
        Path file = tempDir.resolve("elora.txt");
        Files.writeString(file, "Z | 0 | mystery task\n");
        Storage storage = new Storage(file.toString());

        assertTrue(storage.load().isEmpty());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    void getLoadWarnings_afterCleanLoad_isEmpty() throws IOException {
        Path file = tempDir.resolve("elora.txt");
        Files.writeString(file, "T | 0 | read book\n");
        Storage storage = new Storage(file.toString());

        storage.load();

        assertTrue(storage.getLoadWarnings().isEmpty());
    }

    @Test
    void load_secondLoadAfterCorruption_clearsPreviousWarnings() throws IOException {
        Path file = tempDir.resolve("elora.txt");
        Files.writeString(file, "not valid\n");
        Storage storage = new Storage(file.toString());
        storage.load();
        assertFalse(storage.getLoadWarnings().isEmpty());

        Files.writeString(file, "T | 0 | read book\n");
        storage.load();

        assertTrue(storage.getLoadWarnings().isEmpty());
    }
}
