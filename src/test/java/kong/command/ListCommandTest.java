package kong.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import kong.storage.Storage;
import kong.task.TaskList;
import kong.task.Todo;
import kong.ui.Ui;

class ListCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_displaysTaskListViaUi() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        ListCommand command = new ListCommand();

        command.execute(tasks, ui, storage);

        assertTrue(output.contains("Here are the tasks currently recorded in your archives:"));
        assertTrue(output.contains("1. [T][ ] read book"));
    }
}
