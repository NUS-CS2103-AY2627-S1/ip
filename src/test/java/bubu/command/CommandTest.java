package bubu.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bubu.exception.BubuException;
import bubu.storage.Storage;
import bubu.task.TaskList;
import bubu.task.ToDo;
import bubu.ui.Ui;

/** Tests command execution using isolated task storage. */
class CommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void todoCommand_execute_addsTaskAndShowsConfirmation() throws BubuException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        TodoCommand command = new TodoCommand("read notes");

        command.execute(tasks, ui, createStorage());

        assertFalse(command.isExit());
        assertTrue(ui.getResponse().contains("read notes"));
        assertTrue(ui.getResponse().contains("1 task"));
    }

    @Test
    void deadlineCommand_execute_addsTaskAndShowsConfirmation() throws BubuException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();

        new DeadlineCommand("submit report", LocalDateTime.of(2026, 9, 18, 18, 0))
                .execute(tasks, ui, createStorage());

        assertTrue(ui.getResponse().contains("submit report"));
    }

    @Test
    void eventCommand_execute_addsTaskAndShowsConfirmation() throws BubuException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();

        new EventCommand("team meeting", LocalDateTime.of(2026, 9, 18, 10, 0),
                LocalDateTime.of(2026, 9, 18, 11, 0)).execute(tasks, ui, createStorage());

        assertTrue(ui.getResponse().contains("team meeting"));
    }

    @Test
    void findCommand_execute_showsMatchingTasks() {
        TaskList tasks = new TaskList(List.of(new ToDo("read notes"), new ToDo("submit report")));
        Ui ui = new Ui();

        new FindCommand("read").execute(tasks, ui, createStorage());

        assertTrue(ui.getResponse().contains("read notes"));
        assertFalse(ui.getResponse().contains("submit report"));
    }

    @Test
    void listCommand_execute_showsAllTasks() {
        TaskList tasks = new TaskList(List.of(new ToDo("read notes")));
        Ui ui = new Ui();

        new ListCommand().execute(tasks, ui, createStorage());

        assertTrue(ui.getResponse().contains("read notes"));
    }

    @Test
    void exitCommand_execute_showsGoodbyeAndIsExit() {
        Ui ui = new Ui();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), ui, createStorage());

        assertTrue(command.isExit());
        assertTrue(ui.getResponse().contains("Bye"));
    }

    @Test
    void remindCommand_execute_emptyTaskListShowsEmptyMessage() {
        Ui ui = new Ui();

        new RemindCommand().execute(new TaskList(), ui, createStorage());

        assertTrue(ui.getResponse().contains("No incomplete tasks"));
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("bubu.txt"));
    }
}
