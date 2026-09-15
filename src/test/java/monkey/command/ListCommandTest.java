package monkey.command;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_emptyTaskList_reportsThatNoTasksExist() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        new ListCommand().execute(
                new TaskList(), ui, new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("There are no tasks in your list.", output.toString());
    }

    @Test
    void execute_nonEmptyTaskList_displaysNumberedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("first"));
        tasks.add(new ToDos("second"));
        StringBuilder output = new StringBuilder();

        new ListCommand().execute(tasks, new Ui(message -> output.append(message).append('\n')),
                new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Here are the tasks in your list:\n1.[T][ ] first\n2.[T][ ] second\n", output.toString());
    }
}
