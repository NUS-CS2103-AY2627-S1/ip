package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests that shared task operations coordinate parsing, mutation, and persistence. */
class TaskServiceTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void taskOperations_updateAndPersistSharedTaskState() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskService service = new TaskService(storage, new Parser());

        Task added = service.add("todo read book");
        assertEquals("read book", added.getDescription());

        service.mark("mark 1");
        assertTrue(service.getTasks().get(0).isDone());

        service.unmark("unmark 1");
        assertFalse(service.getTasks().get(0).isDone());

        service.delete("delete 1");
        assertTrue(service.getTasks().isEmpty());
        assertTrue(new TaskService(storage, new Parser()).getTasks().isEmpty());
    }

    @Test
    void typedOperations_keepCommandParsingOutOfTheApplicationService() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("typed-tasks.txt"));
        TaskService service = new TaskService(storage, new Parser());
        Task task = new Deadline("submit report", java.time.LocalDate.of(2026, 9, 20));

        service.add(task);
        service.mark(1);

        assertTrue(service.getTasks().get(0).isDone());
        assertEquals("submit report", service.getTasks().get(0).getDescription());
    }

    @Test
    void sortByDeadline_ordersAndPersistsTaskOrder() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskService service = new TaskService(storage, new Parser());
        Task later = service.add("deadline later /by 2026-10-20");
        Task todo = service.add("todo no deadline");
        Task earlier = service.add("deadline earlier /by 2026-09-20");

        service.sortByDeadline();

        assertEquals(List.of("earlier", "later", "no deadline"),
                service.getTasks().stream().map(Task::getDescription).toList());
        assertEquals(List.of("earlier", "later", "no deadline"),
                new TaskService(storage, new Parser()).getTasks().stream()
                        .map(Task::getDescription).toList());
    }

    @Test
    void taskOperations_usingTextFile_persistChangesAcrossRestarts() throws Exception {
        Path filePath = temporaryDirectory.resolve("wangsa.txt");
        TaskService service = new TaskService(new Storage(filePath), new Parser());

        service.add("todo first");
        service.add("todo second");
        service.mark("mark 1");
        service.delete("delete 2");

        TaskService reloaded = new TaskService(new Storage(filePath), new Parser());
        assertEquals(List.of("first"), reloaded.getTasks().stream().map(Task::getDescription).toList());
        assertTrue(reloaded.getTasks().get(0).isDone());
    }

    @Test
    void statusChange_restoresInMemoryStateWhenPersistenceFails() throws Exception {
        Task task = new Todo("recoverable");
        FailingRepository repository = new FailingRepository(List.of(task));
        TaskService service = new TaskService(repository, new Parser());

        assertThrows(StorageException.class, () -> service.mark("mark 1"));
        assertFalse(service.getTasks().get(0).isDone());

        task.markAsDone();
        assertThrows(StorageException.class, () -> service.unmark("unmark 1"));
        assertTrue(service.getTasks().get(0).isDone());
    }

    @Test
    void sort_restoresOriginalOrderWhenPersistenceFails() throws Exception {
        List<Task> initialTasks = List.of(
                new Todo("undated"), new Deadline("later", java.time.LocalDate.of(2026, 10, 20)),
                new Deadline("earlier", java.time.LocalDate.of(2026, 9, 20)));
        TaskService service = new TaskService(new FailingRepository(initialTasks), new Parser());

        assertThrows(StorageException.class, service::sortByDeadline);
        assertEquals(List.of("undated", "later", "earlier"), service.getTasks().stream()
                .map(Task::getDescription).toList());
    }

    @Test
    void addAndDelete_failedPersistence_leaveExistingTasksUnchanged() throws Exception {
        Task original = new Todo("original");
        TaskService service = new TaskService(new FailingRepository(List.of(original)), new Parser());

        assertThrows(StorageException.class, () -> service.add("todo cannot save"));
        assertEquals(List.of(original), service.getTasks());
        assertThrows(StorageException.class, () -> service.delete(1));
        assertEquals(List.of(original), service.getTasks());
    }

    /** Repository test double that fails every write while retaining an initial snapshot. */
    private static final class FailingRepository implements TaskRepository {
        private final List<Task> initialTasks;

        private FailingRepository(List<Task> initialTasks) {
            this.initialTasks = new ArrayList<>(initialTasks);
        }

        @Override
        public List<Task> loadTasks() {
            return List.copyOf(initialTasks);
        }

        @Override
        public void saveTasks(List<Task> tasks) throws StorageException {
            throw failure();
        }

        private StorageException failure() {
            return new StorageException("simulated persistence failure");
        }
    }
}
