package penny.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import penny.common.PennyException;
import penny.storage.Storage;
import penny.task.Deadline;
import penny.task.TaskList;
import penny.ui.Ui;

/**
 * Unit test suite for {@link OnCommand}.
 * Verifies querying tasks occurring on a specified date.
 */
public class OnCommandTest {

    @TempDir
    private Path tempDir;

    private TaskList taskList;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() throws PennyException {
        taskList = new TaskList();
        taskList.add(new Deadline("submit assignment", "2019-10-15 1800"));
        ui = new Ui();
        storage = new Storage(tempDir.resolve("tasks.txt"));
    }

    @Test
    public void execute_matchingDate_returnsMatchingTasks() {
        OnCommand command = new OnCommand(LocalDate.of(2019, 10, 15));
        String result = command.execute(taskList, ui, storage);

        assertNotNull(result);
        assertTrue(result.contains("submit assignment"));
    }

    @Test
    public void execute_nonMatchingDate_returnsZeroMatchesMessage() {
        OnCommand command = new OnCommand(LocalDate.of(2019, 10, 16));
        String result = command.execute(taskList, ui, storage);

        assertNotNull(result);
        assertTrue(result.contains("Oct 16 2019"));
    }

    @Test
    public void constructor_nullDate_assertionError() {
        assertThrows(AssertionError.class, () -> {
            new OnCommand(null);
        });
    }
}
