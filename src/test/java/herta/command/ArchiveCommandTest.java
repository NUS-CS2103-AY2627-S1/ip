package herta.command;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import herta.Herta;
import herta.HertaResponse;
import herta.ResponseCategory;
import herta.exception.HertaException;
import herta.parser.ArchiveRange;
import herta.parser.ArchiveSelection;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.TaskList;
import herta.task.Todo;
import herta.ui.Ui;

/**
 * Tests archiving completed tasks, selectors, capacity, and persistence failures.
 */
class ArchiveCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void archiveCommand_archiveCompletedTasks_preservesActiveOrderAndPersistsBothLists()
            throws Exception {
        Path activeFile = temporaryDirectory.resolve("data").resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());
        addCompletedTasks(herta, 3);

        HertaResponse response = herta.getResponse("archive 3 1-2");

        assertEquals(ResponseCategory.ARCHIVE, response.getResponseCategory());
        assertEquals("There. I've archived 3 completed tasks:\n"
                + "  [T][X] first\n"
                + "  [T][X] second\n"
                + "  [T][X] third\n"
                + "The active list is down to 0 tasks. Much tidier.",
                response.getMessage().replace(System.lineSeparator(), "\n"));
        assertEquals(List.of(), Files.readAllLines(activeFile));
        assertEquals(List.of("T | 1 | first", "T | 1 | second", "T | 1 | third"),
                Files.readAllLines(activeFile.resolveSibling("archive.txt")));
    }

    @Test
    void archiveCommand_overlappingSelectors_archiveEachTaskOnceInOrder() throws Exception {
        Path activeFile = temporaryDirectory.resolve("overlapping").resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());
        addCompletedTasks(herta, 4);

        HertaResponse response = herta.getResponse("archive 1-3 2 3-4");

        assertEquals(ResponseCategory.ARCHIVE, response.getResponseCategory());
        assertTrue(response.getMessage().contains("archived 4 completed tasks"));
        assertEquals(List.of("T | 1 | first", "T | 1 | second", "T | 1 | third",
                "T | 1 | fourth"), Files.readAllLines(activeFile.resolveSibling("archive.txt")));
        assertEquals(List.of(), Files.readAllLines(activeFile));
    }

    @Test
    void archiveCommand_incompleteSelection_rejectsAtomically() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());
        herta.getResponse("todo complete");
        herta.getResponse("todo incomplete");
        herta.getResponse("mark 1");

        HertaResponse response = herta.getResponse("archive 1 2");

        assertEquals(ResponseCategory.ERROR, response.getResponseCategory());
        assertEquals("That task is still unfinished. Complete it before archiving.",
                response.getMessage());
        assertEquals(List.of("T | 1 | complete", "T | 0 | incomplete"),
                Files.readAllLines(activeFile));
        assertTrue(Files.notExists(activeFile.resolveSibling("archive.txt")));
    }

    @Test
    void duplicateTasks_areAllowedInEveryList() throws Exception {
        Path activeFile = temporaryDirectory.resolve("global-duplicate").resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());

        herta.getResponse("todo read book");
        HertaResponse duplicateActiveResponse = herta.getResponse("todo read book");
        herta.getResponse("mark 1");
        herta.getResponse("archive 1");
        herta.getResponse("mark 1");
        HertaResponse duplicateArchiveResponse = herta.getResponse("archive 1");
        HertaResponse firstRestoreResponse = herta.getResponse("restore 1");
        HertaResponse duplicateRestoreResponse = herta.getResponse("restore 1");

        assertEquals(ResponseCategory.ADD, duplicateActiveResponse.getResponseCategory());
        assertEquals(ResponseCategory.ARCHIVE, duplicateArchiveResponse.getResponseCategory());
        assertEquals(ResponseCategory.RESTORE, firstRestoreResponse.getResponseCategory());
        assertEquals(ResponseCategory.RESTORE, duplicateRestoreResponse.getResponseCategory());
        assertEquals(List.of("T | 1 | read book", "T | 1 | read book"),
                Files.readAllLines(activeFile));
        assertEquals(List.of(), Files.readAllLines(activeFile.resolveSibling("archive.txt")));
    }

    @Test
    void archiveAll_skipsIncompleteTasksAndReportsNoOpCases() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());
        herta.getResponse("todo complete");
        herta.getResponse("todo incomplete");
        herta.getResponse("mark 1");

        HertaResponse archiveResponse = herta.getResponse("archive all");
        assertEquals(ResponseCategory.ARCHIVE, archiveResponse.getResponseCategory());
        assertTrue(archiveResponse.getMessage().contains("There. I've archived 1 completed task:"));
        assertEquals(List.of("T | 0 | incomplete"), Files.readAllLines(activeFile));

        HertaResponse noOpResponse = herta.getResponse("archive all");
        assertEquals(ResponseCategory.ARCHIVE, noOpResponse.getResponseCategory());
        assertEquals("Nothing is ready for archiving. Complete a task first.",
                noOpResponse.getMessage());
        assertEquals(List.of("T | 1 | complete"),
                Files.readAllLines(activeFile.resolveSibling("archive.txt")));
    }

    @Test
    void archiveAll_emptyActiveList_reportsNoTasks() throws Exception {
        Path activeFile = temporaryDirectory.resolve("empty-archive").resolve("tasks.txt");
        HertaResponse response = new Herta(activeFile.toString()).getResponse("archive all");

        assertEquals(ResponseCategory.ARCHIVE, response.getResponseCategory());
        assertEquals("No active tasks. There is nothing here to archive.", response.getMessage());
        assertTrue(Files.notExists(activeFile));
    }

    @Test
    void archiveAll_onlyCompletedTasks_movesAllTasksAndReportsPluralCount() throws Exception {
        Path activeFile = temporaryDirectory.resolve("all-completed").resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());
        addCompletedTasks(herta, 2);

        HertaResponse response = herta.getResponse("archive all");

        assertEquals(ResponseCategory.ARCHIVE, response.getResponseCategory());
        assertTrue(response.getMessage().contains("archived 2 completed tasks"));
        assertEquals(List.of(), Files.readAllLines(activeFile));
        assertEquals(List.of("T | 1 | first", "T | 1 | second"),
                Files.readAllLines(activeFile.resolveSibling("archive.txt")));
    }

    @Test
    void archiveCommand_invalidBoundsTakePrecedenceOverIncompleteStatus() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());
        herta.getResponse("todo incomplete");
        HertaResponse response = herta.getResponse("archive 1 2");

        assertEquals("That number points to nothing on the active list. Check again.",
                response.getMessage());
        assertEquals(List.of("T | 0 | incomplete"), Files.readAllLines(activeFile));
    }

    @Test
    void archiveCommand_afterSort_usesUnderlyingActiveOrder() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Herta herta = new Herta(activeFile.toString());
        herta.getResponse("todo unscheduled");
        herta.getResponse("deadline dated /by 2019-10-15");
        herta.getResponse("mark 1");
        herta.getResponse("mark 2");
        herta.getResponse("sort date");

        HertaResponse response = herta.getResponse("archive 1");

        assertTrue(response.getMessage().contains("[T][X] unscheduled"));
        assertEquals(List.of("D | 1 | dated | 2019-10-15T00:00:00"),
                Files.readAllLines(activeFile));
        assertEquals(List.of("T | 1 | unscheduled"),
                Files.readAllLines(activeFile.resolveSibling("archive.txt")));
    }

    @Test
    void archiveCommand_atArchiveCapacity_preservesCollectionsAndFiles() throws Exception {
        Path activeFile = temporaryDirectory.resolve("archive-capacity").resolve("tasks.txt");
        Path archiveFile = activeFile.resolveSibling("archive.txt");
        Todo activeTask = new Todo("active");
        activeTask.markAsDone();
        Todo archivedTask = new Todo("archived");
        TaskList activeTasks = new TaskList(List.of(activeTask));
        TaskList archivedTasks = new TaskList(Collections.nCopies(
                TaskList.MAXIMUM_TASK_COUNT, archivedTask));
        Storage activeStorage = new Storage(activeFile.toString());
        Storage archiveStorage = new Storage(archiveFile.toString());
        activeStorage.save(activeTasks);
        archiveStorage.save(archivedTasks);
        byte[] originalActiveBytes = Files.readAllBytes(activeFile);
        byte[] originalArchiveBytes = Files.readAllBytes(archiveFile);
        TaskRepository repository = new TaskRepository(activeStorage, archiveStorage,
                activeTasks, archivedTasks);

        assertThrows(IllegalArgumentException.class, () -> new ArchiveCommand(
                new ArchiveSelection(List.of(new ArchiveRange(1, 1)), false))
                .execute(repository, new Ui()));

        assertEquals(1, activeTasks.size());
        assertEquals(TaskList.MAXIMUM_TASK_COUNT, archivedTasks.size());
        assertArrayEquals(originalActiveBytes, Files.readAllBytes(activeFile));
        assertArrayEquals(originalArchiveBytes, Files.readAllBytes(archiveFile));
    }

    @Test
    void archiveCommand_persistenceFailureLeavesBothCollectionsUnchanged() throws Exception {
        Path activeFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveDirectory = temporaryDirectory.resolve("archive-directory");
        Files.writeString(activeFile, "T | 1 | completed\n");
        Files.createDirectory(archiveDirectory);
        Todo completed = new Todo("completed");
        completed.markAsDone();
        TaskList activeTasks = new TaskList(List.of(completed));
        TaskList archivedTasks = new TaskList();
        TaskRepository repository = new TaskRepository(new Storage(activeFile.toString()),
                new Storage(archiveDirectory.toString()), activeTasks, archivedTasks);

        ArchiveCommand archiveCommand = new ArchiveCommand(
                new ArchiveSelection(List.of(new ArchiveRange(1, 1)), false));
        HertaException exception = assertThrows(HertaException.class, () ->
                archiveCommand.execute(repository, new Ui()));

        assertTrue(exception.getMessage().startsWith("Failed to archive tasks: "));
        assertEquals(1, activeTasks.size());
        assertTrue(activeTasks.get(0).isCompleted());
        assertEquals("T | 1 | completed\n", Files.readString(activeFile));
    }

    private void addCompletedTasks(Herta herta, int taskCount) {
        List<String> descriptions = List.of("first", "second", "third", "fourth");
        for (int i = 0; i < taskCount; i++) {
            herta.getResponse("todo " + descriptions.get(i));
        }
        for (int i = 1; i <= taskCount; i++) {
            herta.getResponse("mark " + i);
        }
    }
}
