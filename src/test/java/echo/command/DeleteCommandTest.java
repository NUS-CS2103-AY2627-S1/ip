package echo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.storage.Storage;
import echo.task.TaskList;
import echo.task.Todo;
import echo.ui.Ui;

public class DeleteCommandTest {
    @TempDir
    private Path tempDir;

    @Test
    public void execute_oneRemainingTask_usesSingularMissionCount() throws Exception {
        TaskList tasks = taskListWithSize(2);

        String response = new DeleteCommand(1).execute(tasks, new Ui(), storage());

        assertEquals("Mission removed from the manifest:\n"
                + "[T][ ] mission 2\n"
                + "You now have 1 mission on the manifest.", response);
    }

    @Test
    public void execute_twoRemainingTasks_usesPluralMissionCount() throws Exception {
        TaskList tasks = taskListWithSize(3);

        String response = new DeleteCommand(2).execute(tasks, new Ui(), storage());

        assertEquals("Mission removed from the manifest:\n"
                + "[T][ ] mission 3\n"
                + "You now have 2 missions on the manifest.", response);
    }

    private TaskList taskListWithSize(int size) {
        TaskList tasks = new TaskList();
        for (int i = 1; i <= size; i++) {
            tasks.add(new Todo("mission " + i));
        }
        return tasks;
    }

    private Storage storage() {
        return new Storage(tempDir.resolve("echo.txt"));
    }
}
