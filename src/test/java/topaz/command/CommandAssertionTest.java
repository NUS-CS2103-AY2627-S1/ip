package topaz.command;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import topaz.storage.Storage;
import topaz.task.TaskList;
import topaz.ui.Ui;

/** Tests the internal preconditions asserted by task-changing commands. */
class CommandAssertionTest {
    private static final Storage STORAGE = new Storage(Path.of("test-data.txt"));
    private static final Ui UI = new Ui();

    @Test
    void execute_nullAddTask_throwsAssertionError() {
        assertThrows(AssertionError.class,
                () -> new AddCommand(null).execute(new TaskList(), UI, STORAGE));
    }

    @Test
    void execute_invalidTaskIndex_throwsAssertionError() {
        assertThrows(AssertionError.class,
                () -> new DeleteCommand(-1).execute(new TaskList(), UI, STORAGE));
        assertThrows(AssertionError.class,
                () -> new MarkCommand(0).execute(new TaskList(), UI, STORAGE));
        assertThrows(AssertionError.class,
                () -> new UnmarkCommand(1).execute(new TaskList(), UI, STORAGE));
    }
}
