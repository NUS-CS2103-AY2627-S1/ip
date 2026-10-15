package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies persistence of task data, including missing and corrupted files.
 */
class TaskStorageTest {
    @TempDir
    private Path temporaryDirectory;

    /** Empty and whitespace-only files represent a healthy empty list that can still be saved. */
    @Test
    void loadBlankLinesAllowsSubsequentSave() throws IOException {
        Path file = temporaryDirectory.resolve("blank.txt");
        Files.writeString(file, "\n \r\n\t\n");
        TaskStorage storage = new TaskStorage(file);

        assertTrue(storage.load().isEmpty());
        assertEquals("", storage.getLoadWarning());
        assertTrue(storage.save(List.of(new Todo("first"))));
        assertEquals("first", storage.load().get(0).getDescription());
        assertTrue(storage.save(List.of()));
        assertEquals(0, Files.size(file));
        assertTrue(new TaskStorage(file).load().isEmpty());
    }

    /** An unresolved path must fail safely even if save is called before load. */
    @Test
    void saveUnresolvedPathReturnsFalse() {
        TaskStorage storage = new TaskStorage((Path) null);

        assertFalse(storage.save(List.of(new Todo("never saved"))));
        assertTrue(storage.load().isEmpty());
        assertEquals(Ui.LOAD_FAILURE, storage.getLoadWarning());
    }

    /**
     * Saving and loading should preserve task types, details, dates, times, and status.
     */
    @Test
    void saveAndLoadMixedTasksPreservesTaskData() {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        TaskStorage storage = new TaskStorage(dataFile);

        assertTrue(storage.save(createMixedTasks()));

        assertMixedTaskData(storage.load());
        assertTrue(Files.exists(dataFile));
    }

    /** Creates one task of each type, including escaped text and a completed deadline.
     *
     * @return the ordered tasks used to exercise storage round-tripping.
     */
    private List<Task> createMixedTasks() {
        Todo todo = new Todo("read | book\\");
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 6, 6),
                LocalTime.of(18, 0));
        deadline.markAsDone();
        Event event = new Event("project meeting", LocalDate.of(2019, 8, 6),
                LocalTime.of(14, 0), LocalDate.of(2019, 8, 6), LocalTime.of(16, 0));

        return List.of(todo, deadline, event);
    }

    /** Checks the order and details of the mixed-task fixture after reloading.
     *
     * @param loadedTasks the tasks restored from storage.
     */
    private void assertMixedTaskData(List<Task> loadedTasks) {
        assertEquals(3, loadedTasks.size());
        assertLoadedTodo(loadedTasks.get(0));
        assertLoadedDeadline(loadedTasks.get(1));
        assertLoadedEvent(loadedTasks.get(2));
    }

    /** Checks that escaped text, type, and incomplete status survive reloading.
     *
     * @param task the restored to-do task.
     */
    private void assertLoadedTodo(Task task) {
        assertTrue(task instanceof Todo);
        assertEquals("read | book\\", task.getDescription());
        assertFalse(task.isDone());
    }

    /** Checks that a deadline retains its description, due date, time, and completed status.
     *
     * @param task the restored deadline.
     */
    private void assertLoadedDeadline(Task task) {
        assertTrue(task instanceof Deadline);
        Deadline loadedDeadline = (Deadline) task;
        assertEquals("return book", loadedDeadline.getDescription());
        assertEquals(LocalDate.of(2019, 6, 6), loadedDeadline.getBy());
        assertEquals(LocalTime.of(18, 0), loadedDeadline.getByTime());
        assertTrue(loadedDeadline.isDone());
    }

    /** Checks that an event retains its description, schedule, and incomplete status.
     *
     * @param task the restored event.
     */
    private void assertLoadedEvent(Task task) {
        assertTrue(task instanceof Event);
        Event loadedEvent = (Event) task;
        assertEquals("project meeting", loadedEvent.getDescription());
        assertEquals(LocalDate.of(2019, 8, 6), loadedEvent.getFrom());
        assertEquals(LocalTime.of(14, 0), loadedEvent.getFromTime());
        assertEquals(LocalDate.of(2019, 8, 6), loadedEvent.getTo());
        assertEquals(LocalTime.of(16, 0), loadedEvent.getToTime());
        assertFalse(loadedEvent.isDone());
    }

    /**
     * Loading a missing file should return no tasks and create its parent directory.
     */
    @Test
    void loadMissingFileReturnsEmptyListAndCreatesParentDirectory() {
        Path dataFile = temporaryDirectory.resolve("new-folder").resolve("tasks.txt");
        TaskStorage storage = new TaskStorage(dataFile);

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
        assertTrue(Files.isDirectory(dataFile.getParent()));
        assertFalse(Files.exists(dataFile));
    }

    /**
     * Invalid records should be skipped while valid records in the same file are loaded.
     */
    @Test
    void loadCorruptedRecordsIgnoresInvalidLines() throws IOException {
        Path dataFile = temporaryDirectory.resolve("corrupted.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "T | 1 | valid task",
                "not a valid record",
                "D | 0 | invalid date | not-a-date",
                "D | 0 | return book | 2019-06-06"));

        List<Task> loadedTasks = new TaskStorage(dataFile).load();

        assertEquals(2, loadedTasks.size());
        assertEquals("valid task", loadedTasks.get(0).getDescription());
        assertTrue(loadedTasks.get(0).isDone());
        assertNotNull(loadedTasks.get(1));
        assertEquals("return book", loadedTasks.get(1).getDescription());
    }
}
