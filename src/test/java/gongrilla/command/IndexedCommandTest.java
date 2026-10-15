package gongrilla.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gongrilla.exception.GongrillaException;
import gongrilla.storage.Storage;
import gongrilla.task.TaskList;
import gongrilla.task.Todo;
import gongrilla.ui.Ui;

/** Tests the shared selection contract of commands that operate on a task index. */
class IndexedCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_invalidIndices_reportsErrorWithoutSideEffects() {
        for (int size : new int[]{0, 1}) {
            for (int index : new int[]{-1, size}) {
                for (Command command : commandsAt(index)) {
                    TaskList tasks = size == 0 ? new TaskList() : new TaskList(new Todo("only task"));
                    Path path = temporaryDirectory.resolve("tasks.txt");
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    GongrillaException exception = assertThrows(GongrillaException.class, () ->
                            command.execute(tasks, createUi(output), new Storage(path)));

                    assertEquals("No task there. Human seeing things?", exception.getMessage());
                    assertEquals(size, tasks.size());
                    if (size == 1) {
                        assertFalse(tasks.get(0).isDone());
                    }
                    assertFalse(Files.exists(path));
                    assertEquals("", output.toString());
                }
            }
        }
    }

    @Test
    void execute_saveFailure_preservesSelectedTaskAndSuppressesConfirmation() {
        for (Command command : commandsAt(0)) {
            Todo task = new Todo("only task");
            boolean wasDone = command instanceof UnmarkCommand;
            if (wasDone) {
                task.markDone();
            }
            TaskList tasks = new TaskList(task);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Storage storage = new Storage(temporaryDirectory);

            assertThrows(IOException.class, () -> command.execute(tasks, createUi(output), storage));

            assertEquals(List.of(task), tasks.asList());
            assertEquals(wasDone, task.isDone());
            assertEquals("", output.toString());
        }
    }

    @Test
    void execute_validSelection_preservesJournalAndRepeatedCompletionBehavior() throws Exception {
        Todo task = new Todo("only task");
        TaskList tasks = new TaskList(task);
        Path path = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(path);
        storage.appendAdd(task);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = createUi(output);

        new MarkCommand(0).execute(tasks, ui, storage);
        new MarkCommand(0).execute(tasks, ui, storage);
        assertTrue(task.isDone());
        assertTrue(storage.load().get(0).isDone());
        assertEquals(2, Files.readAllLines(path).size());
        assertTrue(output.toString().contains("Marked. oink oink \uD83D\uDC34"));

        output.reset();
        new UnmarkCommand(0).execute(tasks, ui, storage);
        new UnmarkCommand(0).execute(tasks, ui, storage);
        assertFalse(task.isDone());
        assertFalse(storage.load().get(0).isDone());
        assertEquals(3, Files.readAllLines(path).size());
        assertTrue(output.toString().contains("Unmarked. \uD83D\uDC34\uD83D\uDC94"));

        output.reset();
        new DeleteCommand(0).execute(tasks, ui, storage);
        assertEquals(0, tasks.size());
        assertTrue(storage.load().isEmpty());
        assertTrue(output.toString().contains("Gongrilla remove task:"));
    }

    /** Creates each command that shares task-index validation. */
    private List<Command> commandsAt(int index) {
        return List.of(new MarkCommand(index), new UnmarkCommand(index), new DeleteCommand(index));
    }

    /** Creates a UI whose output can be checked without using the console. */
    private Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new ByteArrayInputStream(new byte[0]), new PrintStream(output));
    }
}
