package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence and validation of the task storage format. */
class TaskStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAndLoad_typedTasks_preservesTaskData() throws JarvisException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("jarvis.txt"));
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));
        deadline.markAsDone();
        Event event = new Event("project meeting", "Mon 2pm", "Mon 4pm");

        storage.save(List.of(todo, deadline, event));

        List<Task> loadedTasks = storage.load();
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read book", loadedTasks.get(0).toString());
        assertEquals("[D][X] submit report (by: Sep 20 2026)", loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: Mon 4pm)", loadedTasks.get(2).toString());
    }

    @Test
    void load_malformedRecords_ignoresInvalidRecords() throws IOException {
        Path dataFile = temporaryDirectory.resolve("jarvis.txt");
        Files.write(dataFile, List.of(
                "T | 0 | read book",
                "D | 1 | submit report | 2026-09-20",
                "E | 0 | project meeting | Mon 2pm Mon 4pm",
                "X | 0 | unsupported type",
                "D | 0 | missing date |",
                "not a valid record"), StandardCharsets.UTF_8);
        TaskStorage storage = new TaskStorage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).getDescription());
        assertEquals("submit report", loadedTasks.get(1).getDescription());
        assertEquals("project meeting", loadedTasks.get(2).getDescription());
    }

    @Test
    void load_invalidDateRecord_ignoresRecordAndKeepsValidTasks() throws IOException {
        Path dataFile = temporaryDirectory.resolve("jarvis.txt");
        Files.write(dataFile, List.of(
                "D | 0 | invalid date | 2026-02-30",
                "T | 0 | valid task"), StandardCharsets.UTF_8);
        TaskStorage storage = new TaskStorage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertEquals(1, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
    }

    @Test
    void load_emptyFile_returnsEmptyTaskList() throws IOException {
        Path dataFile = temporaryDirectory.resolve("jarvis.txt");
        Files.createFile(dataFile);
        TaskStorage storage = new TaskStorage(dataFile);

        assertEquals(List.of(), storage.load());
    }
}
