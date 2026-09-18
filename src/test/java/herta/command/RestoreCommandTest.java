package herta.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.Herta;
import herta.HertaResponse;
import herta.ResponseCategory;
import herta.exception.HertaException;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.TaskList;
import herta.task.Todo;
import herta.ui.Ui;

/**
 * Tests viewing and restoring archived tasks, including capacity and failures.
 */
class RestoreCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void archivedAndRestoreCommands_useIndependentArchiveStateAndPreserveStatus() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        new Storage(activeFile.toString()).save(new TaskList());
        Todo completed = new Todo("completed");
        completed.markAsDone();
        Todo incomplete = new Todo("incomplete");
        new Storage(archiveFile.toString()).save(new TaskList(List.of(completed, incomplete)));

        Herta herta = new Herta(activeFile.toString());
        HertaResponse viewResponse = herta.getResponse("archived");
        HertaResponse restoreResponse = herta.getResponse("restore 2");

        assertEquals(ResponseCategory.QUERY, viewResponse.getResponseCategory());
        assertEquals("The archive, as requested:\n"
                + "1. [T][X] completed\n"
                + "2. [T][ ] incomplete", viewResponse.getMessage()
                .replace(System.lineSeparator(), "\n"));
        assertEquals(ResponseCategory.RESTORE, restoreResponse.getResponseCategory());
        assertEquals("There. I've restored it:\n"
                + "  [T][ ] incomplete\n"
                + "That makes 1 active task. Back where it belongs.", restoreResponse.getMessage()
                .replace(System.lineSeparator(), "\n"));
        assertEquals(List.of("T | 0 | incomplete"), Files.readAllLines(activeFile));
        assertEquals(List.of("T | 1 | completed"), Files.readAllLines(archiveFile));

        HertaResponse markResponse = herta.getResponse("mark 1");
        assertEquals(ResponseCategory.MARK, markResponse.getResponseCategory());
        assertEquals("T | 1 | incomplete", Files.readString(activeFile).trim());
    }

    @Test
    void restoreCommand_preservesTypedTaskDataAndCompletionStatus() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        new Storage(activeFile.toString()).save(new TaskList());
        Deadline deadline = new Deadline("deadline", LocalDateTime.of(2019, 10, 15, 18, 0));
        deadline.markAsDone();
        Event event = new Event("event", LocalDateTime.of(2019, 10, 16, 10, 0),
                LocalDateTime.of(2019, 10, 16, 11, 0));
        new Storage(archiveFile.toString()).save(new TaskList(List.of(deadline, event)));

        Herta herta = new Herta(activeFile.toString());
        herta.getResponse("restore 1");
        herta.getResponse("restore 1");

        assertEquals(List.of(deadline.toStorageString(), event.toStorageString()),
                Files.readAllLines(activeFile));
        assertTrue(herta.getResponse("list").getMessage().contains("[D][X] deadline"));
        assertTrue(herta.getResponse("list").getMessage().contains("[E][ ] event"));
    }

    @Test
    void restoreCommand_firstMiddleLast_preservesAppendOrderAndCounts() throws Exception {
        Path activeFile = temporaryDirectory.resolve("restore-order").resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        new Storage(archiveFile.toString()).save(new TaskList(List.of(
                new Todo("first"), new Todo("middle"), new Todo("last"))));
        Herta herta = new Herta(activeFile.toString());

        HertaResponse middleResponse = herta.getResponse("restore 2");
        HertaResponse firstResponse = herta.getResponse("restore 1");
        HertaResponse lastResponse = herta.getResponse("restore 1");

        assertTrue(middleResponse.getMessage().contains("That makes 1 active task."));
        assertTrue(firstResponse.getMessage().contains("That makes 2 active tasks."));
        assertTrue(lastResponse.getMessage().contains("That makes 3 active tasks."));
        assertEquals(List.of("T | 0 | middle", "T | 0 | first", "T | 0 | last"),
                Files.readAllLines(activeFile));
        assertEquals(List.of(), Files.readAllLines(archiveFile));
    }

    @Test
    void restoreCommand_invalidIndex_leavesArchiveUnchanged() throws Exception {
        Path activeFile = temporaryDirectory.resolve("restore-invalid").resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        new Storage(archiveFile.toString()).save(new TaskList(List.of(new Todo("archived"))));
        Herta herta = new Herta(activeFile.toString());

        HertaResponse response = herta.getResponse("restore 2");

        assertEquals(ResponseCategory.ERROR, response.getResponseCategory());
        assertEquals("That number points to nothing in the archive. Check again.",
                response.getMessage());
        assertEquals(List.of("T | 0 | archived"), Files.readAllLines(archiveFile));
    }

    @Test
    void restoreCommand_atCapacity_leavesBothCollectionsAndFilesUnchanged() throws Exception {
        Path activeFile = temporaryDirectory.resolve("restore-capacity").resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Todo activeTask = new Todo("active");
        Todo archivedTask = new Todo("archived");
        TaskList activeTasks = new TaskList(Collections.nCopies(TaskList.MAXIMUM_TASK_COUNT,
                activeTask));
        TaskList archivedTasks = new TaskList(List.of(archivedTask));
        Storage activeStorage = new Storage(activeFile.toString());
        Storage archiveStorage = new Storage(archiveFile.toString());
        archiveStorage.save(archivedTasks);
        TaskRepository repository = new TaskRepository(activeStorage, archiveStorage,
                activeTasks, archivedTasks);

        assertThrows(IllegalArgumentException.class, () ->
                new RestoreCommand(0).execute(repository, new Ui()));

        assertEquals(TaskList.MAXIMUM_TASK_COUNT, activeTasks.size());
        assertEquals(1, archivedTasks.size());
        assertTrue(Files.notExists(activeFile));
        assertEquals(List.of("T | 0 | archived"), Files.readAllLines(archiveFile));
    }

    @Test
    void restoreCommand_persistenceFailureLeavesBothCollectionsUnchanged() throws Exception {
        Path activeDirectory = temporaryDirectory.resolve("active-directory");
        Path archiveFile = temporaryDirectory.resolve("archive.txt");
        Files.createDirectory(activeDirectory);
        Todo archivedTask = new Todo("archived");
        new Storage(archiveFile.toString()).save(new TaskList(List.of(archivedTask)));
        TaskList activeTasks = new TaskList();
        TaskList archivedTasks = new TaskList(List.of(archivedTask));
        TaskRepository repository = new TaskRepository(new Storage(activeDirectory.toString()),
                new Storage(archiveFile.toString()), activeTasks, archivedTasks);

        RestoreCommand restoreCommand = new RestoreCommand(0);
        HertaException exception = assertThrows(HertaException.class, () ->
                restoreCommand.execute(repository, new Ui()));

        assertTrue(exception.getMessage().startsWith("Failed to restore task: "));
        assertEquals(0, activeTasks.size());
        assertEquals(1, archivedTasks.size());
        assertEquals(List.of("T | 0 | archived"), Files.readAllLines(archiveFile));
    }
}
