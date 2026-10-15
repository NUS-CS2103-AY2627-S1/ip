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
 * Unit tests for {@link MarkCommand}.
 */
public class MarkCommandTest {

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
    public void execute_validIndex_marksTaskAsDoneAndPersists() throws JijiException {
        tasks.add(new Todo("read book"));

        MarkCommand command = new MarkCommand(0);
        String result = command.execute(tasks, ui, storage);

        assertTrue(tasks.get(0).isDone());
        assertEquals("[T][X] read book", tasks.get(0).toString());
        assertTrue(result.contains("read book"));
        assertEquals("1", storage.load().get(0).toFileFormat().split(" \\| ")[1]);
    }

    @Test
    public void execute_invalidIndex_throwsJijiInvalidIndexException() {
        tasks.add(new Todo("read book"));

        MarkCommand negative = new MarkCommand(-1);
        assertThrows(JijiInvalidIndexException.class, () -> negative.execute(tasks, ui, storage));

        MarkCommand outOfBounds = new MarkCommand(5);
        assertThrows(JijiInvalidIndexException.class, () -> outOfBounds.execute(tasks, ui, storage));
    }

    @Test
    public void execute_nullDependencies_throwsAssertionError() {
        MarkCommand command = new MarkCommand(0);
        assertThrows(AssertionError.class, () -> command.execute(null, ui, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, null, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, ui, null));
    }
}
