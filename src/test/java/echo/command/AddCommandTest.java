package echo.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.exception.DuplicateTaskException;
import echo.storage.Storage;
import echo.task.TaskList;
import echo.task.Todo;
import echo.ui.Ui;

public class AddCommandTest {
    @TempDir
    private Path tempDir;

    @Test
    public void execute_firstTask_usesSingularMissionCount() throws Exception {
        TaskList tasks = new TaskList();
        AddCommand command = new AddCommand(new Todo("read book"));

        String response = command.execute(tasks, new Ui(), storage());

        assertEquals("Mission logged:\n"
                + "[T][ ] read book\n"
                + "You now have 1 mission on the manifest.", response);
    }

    @Test
    public void execute_secondTask_usesPluralMissionCount() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        AddCommand command = new AddCommand(new Todo("write code"));

        String response = command.execute(tasks, new Ui(), storage());

        assertEquals("Mission logged:\n"
                + "[T][ ] write code\n"
                + "You now have 2 missions on the manifest.", response);
    }

    @Test
    public void execute_duplicateTask_exceptionThrownAndListUnchanged() throws Exception {
        TaskList tasks = new TaskList();
        Todo existingTask = new Todo("read book");
        existingTask.markDone();
        tasks.add(existingTask);

        assertThrows(DuplicateTaskException.class, () -> new AddCommand(new Todo("read book"))
                .execute(tasks, new Ui(), storage()));
        assertEquals(1, tasks.size());
    }

    private Storage storage() {
        return new Storage(tempDir.resolve("echo.txt"));
    }
}
