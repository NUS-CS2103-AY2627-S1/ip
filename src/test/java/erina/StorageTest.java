package erina;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import erina.task.Deadline;
import erina.task.Event;
import erina.task.Task;
import erina.task.Todo;

/**
 * Tests for {@link Storage}, which keeps the task list in a file.
 *
 * <p>Every test works inside its own temporary folder, so the real save file
 * is never touched.
 */
public class StorageTest {
    @TempDir
    Path tempDir;

    private Path saveFile() {
        return tempDir.resolve("erina.txt");
    }

    /** Returns the save form of each task, which is easy to compare. */
    private static List<String> saveStrings(List<Task> tasks) {
        return tasks.stream().map(Task::toSaveString).toList();
    }

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws ErinaException {
        assertTrue(new Storage(saveFile()).load().isEmpty());
    }

    @Test
    public void save_thenLoad_restoresEveryKindOfTaskAndItsStatus() throws ErinaException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        List<Task> tasks = List.of(
                todo,
                new Deadline("return book", LocalDate.of(2026, 9, 18)),
                new Event("project meeting", "Mon 2pm", "4pm"));
        Storage storage = new Storage(saveFile());

        storage.save(tasks);

        assertEquals(saveStrings(tasks), saveStrings(storage.load()));
    }

    @Test
    public void save_folderDoesNotExist_createsIt() throws ErinaException {
        Path nested = tempDir.resolve("missing").resolve("folder").resolve("erina.txt");

        new Storage(nested).save(List.of(new Todo("read book")));

        assertTrue(Files.exists(nested));
    }

    @Test
    public void load_blankLines_areSkipped() throws IOException, ErinaException {
        Files.writeString(saveFile(), "T | 0 | read book\n\n   \nT | 1 | buy milk\n");
        assertEquals(2, new Storage(saveFile()).load().size());
    }

    @Test
    public void load_unknownTaskType_reportsTheLineNumber() throws IOException {
        Files.writeString(saveFile(), "T | 0 | read book\nX | 0 | mystery\n");

        ErinaException e = assertThrows(ErinaException.class, () -> new Storage(saveFile()).load());

        assertTrue(e.getMessage().contains("Line 2"), e.getMessage());
    }

    @Test
    public void load_missingField_throws() throws IOException {
        Files.writeString(saveFile(), "D | 0 | return book\n");
        assertThrows(ErinaException.class, () -> new Storage(saveFile()).load());
    }

    @Test
    public void load_extraField_throws() throws IOException {
        Files.writeString(saveFile(), "T | 0 | read book | surprise\n");
        assertThrows(ErinaException.class, () -> new Storage(saveFile()).load());
    }

    @Test
    public void load_unknownDoneFlag_throws() throws IOException {
        Files.writeString(saveFile(), "T | 2 | read book\n");
        assertThrows(ErinaException.class, () -> new Storage(saveFile()).load());
    }

    @Test
    public void load_nonExistentDate_throws() throws IOException {
        Files.writeString(saveFile(), "D | 0 | pay rent | 2026-02-30\n");
        assertThrows(ErinaException.class, () -> new Storage(saveFile()).load());
    }

    @Test
    public void load_pathIsAFolder_throws() {
        // The temporary folder exists but cannot be read as a file.
        assertThrows(ErinaException.class, () -> new Storage(tempDir).load());
    }

    @Test
    public void backUp_existingFile_copiesItsContentAlongside() throws IOException, ErinaException {
        Files.writeString(saveFile(), "not a task\n");

        Path backup = new Storage(saveFile()).backUp();

        assertEquals(tempDir.resolve("erina.txt.bak"), backup);
        assertEquals("not a task\n", Files.readString(backup));
    }
}
