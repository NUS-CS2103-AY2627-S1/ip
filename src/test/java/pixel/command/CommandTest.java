package pixel.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pixel.storage.Storage;
import pixel.task.Deadline;
import pixel.task.Event;
import pixel.task.TaskList;
import pixel.task.Todo;
import pixel.ui.Ui;

/** Tests command execution independently of parsing and console input. */
class CommandTest {

    @Test
    void commandFlags_regularExitAndUnknownCommands_returnExpectedFlags() {
        Command regular = new ListCommand();
        Command exit = new ExitCommand();
        Command unknown = new UnknownCommand();

        assertFalse(regular.isExit());
        assertFalse(regular.isError());
        assertTrue(exit.isExit());
        assertFalse(exit.isError());
        assertFalse(unknown.isExit());
        assertTrue(unknown.isError());
    }

    @Test
    void execute_listFindAndDateCommands_showMatchingTasks(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.of(2026, 9, 12)));
        tasks.add(new Event("meeting", "2026-09-11", "2026-09-13"));
        Storage storage = new Storage(tempDir.resolve("pixel.txt"));
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new ListCommand().execute(tasks, ui, storage);
        assertEquals(List.of("Here are the tasks in your list:",
                "1.[T][ ] read book",
                "2.[D][ ] return book (by: Sep 12 2026)",
                "3.[E][ ] meeting (from: 2026-09-11 to: 2026-09-13)"), messages);

        messages.clear();
        new FindCommand("book").execute(tasks, ui, storage);
        assertEquals(List.of("Here are the matching tasks in your list:",
                "1.[T][ ] read book",
                "2.[D][ ] return book (by: Sep 12 2026)"), messages);

        messages.clear();
        new DateCommand(LocalDate.of(2026, 9, 12)).execute(tasks, ui, storage);
        assertEquals(List.of("Here are the tasks occurring on Sep 12 2026:",
                "2.[D][ ] return book (by: Sep 12 2026)",
                "3.[E][ ] meeting (from: 2026-09-11 to: 2026-09-13)"), messages);
    }

    @Test
    void execute_queriesWithoutMatches_showEmptyResults(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("pixel.txt"));
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new FindCommand("missing").execute(tasks, ui, storage);
        assertEquals(List.of("Here are the matching tasks in your list:"), messages);

        messages.clear();
        new DateCommand(LocalDate.of(2026, 9, 12)).execute(tasks, ui, storage);
        assertEquals(List.of("There are no tasks occurring on Sep 12 2026."), messages);
    }

    @Test
    void execute_mutatingCommands_updateStateAndStorage(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("pixel.txt");
        Storage storage = new Storage(file);
        TaskList tasks = new TaskList();
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new AddCommand(new Todo("read book")).execute(tasks, ui, storage);
        new MarkCommand(0).execute(tasks, ui, storage);
        new UnmarkCommand(0).execute(tasks, ui, storage);
        new UpdateCommand(0, UpdateField.DESCRIPTION, "read novel").execute(tasks, ui, storage);
        new DeleteCommand(0).execute(tasks, ui, storage);

        assertEquals(0, tasks.size());
        assertTrue(Files.exists(file));
        assertEquals("", Files.readString(file));
        assertTrue(messages.contains("  [T][X] read book"));
        assertTrue(messages.contains("  [T][ ] read novel"));
    }

    @Test
    void execute_invalidIndexes_leaveStateUnchanged(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("pixel.txt"));
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new MarkCommand(1).execute(tasks, ui, storage);
        new UnmarkCommand(-1).execute(tasks, ui, storage);
        new DeleteCommand(3).execute(tasks, ui, storage);
        new UpdateCommand(2, UpdateField.DESCRIPTION, "changed").execute(tasks, ui, storage);

        assertEquals(List.of("That task number does not exist.",
                "That task number does not exist.",
                "That task number does not exist.",
                "That task number does not exist."), messages);
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void execute_typedUpdates_changeApplicableFields(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("report", LocalDate.of(2026, 9, 12)));
        tasks.add(new Event("meeting", "2pm", "4pm"));
        Storage storage = new Storage(tempDir.resolve("pixel.txt"));
        Ui ui = new Ui(new ArrayList<String>()::add);

        new UpdateCommand(0, UpdateField.BY, "2026-09-13").execute(tasks, ui, storage);
        new UpdateCommand(1, UpdateField.FROM, "3pm").execute(tasks, ui, storage);
        new UpdateCommand(1, UpdateField.TO, "5pm").execute(tasks, ui, storage);

        assertEquals("[D][ ] report (by: Sep 13 2026)", tasks.get(0).toString());
        assertEquals("[E][ ] meeting (from: 3pm to: 5pm)", tasks.get(1).toString());
    }

    @Test
    void execute_inapplicableUpdates_throwWithoutChangingTask(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("report", LocalDate.of(2026, 9, 12)));
        Storage storage = new Storage(tempDir.resolve("pixel.txt"));
        Ui ui = new Ui(new ArrayList<String>()::add);

        assertThrows(IllegalArgumentException.class, () ->
                new UpdateCommand(0, UpdateField.BY, "2026-09-13").execute(tasks, ui, storage));
        assertThrows(IllegalArgumentException.class, () ->
                new UpdateCommand(0, UpdateField.FROM, "2pm").execute(tasks, ui, storage));
        assertThrows(IllegalArgumentException.class, () ->
                new UpdateCommand(0, UpdateField.TO, "4pm").execute(tasks, ui, storage));
        assertThrows(IllegalArgumentException.class, () ->
                new UpdateCommand(1, UpdateField.BY, "tomorrow").execute(tasks, ui, storage));

        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[D][ ] report (by: Sep 12 2026)", tasks.get(1).toString());
    }

    @Test
    void execute_duplicateAdd_throwsWithoutChangingState(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Storage storage = new Storage(tempDir.resolve("pixel.txt"));
        Ui ui = new Ui(new ArrayList<String>()::add);

        assertThrows(IllegalArgumentException.class, () ->
                new AddCommand(new Todo("read book")).execute(tasks, ui, storage));
        assertEquals(1, tasks.size());
    }

    @Test
    void execute_whenSaveFails_reportsWarningAndKeepsInMemoryChange(@TempDir Path tempDir)
            throws Exception {
        Path file = tempDir.resolve("pixel.txt");
        Files.createDirectory(tempDir.resolve("pixel.txt.tmp"));
        TaskList tasks = new TaskList();
        ArrayList<String> messages = new ArrayList<>();

        new AddCommand(new Todo("unsaved")).execute(
                tasks, new Ui(messages::add), new Storage(file));

        assertEquals(1, tasks.size());
        assertEquals("Oops! I couldn't save your tasks. "
                + "Your changes will only last until Pixel exits.", messages.get(0));
    }

    @Test
    void execute_exitAndUnknownCommands_showExpectedMessages(@TempDir Path tempDir) {
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("pixel.txt"));
        ArrayList<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new UnknownCommand().execute(tasks, ui, storage);
        new ExitCommand().execute(tasks, ui, storage);

        assertEquals(List.of("Sorry, I don't recognise that command.",
                "Bye. Hope to see you again soon!"), messages);
    }
}
