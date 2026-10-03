package verity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.exception.VerityException;
import verity.storage.Storage;
import verity.task.Deadline;
import verity.task.TaskList;
import verity.task.Todo;
import verity.ui.Ui;

/**
 * Tests command execution and command exit state.
 */
class CommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void exitCommand_isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
    }

    @Test
    void nonExitCommands_isExit_returnsFalse() {
        assertFalse(new AddCommand(new Todo("read book")).isExit());
        assertFalse(new DeleteCommand(0).isExit());
        assertFalse(new FindCommand("book").isExit());
        assertFalse(new FindDateCommand(
                LocalDate.of(2026, 8, 10)).isExit());
        assertFalse(new HelpCommand().isExit());
        assertFalse(new ListCommand().isExit());
        assertFalse(new MarkCommand(0).isExit());
        assertFalse(new UnmarkCommand(0).isExit());
    }

    @Test
    void addCommand_execute_addsTaskSavesItAndReturnsResponse()
            throws IOException, VerityException {
        TaskList tasks = new TaskList();
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Todo todo = new Todo("read book");
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(dataFile));

        String response = new AddCommand(todo).execute(context);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.get(0));
        assertEquals(
                "T\t0\tread book\t\t" + System.lineSeparator(),
                Files.readString(dataFile));
        assertTrue(response.contains("I've added this task"));
    }

    @Test
    void deleteCommand_execute_removesTaskSavesItAndReturnsResponse()
            throws IOException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(todo);
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(dataFile));

        String response = new DeleteCommand(0).execute(context);

        assertEquals(0, tasks.size());
        assertEquals("", Files.readString(dataFile));
        assertTrue(response.contains("I've removed this task"));
    }

    @Test
    void markCommand_execute_marksTaskSavesItAndReturnsResponse()
            throws IOException {
        Todo todo = new Todo("read book");
        TaskList tasks = new TaskList(todo);
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(dataFile));

        String response = new MarkCommand(0).execute(context);

        assertEquals("[T][X] read book", tasks.get(0).getStatus());
        assertEquals(
                "T\t1\tread book\t\t" + System.lineSeparator(),
                Files.readString(dataFile));
        assertTrue(response.contains("marked this task as done"));
    }

    @Test
    void unmarkCommand_execute_unmarksTaskSavesItAndReturnsResponse()
            throws IOException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        TaskList tasks = new TaskList(todo);
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(dataFile));

        String response = new UnmarkCommand(0).execute(context);

        assertEquals("[T][ ] read book", tasks.get(0).getStatus());
        assertEquals(
                "T\t0\tread book\t\t" + System.lineSeparator(),
                Files.readString(dataFile));
        assertTrue(response.contains("marked this task as not done yet"));
    }

    @Test
    void listCommand_execute_returnsAllTasks() {
        TaskList tasks = new TaskList(new Todo("read book"));
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(temporaryDirectory));

        String response = new ListCommand().execute(context);

        assertTrue(response.contains("1.[T][ ] read book"));
    }

    @Test
    void findCommand_execute_returnsOnlyKeywordMatches() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Todo("submit report"),
                new Todo("return BOOK")
        );
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(temporaryDirectory));

        String response = new FindCommand("book").execute(context);

        assertTrue(response.contains("1.[T][ ] read book"));
        assertTrue(response.contains("2.[T][ ] return BOOK"));
        assertFalse(response.contains("[T][ ] submit report"));
    }

    @Test
    void findDateCommand_execute_returnsTasksOnDate() {
        LocalDate date = LocalDate.of(2026, 8, 10);
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", date)
        );
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(temporaryDirectory));

        String response = new FindDateCommand(date).execute(context);

        assertTrue(response.contains("Tasks on 2026-08-10:"));
        assertTrue(response.contains("[D][ ] submit report"));
        assertFalse(response.contains("[T][ ] read book"));
    }

    @Test
    void exitCommand_execute_returnsExitMessageWithoutChangingTasks() {
        TaskList tasks = new TaskList();
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(temporaryDirectory));

        String response = new ExitCommand().execute(context);

        assertTrue(response.contains("Bye. Hope to see you again soon!"));
        assertEquals(0, tasks.size());
    }

    @Test
    void helpCommand_execute_returnsHelpWithoutChangingTasks() {
        TaskList tasks = new TaskList(new Todo("read book"));
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        CommandContext context = new CommandContext(
                tasks, new Ui(), new Storage(dataFile));

        String response = new HelpCommand().execute(context);

        assertTrue(response.contains("Verity command guide"));
        assertEquals(1, tasks.size());
        assertFalse(Files.exists(dataFile));
        assertFalse(Files.exists(dataFile.resolveSibling("clients.txt")));
    }
}
