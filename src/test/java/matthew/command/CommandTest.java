package matthew.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import matthew.storage.Storage;
import matthew.task.Event;
import matthew.task.TaskList;
import matthew.task.Todo;
import matthew.ui.Ui;

/** Tests the effects of each concrete command. */
class CommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void commandsUpdateTasksPersistChangesAndShowFeedback() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Todo todo = new Todo("buy milk");
        Event event = new Event("camp", LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 23, 17, 0));

        String addOutput = capture(() -> new AddCommand(todo).execute(tasks, ui, storage));
        new AddCommand(event).execute(tasks, ui, storage);
        assertEquals(2, tasks.size());
        assertTrue(addOutput.contains("Now you have 1 tasks"));

        new MarkCommand(1).execute(tasks, ui, storage);
        assertTrue(tasks.get(1).isDone());
        new UnmarkCommand(1).execute(tasks, ui, storage);
        assertFalse(tasks.get(1).isDone());
        assertTrue(capture(() -> new ListCommand().execute(tasks, ui, storage)).contains("buy milk"));
        String findOutput = capture(() -> new FindCommand("MILK").execute(tasks, ui, storage));
        assertTrue(findOutput.contains("Here are the matching tasks in your list:"));
        assertTrue(findOutput.contains("1.[T][ ] buy milk"));
        assertFalse(findOutput.contains("camp"));
        assertTrue(capture(() -> new OnCommand(LocalDate.of(2026, 8, 22)).execute(tasks, ui, storage))
                .contains("camp"));

        new DeleteCommand(1).execute(tasks, ui, storage);
        assertEquals(1, tasks.size());
        assertEquals("camp", new TaskList(storage.load()).get(1).getDescription());

        ExitCommand exit = new ExitCommand();
        assertTrue(exit.isExit());
        assertTrue(capture(() -> exit.execute(tasks, ui, storage)).contains("Goodbye!"));
        assertFalse(new ListCommand().isExit());
    }

    private String capture(ThrowingRunnable action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            action.run();
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(original);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
