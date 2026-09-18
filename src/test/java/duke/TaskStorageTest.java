package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests persistence and validation of saved Furina tasks. */
class TaskStorageTest {
    private static final Path STORAGE_PATH = Path.of("data", "duke.txt");

    private byte[] originalContents;

    private boolean fileExisted;

    @BeforeEach
    void backUpStorageFile() throws IOException {
        fileExisted = Files.exists(STORAGE_PATH);
        originalContents = fileExisted
                ? Files.readAllBytes(STORAGE_PATH)
                : new byte[0];
        Files.deleteIfExists(STORAGE_PATH);
    }

    @AfterEach
    void restoreStorageFile() throws IOException {
        Files.deleteIfExists(STORAGE_PATH);
        if (fileExisted) {
            Files.createDirectories(STORAGE_PATH.getParent());
            Files.write(STORAGE_PATH, originalContents);
        }
    }

    @Test
    void load_missingFile_returnsEmptyList() {
        assertTrue(TaskStorage.load().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypesPreserveDetailsAndStatus() {
        Task todo = new Task(TaskType.TODO, "read | book", null, null, null);
        Task deadline = new Task("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Task event = new Task(TaskType.EVENT, "project meeting", null,
                "Mon | 2pm", "4pm");
        deadline.markAsDone();

        assertTrue(TaskStorage.save(List.of(todo, deadline, event)));

        ArrayList<Task> loadedTasks = TaskStorage.load();
        assertEquals(3, loadedTasks.size());
        assertEquals("[T][ ] read | book", loadedTasks.get(0).toString());
        assertEquals("[D][X] return book (by: Dec 2 2019 6:00 PM)",
                loadedTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon | 2pm to: 4pm)",
                loadedTasks.get(2).toString());
    }

    @Test
    void load_invalidRecords_skipsOnlyInvalidRecords() throws IOException {
        Files.createDirectories(STORAGE_PATH.getParent());
        Files.write(STORAGE_PATH, List.of(
                "T | 0 | valid task",
                "X | 0 | unknown type",
                "D | 2 | invalid status | Sunday",
                "E | 0 | missing end | Monday",
                "T | 1 | another valid task"), StandardCharsets.UTF_8);

        ArrayList<Task> loadedTasks = TaskStorage.load();
        assertEquals(2, loadedTasks.size());
        assertEquals("[T][ ] valid task", loadedTasks.get(0).toString());
        assertEquals("[T][X] another valid task", loadedTasks.get(1).toString());
    }

    @Test
    void save_emptyList_createsEmptyStorageFile() throws IOException {
        assertTrue(TaskStorage.save(List.of()));

        assertTrue(Files.exists(STORAGE_PATH));
        assertFalse(Files.readString(STORAGE_PATH).contains("|"));
        assertTrue(TaskStorage.load().isEmpty());
    }
}
