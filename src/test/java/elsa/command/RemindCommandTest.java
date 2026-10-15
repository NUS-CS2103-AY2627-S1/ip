package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.Dates;
import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.Deadline;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/**
 * Tests {@link RemindCommand}, which shows the deadlines needing attention.
 *
 * <p>Unlike the other listings, this one asks what today is, so its dates are
 * worked out from {@link Dates#today()} rather than written down. A date written
 * down would drift into the past and change the answer on its own one day.
 */
public class RemindCommandTest {

    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    @Test
    public void execute_anOverdueDeadline_remindsAboutIt(@TempDir Path folder) throws ElsaException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", Dates.today().minusDays(3)));

        String reply = new RemindCommand().execute(tasks, new Ui(), storeIn(folder));

        assertTrue(reply.startsWith("Here is what needs your attention:"), reply);
        assertTrue(reply.contains("return book"), reply);
    }

    /**
     * A deadline further off than the week looked ahead is not yet the user's
     * problem, and neither is a todo, which has no date to be due on at all.
     */
    @Test
    public void execute_nothingDueSoon_saysSoRatherThanShowingAnEmptyList(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("far off", Dates.today().plusDays(60)));

        assertEquals("Nothing is overdue, and nothing is due in the next 7 days.",
                new RemindCommand().execute(tasks, new Ui(), storeIn(folder)));
    }

    /** A deadline already done is not owed, however long ago it was due. */
    @Test
    public void execute_anOverdueDeadlineAlreadyDone_doesNotRemindAboutIt(@TempDir Path folder)
            throws ElsaException {
        TaskList tasks = new TaskList();
        Deadline done = new Deadline("return book", Dates.today().minusDays(3));
        done.markAsDone();
        tasks.add(done);

        assertEquals("Nothing is overdue, and nothing is due in the next 7 days.",
                new RemindCommand().execute(tasks, new Ui(), storeIn(folder)));
    }

    @Test
    public void execute_always_leavesTheStoreAlone(@TempDir Path folder) throws ElsaException {
        Path file = folder.resolve("tasks.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", Dates.today()));

        new RemindCommand().execute(tasks, new Ui(), new Storage(file.toString()));

        assertFalse(Files.exists(file), "reminding wrote to the disk");
    }
}
