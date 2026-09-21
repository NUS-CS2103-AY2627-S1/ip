package auto.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.task.Task;
import auto.task.TaskList;
import auto.task.ToDo;

class ListCommandTest extends CommandTestSupport {
    @TempDir
    Path tempDirectory;

    @Test
    void execute_populatedList_displaysAllTasksWithoutMutationOrSave() {
        Task first = new ToDo("read book");
        Task second = new ToDo("write essay");
        TaskList tasks = new TaskList(List.of(first, second));

        CommandResult result = new ListCommand().execute(tasks, failingStorage(tempDirectory));

        assertEquals(2, tasks.size());
        assertTrue(result.message().contains("1. [T][ ] read book"));
        assertTrue(result.message().contains("2. [T][ ] write essay"));
    }
}
