package elsa.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.Deadline;
import elsa.task.Event;
import elsa.task.TaskList;
import elsa.task.Todo;
import elsa.ui.Ui;

/** Tests {@link OnCommand}, which shows the tasks falling on one date. */
public class OnCommandTest {

    private static final LocalDate FIRST = LocalDate.of(2999, 1, 1);
    private static final LocalDate SECOND = LocalDate.of(2999, 1, 2);
    private static final LocalDate THIRD = LocalDate.of(2999, 1, 3);

    /** A todo, a deadline due on the second, and an event spanning all three days. */
    private static TaskList mixedTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", SECOND));
        tasks.add(new Event("book fair", FIRST, THIRD));
        return tasks;
    }

    private static Storage storeIn(Path folder) {
        return new Storage(folder.resolve("tasks.txt").toString());
    }

    /**
     * A deadline falls on one day and an event on every day between its two, so
     * asking about the middle day has to find both. The todo never falls on any
     * date and must not appear.
     */
    @Test
    public void execute_aDateBothADeadlineAndAnEventFallOn_showsBoth(@TempDir Path folder)
            throws ElsaException {
        assertEquals("Here are the tasks on Jan 02 2999:\n"
                        + "2.[D][ ] return book (by: Jan 02 2999)\n"
                        + "3.[E][ ] book fair (from: Jan 01 2999 to: Jan 03 2999)",
                new OnCommand(SECOND).execute(mixedTasks(), new Ui(), storeIn(folder)));
    }

    @Test
    public void execute_aDateNothingFallsOn_namesTheDateAskedAbout(@TempDir Path folder)
            throws ElsaException {
        assertEquals("Nothing on Jan 04 2999.",
                new OnCommand(LocalDate.of(2999, 1, 4))
                        .execute(mixedTasks(), new Ui(), storeIn(folder)));
    }

    @Test
    public void execute_always_leavesTheStoreAlone(@TempDir Path folder) throws ElsaException {
        Path file = folder.resolve("tasks.txt");

        new OnCommand(SECOND).execute(mixedTasks(), new Ui(), new Storage(file.toString()));

        assertFalse(Files.exists(file), "asking about a date wrote to the disk");
    }
}
