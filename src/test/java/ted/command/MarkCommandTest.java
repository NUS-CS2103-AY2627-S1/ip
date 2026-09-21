package ted.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ted.Storage;
import ted.TedException;
import ted.Ui;
import ted.task.TaskList;
import ted.task.Todo;

/**
 * Tests {@link MarkCommand}, which marks a task as done or reverses that.
 * <p>
 * Every test saves inside a {@link TempDir}, so running the tests can never
 * disturb the user's own tasks.
 */
public class MarkCommandTest {
    /**
     * Verifies that marking and then unmarking a task changes its status both ways.
     */
    @Test
    public void execute_markThenUnmark_changesStatusBothWays(@TempDir Path tempDir) throws TedException {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        TaskList tasks = new TaskList(List.of(new Todo("borrow book")));

        new MarkCommand(0, true).execute(tasks, new Ui(), storage);
        assertTrue(tasks.get(0).isDone());

        new MarkCommand(0, false).execute(tasks, new Ui(), storage);
        assertFalse(tasks.get(0).isDone());
    }

    /**
     * Verifies that marking a task that is already done is reported rather
     * than confirmed as a change.
     */
    @Test
    public void execute_alreadyDone_exceptionThrown(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        Todo doneTask = new Todo("borrow book");
        doneTask.markAsDone();
        TaskList tasks = new TaskList(List.of(doneTask));

        MarkCommand markAgain = new MarkCommand(0, true);
        TedException e = assertThrows(TedException.class, () -> markAgain.execute(tasks, new Ui(), storage));
        assertTrue(e.getMessage().contains("Task 1 is already done."));
    }

    /**
     * Verifies that unmarking a task that is not done is reported rather than
     * confirmed as a change.
     */
    @Test
    public void execute_alreadyNotDone_exceptionThrown(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("ted.txt").toString());
        TaskList tasks = new TaskList(List.of(new Todo("borrow book")));

        MarkCommand unmarkAgain = new MarkCommand(0, false);
        TedException e = assertThrows(TedException.class, () -> unmarkAgain.execute(tasks, new Ui(), storage));
        assertTrue(e.getMessage().contains("Task 1 isn't done yet"));
    }
}
