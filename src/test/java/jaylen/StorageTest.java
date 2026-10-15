package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence of task types, dates, and completion status. */
public class StorageTest {
    private static final String STORAGE_PROPERTY = "jaylen.storage";

    @TempDir
    private Path temporaryDirectory;

    private String previousStoragePath;
    private Path storageFile;

    @BeforeEach
    public void useTemporaryStorageFile() {
        previousStoragePath = System.getProperty(STORAGE_PROPERTY);
        storageFile = temporaryDirectory.resolve("tasks.txt");
        System.setProperty(STORAGE_PROPERTY, storageFile.toString());
    }

    @AfterEach
    public void restoreStorageProperty() {
        if (previousStoragePath == null) {
            System.clearProperty(STORAGE_PROPERTY);
        } else {
            System.setProperty(STORAGE_PROPERTY, previousStoragePath);
        }
    }

    @Test
    public void load_missingFile_returnsEmptyList() {
        assertFalse(Files.exists(storageFile));
        assertEquals(List.of(), new Storage().load());
    }

    @Test
    public void saveThenLoad_validTasks_restoresData() {
        Task completedTodo = new Todo("persisted task");
        completedTodo.markAsDone();
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 6, 18, 0));
        Event event = new Event("planning", LocalDateTime.of(2019, 12, 7, 9, 0),
                LocalDateTime.of(2019, 12, 7, 10, 0));

        new Storage().save(List.of(completedTodo, deadline, event));
        List<Task> loaded = new Storage().load();

        assertEquals(3, loaded.size());
        assertEquals("[T][X] persisted task", loaded.get(0).toString());
        assertEquals("[D][ ] submit report (by: Dec 06 2019 18:00)", loaded.get(1).toString());
        assertEquals("[E][ ] planning (from: Dec 07 2019 09:00 to: Dec 07 2019 10:00)",
                loaded.get(2).toString());
    }

    @Test
    public void loadMalformedFile_unsupportedType_throwsException() throws IOException {
        Files.writeString(storageFile, "X | 0 | unknown task");

        JaylenException exception = assertThrows(JaylenException.class, () -> new Storage().load());

        assertEquals("The save file contains an unknown task type.", exception.getMessage());
    }

    @Test
    public void loadMalformedFile_invalidCompletionStatus_throwsException() throws IOException {
        Files.writeString(storageFile, "T | yes | unknown task");

        JaylenException exception = assertThrows(JaylenException.class, () -> new Storage().load());

        assertEquals("The save file contains an invalid completion status.",
                exception.getMessage());
    }

    @Test
    public void loadMalformedFile_extraDelimiter_throwsException() throws IOException {
        Files.writeString(storageFile, "T | 0 | compare A | B");

        JaylenException exception = assertThrows(JaylenException.class, () -> new Storage().load());

        assertEquals("The save file is malformed on line 1.", exception.getMessage());
    }
}
