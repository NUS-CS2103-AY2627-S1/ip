package samantha.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import samantha.exception.InputException;
import samantha.model.TaskList;
import samantha.model.Todo;
import samantha.storage.Storage;
import samantha.ui.Ui;

class DeleteCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_middleTaskId_removesTargetAndPersistsSurvivorOrder() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        TaskList tasks = taskListWithThreeTodos();

        new DeleteCommand(2).execute(tasks, new Ui(), storage);

        assertEquals(2, tasks.size());
        assertEquals("first", tasks.getTask(1).getTaskName());
        assertEquals("third", tasks.getTask(2).getTaskName());
        assertEquals("T | 0 | third", storage.load().get(1).toFileString());
    }

    @Test
    void execute_invalidTaskId_inputExceptionAndNoTaskRemoved() throws Exception {
        TaskList tasks = taskListWithThreeTodos();

        assertThrows(InputException.class, () -> new DeleteCommand(4).execute(tasks, new Ui(),
                new Storage(temporaryDirectory.resolve("tasks.txt"))));
        assertEquals(3, tasks.size());
    }

    private TaskList taskListWithThreeTodos() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));
        return tasks;
    }
}
