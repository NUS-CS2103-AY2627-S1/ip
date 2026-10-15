package yapatron.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import yapatron.save.Save;
import yapatron.task.TaskList;
import yapatron.task.Todo;
import yapatron.ui.Ui;
import yapatron.YapException;

public class CommandsTest {
    @Test
    public void doCommands_bye_returnsTrue() throws YapException {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Save save = new Save("./data/test.txt");
        boolean isExit = Commands.doCommands("bye", tasks, ui, save);

        assertTrue(isExit);
    }

    @Test 
    public void doCommands_emptyTodoDesc_exceptionThrown() {
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Save save = new Save("./data/test.txt");
        assertThrows(YapException.class, () -> Commands.doCommands("todo", tasks, ui, save));
    }

    @Test
    public void doCommands_list_updatesUiWithTaskList() throws YapException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("finish tutorial"));

        Ui ui = new Ui();
        Save save = new Save("./data/test.txt");

        Commands.doCommands("list", tasks, ui, save);

        assertTrue(ui.getLastResponse().contains("finish tutorial"));
        assertTrue(ui.getLastResponse().contains("Here's your current list!"));
    }

    @Test
    public void doCommands_help_updatesUiWithSupportedCommands()
        throws YapException {
        Ui ui = new Ui();

        Commands.doCommands(
                "HELP ",
                new TaskList(),
                ui,
                new Save("./data/test.txt"));

        assertTrue(ui.getLastResponse().contains("deadline <description>"));
        assertTrue(ui.getLastResponse().contains("help"));
    }

    @Test
    public void doCommands_findWithoutKeyword_throwsFriendlyError() {
        assertThrows(YapException.class, () ->
                Commands.doCommands(
                    "find",
                    new TaskList(),
                    new Ui(),
                    new Save("./data/test.txt")));
    }

    @Test
    public void doCommands_duplicateTodo_throwsFriendlyError()
        throws YapException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("same task"));

        assertThrows(YapException.class, () ->
                Commands.doCommands(
                    "todo   same task",
                    tasks,
                    new Ui(),
                    new Save("./data/test.txt")));
    }
}

