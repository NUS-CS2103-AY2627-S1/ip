package alfred.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alfred.exception.AlfredException;
import alfred.storage.Storage;
import alfred.task.TaskList;
import alfred.task.Todo;
import alfred.ui.Ui;

class CommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_stateChangingCommands_persistsTasksAndDisplaysConfirmations() throws AlfredException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        Storage storage = createStorage();
        StringBuilder responseBuffer = new StringBuilder();
        Ui ui = new Ui(responseBuffer);

        new AddCommand(new Todo("submit report")).execute(tasks, ui, storage);
        new MarkCommand(0).execute(tasks, ui, storage);
        new UnmarkCommand(0).execute(tasks, ui, storage);
        new DeleteCommand(1).execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
        assertEquals(1, storage.load().size());
        assertEquals("read book", storage.load().get(0).getDescription());
        assertEquals("Consider it noted in the cave ledger:\n"
                        + "  [T][ ] submit report\n"
                        + "Your cave ledger now holds 2 tasks.\n"
                        + "Mission accomplished. I've marked this task as done:\n"
                        + "  [T][X] read book\n"
                        + "I've reopened this mission:\n"
                        + "  [T][ ] read book\n"
                        + "The following mission has left the cave ledger:\n"
                        + "  [T][ ] submit report\n"
                        + "Your cave ledger now holds 1 task.\n",
                responseBuffer.toString());
    }

    @Test
    void execute_storageFailure_restoresTaskListAndTaskStatus() throws IOException {
        Storage failingStorage = createFailingStorage();
        StringBuilder responseBuffer = new StringBuilder();
        Ui ui = new Ui(responseBuffer);

        TaskList addTasks = new TaskList();
        AddCommand addCommand = new AddCommand(new Todo("read book"));
        assertThrows(AlfredException.class, () -> addCommand.execute(addTasks, ui, failingStorage));
        assertEquals(0, addTasks.size());

        Todo deletedTodo = new Todo("delete me");
        TaskList deleteTasks = new TaskList(List.of(deletedTodo));
        assertThrows(AlfredException.class, () -> new DeleteCommand(0).execute(deleteTasks, ui, failingStorage));
        assertEquals(1, deleteTasks.size());
        assertEquals(deletedTodo, deleteTasks.get(0));

        Todo unmarkedTodo = new Todo("mark me");
        TaskList markTasks = new TaskList(List.of(unmarkedTodo));
        assertThrows(AlfredException.class, () -> new MarkCommand(0).execute(markTasks, ui, failingStorage));
        assertFalse(unmarkedTodo.isDone());

        Todo markedTodo = new Todo("unmark me");
        markedTodo.markAsDone();
        TaskList unmarkTasks = new TaskList(List.of(markedTodo));
        assertThrows(AlfredException.class, () -> new UnmarkCommand(0).execute(unmarkTasks, ui, failingStorage));
        assertTrue(markedTodo.isDone());
        assertEquals("", responseBuffer.toString());
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data").resolve("alfred.txt"));
    }

    private Storage createFailingStorage() throws IOException {
        Path blockedPath = temporaryDirectory.resolve("blocked-data");
        Files.writeString(blockedPath, "not a directory");
        return new Storage(blockedPath.resolve("alfred.txt"));
    }
}
