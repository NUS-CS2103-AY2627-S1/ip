package sage.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import sage.core.TaskList;
import sage.exception.SageException;
import sage.storage.Storage;
import sage.task.Task;
import sage.task.Todo;
import sage.ui.Ui;

class CommandTest {
    @TempDir
    Path tempDir;

    @Test
    void addCommand_executesAndSavesTask() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Ui ui = new Ui();

        String output = captureOutput(() -> new AddCommand(new Todo("read chapter")).execute(tasks, ui, storage));

        assertEquals(1, tasks.size());
        assertEquals("read chapter", tasks.get(0).getDescription());
        assertTrue(output.contains("Noted. One less thing to remember:"));
    }

    @Test
    void deleteCommand_removesTaskAndPersistsChange() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Ui ui = new Ui();
        Task task = new Todo("write summary");
        tasks.add(task);

        String output = captureOutput(() -> {
            try {
                new DeleteCommand(1).execute(tasks, ui, storage);
            } catch (SageException e) {
                throw new RuntimeException(e);
            }
        });

        assertEquals(0, tasks.size());
        assertTrue(output.contains("Cleared from your list:"));
    }

    @Test
    void markAndUnmarkCommands_toggleCompletionState() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Ui ui = new Ui();
        tasks.add(new Todo("review PR"));

        String markOutput = captureOutput(() -> {
            try {
                new MarkCommand(1).execute(tasks, ui, storage);
            } catch (SageException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals("X", tasks.get(0).getStatusIcon());
        assertTrue(markOutput.contains("A little progress. Marked as done:"));

        String unmarkOutput = captureOutput(() -> {
            try {
                new UnmarkCommand(1).execute(tasks, ui, storage);
            } catch (SageException e) {
                throw new RuntimeException(e);
            }
        });
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertTrue(unmarkOutput.contains("Back on your list. Marked as not done:"));
    }

    @Test
    void listAndExitCommandsDisplayExpectedOutput() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Ui ui = new Ui();
        tasks.add(new Todo("plan sprint"));

        String listOutput = captureOutput(() -> new ListCommand().execute(tasks, ui, storage));
        String exitOutput = captureOutput(() -> new ExitCommand().execute(tasks, ui, storage));

        assertTrue(listOutput.contains("Here's your list:"));
        assertTrue(exitOutput.contains("Take care. One step at a time."));
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    void findCommand_filtersTasksCaseInsensitive() throws SageException {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        Ui ui = new Ui();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));
        tasks.add(new Todo("write report"));

        String output = captureOutput(() -> new FindCommand("BOOK").execute(tasks, ui, storage));

        assertTrue(output.contains("Here's what I found:"));
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("return book"));
        assertTrue(output.contains("write report") == false);
    }

    @Test
    void commands_failedSave_rollsBackAddDeleteAndCompletion() throws Exception {
        Path blocker = tempDir.resolve("blocked");
        Files.writeString(blocker, "Keep this file");
        Storage storage = new Storage(blocker.resolve("tasks.txt").toString());
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first);
        tasks.add(second);
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        assertThrows(SageException.class, () -> new AddCommand(new Todo("third")).execute(tasks, ui, storage));
        assertEquals(List.of(first, second), tasks.getTasks());
        assertThrows(SageException.class, () -> new DeleteCommand(1).execute(tasks, ui, storage));
        assertEquals(List.of(first, second), tasks.getTasks());
        assertThrows(SageException.class, () -> new MarkCommand(1).execute(tasks, ui, storage));
        assertEquals(" ", first.getStatusIcon());
        assertThrows(SageException.class, () -> new UnmarkCommand(1).execute(tasks, ui, storage));
        assertEquals(" ", first.getStatusIcon());
        first.markAsDone();
        assertThrows(SageException.class, () -> new UnmarkCommand(1).execute(tasks, ui, storage));
        assertEquals("X", first.getStatusIcon());
        assertThrows(SageException.class, () -> new MarkCommand(1).execute(tasks, ui, storage));
        assertEquals("X", first.getStatusIcon());
        assertTrue(output.isEmpty());
        assertEquals("Keep this file", Files.readString(blocker));
    }

    @Test
    void commands_invalidIndexes_returnErrorsWithoutMutatingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read"));
        Storage storage = new Storage(tempDir.resolve("indexes.txt").toString());
        Ui ui = new Ui(line -> { });
        for (int index : List.of(Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE)) {
            assertThrows(SageException.class, () -> new MarkCommand(index).execute(tasks, ui, storage));
            assertThrows(SageException.class, () -> new UnmarkCommand(index).execute(tasks, ui, storage));
            assertThrows(SageException.class, () -> new DeleteCommand(index).execute(tasks, ui, storage));
        }
        assertEquals(1, tasks.size());
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    private String captureOutput(ThrowingRunnable action) throws SageException {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            action.run();
            return captured.toString();
        } finally {
            System.setOut(originalOut);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws SageException;
    }
}
