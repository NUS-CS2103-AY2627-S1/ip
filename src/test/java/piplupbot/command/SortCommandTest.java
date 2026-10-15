package piplupbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import piplupbot.RecordingUi;
import piplupbot.Storage;
import piplupbot.task.Deadline;
import piplupbot.task.SortDirection;
import piplupbot.task.SortKey;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

/**
 * Tests {@link SortCommand}, which puts the list in order, shows it, and saves
 * it.
 *
 * <p>The orders themselves belong to {@link SortKey} and are tested there. These
 * cases check what the command adds to them. The heading names the key, and
 * says when the order is reversed -- without that, {@code sort date} and
 * {@code sort date desc} would be introduced by the same words. And the list is
 * saved, so that the new order lasts beyond this session.</p>
 *
 * <p>Every case keeps its tasks in a folder JUnit creates and deletes for it, so
 * no test can read or damage the real save file.</p>
 */
public class SortCommandTest {

    /**
     * A folder JUnit creates for each test and deletes afterwards. It is left
     * package-private because {@code @TempDir} cannot fill in a private field.
     */
    @TempDir
    Path tempDir;

    /** Catches what the command says. */
    private final RecordingUi ui = new RecordingUi();

    /**
     * Returns the save file this test should use, inside the temporary folder.
     *
     * @return The path to a save file of this test's own.
     */
    private Path getSaveFile() {
        return tempDir.resolve("piplupbot.txt");
    }

    @Test
    public void execute_nameAscending_showsAndSavesTheSortedList() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("write notes"));
        tasks.add(new Todo("read book"));

        new SortCommand(SortKey.NAME, SortDirection.ASC).execute(tasks, ui, new Storage(getSaveFile()));

        assertEquals(List.of("Here are your tasks, sorted by name:\n"
                + "1.[T][ ] read book\n"
                + "2.[T][ ] write notes"), ui.getReplies());
        assertEquals("T | 0 | read book\nT | 0 | write notes\n", Files.readString(getSaveFile()));
    }

    @Test
    public void execute_dateDescending_headingSaysTheOrderIsReversed() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", "2019-10-15 1800"));
        tasks.add(new Deadline("pay fees", "2019-12-01 0900"));

        new SortCommand(SortKey.DATE, SortDirection.DESC).execute(tasks, ui, new Storage(getSaveFile()));

        assertEquals(List.of("Here are your tasks, sorted by date, in reverse:\n"
                + "1.[D][ ] pay fees (by: Dec 1 2019 09:00 AM)\n"
                + "2.[D][ ] return book (by: Oct 15 2019 06:00 PM)"), ui.getReplies());
    }

    /**
     * An empty list is sorted like any other: the heading appears with no rows
     * under it, as it does for {@code list}, and the save leaves an empty file.
     */
    @Test
    public void execute_emptyList_showsTheHeadingAloneAndSaves() throws Exception {
        new SortCommand(SortKey.STATUS, SortDirection.ASC)
                .execute(new TaskList(), ui, new Storage(getSaveFile()));

        assertEquals(List.of("Here are your tasks, sorted by status:"), ui.getReplies());
        assertEquals("", Files.readString(getSaveFile()));
    }
}
