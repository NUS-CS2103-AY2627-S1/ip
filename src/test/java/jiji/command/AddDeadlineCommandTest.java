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
 * Unit tests for {@link AddDeadlineCommand}.
 */
public class AddDeadlineCommandTest {

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
    public void execute_validDeadline_addsTaskAndPersists() throws JijiException {
        AddDeadlineCommand command = new AddDeadlineCommand("return book", "2026-08-30");
        String result = command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: Aug 30 2026)", tasks.get(0).toString());
        assertTrue(result.contains("return book"));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_duplicateDeadline_throwsException() throws JijiException {
        AddDeadlineCommand command1 = new AddDeadlineCommand("return book", "2026-08-30");
        command1.execute(tasks, ui, storage);

        AddDeadlineCommand duplicateCommand = new AddDeadlineCommand("RETURN BOOK", "2026-08-30");
        JijiException e = assertThrows(JijiException.class, () ->
                duplicateCommand.execute(tasks, ui, storage));
        assertTrue(e.getMessage().contains("already in your list"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_nullDependencies_throwsAssertionError() {
        AddDeadlineCommand command = new AddDeadlineCommand("return book", "2026-08-30");
        assertThrows(AssertionError.class, () -> command.execute(null, ui, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, null, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, ui, null));
    }
}
