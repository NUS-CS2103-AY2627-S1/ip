package leo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Storage}. Storage always reads/writes the fixed
 * path "data/leo.txt" relative to the working directory (there is no
 * way to point it elsewhere), so every test here backs up whatever is
 * at that path before it runs and restores it afterwards - otherwise
 * running this test class would overwrite a real save file sitting in
 * the project directory.
 */
public class StorageTest {
    private static final Path FILE_PATH = Paths.get("data", "leo.txt");

    private byte[] originalContent;
    private boolean originalFileExisted;

    @BeforeEach
    public void backUpExistingSaveFile() throws IOException {
        originalFileExisted = Files.exists(FILE_PATH);
        if (originalFileExisted) {
            originalContent = Files.readAllBytes(FILE_PATH);
        }
    }

    @AfterEach
    public void restoreExistingSaveFile() throws IOException {
        if (originalFileExisted) {
            Files.write(FILE_PATH, originalContent);
        } else if (Files.exists(FILE_PATH)) {
            Files.delete(FILE_PATH);
        }
    }

    @Test
    public void loadTasks_missingFile_createsEmptyFileAndReturnsEmptyList() throws IOException {
        if (Files.exists(FILE_PATH)) {
            Files.delete(FILE_PATH);
        }

        List<Task> loaded = Storage.loadTasks();

        assertTrue(Files.exists(FILE_PATH));
        assertEquals(0, loaded.size());
    }

    @Test
    public void loadTasks_blankLinesAreSkipped() throws IOException {
        Files.write(FILE_PATH, List.of("T | 0 | a", "", "   ", "T | 1 | b"));

        List<Task> loaded = Storage.loadTasks();

        assertEquals(2, loaded.size());
        assertEquals("a", loaded.get(0).getDescription());
        assertEquals("b", loaded.get(1).getDescription());
        assertTrue(loaded.get(1).isDone());
    }

    @Test
    public void loadTasks_notCappedAtOldFixedCapacityOfOneHundred() throws IOException {
        // A-MoreErrorHandling: Storage.loadTasks() used to populate a
        // caller-supplied Task[100] array (see Leo's old constructor);
        // this is a regression test guarding against that capacity
        // limit being reintroduced.
        StringBuilder fileContent = new StringBuilder();
        for (int i = 0; i < 150; i++) {
            fileContent.append("T | 0 | task ").append(i).append(System.lineSeparator());
        }
        Files.writeString(FILE_PATH, fileContent.toString());

        List<Task> loaded = Storage.loadTasks();

        assertEquals(150, loaded.size());
        assertEquals("task 149", loaded.get(149).getDescription());
    }

    @Test
    public void loadTasks_malformedLine_throwsIoException() throws IOException {
        Files.write(FILE_PATH, List.of("not a valid task line"));

        assertThrows(IOException.class, () -> Storage.loadTasks());
    }

    @Test
    public void loadTasks_unknownTypeCode_throwsIoException() throws IOException {
        Files.write(FILE_PATH, List.of("Z | 0 | mystery task"));

        assertThrows(IOException.class, () -> Storage.loadTasks());
    }

    @Test
    public void saveTasks_thenLoadTasks_roundTripsAllThreeTaskTypes() throws IOException {
        Task[] toSave = new Task[]{
            new Todo("read book"),
            new Deadline("return book", "2019-12-01 1800"),
            new Event("project meeting", "2019-12-02 1400", "2019-12-02 1600")
        };
        toSave[0].markAsDone();

        Storage.saveTasks(toSave, toSave.length);
        List<Task> loaded = Storage.loadTasks();

        assertEquals(3, loaded.size());
        assertEquals("T", loaded.get(0).getType());
        assertTrue(loaded.get(0).isDone());
        assertEquals("D", loaded.get(1).getType());
        assertEquals("E", loaded.get(2).getType());
        assertEquals(toSave[1].toFileString(), loaded.get(1).toFileString());
        assertEquals(toSave[2].toFileString(), loaded.get(2).toFileString());
    }

    @Test
    public void saveTasks_onlySavesFirstItemCountEntries() throws IOException {
        Task[] array = new Task[]{new Todo("a"), new Todo("b"), new Todo("c")};

        Storage.saveTasks(array, 2);
        List<Task> loaded = Storage.loadTasks();

        assertEquals(2, loaded.size());
        assertEquals("a", loaded.get(0).getDescription());
        assertEquals("b", loaded.get(1).getDescription());
    }
}
