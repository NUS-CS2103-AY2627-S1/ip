package kong.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.storage.Storage;
import kong.task.Deadline;
import kong.task.TaskList;
import kong.ui.Ui;

class OnDateCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_tasksOnDate_displaysTasksViaUi() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDate.parse("2019-10-15")));
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        OnDateCommand command = new OnDateCommand(LocalDate.parse("2019-10-15"));

        command.execute(tasks, ui, storage);

        assertTrue(output.contains(
                "Consulting the ledger. Here are the deadlines and events on this date:"));
        assertTrue(output.contains("1. [D][ ] return book (by: Oct 15 2019)"));
    }

    @Test
    void execute_noTasksOnDate_displaysNoTasksMessageViaUi() {
        TaskList tasks = new TaskList();
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        OnDateCommand command = new OnDateCommand(LocalDate.parse("2019-10-15"));

        command.execute(tasks, ui, storage);

        assertTrue(output.contains("The ledger is clear. There are no deadlines or events on this date."));
    }
}
