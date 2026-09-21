package quackie.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import quackie.storage.Storage;
import quackie.task.TaskList;
import quackie.task.ToDo;
import quackie.ui.Ui;

/** Tests command execution and its delegation to application services. */
class CommandTest {
    @TempDir
    Path temporaryDirectory;

    /** Verifies add, list, mark, unmark, and delete command effects. */
    @Test
    void executesTaskCommands() {
        TaskList tasks = new TaskList();
        Ui ui = createUi();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));

        new AddCommand(new ToDo("read book")).execute(tasks, ui, storage);
        assertEquals(1, tasks.size());
        new MarkCommand(0).execute(tasks, ui, storage);
        assertTrue(tasks.get(0).isDone());
        new UnmarkCommand(0).execute(tasks, ui, storage);
        assertFalse(tasks.get(0).isDone());
        new UpdateCommand(0, new ToDo("read novel")).execute(tasks, ui, storage);
        assertEquals("read novel", tasks.get(0).getDescription());
        new ListCommand().execute(tasks, ui, storage);
        new FindCommand("book").execute(tasks, ui, storage);
        new DeleteCommand(0).execute(tasks, ui, storage);
        assertEquals(0, tasks.size());
    }

    /** Verifies exit and unknown commands expose their control-flow behaviour. */
    @Test
    void identifiesExitAndUnknownCommands() {
        Ui ui = createUi();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = new TaskList();
        Command exit = new ExitCommand();

        assertTrue(exit.isExit());
        assertFalse(new UnknownCommand().isExit());
        exit.execute(tasks, ui, storage);
        new UnknownCommand().execute(tasks, ui, storage);
        new DeleteCommand(-1).execute(tasks, ui, storage);
        new MarkCommand(-1).execute(tasks, ui, storage);
        new UnmarkCommand(-1).execute(tasks, ui, storage);
    }

    /** Verifies duplicate additions and updates leave the task list unchanged. */
    @Test
    void rejectsDuplicateTaskDetails() {
        TaskList tasks = new TaskList();
        Ui ui = createUi();
        Storage storage = new Storage(temporaryDirectory.resolve("duplicates.txt"));

        new AddCommand(new ToDo("read book")).execute(tasks, ui, storage);
        new AddCommand(new ToDo("READ BOOK")).execute(tasks, ui, storage);
        new AddCommand(new ToDo("write report")).execute(tasks, ui, storage);
        new UpdateCommand(1, new ToDo("read book")).execute(tasks, ui, storage);

        assertEquals(2, tasks.size());
        assertEquals("write report", tasks.get(1).getDescription());
    }

    /** Verifies a full task list rejects another task with a useful message. */
    @Test
    void rejectsTaskWhenListIsFull() {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            tasks.add(new ToDo("task " + i));
        }
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        Ui ui = createUi(capturedOutput);

        new AddCommand(new ToDo("one too many")).execute(
                tasks, ui, new Storage(temporaryDirectory.resolve("full.txt")));

        assertEquals(100, tasks.size());
        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8).contains("task list is full"));
    }

    /** Verifies storage failures are reported while leaving the session usable. */
    @Test
    void reportsStorageWriteFailure() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        Ui ui = createUi(capturedOutput);
        Storage failingStorage = new Storage(temporaryDirectory.resolve("unused.txt")) {
            @Override
            public void save(TaskList taskList) throws IOException {
                throw new IOException("simulated write failure");
            }
        };

        new MarkCommand(0).execute(tasks, ui, failingStorage);

        assertTrue(tasks.get(0).isDone());
        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8).contains("couldn't save your tasks"));
    }

    /** Creates an in-memory UI for command tests. */
    private Ui createUi() {
        return createUi(new ByteArrayOutputStream());
    }

    /** Creates an in-memory UI whose output remains available to the caller. */
    private Ui createUi(ByteArrayOutputStream output) {
        return new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
