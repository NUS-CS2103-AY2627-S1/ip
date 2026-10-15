package piplupbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import piplupbot.RecordingUi;
import piplupbot.Storage;
import piplupbot.task.Deadline;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

/**
 * Tests {@link ListCommand}, which shows every stored task under a heading.
 *
 * <p>How the tasks are numbered is {@link TaskList}'s business, and is tested
 * there. These cases check the heading, what an empty list looks like, and
 * that showing the list saves nothing.</p>
 */
public class ListCommandTest {

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
    public void execute_twoKindsOfTask_showsEachInItsOwnFormUnderTheHeading() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2019-10-15 1800"));

        new ListCommand().execute(tasks, ui, new Storage(getSaveFile()));

        assertEquals(List.of("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][ ] return book (by: Oct 15 2019 06:00 PM)"), ui.getReplies());
        assertFalse(Files.exists(getSaveFile()), "Showing the list should save nothing");
    }

    /** An empty list is answered with the heading alone, rather than with no reply at all. */
    @Test
    public void execute_emptyList_showsTheHeadingAlone() {
        new ListCommand().execute(new TaskList(), ui, new Storage(getSaveFile()));

        assertEquals(List.of("Here are the tasks in your list:"), ui.getReplies());
    }
}
