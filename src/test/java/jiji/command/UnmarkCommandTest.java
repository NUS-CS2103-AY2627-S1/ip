package jiji.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Unit tests for {@link UnmarkCommand}.
 */
public class UnmarkCommandTest {

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
    public void execute_validIndex_unmarksTaskAndPersists() throws JijiException {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);

        UnmarkCommand command = new UnmarkCommand(0);
        String result = command.execute(tasks, ui, storage);

        assertFalse(tasks.get(0).isDone());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertTrue(result.contains("read book"));
        assertEquals("0", storage.load().get(0).toFileFormat().split(" \\| ")[1]);
    }

    @Test
    public void execute_invalidIndex_throwsJijiInvalidIndexException() {
        tasks.add(new Todo("read book"));

        UnmarkCommand negative = new UnmarkCommand(-1);
        assertThrows(JijiInvalidIndexException.class, () -> negative.execute(tasks, ui, storage));

        UnmarkCommand outOfBounds = new UnmarkCommand(3);
        assertThrows(JijiInvalidIndexException.class, () -> outOfBounds.execute(tasks, ui, storage));
    }

    @Test
    public void execute_nullDependencies_throwsAssertionError() {
        UnmarkCommand command = new UnmarkCommand(0);
        assertThrows(AssertionError.class, () -> command.execute(null, ui, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, null, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, ui, null));
    }
}
