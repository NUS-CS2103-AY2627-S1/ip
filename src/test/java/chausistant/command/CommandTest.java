package chausistant.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chausistant.exception.ChausistantException;
import chausistant.storage.Storage;
import chausistant.task.DeadlineTask;
import chausistant.task.TaskList;
import chausistant.task.TodoTask;
import chausistant.ui.Ui;

/** Tests individual command effects, validation, and persistence failure recovery. */
class CommandTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void addCommand_addsSavesAndDisplaysTask() throws IOException, ChausistantException {
        TaskList tasks = new TaskList();
        StringBuilder response = new StringBuilder();
        Storage storage = new Storage(temporaryDirectory.resolve("chausistant.txt"));

        new AddCommand(new TodoTask("read book")).execute(tasks, new Ui(response), storage);

        assertEquals("[T][ ] read book", tasks.get(0).printTask());
        assertEquals("T | 0 | read book",
                Files.readString(temporaryDirectory.resolve("chausistant.txt")).strip());
        assertEquals(String.join(System.lineSeparator(),
                "yay! i've added this little mission:",
                "[T][ ] read book",
                "don't give up! you have 1 task ahead of you...") + System.lineSeparator(),
                response.toString());
    }

    @Test
    void addCommand_rejectsTaskWithDuplicateDetails() {
        TaskList tasks = new TaskList(new TodoTask("read book"));

        ChausistantException error = assertThrows(ChausistantException.class, () ->
                new AddCommand(new TodoTask("read book")).execute(tasks,
                        new Ui(new StringBuilder()), new Storage(Path.of("unused.txt"))));

        assertEquals("This task is already in your list.", error.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    void deleteCommand_removesSavesAndDisplaysSelectedTask() throws IOException, ChausistantException {
        TaskList tasks = new TaskList(new TodoTask("read book"), new TodoTask("return book"));
        StringBuilder response = new StringBuilder();
        Storage storage = new Storage(temporaryDirectory.resolve("chausistant.txt"));

        new DeleteCommand("2").execute(tasks, new Ui(response), storage);

        assertEquals(List.of("[T][ ] read book"), tasks.getTasks().stream().map(task -> task.printTask()).toList());
        assertEquals("T | 0 | read book",
                Files.readString(temporaryDirectory.resolve("chausistant.txt")).strip());
        assertEquals(String.join(System.lineSeparator(),
                "poof! i've tucked this task away:",
                "[T][ ] return book",
                "your list now has 1 task.") + System.lineSeparator(), response.toString());
    }

    @Test
    void statusCommands_updateAndDisplayCompletionState() throws IOException, ChausistantException {
        TodoTask task = new TodoTask("read book");
        TaskList tasks = new TaskList(task);
        Storage storage = new Storage(temporaryDirectory.resolve("chausistant.txt"));
        StringBuilder markedResponse = new StringBuilder();
        StringBuilder unmarkedResponse = new StringBuilder();

        new MarkCommand("1").execute(tasks, new Ui(markedResponse), storage);
        new UnmarkCommand("1").execute(tasks, new Ui(unmarkedResponse), storage);

        assertFalse(task.isCompleted());
        assertEquals("T | 0 | read book",
                Files.readString(temporaryDirectory.resolve("chausistant.txt")).strip());
        assertEquals("looking good! here's your task now:" + System.lineSeparator()
                + "[T][X] read book" + System.lineSeparator(), markedResponse.toString());
        assertEquals("looking good! here's your task now:" + System.lineSeparator()
                + "[T][ ] read book" + System.lineSeparator(), unmarkedResponse.toString());
    }

    @Test
    void statusCommands_rejectRepeatedStatusChange() throws IOException, ChausistantException {
        TodoTask task = new TodoTask("read book");
        TaskList tasks = new TaskList(task);
        Storage storage = new Storage(temporaryDirectory.resolve("chausistant.txt"));

        new MarkCommand("1").execute(tasks, new Ui(new StringBuilder()), storage);
        ChausistantException markError = assertThrows(ChausistantException.class, () ->
                new MarkCommand("1").execute(tasks, new Ui(new StringBuilder()), storage));

        new UnmarkCommand("1").execute(tasks, new Ui(new StringBuilder()), storage);
        ChausistantException unmarkError = assertThrows(ChausistantException.class, () ->
                new UnmarkCommand("1").execute(tasks, new Ui(new StringBuilder()), storage));

        assertEquals("This task is already marked as completed.", markError.getMessage());
        assertEquals("This task is already marked as incomplete.", unmarkError.getMessage());
        assertFalse(task.isCompleted());
    }

    @Test
    void listFindAndExitCommands_displayTheirResults() {
        TaskList tasks = new TaskList(new TodoTask("read book"),
                new DeadlineTask("return book", LocalDateTime.of(2026, 6, 8, 12, 0), true));

        StringBuilder listResponse = new StringBuilder();
        new ListCommand().execute(tasks, new Ui(listResponse), new Storage(Path.of("unused.txt")));
        StringBuilder findResponse = new StringBuilder();
        new FindCommand("RETURN").execute(tasks, new Ui(findResponse), new Storage(Path.of("unused.txt")));
        StringBuilder emptyFindResponse = new StringBuilder();
        new FindCommand("receipt").execute(tasks, new Ui(emptyFindResponse), new Storage(Path.of("unused.txt")));
        StringBuilder exitResponse = new StringBuilder();
        ExitCommand exitCommand = new ExitCommand();
        exitCommand.execute(tasks, new Ui(exitResponse), new Storage(Path.of("unused.txt")));

        assertEquals(String.join(System.lineSeparator(),
                "here's your tiny adventure list:",
                "1.[T][ ] read book",
                "2.[D][ ] return book (by: Jun 8 2026 1200)") + System.lineSeparator(),
                listResponse.toString());
        assertEquals(String.join(System.lineSeparator(),
                "i found these task twins:",
                "1.[D][ ] return book (by: Jun 8 2026 1200)") + System.lineSeparator(),
                findResponse.toString());
        assertEquals(String.join(System.lineSeparator(),
                "i found these task twins:",
                "no task twins found yet!") + System.lineSeparator(), emptyFindResponse.toString());
        assertEquals("bye-bye for now! chausistant will be cheering for you!" + System.lineSeparator(),
                exitResponse.toString());
        assertTrue(exitCommand.isExit());
    }

    @Test
    void numberedCommands_rejectBlankNonNumericAndOutOfRangeTaskNumbers() {
        TaskList tasks = new TaskList(new TodoTask("read book"));

        assertValidationError(new MarkCommand(""), tasks, "Use: mark <task number>.");
        assertValidationError(new UnmarkCommand("first"), tasks,
                "A task number must be a whole number.");
        assertValidationError(new DeleteCommand("0"), tasks, "There is no task numbered 0.");
        assertValidationError(new DeleteCommand("2"), tasks, "There is no task numbered 2.");
    }

    @Test
    void mutatingCommands_restoreTaskListWhenSavingFails() throws IOException {
        Path blockedSavePath = temporaryDirectory.resolve("save-directory");
        Files.createDirectory(blockedSavePath);
        Storage blockedStorage = new Storage(blockedSavePath);

        TaskList addedTasks = new TaskList();
        assertThrows(IOException.class, () ->
                new AddCommand(new TodoTask("new task")).execute(addedTasks,
                        new Ui(new StringBuilder()), blockedStorage));
        assertEquals(0, addedTasks.size());

        TodoTask deletedTask = new TodoTask("keep task");
        TaskList deletedTasks = new TaskList(deletedTask);
        assertThrows(IOException.class, () ->
                new DeleteCommand("1").execute(deletedTasks, new Ui(new StringBuilder()), blockedStorage));
        assertEquals(List.of(deletedTask), deletedTasks.getTasks());

        TodoTask markedTask = new TodoTask("unfinished task");
        TaskList markedTasks = new TaskList(markedTask);
        assertThrows(IOException.class, () ->
                new MarkCommand("1").execute(markedTasks, new Ui(new StringBuilder()), blockedStorage));
        assertFalse(markedTask.isCompleted());
    }

    /** Asserts that an invalid numbered command reports its user-facing validation message. */
    private static void assertValidationError(NumberedTaskCommand command, TaskList tasks,
                                              String expectedMessage) {
        ChausistantException error = assertThrows(ChausistantException.class, () ->
                command.execute(tasks, new Ui(new StringBuilder()), new Storage(Path.of("unused.txt"))));

        assertEquals(expectedMessage, error.getMessage());
    }
}
