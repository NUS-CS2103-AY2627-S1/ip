package trybot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import trybot.exception.TryBotException;
import trybot.parser.Parser;
import trybot.storage.Storage;
import trybot.task.TaskList;
import trybot.task.Todo;
import trybot.ui.Ui;

/** Tests command execution, state changes, error handling, and persistence calls. */
class CommandTest {
    @TempDir
    Path directory;

    @Test
    void addCommands_createDifferentTaskTypesAndSaveThem() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(directory.resolve("tasks.txt"));
        Ui ui = silentUi();

        new AddTodoCommand("todo").execute(tasks, ui, storage);
        new AddDeadlineCommand(new Parser.ParsedDeadline("deadline", "Friday"))
                .execute(tasks, ui, storage);
        new AddEventCommand(new Parser.ParsedEvent("event", "Monday", "Tuesday"))
                .execute(tasks, ui, storage);

        assertEquals(3, tasks.size());
        assertEquals(3, storage.loadTasks().size());
    }

    @Test
    void addCommands_rejectInvalidFieldsAndDuplicates() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(directory.resolve("tasks.txt"));
        Ui ui = silentUi();

        assertThrows(TryBotException.class, () -> new AddTodoCommand(" ").execute(tasks, ui, storage));
        assertThrows(TryBotException.class,
                () -> new AddDeadlineCommand(new Parser.ParsedDeadline("report", "31/02/2024"))
                        .execute(tasks, ui, storage));

        new AddTodoCommand("same").execute(tasks, ui, storage);
        assertThrows(TryBotException.class, () -> new AddTodoCommand("same").execute(tasks, ui, storage));
    }

    @Test
    void taskCommands_updateAndDeleteState() throws Exception {
        TaskList tasks = new TaskList(java.util.List.of(new Todo("read")));
        Storage storage = new Storage(directory.resolve("tasks.txt"));
        Ui ui = silentUi();

        new MarkCommand(1).execute(tasks, ui, storage);
        assertEquals("X", tasks.get(0).getStatusIcon());
        new UnmarkCommand(1).execute(tasks, ui, storage);
        assertEquals(" ", tasks.get(0).getStatusIcon());
        new TagCommand(1, "urgent").execute(tasks, ui, storage);
        assertEquals(java.util.List.of("urgent"), tasks.get(0).getTags());
        new TagDeleteCommand(1).execute(tasks, ui, storage);
        assertTrue(tasks.get(0).getTags().isEmpty());
        new DeleteCommand(1).execute(tasks, ui, storage);
        assertEquals(0, tasks.size());
    }

    @Test
    void taskCommands_invalidNumbers_doNotChangeState() {
        TaskList tasks = new TaskList(java.util.List.of(new Todo("read")));
        Storage storage = new Storage(directory.resolve("tasks.txt"));
        Ui ui = silentUi();

        assertThrows(TryBotException.class, () -> new MarkCommand(0).execute(tasks, ui, storage));
        assertThrows(TryBotException.class, () -> new UnmarkCommand(2).execute(tasks, ui, storage));
        assertThrows(TryBotException.class, () -> new TagCommand(2, "urgent").execute(tasks, ui, storage));
        assertThrows(TryBotException.class, () -> new TagDeleteCommand(0).execute(tasks, ui, storage));
        assertThrows(TryBotException.class, () -> new DeleteCommand(2).execute(tasks, ui, storage));
        assertEquals(1, tasks.size());
    }

    @Test
    void readOnlyCommands_reportResultsAndExitOnlyForBye() throws Exception {
        TaskList tasks = new TaskList(java.util.List.of(new Todo("read book")));
        Ui ui = silentUi();

        new ListCommand().execute(tasks, ui, new Storage(directory.resolve("tasks.txt")));
        new FindCommand("book").execute(tasks, ui, new Storage(directory.resolve("tasks.txt")));
        new HelpCommand().execute(tasks, ui, new Storage(directory.resolve("tasks.txt")));
        new ExitCommand().execute(tasks, ui, new Storage(directory.resolve("tasks.txt")));

        assertTrue(new ExitCommand().isExit());
        assertFalse(new ListCommand().isExit());
        assertThrows(TryBotException.class,
                () -> new EmptyCommand().execute(tasks, ui, new Storage(directory.resolve("tasks.txt"))));
        assertThrows(TryBotException.class,
                () -> new UnknownCommand().execute(tasks, ui, new Storage(directory.resolve("tasks.txt"))));
    }

    private static Ui silentUi() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        return new Ui(new PrintStream(output, true, StandardCharsets.UTF_8),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }
}
