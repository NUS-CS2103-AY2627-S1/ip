package herta.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.exception.HertaException;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.Task;
import herta.task.TaskList;
import herta.task.Todo;
import herta.ui.Ui;
import herta.ui.UiOutput;

/**
 * Tests command adapters that operate on separate active and archive storage.
 */
class RepositoryAdapterCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void repositoryAdapter_addChangesActiveFileOnly() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        TaskList activeTasks = new TaskList();
        Todo archivedTask = new Todo("archived task");
        TaskRepository repository = createRepositoryWithArchive(activeFile, archiveFile,
                activeTasks, archivedTask);

        new TodoCommand(new Todo("active task")).execute(repository, new Ui());

        assertEquals(List.of("T | 0 | active task"), Files.readAllLines(activeFile));
        assertEquals(List.of("T | 0 | archived task"), Files.readAllLines(archiveFile));
    }

    @Test
    void repositoryAdapter_markChangesActiveFileOnlyAndReportsInOrder() throws Exception {
        Path activeFile = temporaryDirectory.resolve("mark-repository.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Todo activeTask = new Todo("active task");
        Todo archivedTask = new Todo("archived task");
        TaskRepository repository = createRepositoryWithArchive(activeFile, archiveFile,
                new TaskList(List.of(activeTask)), archivedTask);
        byte[] originalArchiveBytes = Files.readAllBytes(archiveFile);
        RecordingUiOutput output = new RecordingUiOutput();

        new MarkCommand(0).execute(repository, output);

        assertTrue(activeTask.isCompleted());
        assertEquals(List.of("Done. It's marked complete:", "  [T][X] active task"),
                output.getMessages());
        assertEquals(List.of("T | 1 | active task"), Files.readAllLines(activeFile));
        assertArrayEquals(originalArchiveBytes, Files.readAllBytes(archiveFile));
    }

    @Test
    void repositoryAdapter_unmarkChangesActiveFileOnlyAndReportsInOrder() throws Exception {
        Path activeFile = temporaryDirectory.resolve("unmark-repository.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Todo activeTask = new Todo("active task");
        activeTask.markAsDone();
        TaskRepository repository = createRepositoryWithArchive(activeFile, archiveFile,
                new TaskList(List.of(activeTask)), new Todo("archived task"));
        byte[] originalArchiveBytes = Files.readAllBytes(archiveFile);
        RecordingUiOutput output = new RecordingUiOutput();

        new UnmarkCommand(0).execute(repository, output);

        assertFalse(activeTask.isCompleted());
        assertEquals(List.of("Fine. It's incomplete again:", "  [T][ ] active task"),
                output.getMessages());
        assertEquals(List.of("T | 0 | active task"), Files.readAllLines(activeFile));
        assertArrayEquals(originalArchiveBytes, Files.readAllBytes(archiveFile));
    }

    @Test
    void repositoryAdapter_repeatedStatusDoesNotWriteOrShowTask() throws Exception {
        Path activeFile = temporaryDirectory.resolve("repeated-status-repository.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Todo completedTask = new Todo("completed task");
        completedTask.markAsDone();
        Todo incompleteTask = new Todo("incomplete task");
        TaskList activeTasks = new TaskList(List.of(completedTask, incompleteTask));
        TaskList archivedTasks = new TaskList(List.of(new Todo("archived task")));
        Storage activeStorage = new Storage(activeFile.toString());
        Storage archiveStorage = new Storage(archiveFile.toString());
        activeStorage.save(activeTasks);
        archiveStorage.save(archivedTasks);
        TaskRepository repository = new TaskRepository(activeStorage, archiveStorage,
                activeTasks, archivedTasks);
        byte[] originalActiveBytes = Files.readAllBytes(activeFile);
        byte[] originalArchiveBytes = Files.readAllBytes(archiveFile);
        RecordingUiOutput output = new RecordingUiOutput();

        new MarkCommand(0).execute(repository, output);
        new UnmarkCommand(1).execute(repository, output);

        assertEquals(List.of("Already complete. There is nothing more to do.",
                "Already incomplete. There is nothing to undo."), output.getMessages());
        assertTrue(completedTask.isCompleted());
        assertFalse(incompleteTask.isCompleted());
        assertArrayEquals(originalActiveBytes, Files.readAllBytes(activeFile));
        assertArrayEquals(originalArchiveBytes, Files.readAllBytes(archiveFile));
    }

    @Test
    void repositoryAdapter_deleteChangesActiveFileOnlyAndReportsInOrder() throws Exception {
        Path activeFile = temporaryDirectory.resolve("delete-repository.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        TaskRepository repository = createRepositoryWithArchive(activeFile, archiveFile,
                new TaskList(List.of(new Todo("active task"))), new Todo("archived task"));
        byte[] originalArchiveBytes = Files.readAllBytes(archiveFile);
        RecordingUiOutput output = new RecordingUiOutput();

        new DeleteCommand(0).execute(repository, output);

        assertEquals(0, repository.getActiveTasks().size());
        assertEquals(List.of("Gone. I've removed it:", "  [T][ ] active task",
                "That makes 0 active tasks. Try to keep up."), output.getMessages());
        assertEquals(List.of(), Files.readAllLines(activeFile));
        assertArrayEquals(originalArchiveBytes, Files.readAllBytes(archiveFile));
    }

    @Test
    void statusCommands_saveFailureRestoresStatusAndSuppressesSuccessOutput() {
        Storage failingStorage = new SaveFailingStorage(
                temporaryDirectory.resolve("status-failure.txt"));
        Todo incompleteTask = new Todo("incomplete task");
        TaskList incompleteTasks = new TaskList(List.of(incompleteTask));
        RecordingUiOutput markOutput = new RecordingUiOutput();

        assertThrows(IllegalStateException.class, () ->
                new MarkCommand(0).execute(incompleteTasks, markOutput, failingStorage));
        assertFalse(incompleteTask.isCompleted());
        assertEquals(List.of(), markOutput.getMessages());

        Todo completedTask = new Todo("completed task");
        completedTask.markAsDone();
        TaskList completedTasks = new TaskList(List.of(completedTask));
        RecordingUiOutput unmarkOutput = new RecordingUiOutput();

        assertThrows(IllegalStateException.class, () ->
                new UnmarkCommand(0).execute(completedTasks, unmarkOutput, failingStorage));
        assertTrue(completedTask.isCompleted());
        assertEquals(List.of(), unmarkOutput.getMessages());
    }

    private TaskRepository createRepositoryWithArchive(Path activeFile, Path archiveFile,
                                                       TaskList activeTasks, Task archivedTask)
            throws HertaException {
        Storage archiveStorage = new Storage(archiveFile.toString());
        TaskList archivedTasks = new TaskList(List.of(archivedTask));
        archiveStorage.save(archivedTasks);
        return new TaskRepository(new Storage(activeFile.toString()), archiveStorage,
                activeTasks, archivedTasks);
    }

    /** Forces save failures when testing command adapter rollback behavior. */
    private static final class SaveFailingStorage extends Storage {
        SaveFailingStorage(Path dataFile) {
            super(dataFile.toString());
        }

        @Override
        public void save(TaskList tasks) {
            throw new IllegalStateException("test save failure");
        }
    }

    /** Captures UI messages emitted by command adapter tests. */
    private static final class RecordingUiOutput implements UiOutput {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void showMessage(String message) {
            messages.add(message);
        }

        @Override
        public void showGoodbye() {
            messages.add(UiOutput.GOODBYE_MESSAGE);
        }

        List<String> getMessages() {
            return messages;
        }
    }
}
