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

class FindCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_matchingKeyword_displaysMatchingTasksViaUi() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy groceries"));
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        FindCommand command = new FindCommand("book");

        command.execute(tasks, ui, storage);

        assertTrue(output.contains("Eureka! Here are the matching tasks found in your archives:"));
        assertTrue(output.contains("1. [T][ ] read book"));
    }

    @Test
    void execute_noMatchingKeyword_displaysNoMatchesMessageViaUi() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        FindCommand command = new FindCommand("swimming");

        command.execute(tasks, ui, storage);

        assertTrue(output.contains("Search complete. There are no matching tasks in your list."));
    }
}
