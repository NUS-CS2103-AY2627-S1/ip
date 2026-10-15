package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jiji.exception.JijiException;
import jiji.exception.JijiInvalidIndexException;
import jiji.storage.Storage;
import jiji.task.TaskList;
import jiji.task.Todo;
import jiji.ui.Ui;

/**
 * Unit tests for {@link DeleteCommand}.
 */
public class DeleteCommandTest {

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
    public void execute_validIndex_removesTaskAndPersists() throws JijiException {
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));

        DeleteCommand command = new DeleteCommand(0);
        String result = command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] write essay", tasks.get(0).toString());
        assertTrue(result.contains("read book"));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_invalidIndex_throwsJijiInvalidIndexException() {
        tasks.add(new Todo("read book"));

        DeleteCommand negativeCommand = new DeleteCommand(-1);
        assertThrows(JijiInvalidIndexException.class, () -> negativeCommand.execute(tasks, ui, storage));

        DeleteCommand outOfBoundsCommand = new DeleteCommand(1);
        assertThrows(JijiInvalidIndexException.class, () -> outOfBoundsCommand.execute(tasks, ui, storage));
    }

    @Test
    public void execute_emptyTaskList_throwsJijiInvalidIndexException() {
        DeleteCommand command = new DeleteCommand(0);
        assertThrows(JijiInvalidIndexException.class, () -> command.execute(tasks, ui, storage));
    }

    @Test
    public void execute_nullDependencies_throwsAssertionError() {
        DeleteCommand command = new DeleteCommand(0);
        assertThrows(AssertionError.class, () -> command.execute(null, ui, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, null, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, ui, null));
    }
}
