package turtley.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import turtley.exception.TurtleyException;
import turtley.model.Task;
import turtley.model.TaskList;
import turtley.model.ToDo;
import turtley.storage.Storage;
import turtley.ui.Ui;

/** Tests that mutating commands restore in-memory state when persistence fails. */
class CommandRollbackTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void addCommand_saveFailure_removesNewTask() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = failingStorage();

        assertThrows(TurtleyException.class,
                () -> new TodoCommand("new task").execute(tasks, quietUi(), storage));

        assertTrue(tasks.isEmpty());
    }

    @Test
    void deleteCommand_saveFailure_restoresDeletedTaskAtOriginalIndex() throws Exception {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList();
        tasks.add(first);
        tasks.add(second);

        assertThrows(TurtleyException.class,
                () -> new DeleteCommand("1").execute(tasks, quietUi(), failingStorage()));

        assertTrue(tasks.size() == 2);
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    void markCommand_saveFailure_restoresIncompleteState() throws Exception {
        Task task = new ToDo("task");
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(TurtleyException.class,
                () -> new MarkCommand("1").execute(tasks, quietUi(), failingStorage()));

        assertFalse(task.isDone());
    }

    @Test
    void unmarkCommand_saveFailure_restoresCompletedState() throws Exception {
        Task task = new ToDo("task");
        task.markAsDone();
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(TurtleyException.class,
                () -> new UnmarkCommand("1").execute(tasks, quietUi(), failingStorage()));

        assertTrue(task.isDone());
    }

    @Test
    void tagCommand_saveFailure_restoresPreviousTags() throws Exception {
        Task task = new ToDo("task", List.of("#existing"));
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(TurtleyException.class,
                () -> new TagCommand("1 #new").execute(tasks, quietUi(), failingStorage()));

        assertEquals(List.of("#existing"), task.getTags());
    }

    @Test
    void untagCommand_saveFailure_restoresRemovedTags() throws Exception {
        Task task = new ToDo("task", List.of("#existing"));
        TaskList tasks = new TaskList();
        tasks.add(task);

        assertThrows(TurtleyException.class,
                () -> new UntagCommand("1 #existing").execute(tasks, quietUi(), failingStorage()));

        assertEquals(List.of("#existing"), task.getTags());
    }

    private Storage failingStorage() throws Exception {
        Path blocker = temporaryDirectory.resolve("file-blocking-parent");
        Files.writeString(blocker, "not a directory");
        return new Storage(blocker.resolve("tasks.txt").toString());
    }

    private static Ui quietUi() {
        return new Ui(new PrintStream(OutputStream.nullOutputStream()));
    }
}
