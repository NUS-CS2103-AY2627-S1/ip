package davidgoggins.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import davidgoggins.DavidGogginsException;
import davidgoggins.task.Deadline;
import davidgoggins.task.Event;
import davidgoggins.task.Task;
import davidgoggins.task.Todo;
import davidgoggins.ui.Ui;

/**
 * Tests {@link Storage} against real files in a temporary folder.
 *
 * <p>Real files are used rather than a fake, because what matters here is how the class
 * copes with the file system itself: missing folders, damaged files, and permissions.
 * Warnings are captured through {@link Ui} so each test can check what the user is told.
 */
public class StorageTest {

    /** A new, empty folder for each test, created and deleted by JUnit. */
    @TempDir
    Path tempDir;

    private Ui ui;

    /** The save file, inside a data folder that does not exist until a save creates it. */
    private Path file;

    private Storage storage;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
        file = tempDir.resolve("data").resolve("tasks.txt");
        storage = new Storage(file.toString(), ui);
    }

    /** Returns what the user is told while running {@code action}. */
    private String warningsDuring(Runnable action) {
        ui.startCapture();
        action.run();
        return ui.takeCaptured();
    }

    /** Writes the given text as the save file, creating the data folder first. */
    private void writeSaveFile(String text) throws IOException {
        Files.createDirectories(file.getParent());
        Files.writeString(file, text);
    }

    /** Returns each task's save line, which is easier to compare than the tasks themselves. */
    private static List<String> saveLines(List<Task> tasks) {
        return tasks.stream().map(Task::toSaveFormat).toList();
    }

    /** A first run on a new machine is normal, so nothing should be said about it. */
    @Test
    public void load_noSaveFile_emptyListWithoutWarning() {
        String warnings = warningsDuring(() -> assertTrue(storage.load().isEmpty()));
        assertEquals("", warnings);
    }

    @Test
    public void save_thenLoad_sameTasksBack() throws DavidGogginsException {
        Todo done = new Todo("read book");
        done.markAsDone();
        List<Task> tasks = List.of(done,
                new Deadline("return book", "2026-09-10"),
                new Event("camp", "2026-10-01", "2026-10-03"));

        String warnings = warningsDuring(() -> storage.save(tasks));

        assertEquals("", warnings);
        assertEquals(saveLines(tasks), saveLines(storage.load()));
    }

    @Test
    public void save_missingDataFolder_folderCreated() {
        storage.save(List.of(new Todo("read book")));
        assertTrue(Files.isRegularFile(file));
    }

    @Test
    public void save_emptyList_emptyFileWritten() throws IOException {
        storage.save(List.of());
        assertEquals("", Files.readString(file));
    }

    /** The file is replaced through a temporary copy, which must not be left behind. */
    @Test
    public void save_twice_noTemporaryFilesLeft() throws IOException {
        storage.save(List.of(new Todo("read book")));
        storage.save(List.of(new Todo("run")));
        try (var entries = Files.list(file.getParent())) {
            assertEquals(List.of(file), entries.toList());
        }
    }

    @Test
    public void load_blankLines_ignoredWithoutWarning() throws IOException {
        writeSaveFile("\nT | 0 | read book\n   \n");
        String warnings = warningsDuring(() -> assertEquals(1, storage.load().size()));
        assertEquals("", warnings);
    }

    @Test
    public void load_corruptLine_readableLinesKeptAndFileBackedUp() throws IOException {
        String original = "T | 1 | read book\ngarbage\nD | 0 | return book | 2026-09-10\n";
        writeSaveFile(original);

        String warnings = warningsDuring(() -> assertEquals(2, storage.load().size()));

        assertTrue(warnings.contains("skipped 1 unreadable line in"), warnings);
        assertTrue(warnings.contains("backed up to"), warnings);
        assertEquals(original, Files.readString(tempDir.resolve("data").resolve("tasks.txt.bak")));
    }

    @Test
    public void load_textThatIsNotUtf8_warningAndFileBackedUp() throws IOException {
        Files.createDirectories(file.getParent());
        // 0xC3 starts a two-byte UTF-8 character, but 0x28 cannot follow it.
        Files.write(file, new byte[] {(byte) 0xC3, (byte) 0x28});

        String warnings = warningsDuring(() -> assertTrue(storage.load().isEmpty()));

        assertTrue(warnings.contains("not a plain text file"), warnings);
        assertTrue(Files.exists(tempDir.resolve("data").resolve("tasks.txt.bak")));
    }

    /** A folder has no lines worth keeping, so no backup is attempted for it. */
    @Test
    public void load_folderInPlaceOfFile_warningWithoutBackup() throws IOException {
        Files.createDirectories(file);

        String warnings = warningsDuring(() -> assertTrue(storage.load().isEmpty()));

        assertTrue(warnings.contains("could not read your saved tasks"), warnings);
        assertFalse(warnings.contains("backed up"), warnings);
    }

    /**
     * A file that can be neither read nor backed up must survive the next save, since it
     * may hold the user's only copy of their tasks.
     */
    @Test
    public void save_afterUnreadableFileLoaded_fileNotOverwritten() throws IOException {
        writeSaveFile("T | 0 | only copy\n");
        file.toFile().setReadable(false);
        // Skipped where permissions cannot be taken away, e.g. on Windows or as root.
        assumeFalse(Files.isReadable(file), "file permissions are not enforced here");

        try {
            String loadWarnings = warningsDuring(() -> storage.load());
            String saveWarnings = warningsDuring(() -> storage.save(List.of(new Todo("new task"))));

            assertTrue(loadWarnings.contains("permission denied"), loadWarnings);
            assertTrue(saveWarnings.contains("not saving"), saveWarnings);
        } finally {
            file.toFile().setReadable(true);
        }
        assertEquals("T | 0 | only copy\n", Files.readString(file));
    }

    @Test
    public void save_fileInPlaceOfDataFolder_warningShown() throws IOException {
        Files.writeString(tempDir.resolve("data"), "not a folder");

        String warnings = warningsDuring(() -> storage.save(List.of(new Todo("read book"))));

        assertTrue(warnings.contains("could not save your tasks"), warnings);
    }
}
