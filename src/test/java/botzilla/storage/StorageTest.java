package botzilla.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import botzilla.BotzillaException;
import botzilla.task.DeadlineTask;
import botzilla.task.EventTask;
import botzilla.task.Task;
import botzilla.task.ToDoTask;

/**
 * Tests that tags survive a save/load round trip through the save file,
 * and that save files written before tags existed (with no trailing
 * tags field) still load correctly.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("botzilla.txt").toString());
    }

    @Test
    void saveThenLoad_todoWithTags_preservesTags() throws BotzillaException {
        Storage storage = newStorage();
        Task task = new ToDoTask("read book");
        task.addTag("fun");
        task.addTag("easy");

        storage.save(new ArrayList<>(List.of(task)));
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(Set.of("fun", "easy"), loaded.get(0).getTags());
    }

    @Test
    void saveThenLoad_deadlineWithTags_preservesTagsAndDate() throws BotzillaException {
        Storage storage = newStorage();
        Task task = new DeadlineTask("return book", "2/12/2019 1800");
        task.addTag("urgent");

        storage.save(new ArrayList<>(List.of(task)));
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(Set.of("urgent"), loaded.get(0).getTags());
        assertTrue(loaded.get(0).getDate().isPresent());
    }

    @Test
    void saveThenLoad_eventWithTags_preservesTagsAndBothDates() throws BotzillaException {
        Storage storage = newStorage();
        Task task = new EventTask("party", "1/1/2026 1800", "1/1/2026 2200");
        task.addTag("fun");
        task.addTag("social");

        storage.save(new ArrayList<>(List.of(task)));
        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(Set.of("fun", "social"), loaded.get(0).getTags());
        assertTrue(loaded.get(0).getDate().isPresent());
    }

    @Test
    void saveThenLoad_taskWithoutTags_loadsWithEmptyTags() throws BotzillaException {
        Storage storage = newStorage();
        Task task = new ToDoTask("read book");

        storage.save(new ArrayList<>(List.of(task)));
        ArrayList<Task> loaded = storage.load();

        assertTrue(loaded.get(0).getTags().isEmpty());
    }

    @Test
    void load_oldFormatLineWithoutTagsField_stillLoadsWithEmptyTags() throws BotzillaException, IOException {
        Path file = tempDir.resolve("botzilla.txt");
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            // Pre-tags save format: no trailing tags column.
            writer.write("D | 1 | return book | 2/12/2019 1800\n");
        }
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).getTags().isEmpty());
    }

    // ---- environment issues: file content not as expected ----
    @Test
    void load_corruptedLines_areSkippedButCounted() throws BotzillaException, IOException {
        Path file = tempDir.resolve("botzilla.txt");
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write("T | 0 | read book\n");
            writer.write("this line is not in the expected format\n");
            writer.write("Z | 0 | unknown type code\n");
            writer.write("D | 0 | return book\n"); // missing the required "/by" field
            writer.write("E | 1 | party | 1/1/2026 1800\n"); // missing the required "/to" field
        }
        Storage storage = new Storage(file.toString());

        ArrayList<Task> loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("read book", loaded.get(0).getName());
        assertEquals(4, storage.getSkippedLineCount());
    }

    @Test
    void load_noCorruptedLines_skippedCountIsZero() throws BotzillaException {
        Storage storage = newStorage();
        storage.save(new ArrayList<>(List.of(new ToDoTask("read book"))));

        storage.load();

        assertEquals(0, storage.getSkippedLineCount());
    }

    // ---- environment issues: file access denied ----
    @Test
    void save_parentPathIsAFileNotADirectory_throwsException() throws IOException {
        // Force the save to fail: the "parent directory" the save file
        // would need to live under is actually a regular file, so it can
        // neither be treated as an existing directory nor created as one.
        Path blockingFile = tempDir.resolve("blocked");
        Files.writeString(blockingFile, "not a directory");
        Storage storage = new Storage(blockingFile.resolve("botzilla.txt").toString());

        assertThrows(BotzillaException.class, () -> storage.save(new ArrayList<>(List.of(new ToDoTask("x")))));
    }
}
