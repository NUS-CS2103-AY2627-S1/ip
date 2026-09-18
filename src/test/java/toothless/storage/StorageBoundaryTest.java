package toothless.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import toothless.task.Deadline;
import toothless.task.Event;
import toothless.task.TaskList;
import toothless.task.Todo;

/**
 * Tests saved-data boundaries through isolated temporary files.
 */
public class StorageBoundaryTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void save_emptyList_replacesOldContentsWithEmptyFile() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("old"));
        storage.save(tasks);

        storage.save(new TaskList());

        assertEquals(List.of(), Files.readAllLines(file, StandardCharsets.UTF_8));
        assertTrue(storage.load().getTaskList().isEmpty());
    }

    @Test
    public void save_repeatedly_replacesRatherThanAppends() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(file);
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("读书 | read \\ notes"));

        storage.save(tasks);
        storage.save(tasks);

        assertEquals(1, Files.readAllLines(file, StandardCharsets.UTF_8).size());
        assertEquals("读书 | read \\ notes", storage.load().getTaskList().getTask(0).getDescription());
    }

    @Test
    public void load_emptyFile_returnsEmptyListWithoutWarning() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.createFile(file);

        StorageLoadResult result = new Storage(file).load();

        assertEquals(0, result.getMalformedLineCount());
        assertTrue(result.getTaskList().isEmpty());
    }

    @Test
    public void load_malformedRecords_keepsValidRecordsAndBlocksOverwrite() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        List<String> records = List.of(
                "T | 0 | valid 中文", "T | 2 | invalid status", "D | 0 | date | 2025-02-29",
                "E | 0 | meeting | 3pm | 2pm", "E | 0 | meeting | 2pm | ",
                "T | 0 | bad \\q escape", "T | 0 | incomplete \\",
                "T | 0 | ", "X | 0 | unknown", "T | 0 | valid 中文");
        Files.write(file, records, StandardCharsets.UTF_8);
        byte[] original = Files.readAllBytes(file);
        Storage storage = new Storage(file);

        StorageLoadResult result = storage.load();

        assertEquals(9, result.getMalformedLineCount());
        assertEquals(1, result.getTaskList().size());
        assertEquals("valid 中文", result.getTaskList().getTask(0).getDescription());
        assertThrows(StorageException.class, () -> storage.save(result.getTaskList()));
        assertTrue(java.util.Arrays.equals(original, Files.readAllBytes(file)));
    }

    @Test
    public void saveAndLoad_allTypesWithUnicodeAndEscapes_preservesFieldsAndOrder() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("读书 | chapter \\ one"));
        tasks.addTask(new Deadline("交报告", LocalDate.of(2024, 2, 29)));
        tasks.addTask(new Event("Meet 朋友", "2026-12-31 23:00", "2027-01-01 01:00"));
        tasks.markTask(2);

        storage.save(tasks);
        TaskList loaded = storage.load().getTaskList();

        assertEquals(3, loaded.size());
        assertEquals("读书 | chapter \\ one", loaded.getTask(0).getDescription());
        Deadline deadline = (Deadline) loaded.getTask(1);
        assertEquals(LocalDate.of(2024, 2, 29), deadline.getBy());
        assertTrue(deadline.isDone());
        Event event = (Event) loaded.getTask(2);
        assertEquals("Meet 朋友", event.getDescription());
        assertEquals("2026-12-31 23:00", event.getFrom());
        assertEquals("2027-01-01 01:00", event.getTo());
        assertFalse(event.isDone());
    }
}
