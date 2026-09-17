package lion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests conversion between tasks and their save-file representations, and the
 * save/load round trip through disk.
 */
public class StorageTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void redirectStorageToTempDir() {
        Storage.setDirectoryPathForTesting(tempDir.toString());
    }

    @AfterEach
    void restoreStorage() {
        Storage.setDirectoryPathForTesting(null);
    }

    @Test
    void save_nullTaskList_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Storage.save(null));
    }

    @Test
    void save_thenLoad_roundTripsAllTaskTypesInOrder() throws IOException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", "2/12/2026 1800");
        Event event = new Event("meeting", "Monday 2pm", "Monday 4pm");

        Storage.save(List.of(todo, deadline, event));
        List<Task> loaded = Storage.load();

        assertEquals(3, loaded.size());
        assertEquals(todo.toString(), loaded.get(0).toString());
        assertEquals(deadline.toString(), loaded.get(1).toString());
        assertEquals(event.toString(), loaded.get(2).toString());
    }

    @Test
    void save_directoryDoesNotExist_createsIt() throws IOException {
        Storage.save(List.of(new Todo("read book")));

        assertTrue(new File(tempDir.toFile(), "lion.txt").exists());
    }

    @Test
    void load_noSaveFileYet_returnsEmptyList() throws IOException {
        assertTrue(Storage.load().isEmpty());
    }

    @Test
    void load_fileWithBlankLines_skipsThem() throws IOException {
        File file = new File(tempDir.toFile(), "lion.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("T | 0 | read book\n\n   \nT | 1 | write notes\n");
        }

        List<Task> loaded = Storage.load();

        assertEquals(2, loaded.size());
    }

    @Test
    void load_fileWithOneCorruptedLine_skipsItButKeepsTheRest() throws IOException {
        File file = new File(tempDir.toFile(), "lion.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("T | 0 | read book\nthis line is corrupted\nT | 0 | write notes\n");
        }

        List<Task> loaded = Storage.load();

        assertEquals(2, loaded.size());
        assertEquals("[T] [ ] read book", loaded.get(0).toString());
        assertEquals("[T] [ ] write notes", loaded.get(1).toString());
    }

    @Test
    void loadTaskList_wrapsLoadedTasksInTaskList() throws IOException {
        Storage.save(List.of(new Todo("read book"), new Todo("write notes")));

        TaskList tasks = Storage.loadTaskList();

        assertEquals(2, tasks.size());
    }

    @Test
    void loadTaskList_noSaveFileYet_returnsEmptyTaskList() throws IOException {
        assertEquals(0, Storage.loadTaskList().size());
    }

    @Test
    void save_emptyTaskList_writesEmptyFile() throws IOException {
        Storage.save(List.of());

        assertTrue(Storage.load().isEmpty());
    }

    @Test
    void save_directoryDoesNotExistYetAtAll_createsNestedDirectory() throws IOException {
        // Distinct from save_directoryDoesNotExist_createsIt above: @TempDir already
        // creates tempDir itself, so that test never actually exercises the
        // directory.mkdirs() branch. A not-yet-existing nested path does.
        Storage.setDirectoryPathForTesting(tempDir.resolve("nested/dir").toString());

        Storage.save(List.of(new Todo("read book")));

        assertTrue(new File(tempDir.resolve("nested/dir").toFile(), "lion.txt").exists());
    }

    @Test
    void load_saveFilePathIsActuallyADirectory_throwsIoException() throws IOException {
        // A directory existing where the save file is expected is an unusual but
        // possible environment problem (e.g. manual tampering); Scanner cannot open a
        // directory for reading, so this should surface as IOException, not crash
        // with an unrelated unchecked exception.
        File directoryWhereFileIsExpected = new File(tempDir.toFile(), "lion.txt");
        directoryWhereFileIsExpected.mkdirs();

        assertThrows(IOException.class, Storage::load);
    }

    @Test
    void save_directoryPathIsActuallyAFile_throwsIoException() throws IOException {
        // An existing regular file where the data directory is expected means the
        // directory can never be created; the write itself should then fail with
        // IOException rather than an unchecked exception.
        File fileBlockingDirectory = new File(tempDir.toFile(), "not-a-directory");
        fileBlockingDirectory.createNewFile();
        Storage.setDirectoryPathForTesting(fileBlockingDirectory.getPath());

        assertThrows(IOException.class, () -> Storage.save(List.of(new Todo("read book"))));
    }

    @Test
    void encodeThenDecode_deadline_preservesAllDetails() {
        Deadline original = new Deadline("return book", "30/8/2026 1800");
        original.markAsDone();

        Task restored = Storage.decode(Storage.encode(original));

        assertEquals(original.toString(), restored.toString());
    }

    @Test
    void encodeThenDecode_event_preservesAllDetails() {
        Event original = new Event("project meeting", "Monday 2pm", "Monday 4pm");

        Task restored = Storage.decode(Storage.encode(original));

        assertEquals(original.toString(), restored.toString());
    }

    @Test
    void decode_unrecognizedTaskType_fallsBackToTodo() {
        Task restored = Storage.decode("X | 0 | mystery task");

        assertEquals("[T] [ ] mystery task", restored.toString());
    }

    @Test
    void decode_tooFewFields_throwsIllegalArgumentException() {
        // A hand-edited or truncated save-file line should be rejected outright, not
        // silently indexed out of bounds (which relying on disabled-by-default asserts
        // used to risk in a normally-run/packaged build).
        assertThrows(IllegalArgumentException.class, () -> Storage.decode("T | 0"));
    }

    @Test
    void decode_deadlineMissingByField_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Storage.decode("D | 0 | return book"));
    }

    @Test
    void decode_eventMissingToField_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Storage.decode("E | 0 | meeting | Monday 2pm"));
    }

    @Test
    void encode_taskDescriptionWithPipeCharacter_wouldCorruptTheField() {
        // Documents the storage-format limitation that motivates Lion.getResponse()
        // rejecting '|' in user input up front: the pipe is also the field separator,
        // so a description containing one desyncs decode()'s field count on reload.
        Task task = new Todo("buy milk | bread");

        String encoded = Storage.encode(task);

        assertTrue(encoded.split("\\s*\\|\\s*", -1).length > 3);
    }
}
