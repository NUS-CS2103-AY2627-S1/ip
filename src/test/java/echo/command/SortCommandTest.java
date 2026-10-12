package echo.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import echo.EchoException;
import echo.storage.Storage;
import echo.task.SortCriteria;
import echo.task.TaskList;
import echo.task.TaskManager;
import echo.ui.Ui;

/** Tests execution of {@link SortCommand}. */
public class SortCommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_emptyList_returnsEmptyMessage() throws EchoException {
        TaskManager manager = new TaskManager(new TaskList(), new Storage(storagePath()));
        Ui ui = new Ui();
        SortCommand command = new SortCommand(SortCriteria.DATE);

        String result = command.execute(manager, ui);

        assertTrue(result.contains("Directive manifest is empty! Nothing to sort."));
    }

    @Test
    public void execute_withTasks_returnsSuccessMessage() throws EchoException {
        TaskManager manager = new TaskManager(new TaskList(), new Storage(storagePath()));
        manager.addTodo("read book");
        manager.addDeadline("return book", "15-10-2026");
        Ui ui = new Ui();
        SortCommand command = new SortCommand(SortCriteria.DATE);

        String result = command.execute(manager, ui);

        assertTrue(result.contains("Directives organized successfully:"));
        assertTrue(result.indexOf("return book") < result.indexOf("read book"));
    }

    private Path storagePath() {
        return temporaryDirectory.resolve("echo.txt");
    }
}
