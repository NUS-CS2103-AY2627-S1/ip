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
 * Unit tests for {@link AddEventCommand}.
 */
public class AddEventCommandTest {

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
    public void execute_validEvent_addsTaskAndPersists() throws JijiException {
        AddEventCommand command = new AddEventCommand("camp", "2026-08-29", "2026-08-31");
        String result = command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals("[E][ ] camp (from: Aug 29 2026 to: Aug 31 2026)", tasks.get(0).toString());
        assertTrue(result.contains("camp"));
        assertEquals(1, storage.load().size());
    }

    @Test
    public void execute_duplicateEvent_throwsException() throws JijiException {
        AddEventCommand command1 = new AddEventCommand("camp", "2026-08-29", "2026-08-31");
        command1.execute(tasks, ui, storage);

        AddEventCommand duplicateCommand = new AddEventCommand("CAMP", "2026-08-29", "2026-08-31");
        JijiException e = assertThrows(JijiException.class, () ->
                duplicateCommand.execute(tasks, ui, storage));
        assertTrue(e.getMessage().contains("already in your list"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_eventEndBeforeStart_throwsException() {
        AddEventCommand invertedDates = new AddEventCommand("party", "2026-09-03", "2026-09-01");
        JijiException e1 = assertThrows(JijiException.class, () ->
                invertedDates.execute(tasks, ui, storage));
        assertTrue(e1.getMessage().contains("cannot be earlier than start"));

        AddEventCommand invertedDateTimes = new AddEventCommand("meeting", "2026-09-03 1800", "2026-09-03 1400");
        JijiException e2 = assertThrows(JijiException.class, () ->
                invertedDateTimes.execute(tasks, ui, storage));
        assertTrue(e2.getMessage().contains("cannot be earlier than start"));
    }

    @Test
    public void execute_nullDependencies_throwsAssertionError() {
        AddEventCommand command = new AddEventCommand("camp", "2026-08-29", "2026-08-31");
        assertThrows(AssertionError.class, () -> command.execute(null, ui, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, null, storage));
        assertThrows(AssertionError.class, () -> command.execute(tasks, ui, null));
    }
}
