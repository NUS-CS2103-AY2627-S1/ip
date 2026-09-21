package auto.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.task.Task;
import auto.task.TaskList;
import auto.task.ToDo;

class FindCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_mixedTasks_displaysMatchesWithoutMutationOrSave() {
        List<Task> originalTasks = List.of(
                new ToDo("read book"),
                new ToDo("return BOOK"),
                new ToDo("write essay"));
        TaskList tasks = new TaskList(originalTasks);

        CommandResult result = new FindCommand("book").execute(tasks, failingStorage(tempDirectory));

        assertEquals(originalTasks, tasks.asList());
        assertTrue(result.message().contains("1. [T][ ] read book"));
        assertTrue(result.message().contains("2. [T][ ] return BOOK"));
        assertFalse(result.message().contains("3. [T][ ] write essay"));
    }
}
