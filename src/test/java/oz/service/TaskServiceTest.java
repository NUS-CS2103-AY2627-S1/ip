package oz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import oz.exception.OzException;
import oz.storage.Storage;
import oz.task.RecurringEvent;
import oz.task.Task;
import oz.task.TaskDateTime;
import oz.task.ToDo;

/**
 * Tests persisted task mutations and transition validation in {@link TaskService}.
 */
public class TaskServiceTest {
    /** Temporary directory isolating each test's storage file. */
    @TempDir
    Path temporaryFolder;

    /** Verifies added and deleted tasks are immediately persisted. */
    @Test
    public void addAndDelete_validTask_persistsEachMutation() throws OzException {
        Path storageFile = this.temporaryFolder.resolve("tasks.txt");
        TaskService service = createService(storageFile);

        Task addedTask = service.add(new ToDo("read book"));

        assertEquals("[T][ ] read book", addedTask.toString());
        assertEquals(1, createService(storageFile).size());

        Task deletedTask = service.delete(0);

        assertEquals(addedTask, deletedTask);
        assertEquals(0, createService(storageFile).size());
    }

    /** Verifies ordinary task completion changes survive a storage round trip. */
    @Test
    public void markAndUnmark_ordinaryTask_persistsEachState() throws OzException {
        Path storageFile = this.temporaryFolder.resolve("tasks.txt");
        TaskService service = createService(storageFile);
        service.add(new ToDo("read book"));

        service.markTask(0, null);

        assertTrue(createService(storageFile).getTasks().get(0).isDone());

        service.unmarkTask(0, null);

        assertFalse(createService(storageFile).getTasks().get(0).isDone());
    }

    /** Verifies occurrence dates are accepted only for recurring tasks. */
    @Test
    public void markTask_ordinaryTaskWithOccurrenceDate_throwsException() throws OzException {
        TaskService service = createService(this.temporaryFolder.resolve("tasks.txt"));
        service.add(new ToDo("read book"));

        OzException exception = assertThrows(OzException.class, () ->
                service.markTask(0, LocalDate.of(2026, 9, 17)));

        assertEquals("The /on argument can only be used with recurring tasks.", exception.getMessage());
    }

    /** Verifies recurring tasks require a specific occurrence date. */
    @Test
    public void markTask_recurringTaskWithoutOccurrenceDate_throwsException() throws OzException {
        TaskService service = createService(this.temporaryFolder.resolve("tasks.txt"));
        service.add(new RecurringEvent("team meeting",
                TaskDateTime.parse("2026-09-17 1400"),
                TaskDateTime.parse("2026-09-17 1500"), 1, null));

        OzException exception = assertThrows(OzException.class, () ->
                service.markTask(0, null));

        assertEquals("Please specify which occurrence to mark. "
                + "Use: mark <number> /on <date>.", exception.getMessage());
    }

    /** Verifies callers cannot alter the service collection without persistence. */
    @Test
    public void getTasks_returnedSnapshot_cannotBeModified() throws OzException {
        TaskService service = createService(this.temporaryFolder.resolve("tasks.txt"));
        service.add(new ToDo("read book"));
        List<Task> tasks = service.getTasks();

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.add(new ToDo("write report")));
        assertEquals(1, service.size());
    }

    /**
     * Creates a service backed by the supplied storage file.
     *
     * @param storageFile Storage file path.
     * @return Task service loaded from the storage file.
     */
    private static TaskService createService(Path storageFile) {
        return new TaskService(new Storage(storageFile.toString()));
    }
}
