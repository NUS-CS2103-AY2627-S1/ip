package monkey.command;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeleteCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_validIndex_removesTaskAndSavesRemainingTasks() throws Exception {
        TaskList tasks = tasksWithTwoTodos();
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        StringBuilder output = new StringBuilder();

        new DeleteCommand("1").execute(tasks, createUi(output), new Storage(saveFile.toString()));

        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getDescription());
        assertEquals("T | 0 | second", Files.readString(saveFile).trim());
        assertEquals("Noted. I've removed this task:\n  [T][ ] first\nNow you have 1 tasks in the list.",
                output.toString());
    }

    @Test
    void execute_nonNumericIndex_reportsErrorAndPreservesTasks() {
        TaskList tasks = tasksWithTwoTodos();
        StringBuilder output = new StringBuilder();

        new DeleteCommand("one").execute(tasks, createUi(output), createStorage());

        assertEquals(2, tasks.size());
        assertEquals("OOPS! Monkey says: This monkey needs a valid task number after 'delete'.", output.toString());
    }

    @Test
    void execute_outOfRangeIndexes_reportErrorAndPreserveTasks() {
        TaskList tasks = tasksWithTwoTodos();
        StringBuilder output = new StringBuilder();

        new DeleteCommand("0").execute(tasks, createUi(output), createStorage());
        new DeleteCommand("3").execute(tasks, createUi(output), createStorage());

        assertEquals(2, tasks.size());
        assertEquals("That task number does not exist.\nThat task number does not exist.", output.toString());
    }

    private TaskList tasksWithTwoTodos() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("first"));
        tasks.add(new ToDos("second"));
        return tasks;
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("monkey.txt").toString());
    }

    private static Ui createUi(StringBuilder output) {
        return new Ui(message -> {
            if (!output.isEmpty()) {
                output.append('\n');
            }
            output.append(message);
        });
    }
}
