package nori.storage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import nori.NoriException;
import nori.task.Deadline;
import nori.task.Event;
import nori.task.Task;
import nori.task.Todo;
import nori.testutil.IsolatedSessionTest;

/**
 * Tests saving, loading, and recovering Nori's task file.
 *
 * Every test runs against its own temporary directory, named by the same system
 * property the application reads, so none of them can see or disturb the task
 * file of whoever is running them.
 */
public class StorageTest extends IsolatedSessionTest {
    private static final String STORAGE_FILE = "nori.txt";
    private static final String BACKUP_FILE = "nori.txt.bak";
    private static final String CORRUPT_FILE = "nori.txt.corrupt";

    @Test
    public void loadTasks_noStorageFile_returnsNoTasks() throws NoriException {
        assertEquals(List.of(), new Storage().loadTasks());
    }

    @Test
    public void loadTasks_noStorageFile_producesNoNotice() throws NoriException {
        Storage storage = new Storage();

        storage.loadTasks();

        assertNull(storage.getLoadingNotice());
    }

    @Test
    public void saveTasks_everyTaskType_restoresThemAllInOrder() throws NoriException {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-06-06"));
        Event event = new Event("book fair", "2019-06-06 1000", "1800");
        new Storage().saveTasks(List.of(todo, deadline, event));

        List<Task> restoredTasks = new Storage().loadTasks();

        assertEquals(3, restoredTasks.size());
        assertEquals("[T][ ] read book", restoredTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Jun 06 2019)", restoredTasks.get(1).toString());
        assertEquals("[E][ ] book fair (from: 2019-06-06 1000 to: 1800)",
                restoredTasks.get(2).toString());
    }

    @Test
    public void saveTasks_completedTask_restoresItCompleted() throws NoriException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        new Storage().saveTasks(List.of(todo));

        assertTrue(new Storage().loadTasks().get(0).isDone());
    }

    @Test
    public void saveTasks_noTasks_restoresAnEmptyList() throws NoriException {
        new Storage().saveTasks(List.of());

        assertEquals(List.of(), new Storage().loadTasks());
    }

    @Test
    public void saveTasks_descriptionHoldingTheFieldSeparator_restoresItLiterally()
            throws NoriException {
        new Storage().saveTasks(List.of(new Todo("revise | annotate | notes")));

        assertEquals("[T][ ] revise | annotate | notes",
                new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void saveTasks_descriptionHoldingNonAsciiText_restoresItLiterally() throws NoriException {
        new Storage().saveTasks(List.of(new Todo("pack éclairs — and 雪")));

        assertEquals("[T][ ] pack éclairs — and 雪",
                new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void saveTasks_missingTaskList_throwsNoriException() {
        Storage storage = new Storage();

        NoriException exception = assertThrows(NoriException.class, () -> storage.saveTasks(null));

        assertEquals("SAD NOOT! I couldn't save a task list that isn't there.", exception.getMessage());
    }

    @Test
    public void saveTasks_listHoldingNoTask_throwsNoriException() {
        Storage storage = new Storage();
        List<Task> tasksWithAGap = new ArrayList<>();
        tasksWithAGap.add(null);

        NoriException exception = assertThrows(
                NoriException.class, () -> storage.saveTasks(tasksWithAGap));

        assertEquals("SAD NOOT! I couldn't save a task that isn't there.", exception.getMessage());
    }

    @Test
    public void saveTasks_anySave_writesABackupBesideTheStorageFile() throws NoriException {
        new Storage().saveTasks(List.of(new Todo("read book")));

        assertTrue(Files.isRegularFile(storageDirectory.resolve(STORAGE_FILE)));
        assertTrue(Files.isRegularFile(storageDirectory.resolve(BACKUP_FILE)));
    }

    @Test
    public void saveTasks_repeatedSaves_leaveNoTemporaryFilesBehind() throws Exception {
        Storage storage = new Storage();
        storage.saveTasks(List.of(new Todo("first")));
        storage.saveTasks(List.of(new Todo("second")));

        try (Stream<Path> files = Files.list(storageDirectory)) {
            assertFalse(files.anyMatch(file -> file.getFileName().toString().endsWith(".tmp")));
        }
    }

    @Test
    public void loadTasks_unreadableLine_recoversFromTheBackup() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        writeStorageFile("this is not a task");

        Storage storage = new Storage();
        List<Task> restoredTasks = storage.loadTasks();

        assertEquals("[T][ ] keep this task", restoredTasks.get(0).toString());
        assertTrue(storage.getLoadingNotice().contains("restored the backup"));
    }

    @Test
    public void loadTasks_unreadableLine_keepsTheDamagedFile() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        writeStorageFile("this is not a task");

        new Storage().loadTasks();

        assertEquals("this is not a task",
                Files.readString(storageDirectory.resolve(CORRUPT_FILE), StandardCharsets.UTF_8)
                        .strip());
    }

    @Test
    public void loadTasks_unknownStatus_recoversFromTheBackup() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        writeStorageFile(storedLine("T", "2", "a task"));

        assertEquals("[T][ ] keep this task", new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_wrongFieldCountForItsType_recoversFromTheBackup() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        writeStorageFile(storedLine("D", "0", "a deadline"));

        assertEquals("[T][ ] keep this task", new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_emptyDescription_recoversFromTheBackup() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        writeStorageFile(storedLine("T", "0", ""));

        assertEquals("[T][ ] keep this task", new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_impossibleDeadlineDate_recoversFromTheBackup() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        writeStorageFile(storedLine("D", "0", "a deadline", "2019-02-31"));

        assertEquals("[T][ ] keep this task", new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_malformedEncodedField_recoversFromTheBackup() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        writeStorageFile("T | 0 | b64:!!!not-base64!!!");

        assertEquals("[T][ ] keep this task", new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_malformedUtf8InAnEncodedField_recoversFromTheBackup() throws Exception {
        new Storage().saveTasks(List.of(new Todo("keep this task")));
        byte[] loneContinuationByte = {(byte) 0x80};
        writeStorageFile("T | 0 | b64:" + Base64.getEncoder().encodeToString(loneContinuationByte));

        assertEquals("[T][ ] keep this task", new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_plainTextFieldFromTheOlderFormat_isStillRead() throws Exception {
        writeStorageFile("T | 0 | read book");

        assertEquals("[T][ ] read book", new Storage().loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_damagedFileAndNoBackup_throwsNoriException() throws Exception {
        writeStorageFile("this is not a task");

        NoriException exception = assertThrows(NoriException.class, () -> new Storage().loadTasks());

        assertEquals("SAD NOOT! Your saved tasks in data/nori.txt are corrupted"
                + " and I have no backup to waddle back to."
                + " Repair that file or move it out of the data folder, then open me again.",
                exception.getMessage());
    }

    @Test
    public void loadTasks_damagedFileAndDamagedBackup_throwsNoriException() throws Exception {
        writeStorageFile("this is not a task");
        Files.writeString(storageDirectory.resolve(BACKUP_FILE), "nor is this",
                StandardCharsets.UTF_8);

        NoriException exception = assertThrows(NoriException.class, () -> new Storage().loadTasks());

        assertEquals("SAD NOOT! Your saved tasks in data/nori.txt are corrupted"
                + " and the backup wouldn't come back either."
                + " Repair that file or move it out of the data folder, then open me again.",
                exception.getMessage());
    }

    @Test
    public void saveTasks_afterAnUnrecoverableLoad_refusesToOverwriteTheFile() throws Exception {
        writeStorageFile("this is not a task");
        Storage storage = new Storage();
        assertThrows(NoriException.class, storage::loadTasks);

        NoriException exception = assertThrows(
                NoriException.class, () -> storage.saveTasks(List.of(new Todo("replacement"))));

        assertEquals("SAD NOOT! I won't save over data/nori.txt while I can't read the tasks in it."
                + " Repair that file or move it out of the data folder, then open me again.",
                exception.getMessage());
        assertEquals("this is not a task",
                Files.readString(storageDirectory.resolve(STORAGE_FILE), StandardCharsets.UTF_8)
                        .strip());
    }

    @Test
    public void saveTasks_missingDirectory_createsIt() throws Exception {
        Path missingDirectory = storageDirectory.resolve("not-yet-there");
        System.setProperty(STORAGE_DIRECTORY_PROPERTY, missingDirectory.toString());

        new Storage().saveTasks(List.of(new Todo("read book")));

        assertTrue(Files.isRegularFile(missingDirectory.resolve(STORAGE_FILE)));
    }

    /**
     * Writes the storage file, replacing whatever it held.
     *
     * @param contents the exact text to write.
     * @throws IOException if the file cannot be written.
     */
    private void writeStorageFile(String contents) throws IOException {
        Files.writeString(storageDirectory.resolve(STORAGE_FILE), contents, StandardCharsets.UTF_8);
    }

    /**
     * Builds one line of the storage file, encoding each field as a save would.
     *
     * @param type the stored type code.
     * @param status the stored completion status.
     * @param fields the remaining fields, in order.
     * @return the storage line.
     */
    private static String storedLine(String type, String status, String... fields) {
        StringBuilder line = new StringBuilder(type).append(" | ").append(status);
        for (String field : fields) {
            line.append(" | b64:")
                    .append(Base64.getEncoder().encodeToString(field.getBytes(StandardCharsets.UTF_8)));
        }
        return line.toString();
    }
}
