package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jiji.exception.JijiException;
import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Unit tests for {@link AddTodoCommand}.
 */
public class AddTodoCommandTest {

    @TempDir
    public Path tempDir;

    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test.txt").toString());
    }

    @Test
    public void execute_validTodo_addsTaskAndPersists() throws JijiException {
        AddTodoCommand command = new AddTodoCommand("read book");
        String result = command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertTrue(result.contains("read book"));
        assertTrue(result.contains("1 tasks"));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_duplicateTodo_throwsException() throws JijiException {
        AddTodoCommand command1 = new AddTodoCommand("read book");
        command1.execute(tasks, ui, storage);

        AddTodoCommand duplicateCommand = new AddTodoCommand("READ BOOK");
        JijiException e = assertThrows(JijiException.class, () ->
                duplicateCommand.execute(tasks, ui, storage));
        assertTrue(e.getMessage().contains("already in your list"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_nullDependencies_throwsAssertionError() {
        AddTodoCommand command = new AddTodoCommand("read book");
        assertThrows(AssertionError.class, () -> command.execute(null, ui, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, null, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, ui, null));
    }
}
